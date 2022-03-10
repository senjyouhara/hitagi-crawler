package com.senjyouhara.crawler.http.okhttp;

import com.senjyouhara.core.json.JsonUtil;
import com.senjyouhara.core.ssl.MyX509TrustManager;
import com.senjyouhara.crawler.base.AbstractCrawler;
import com.senjyouhara.crawler.enums.CrawlerContentType;
import com.senjyouhara.crawler.enums.HttpMethod;
import com.senjyouhara.crawler.model.CrawlerCookie;
import com.senjyouhara.crawler.model.CrawlerRequest;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;

@Log4j2
public class OkHttpBuild {

	private OkHttpBuild(){}

	public static OkHttpClient.Builder getInstance(){
		OkHttpClient.Builder httpBuild = new OkHttpClient.Builder();
		MyX509TrustManager myX509TrustManager = new MyX509TrustManager();
		final TrustManager[] trustAllCerts = new TrustManager[]{
				myX509TrustManager
		};
		HostnameVerifier hostnameVerifier = new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			httpBuild.sslSocketFactory(sslSocketFactory, myX509TrustManager)
					.hostnameVerifier(hostnameVerifier);
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
			log.error("ssl init error : {}", e);
		}

		return httpBuild;

	}

	public static Request.Builder createHttp(CrawlerRequest crawlerRequest, AbstractCrawler abstractCrawler){

		Request.Builder requestBuilder = new Request.Builder();

		requestBuilder.url(crawlerRequest.getUrl());
		StringBuilder cookies = new StringBuilder();
		Set<CrawlerCookie> crawlerCookies = crawlerRequest.getCrawlerCookies();
		if(crawlerCookies != null && crawlerCookies.size() > 0){
			int i = 0;
			for (CrawlerCookie crawlerCookie : crawlerCookies) {
				cookies.append(crawlerCookie.getName()).append("=").append(crawlerCookie.getValue());
				if(crawlerRequest.getCrawlerCookies().size()-1 != i++){
					cookies.append("; ");
				}
			}
		}
		requestBuilder.header("User-Agent", StringUtils.isNotBlank(abstractCrawler.getCurrentUserAgent()) ? abstractCrawler.getCurrentUserAgent() : abstractCrawler.getUserAgent());

		if(StringUtils.isNotBlank(cookies)){
			requestBuilder.header("Cookie", cookies.toString());
		}

		if(crawlerRequest.getHeader() != null && crawlerRequest.getHeader().size() > 0){
			for (Map.Entry<String,String> entry:crawlerRequest.getHeader().entrySet()) {
				requestBuilder.addHeader(entry.getKey(), entry.getValue());
			}
		}

		if (HttpMethod.POST.equals(crawlerRequest.getHttpMethod())) {
			if (crawlerRequest.isHasJsonBody()){
				RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JsonUtil.toJsonStr(crawlerRequest.getParams()));
				requestBuilder.post(requestBody);
			}else {
				FormBody.Builder formBodyBuilder = new FormBody.Builder();
				if (crawlerRequest.getParams() != null) {
					for (Map.Entry<String, String> entry : crawlerRequest.getParams().entrySet()) {
						formBodyBuilder.add(entry.getKey(), entry.getValue());
					}
				}
				requestBuilder.post(formBodyBuilder.build());
			}
		} else {
			String queryStr = "";
			if (crawlerRequest.getParams()!=null&&!crawlerRequest.getParams().isEmpty()){
				queryStr += "?";
				for (Map.Entry<String, String> entry : crawlerRequest.getParams().entrySet()) {
					queryStr= queryStr+entry.getKey()+"="+entry.getValue()+"&";
				}
				requestBuilder.url(crawlerRequest.getUrl()+queryStr);
			}
			requestBuilder.get();
		}



		return requestBuilder;


	}

}
