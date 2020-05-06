package com.senjyouhara.crawler.ThreadPool;


import com.senjyouhara.crawler.annotation.Crawler;
import com.senjyouhara.crawler.base.AbstractCrawler;
import com.senjyouhara.crawler.base.BaseManage;
import com.senjyouhara.crawler.confg.CrawlerContext;
import com.senjyouhara.crawler.confg.CrawlerProperty;
import com.senjyouhara.crawler.model.CrawlerProcess;
import com.senjyouhara.crawler.model.CrawlerResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.*;

@Log4j2
public class ThreadManager implements BaseManage<CrawlerProcess> {

	@Autowired
	private CrawlerProperty crawlerProperty;

	private Map<String, List<CrawlerProcess>> map = new ConcurrentHashMap<>();

	private Map<String, ExecutorService> executorServiceMap = new ConcurrentHashMap<>();

	@Override
	public CrawlerProcess add(String name, CrawlerProcess crawlerProcess) {
		if (crawlerProcess != null && name != null) {

			List<CrawlerProcess> list = getAll(name);

			if(!list.contains(crawlerProcess)){
				list.add(crawlerProcess);
			}

			ExecutorService executorService = executorServiceMap.get(name);
			if (executorService == null) {
				executorService = Executors.newFixedThreadPool(crawlerProperty.getPoolCount());
				executorServiceMap.put(name, executorService);
			}

			executorService.execute(crawlerProcess);

			return crawlerProcess;
		}

		return null;
	}

	@Override
	public synchronized CrawlerProcess remove(String name, CrawlerProcess crawlerProcess) {
		if (crawlerProcess != null) {
			List<CrawlerProcess> list = getAll(name);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).equals(crawlerProcess)){
					return list.remove(i);
				}
			}
		}
		return null;
	}

	@Override
	public List<CrawlerProcess> getAll(String name) {
		List<CrawlerProcess> crawlerProcesses = map.computeIfAbsent(name, k -> new ArrayList<>());
		return crawlerProcesses;
	}

	public Map<String, List<CrawlerProcess>> getMap() {
		return map;
	}

	public void invock(String name) throws Exception {

		AbstractCrawler crawler = CrawlerContext.getCrawler(name);

		List<CrawlerProcess> list = getAll(name);
		ExecutorService executorService = executorServiceMap.get(name);

		list.forEach(executorService::execute);

	}


}
