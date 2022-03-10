package com.senjyouhara.crawler.base;

@FunctionalInterface
public interface CrawlerCallback<T,A> {

	void call(T t, A a);

}
