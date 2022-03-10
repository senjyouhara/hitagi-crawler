package com.senjyouhara.crawler.ThreadPool;

import com.senjyouhara.crawler.http.CrawlerProcess;

@FunctionalInterface
public interface ThreadManager {

	boolean invoke(CrawlerProcess crawlerProcess);

}
