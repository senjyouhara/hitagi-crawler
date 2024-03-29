package com.senjyouhara.crawler.confg;

import com.senjyouhara.crawler.annotation.EnabledCrawler;
import com.senjyouhara.crawler.base.AbstractCrawler;
import com.senjyouhara.crawler.property.CrawlerProperty;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@Log4j2
public class CrawlerInit implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		ApplicationContext applicationContext = CrawlerContext.getApplicationContext();

		Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(EnabledCrawler.class);
		if(beansWithAnnotation.keySet().size() == 0){
			log.error("The annotation [ EnabledCrawler ] isn't yet enabled");
			return ;
		}
		Map<String, AbstractCrawler> crawlers = CrawlerContext.getCrawlers();

		for (Map.Entry<String,AbstractCrawler> e : crawlers.entrySet()){

			log.info("crawler name: {},  value : {}" , e.getKey(), e.getValue());

		}

	}
}
