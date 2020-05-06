package com.senjyouhara.crawler.http;

public enum CrawlerHttpType {

	OK_HTTP("ok_http"),
	HTTP_CLIENT("http_client");

	private String val;

	CrawlerHttpType(String val){
		this.val = val;
	}
	public String val(){
		return this.val;
	}

}
