package com.senjyouhara.example;

import com.senjyouhara.crawler.annotation.EnabledCrawler;
import com.senjyouhara.crawler.confg.CrawlerContext;
import com.senjyouhara.example.reptile.Bilibili;
import com.senjyouhara.example.reptile.Cordcloud;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnabledCrawler
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class,args);
		CrawlerContext.run(Cordcloud.class);
	}
}
