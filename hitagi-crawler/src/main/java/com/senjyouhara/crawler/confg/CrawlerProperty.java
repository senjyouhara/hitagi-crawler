package com.senjyouhara.crawler.confg;


import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("senjyouhara.crawler")
public class CrawlerProperty {

//	每个被@Crawler注解的对象又多少个线程运行
	private int poolCount = 20;

//	全局域名  如  www.baidu.com  或  123.45.56.78
	private String host;

//	全局端口
	private int port;



}
