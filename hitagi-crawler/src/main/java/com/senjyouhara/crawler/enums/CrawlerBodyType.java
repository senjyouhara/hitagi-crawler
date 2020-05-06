package com.senjyouhara.crawler.enums;

public enum CrawlerBodyType {

	BINARY("binary"),TEXT("text");
	private String val;
	private CrawlerBodyType(String type){
		this.val = type;
	}
	public String val() {
		return this.val;
	}

}
