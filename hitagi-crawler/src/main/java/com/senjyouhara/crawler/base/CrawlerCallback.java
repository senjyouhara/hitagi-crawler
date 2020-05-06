package com.senjyouhara.crawler.base;

import jdk.nashorn.internal.objects.annotations.Function;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface CrawlerCallback<T,A> {

	void call(T t, A a);

}
