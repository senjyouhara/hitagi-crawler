package com.senjyouhara.crawler.annotation;


import com.senjyouhara.crawler.enums.CrawlerHttpType;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *  爬虫注解，作用于类上表示该类为爬虫类
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Crawler {

	/**
	 *  爬虫名  默认为类名小驼峰
	 * @return
	 */
	String value() default "";

	/**
	 * 请求间隔 单位秒
	 * @return
	 */
	int delay() default 0;

	/**
	 * 是否对请求去重
	 * @return
	 */
	boolean isUnRepreated() default true;

	/**
	 * http请求方式  主要为2种 http_client ok_http  默认ok_http
	 * @return
	 */
	CrawlerHttpType httpType() default CrawlerHttpType.OK_HTTP;

	/**
	 * 请求超时时间  单位毫秒
	 * @return
	 */
	int timeOut() default 15000;

	/**
	 * 请求失败重试次数  默认不重试
	 * @return
	 */
	int tryCount() default 0;

}
