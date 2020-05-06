package com.senjyouhara.crawler.enums;

public enum CrawlerContentType {

	HTML("html"),
	JSON("json");

	private String val;

	CrawlerContentType(String val){
		this.val = val;
	}
	public String val(){
		return this.val;
	}

}
