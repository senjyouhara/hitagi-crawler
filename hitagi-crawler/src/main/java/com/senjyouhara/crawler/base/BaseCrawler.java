package com.senjyouhara.crawler.base;

import com.senjyouhara.crawler.model.CrawlerRequest;
import com.senjyouhara.crawler.model.CrawlerResponse;

import java.util.List;

public interface BaseCrawler {

	String[] startUrls();

	void responseHandler(CrawlerResponse crawlerResponse);

	void errorRequest(CrawlerRequest crawlerRequest);

	List<CrawlerRequest> startRequests();

}
