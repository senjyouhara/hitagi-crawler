package com.senjyouhara.example;

import com.senjyouhara.crawler.annotation.EnabledCrawler;
import com.senjyouhara.crawler.confg.CrawlerContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnabledCrawler
@SpringBootApplication
public class Application {
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(Application.class,args);
		CrawlerContext.run(Bilibili.class);

	}
}
