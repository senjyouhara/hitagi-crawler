package com.senjyouhara.core.bean;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class BeanUtil {

  public static <S, T> List<T> copyDataToNewData(List<S> source, Class<T> clazz) {
    return copyDataToNewData(source, clazz, null,null);
  }

  public static <S, T> List<T> copyDataToNewData(List<S> source, Class<T> clazz, BiConsumer<S, T> consumer) {
    return copyDataToNewData(source, clazz, consumer, null);
  }

  public static <S, T> List<T> copyDataToNewData(List<S> source, Class<T> clazz, BiFunction<S, T, T> func) {
    return copyDataToNewData(source, clazz, null, func);
  }

  public static <S, T> List<T> copyDataToNewData(List<S> source, Class<T> clazz, BiConsumer<S, T> consumer, BiFunction<S, T, T> func) {
    return listHandler(source, clazz, consumer, func);
  }

  public static <T> T copyProperties(Object source, Class<T> targetClass) {
    return copyProperties(true,source,targetClass);
  }
  public static <T> T copyProperties(boolean condition, Object source, Class<T> targetClass) {
    if(!condition) return null;
    T t = null;
    try {
      t = targetClass.newInstance();
      BeanUtils.copyProperties(source, t);
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }

    return t;
  }

  private static <S, T> List<T> listHandler(List<S> source, Class<T> clazz, BiConsumer<S, T> consumer, BiFunction<S, T, T> func) {
    return source.stream().map((v) -> {
      T t = null;
      try {
        t = clazz.newInstance();
        BeanUtils.copyProperties(v, t);
      } catch (InstantiationException | IllegalAccessException e) {
        e.printStackTrace();
      }
      if (consumer == null) {
        if (func == null) {
          return t;
        } else {
          return func.apply(v, t);
        }
      } else {
        consumer.accept(v, t);
        return t;
      }
    }).collect(Collectors.toList());
  }

}
