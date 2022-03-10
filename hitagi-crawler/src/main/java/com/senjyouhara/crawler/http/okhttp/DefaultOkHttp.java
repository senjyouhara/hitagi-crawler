package com.senjyouhara.crawler.http.okhttp;

import com.senjyouhara.crawler.annotation.Crawler;
import com.senjyouhara.crawler.base.AbstractCrawler;
import com.senjyouhara.crawler.confg.CrawlerContext;
import com.senjyouhara.crawler.enums.CrawlerBodyType;
import com.senjyouhara.crawler.enums.CrawlerHttpType;
import com.senjyouhara.crawler.http.AbstractHttpDownload;
import com.senjyouhara.crawler.http.HttpDownload;
import com.senjyouhara.crawler.model.CrawlerCookie;
import com.senjyouhara.crawler.model.CrawlerRequest;
import com.senjyouhara.crawler.model.CrawlerResponse;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.exception.XpathSyntaxErrorException;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class DefaultOkHttp extends AbstractHttpDownload {

	private CrawlerHttpType crawlerHttpType = CrawlerHttpType.OK_HTTP;

	private CrawlerRequest oldRequest;

	private CrawlerResponse crawlerResponse;

	private okhttp3.Response response;

	private OkHttpClient okHttpClient;

	private okhttp3.Request.Builder httpBuild;

	public DefaultOkHttp( String crawlerName){
		super(crawlerName);
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

		CrawlerResponse crawlerResponse = handlerResponse(crawlerRequest, response.headers("Set-Cookie"));

		ResponseBody okResponseBody = response.body();
		if (okResponseBody!=null){
			String type = okResponseBody.contentType().type().toLowerCase();
			String subtype = okResponseBody.contentType().subtype().toLowerCase();

			if (type.contains("text")||type.contains("json")||type.contains("ajax")||subtype.contains("json")
					||subtype.contains("ajax")){
				this.crawlerResponse.setBodyType(CrawlerBodyType.TEXT);
				try {
					byte[] data = okResponseBody.bytes();
					String utfContent = new String(data, StandardCharsets.UTF_8);
					String charsetFinal = renderRealCharset(utfContent);
					log.info("charsetFinal :{}",charsetFinal);
					if (charsetFinal.equals("UTF-8")){
						this.crawlerResponse.setContent(utfContent);
					}else {
						this.crawlerResponse.setContent(new String(data,charsetFinal));
					}
				} catch (Exception e) {
					log.error("no content data");
				}
			}else {
				this.crawlerResponse.setBodyType(CrawlerBodyType.BINARY);
				try {
					this.crawlerResponse.setData(okResponseBody.bytes());
				} catch (Exception e) {
					log.error("no content data");
				}
			}
		}

		return this.crawlerResponse;
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
