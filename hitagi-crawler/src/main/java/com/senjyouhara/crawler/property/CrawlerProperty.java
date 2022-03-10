package com.senjyouhara.crawler.property;


import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("senjyouhara.crawler")
public class CrawlerProperty {

//	核心线程数
	private Integer corePoolSize = 200;

//	最大线程数
	private Integer maxPoolSize = 1000;

//	队列大小
	private Integer queueCapacity = 100000;

//	线程名前缀
	private String threadNamePrefix = "hitagiExecutor-";

}
