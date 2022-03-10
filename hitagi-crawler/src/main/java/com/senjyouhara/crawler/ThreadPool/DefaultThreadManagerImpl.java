package com.senjyouhara.crawler.ThreadPool;


import com.senjyouhara.crawler.http.CrawlerProcess;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.Resource;

@Log4j2
public class DefaultThreadManagerImpl implements ThreadManager {

	@Resource
	private TaskExecutor taskExecutor;

	@Override
	public boolean invoke(CrawlerProcess crawlerProcess) {

		if (crawlerProcess != null) {
			taskExecutor.execute(crawlerProcess);
			return true;
		}

		return false;
	}

}
