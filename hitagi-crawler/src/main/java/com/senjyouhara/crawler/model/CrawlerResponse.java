package com.senjyouhara.crawler.model;

import com.senjyouhara.crawler.enums.CrawlerBodyType;
import com.senjyouhara.crawler.enums.CrawlerHttpType;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.seimicrawler.xpath.JXDocument;

import java.io.Serializable;
import java.util.*;

@Data
public class CrawlerResponse implements Serializable {

	private CrawlerBodyType bodyType;

	private CrawlerRequest request;

	private String charset;

	private String referer;

	private byte[] data;

	private String content;

	private String userAgent;

	private Set<CrawlerCookie> crawlerCookies = new HashSet<>();

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
	private CrawlerHttpType httpType = CrawlerHttpType.HTTP_CLIENT;

	public JXDocument document() {
		return CrawlerBodyType.TEXT.equals(bodyType) && content != null ? JXDocument.create(content) : null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		CrawlerResponse that = (CrawlerResponse) o;

		return new EqualsBuilder()
				.append(bodyType, that.bodyType)
				.append(request, that.request)
				.append(content, that.content)
				.append(meta, that.meta)
				.append(url, that.url)
				.append(params, that.params)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(bodyType)
				.append(request)
				.append(content)
				.append(meta)
				.append(url)
				.append(params)
				.toHashCode();
	}
}
