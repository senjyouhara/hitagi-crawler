package com.senjyouhara.crawler.confg;

import com.senjyouhara.crawler.annotation.Crawler;
import com.senjyouhara.crawler.base.AbstractCrawler;
import com.senjyouhara.crawler.exception.CrawlerInitializingException;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Log4j2
public class CrawlerBeanPostProcessor implements BeanPostProcessor {
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

		Class aClass = bean.getClass();
		Crawler crawler = (Crawler) aClass.getAnnotation(Crawler.class);
		if(crawler != null){

			if(AbstractCrawler.class.isAssignableFrom(aClass)){

				if(StringUtils.isNotBlank(crawler.value())){
					AbstractCrawler a = (AbstractCrawler) bean;
					a.setCrawlerName(crawler.value());
				}else{
					AbstractCrawler a = (AbstractCrawler) bean;
					a.setCrawlerName(beanName);
				}

				AbstractCrawler crawlersByName = CrawlerContext.getCrawler(beanName);

				if(crawlersByName != null){
					log.error("crawler name [ " + aClass.getName() + " ] is repeated ");
				}else{
					CrawlerContext.addCrawler(beanName,(AbstractCrawler) bean);
				}

				CrawlerContext.addCrawlerClass(aClass);
			}else{
				log.error("Crawler init faild! [ " + aClass.getName() + " ] extends AbstractCrawler please!");

			}

		}

		return bean;
	}
}
