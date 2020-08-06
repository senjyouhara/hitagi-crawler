package com.senjyouhara.crawler.base;

public interface CrawlerCallback<T,A> {

	void call(T t, A a);

}
