package com.senjyouhara.example;


import com.senjyouhara.crawler.annotation.EnabledCrawler;
import com.senjyouhara.crawler.confg.CrawlerContext;
import com.senjyouhara.example.reptile.*;
import lombok.extern.log4j.Log4j2;

import org.jasypt.encryption.StringEncryptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;


import java.text.ParseException;
import java.util.*;


@Log4j2
@MapperScan("com.senjyouhara.example.mapper")
@EnabledCrawler
@EnableScheduling
@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private StringEncryptor stringEncryptor;

	@Autowired(required = false)
	private TestRunner testRunner;

	public static void main(String[] args) throws ParseException {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		if(testRunner != null){
			testRunner.start();
		}

	}


	@Component
	@Profile({"default", "dev"})
	public static class TestRunner {

		public void start(){
			CrawlerContext.run(Doki8.class);
		}


	}

}
