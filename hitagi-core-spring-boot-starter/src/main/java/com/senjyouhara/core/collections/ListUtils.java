package com.senjyouhara.core.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListUtils {

	private ListUtils(){}

	public static  <T>  T find(List<T> list, Predicate<T> t){

		List<T> collect = list.stream().filter(t).collect(Collectors.toList());

		if(collect.size() == 0){
			return null;
		}

		return collect.get(0);
	}

	public static  <T>  List<T> filter(List<T> list, Predicate<T> t){

		List<T> collect = list.stream().filter(t).collect(Collectors.toList());

		if(collect.size() == 0){
			return new ArrayList<T>();
		}

		return collect;
	}

	public static  <T,S>  List<S> map(List<T> list, Function<T,S> t){

		List<S> collect = list.stream().map(t).collect(Collectors.toList());

		return collect;
	}

	public static  <T>  Integer reduce(List<T> list, BiFunction<Integer, ? super T, Integer> t){

		Integer reduce = list.stream().reduce(0, t,(total,cur)-> 0);

		return reduce;
	}

	public static  <T>  boolean some(List<T> list, Predicate<T> t){

		boolean collect = list.stream().anyMatch(t);

		return collect;
	}



}
