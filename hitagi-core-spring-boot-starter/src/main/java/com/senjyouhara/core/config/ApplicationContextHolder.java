package com.senjyouhara.core.config;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.Validate;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Log4j2
public class ApplicationContextHolder implements ApplicationContextAware, DisposableBean {

	private static ApplicationContext applicationContext;

	/**
	 * 获取存储在静态变量中的 ApplicationContext
	 *
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	/**
	 * 从静态变量 applicationContext 中获取 Bean，自动转型成所赋值对象的类型
	 *
	 * @param name
	 * @param <T>
	 * @return
	 */
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量 applicationContext 中获取 Bean，自动转型成所赋值对象的类型
	 *
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz) {
		assertContextInjected();
		return applicationContext.getBean(clazz);
	}

	public static <T> T getBean(String name, Class<T> cal) {
		assertContextInjected();
		return applicationContext.getBean(name, cal);
	}

	public static String getProperty(String key) {
		assertContextInjected();
		return applicationContext.getBean(Environment.class).getProperty(key);
	}

	/**
	 * 实现 DisposableBean 接口，在 Context 关闭时清理静态变量
	 *
	 * @throws Exception
	 */
	@Override
	public void destroy() throws Exception {
		log.debug("清除 SpringContext 中的 ApplicationContext: {}", applicationContext);
		applicationContext = null;
	}

	/**
	 * 实现 ApplicationContextAware 接口，注入 Context 到静态变量中
	 *
	 * @param applicationContext
	 * @throws BeansException
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContextHolder.applicationContext = applicationContext;
	}

	/**
	 * 断言 Context 已经注入
	 */
	private static void assertContextInjected() {
		Validate.validState(applicationContext != null, "applicationContext 属性未注入，请在 spring-context.xml 配置中定义 ApplicationContextHolder");
	}

	public static String getProfileActive() {
		return getApplicationContext().getEnvironment().getProperty("spring.profiles.active");
	}

	public static Boolean hasDevActive() {
		String active = getProfileActive();
		if ("dev".equals(active) || "default".equals(active)) {
			return true;
		}
		return false;
	}


	/**
	 * 获取 目标对象
	 *
	 * @param proxy 代理对象
	 * @return 目标对象
	 * @throws Exception
	 */
	public static Object getTarget(Object proxy) throws Exception {
		if (!AopUtils.isAopProxy(proxy)) {
			// 不是代理对象,直接返回
			return proxy;
		}

		if (AopUtils.isJdkDynamicProxy(proxy)) {
			return getJdkDynamicProxyTargetObject(proxy);
		} else {
			// cglib
			return getCglibProxyTargetObject(proxy);
		}
	}

	private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
		Field field = proxy.getClass().getSuperclass().getDeclaredField("h");
		field.setAccessible(true);
		AopProxy aopProxy = (AopProxy) field.get(proxy);
		Field advised = aopProxy.getClass().getDeclaredField("advised");
		advised.setAccessible(true);

		Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
		return target;
	}

	private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
		Field field = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
		field.setAccessible(true);
		Object dynamicAdvisedInterceptor = field.get(proxy);

		Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
		advised.setAccessible(true);

		Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
		return target;
	}


}
