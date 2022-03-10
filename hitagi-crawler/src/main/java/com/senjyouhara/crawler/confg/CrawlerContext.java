package com.senjyouhara.crawler.confg;

import com.senjyouhara.crawler.base.AbstractCrawler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;

import java.util.*;

@Log4j2
@Order(1)
public class CrawlerContext implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public static List<Class<? extends AbstractCrawler>> crawlerClasses = new ArrayList<>();

	public static Map<String, AbstractCrawler> crawlers = new HashMap<>();

	public static List<Class<? extends AbstractCrawler>> getCrawlerClass() {
		return crawlerClasses;
	}

	public static void addCrawlerClass(Class<? extends AbstractCrawler> e) {

		if(e == null) {
			return;
		}

		for (int i = 0; i < crawlerClasses.size(); i++) {
			if(crawlerClasses.get(i).equals(e)){
				return;
			}
		}

		crawlerClasses.add(e);
	}
	public static void removeCrawlerClass(Class<? extends AbstractCrawler> e) {
		if(e == null) {
			return;
		}

		for (int i = 0; i < crawlerClasses.size(); i++) {
			if(crawlerClasses.get(i).equals(e)){
				crawlerClasses.remove(i);
				return;
			}
		}
	}

	public static Map<String, AbstractCrawler> getCrawlers() {
		return crawlers;

	}
	public static AbstractCrawler getCrawler(String name) {
		return crawlers.get(name);

	}

	public static void addCrawler(String name, AbstractCrawler e) {

		if(e == null || name == null) {
			return;
		}

		crawlers.put(name, e);
	}

	public static void removeCrawler(String name) {
		if(name == null) {
			return;
		}

		crawlers.remove(name);
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		CrawlerContext.applicationContext = applicationContext;
	}

	public static void run(String name){
		AbstractCrawler crawler = crawlers.get(name);
		if(crawler == null){
			log.error("The name [ {} ] is not found! ", name);
			return;
		}
		crawler.start();
	}

	public static void run(Class<? extends AbstractCrawler> clazz){
		AbstractCrawler bean = applicationContext.getBean(clazz);
		bean.start();
	}

	public static void run(AbstractCrawler crawler){
		crawler.start();
	}
}
