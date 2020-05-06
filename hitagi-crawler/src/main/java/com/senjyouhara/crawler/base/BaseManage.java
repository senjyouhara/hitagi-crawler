package com.senjyouhara.crawler.base;

import java.util.List;

public interface BaseManage<T> {

	T add(String name, T t);

	T remove(String name,T t);

	List<T> getAll(String name);


}
