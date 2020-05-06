package com.senjyouhara.crawler.base;

import com.senjyouhara.crawler.model.CrawlerCookie;
import com.senjyouhara.crawler.model.CrawlerRequest;
import com.senjyouhara.crawler.model.CrawlerResponse;

import java.util.List;
import java.util.Map;

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
		return new CrawlerRequest().setUrl(url).setParams(params).setHeader(header).setCallBackFunc(crawlerCallback).setMeta(meta).setCrawlerCookies(crawlerCookies);
	}
}
