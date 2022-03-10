package com.senjyouhara.crawler.base;

import com.senjyouhara.crawler.model.CrawlerCookie;
import com.senjyouhara.crawler.model.CrawlerRequest;
import com.senjyouhara.crawler.model.CrawlerResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestBuild {

	public static <T,A> CrawlerRequest build(String url, CrawlerCallback<T,A> crawlerCallback) {
		return build(url, null, null, null, null, crawlerCallback);
	}

	public static <T,A> CrawlerRequest build(String url, Map<String, String> params, CrawlerCallback<T,A> crawlerCallback) {
		return build(url, params, null, null, null, crawlerCallback);
	}

	public static <T,A> CrawlerRequest build(String url, Map<String, String> params, Map<String, String> header, CrawlerCallback<T,A> crawlerCallback) {
		return build(url, params, header, null, null, crawlerCallback);
	}

	public static <T,A> CrawlerRequest build(String url, Map<String, String> params, Map<String, String> header, Map<String, Object> meta, CrawlerCallback<T,A> crawlerCallback) {
		return build(url, params, header, meta, null, crawlerCallback);
	}

	public static <T,A> CrawlerRequest build(String url, Map<String, String> params, Map<String, String> header, Map<String, Object> meta, List<CrawlerCookie> crawlerCookies, CrawlerCallback<T,A> crawlerCallback) {
		CrawlerRequest crawlerRequest = new CrawlerRequest();
		Map<String, String> header1 = crawlerRequest.getHeader();
		Map<String, Object> meta1 = crawlerRequest.getMeta();
		Map<String, String> params1 = crawlerRequest.getParams();
		Set<CrawlerCookie> crawlerCookies1 = crawlerRequest.getCrawlerCookies();
		if(header != null){
			header1.putAll(header);
		}
		if(meta != null){
			meta1.putAll(meta);
		}
		if(params != null){
			params1.putAll(params);
		}
		if(crawlerCookies != null){
			crawlerCookies1.addAll(crawlerCookies);
		}

		return crawlerRequest
				.setUrl(url)
				.setCallBackFunc(crawlerCallback)
				.setParams(params1)
				.setMeta(meta1)
				.setHeader(header1)
				.setCrawlerCookies(crawlerCookies1);
	}
}
