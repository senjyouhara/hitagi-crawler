package com.senjyouhara.crawler;


import com.senjyouhara.crawler.ThreadPool.DefaultThreadManagerImpl;
import com.senjyouhara.crawler.pool.MyThreadPool;
import com.senjyouhara.crawler.confg.CrawlerBeanPostProcessor;
import com.senjyouhara.crawler.confg.CrawlerContext;
import com.senjyouhara.crawler.confg.CrawlerInit;
import com.senjyouhara.crawler.property.CrawlerProperty;
import org.springframework.context.annotation.Import;

@Import({
		CrawlerProperty.class,
		DefaultThreadManagerImpl.class,
		MyThreadPool.class,
		CrawlerContext.class,
		CrawlerBeanPostProcessor.class,
		CrawlerInit.class})
public class AutoConfigureCrawler {

}
