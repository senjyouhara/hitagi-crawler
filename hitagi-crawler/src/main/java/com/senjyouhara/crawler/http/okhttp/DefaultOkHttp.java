package com.senjyouhara.crawler.http.okhttp;

import com.senjyouhara.crawler.annotation.Crawler;
import com.senjyouhara.crawler.base.AbstractCrawler;
import com.senjyouhara.crawler.confg.CrawlerContext;
import com.senjyouhara.crawler.enums.CrawlerBodyType;
import com.senjyouhara.crawler.http.CrawlerHttpType;
import com.senjyouhara.crawler.http.base.HttpDownload;
import com.senjyouhara.crawler.model.CrawlerCookie;
import com.senjyouhara.crawler.model.CrawlerRequest;
import com.senjyouhara.crawler.model.CrawlerResponse;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.exception.XpathSyntaxErrorException;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class DefaultOkHttp implements HttpDownload {

	private String crawlerName;

	private CrawlerRequest oldRequest;

	private CrawlerResponse crawlerResponse;

	private okhttp3.Response response;

	private OkHttpClient okHttpClient;

	private okhttp3.Request.Builder httpBuild;

	public DefaultOkHttp( String crawlerName){
		this.crawlerName = crawlerName;
	}

	@Override
	public CrawlerResponse process(CrawlerRequest crawlerRequest) throws Exception {

		OkHttpClient.Builder okBuild = OkHttpBuild.getInstance();

		Object bean = CrawlerContext.getApplicationContext().getBean(crawlerName);

		AbstractCrawler ab = (AbstractCrawler) bean;
		Crawler annotation = ab.getClass().getAnnotation(Crawler.class);

		if(annotation != null){
			okBuild.readTimeout(annotation.timeOut(), TimeUnit.SECONDS);
			okBuild.connectTimeout(annotation.timeOut(), TimeUnit.SECONDS);
		}

		okHttpClient = okBuild.build();
		httpBuild = OkHttpBuild.createHttp(crawlerRequest, (AbstractCrawler) bean);
		response = okHttpClient.newCall(httpBuild.build()).execute();

		oldRequest = crawlerRequest;
		return renderResponse(response, crawlerRequest);
	}

	private CrawlerResponse renderResponse(Response response, CrawlerRequest crawlerRequest) {


		CrawlerResponse crawlerResponse = new CrawlerResponse();
		crawlerResponse.setHttpType(CrawlerHttpType.OK_HTTP);
		crawlerResponse.setUrl(crawlerRequest.getUrl());
		crawlerResponse.setRealUrl(response.request().url().toString());
		crawlerResponse.setRequest(crawlerRequest);
		crawlerResponse.setMeta(crawlerRequest.getMeta());

		List<String> nameAndValues = response.headers("Set-Cookie");
		List<CrawlerCookie> crawlerCookies = new ArrayList<>();
		Map<String,String> cookie = new HashMap<>();
		for (int i = 0; i < nameAndValues.size(); i++) {
			String s = nameAndValues.get(i);
			String[] split = s.split(";");
			if(split.length > 0){
				String s1 = split[0];
				String[] split1 = s1.split("=");
				if(split1.length == 2){
					cookie.put(split1[0],split1[1]);
					CrawlerCookie crawlerCookie = new CrawlerCookie(split1[0],split1[1]);
					crawlerCookies.add(crawlerCookie);
				}
			}
		}
		ArrayList<CrawlerCookie> crawlerCookies1 = new ArrayList<>();

		if(crawlerRequest.getCrawlerCookies() != null && crawlerRequest.getCrawlerCookies().size() > 0){
			crawlerCookies1.addAll(crawlerRequest.getCrawlerCookies());
		}

		if(crawlerCookies.size() > 0){
			crawlerCookies1.addAll(crawlerCookies);
		}
		crawlerResponse.setCrawlerCookies(crawlerCookies1);
		cookie.values().forEach(System.out::println);



		ResponseBody okResponseBody = response.body();
		if (okResponseBody!=null){
			String type = okResponseBody.contentType().type().toLowerCase();
			String subtype = okResponseBody.contentType().subtype().toLowerCase();

			if (type.contains("text")||type.contains("json")||type.contains("ajax")||subtype.contains("json")
					||subtype.contains("ajax")){
				crawlerResponse.setBodyType(CrawlerBodyType.TEXT);
				try {
					byte[] data = okResponseBody.bytes();
					String utfContent = new String(data, StandardCharsets.UTF_8);
					String charsetFinal = renderRealCharset(utfContent);
					log.info("charsetFinal :{}",charsetFinal);
					if (charsetFinal.equals("UTF-8")){
						crawlerResponse.setContent(utfContent);
					}else {
						crawlerResponse.setContent(new String(data,charsetFinal));
					}
				} catch (Exception e) {
					log.error("no content data");
				}
			}else {
				crawlerResponse.setBodyType(CrawlerBodyType.BINARY);
				try {
					crawlerResponse.setData(okResponseBody.bytes());
				} catch (Exception e) {
					log.error("no content data");
				}
			}
		}
		this.crawlerResponse = crawlerResponse;

		return crawlerResponse;
	}

private String getFirstElementStr(List<Object> list, String val){
	if (CollectionUtils.isEmpty(list)){
		return val;
	}
	return list.get(0).toString();
}

	public String parseCharset(String target){
		 final Pattern charsetPattern = Pattern.compile("charset=([1-9a-zA-Z-]+)$");
		Matcher matcher = charsetPattern.matcher(target);
		if (matcher.find()){
			return matcher.group(1);
		}
		return "";
	}

	private String renderRealCharset(String content) throws XpathSyntaxErrorException {
		String charset;
		JXDocument doc = JXDocument.create(content);
		charset = getFirstElementStr(doc.sel("//meta[@charset]/@charset"),"").trim();
		if (StringUtils.isBlank(charset)){
			charset = getFirstElementStr(doc.sel("//meta[@http-equiv='charset']/@content"),"").trim();
		}
		if (StringUtils.isBlank(charset)){
			String ct = StringUtils.join(doc.sel("//meta[@http-equiv='Content-Type']/@content|//meta[@http-equiv='content-type']/@content"),";").trim();
			charset = parseCharset(ct.toLowerCase());
		}
		return StringUtils.isNotBlank(charset)?charset:"UTF-8";
	}

	@Override
	public CrawlerResponse metaRefresh(String nextUrl) throws Exception {
		HttpUrl lastUrl = response.request().url();
		if (!nextUrl.startsWith("http")){
			String prefix = lastUrl.scheme()+"://"+lastUrl.host()+lastUrl.encodedPath();
			nextUrl = prefix + nextUrl;
		}
		log.info("refresh url to={} from={}",nextUrl,lastUrl.toString());
		httpBuild.url(nextUrl);
		response = okHttpClient.newCall(httpBuild.build()).execute();
		return renderResponse(response,oldRequest);
	}
}
