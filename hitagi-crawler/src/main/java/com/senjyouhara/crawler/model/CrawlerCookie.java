package com.senjyouhara.crawler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class CrawlerCookie {

//	域名
	private String domain;
//	路径
	private String path;
//	属性名
	private String name;
//	属性值
	private String value;

}
