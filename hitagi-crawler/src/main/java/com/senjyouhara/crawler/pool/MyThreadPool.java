package com.senjyouhara.crawler.pool;

import com.senjyouhara.crawler.property.CrawlerProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


public class MyThreadPool {

	@Autowired
	private CrawlerProperty crawlerProperty;

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(crawlerProperty.getCorePoolSize());
		executor.setMaxPoolSize(crawlerProperty.getMaxPoolSize());
		executor.setQueueCapacity(crawlerProperty.getQueueCapacity());
		executor.setThreadNamePrefix(crawlerProperty.getThreadNamePrefix());
        /*
           rejection-policy：当pool已经达到max size的时候，如何处理新任务
           CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        */
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return executor;
	}

}
