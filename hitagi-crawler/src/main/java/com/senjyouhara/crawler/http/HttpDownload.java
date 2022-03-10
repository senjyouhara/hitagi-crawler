package com.senjyouhara.crawler.http;

import com.senjyouhara.crawler.model.CrawlerRequest;
import com.senjyouhara.crawler.model.CrawlerResponse;

public interface HttpDownload {

	CrawlerResponse process(CrawlerRequest crawlerRequest) throws Exception;

	CrawlerResponse metaRefresh(String nextUrl) throws Exception;
}
