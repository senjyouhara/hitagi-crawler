package com.senjyouhara.crawler;


import com.senjyouhara.crawler.ThreadPool.ThreadManager;
import com.senjyouhara.crawler.queue.QueueManager;
import com.senjyouhara.crawler.confg.CrawlerBeanPostProcessor;
import com.senjyouhara.crawler.confg.CrawlerContext;
import com.senjyouhara.crawler.confg.CrawlerInit;
import com.senjyouhara.crawler.confg.CrawlerProperty;
import org.springframework.context.annotation.Import;

@Import({CrawlerProperty.class,QueueManager.class,ThreadManager.class,CrawlerContext.class, CrawlerBeanPostProcessor.class,CrawlerInit.class})
public class AutoConfigureCrawler {

}
