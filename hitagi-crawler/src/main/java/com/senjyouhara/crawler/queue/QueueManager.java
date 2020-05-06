package com.senjyouhara.crawler.queue;

import com.senjyouhara.crawler.base.BaseManage;
import com.senjyouhara.crawler.model.CrawlerRequest;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.*;

@Log4j2
public class QueueManager implements BaseManage<CrawlerRequest> {

	private Map<String, List<CrawlerRequest>> map = new ConcurrentHashMap<>();

	@Override
	public CrawlerRequest add(String name, CrawlerRequest crawlerRequest) {
		if(crawlerRequest != null && name != null) {
			List<CrawlerRequest> list = getAll(name);
			if(!list.contains(crawlerRequest)){
				list.add(crawlerRequest);
				return crawlerRequest;
			}
			return null;
		}
		return null;
	}

	public synchronized CrawlerRequest remove(String name, CrawlerRequest crawlerRequest) {
		if(crawlerRequest != null) {
			List<CrawlerRequest> list = getAll(name);
			for(int i = 0 ; i < list.size(); i++){
				if (list.get(i).equals(crawlerRequest)){
					return list.remove(i);
				}
			}
		}
		return null;
	}

	@Override
	public List<CrawlerRequest> getAll(String name) {
		map.computeIfAbsent(name, k -> new ArrayList<>());
		return map.get(name);
	}

	public Map<String, List<CrawlerRequest>> getMap() {
		return map;
	}


}
