package com.senjyouhara.crawler.enums;

public enum CrawlerHttpType {

	OK_HTTP("ok_http"),
	OTHER("other"),
	HTTP_CLIENT("http_client");

	private String val;

	CrawlerHttpType(String val){
		this.val = val;
	}
	public String val(){
		return this.val;
	}

}
