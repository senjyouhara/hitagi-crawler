package com.senjyouhara.crawler.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CrawlerProxy {

//	属性名
	private String hostname;
//	属性值
	private String port;

	private String schemeName = "http";

	public CrawlerProxy(String hostname, String port) {
		this.hostname = hostname;
		this.port = port;
	}

	public CrawlerProxy(String hostname, String port, String schemeName) {
		this.hostname = hostname;
		this.port = port;
		this.schemeName = schemeName;
	}
}
