package com.senjyouhara.crawler.http.httpclient;

import com.senjyouhara.core.json.JsonUtil;
import com.senjyouhara.crawler.annotation.Crawler;
import com.senjyouhara.crawler.base.AbstractCrawler;
import com.senjyouhara.crawler.confg.CrawlerContext;
import com.senjyouhara.crawler.enums.CrawlerBodyType;
import com.senjyouhara.crawler.enums.CrawlerHttpType;
import com.senjyouhara.crawler.enums.HttpMethod;
import com.senjyouhara.crawler.exception.CrawlerRequestException;
import com.senjyouhara.crawler.http.AbstractHttpDownload;
import com.senjyouhara.crawler.http.HttpDownload;
import com.senjyouhara.crawler.model.CrawlerCookie;
import com.senjyouhara.crawler.model.CrawlerRequest;
import com.senjyouhara.crawler.model.CrawlerResponse;
import com.senjyouhara.crawler.utils.HttpUtil;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
public class DefaultHttpClient extends AbstractHttpDownload {

	private CrawlerHttpType crawlerHttpType = CrawlerHttpType.HTTP_CLIENT;

	private CrawlerRequest oldRequest;

	private HttpResponse response;

	private CrawlerResponse crawlerResponse;

	public DefaultHttpClient(String crawlerName) {
		super(crawlerName);
	}

	@Override
	public CrawlerResponse process(CrawlerRequest crawlerRequest) throws Exception {
		HttpResponse httpResponse = requestLink(crawlerRequest);
		oldRequest = crawlerRequest;
		CrawlerResponse crawlerResponse = renderResponse(httpResponse, crawlerRequest);
		this.crawlerResponse = crawlerResponse;
		return crawlerResponse;
	}

	private HttpResponse requestLink(CrawlerRequest crawlerRequest) throws Exception {

		Object bean = CrawlerContext.getApplicationContext().getBean(crawlerName);
		AbstractCrawler ab = (AbstractCrawler) bean;

		HttpResponse result;

		String cookies = HttpUtil.cookie2String(crawlerRequest.getCrawlerCookies());
		Map<String, String> header = crawlerRequest.getHeader();

		header.put("User-Agent", StringUtils.isNotBlank(ab.getCurrentUserAgent()) ? ab.getCurrentUserAgent() : ab.getUserAgent());
		header.put("Content-Type", crawlerRequest.getRequestType());

		if (StringUtils.isNotBlank(cookies)) {
			header.put("Cookie", cookies);
		}

		log.info("request url : {}, method: {}, headers: {},  params: {}, proxy: {}", crawlerRequest.getUrl(),crawlerRequest.getHttpMethod(), header, crawlerRequest.getParams(),crawlerRequest.getProxy());

		if (crawlerRequest.getHttpMethod().equals(HttpMethod.POST)) {
			String jsonStr = JsonUtil.toJsonStr(crawlerRequest.getParams());
			if (crawlerRequest.getRequestType().contains("x-www-form-urlencoded")) {
				result = HttpUtil.doPost(crawlerRequest.getUrl(), null, header, null, crawlerRequest.getParams(), crawlerRequest.getProxy());
			} else {
				result = HttpUtil.doPost(crawlerRequest.getUrl(), null, header, null, jsonStr, crawlerRequest.getProxy());
			}
		} else if (crawlerRequest.getHttpMethod().equals(HttpMethod.PUT)) {
			String jsonStr = JsonUtil.toJsonStr(crawlerRequest.getParams());
			result = HttpUtil.doPut(crawlerRequest.getUrl(), null, header, null, StringUtils.isBlank(jsonStr) ? "" : jsonStr, crawlerRequest.getProxy());
		} else {
			result = HttpUtil.doGet(crawlerRequest.getUrl(), null, header, crawlerRequest.getParams());
		}
		return result;
	}

	private CrawlerResponse renderResponse(HttpResponse result, CrawlerRequest crawlerRequest) throws IOException {
		log.info("renderResponse start");
		crawlerRequest.setCurrentReqCount(crawlerRequest.getCurrentReqCount() + 1);
		if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

			HttpEntity entity = result.getEntity();
			Header contentType = entity.getContentType();
			String type = contentType.getValue();

			Header[] allHeaders = result.getHeaders("Set-Cookie");

			List<String> nameAndValues = new ArrayList<>();
			for (Header header : allHeaders) {
				nameAndValues.add(header.getValue());
			}
			crawlerResponse = handlerResponse(crawlerRequest, nameAndValues);
			crawlerResponse.setHttpType(crawlerHttpType);
			if (type.contains("text") || type.contains("json") || type.contains("ajax")) {
				crawlerResponse.setBodyType(CrawlerBodyType.TEXT);
				String s = EntityUtils.toString(result.getEntity(), "UTF-8");
				crawlerResponse.setContent(s);
			} else {
				InputStream content = result.getEntity().getContent();
				byte[] bytes = IOUtils.toByteArray(content);
				crawlerResponse.setData(bytes);
				crawlerResponse.setBodyType(CrawlerBodyType.BINARY);
			}
//		302重定向
		} else if (result.getStatusLine().getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY) {
			Header header = result.getFirstHeader("location"); // 跳转的目标地址是在 HTTP-HEAD 中的
			String newuri = header.getValue(); // 这就是跳转后的地址，再向这个地址发出新申请，以便得到跳转后的信息是啥。
			log.info("重定向后的uri为 : {}", newuri);
			crawlerRequest.setUrl(newuri);
			try {
				return process(crawlerRequest);
			} catch (Exception e) {
				throw new CrawlerRequestException("重定向请求出错，请检查。 请求状态码：" + result.getStatusLine().getStatusCode());
			}
		} else {
			throw new CrawlerRequestException("请求出错，请检查。 请求状态码：" + result.getStatusLine().getStatusCode());
		}
		log.info("renderResponse end");
		return crawlerResponse;

	}

	@Override
	public CrawlerResponse metaRefresh(String nextUrl) throws Exception {

		URI uri = new URI(nextUrl);
		if (!nextUrl.startsWith("http")) {
			String prefix = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
			nextUrl = prefix + nextUrl;
		}

		log.info("refresh url to={} from={}", nextUrl, uri.toString());
		oldRequest.setUrl(nextUrl);
		response = requestLink(oldRequest);
		return renderResponse(response, oldRequest);
	}
}
