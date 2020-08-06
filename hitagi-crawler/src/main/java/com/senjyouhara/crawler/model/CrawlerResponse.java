package com.senjyouhara.crawler.model;

import com.senjyouhara.crawler.enums.CrawlerBodyType;
import com.senjyouhara.crawler.http.CrawlerHttpType;
import lombok.Data;
import org.jsoup.nodes.Document;
import org.seimicrawler.xpath.JXDocument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class CrawlerResponse implements Serializable {

	private CrawlerBodyType bodyType;

	private CrawlerRequest request;

	private String charset;

	private String referer;

	private byte[] data;

	private String content;

	private String userAgent;

	private List<CrawlerCookie> crawlerCookies = new ArrayList<>();

	/**
	 * 这个主要用于存储上游传递的一些自定义数据
	 */
	private Map<String, Object> meta;

	private String url;

	private Map<String, String> params;
	/**
	 * 网页内容真实源地址
	 */
	private String realUrl;
	/**
	 * 此次请求结果的http处理器类型
	 */
	private CrawlerHttpType httpType;

	public JXDocument document() {
		return CrawlerBodyType.TEXT.equals(bodyType) && content != null ? JXDocument.create(content) : null;
	}

}
