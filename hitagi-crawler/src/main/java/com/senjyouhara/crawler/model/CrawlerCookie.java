package com.senjyouhara.crawler.model;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CrawlerCookie {

//	属性名
	private String name;
//	属性值
	private String value;

	private LocalDateTime expires;

	public CrawlerCookie(String name, String value, LocalDateTime expires) {
		this.name = name;
		this.value = value;
		this.expires = expires;
	}

	public CrawlerCookie(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		CrawlerCookie that = (CrawlerCookie) o;

		return new EqualsBuilder()
				.append(name, that.name)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(name)
				.toHashCode();
	}
}
