package com.senjyouhara.crawler.model;

import com.senjyouhara.crawler.base.AbstractCrawler;
import com.senjyouhara.crawler.base.BaseCrawler;
import com.senjyouhara.crawler.base.CrawlerCallback;
import com.senjyouhara.crawler.enums.CrawlerContentType;
import com.senjyouhara.crawler.enums.HttpMethod;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpHost;

import java.io.Serializable;
import java.util.*;

@Getter
public class CrawlerRequest implements Serializable {


	private String crawlerName;

	/**
	 * 需要请求的url
	 */
	private String url;
	/**
	 * 要请求的方法类型 get,post,put...
	 */
	private HttpMethod httpMethod = HttpMethod.GET;
	/**
	 * 如果请求需要参数，那么将参数放在这里
	 */
	private Map<String,String> params = new HashMap<>();
	/**
	 * 这个主要用于存储向下级回调函数传递的一些自定义数据
	 */
	private Map<String,Object> meta = new HashMap<>();
	/**
	 * 代理配置 暂未实现
	 */
	private HttpHost proxy;

	/**
	 * 回调函数
	 */
	private transient CrawlerCallback callBackFunc;
	/**
	 * 是否停止的信号，收到该信号的处理线程会退出
	 */
	private boolean stop = false;

	/**
	 * 是否是json请求方式
	 */
	private boolean hasJsonBody = false;

	/**
	 * 最大可被重新请求次数
	 */
	private int maxReqCount = 0;

	/**
	 * 用来记录当前请求被执行过的次数
	 */
	private int currentReqCount = 0;

	/**
	 * 用来指定一个请求是否要经过去重机制
	 */
	private boolean isUnRepeat = false;

	/**
	 * 针对该请求是否启用Agent
	 */
	private boolean useUserAgent = false;

	/**
	 * 是否cookie进行传递下去
	 */
	private boolean isCookieTransfer = false;

	/**
	 * 自定义Http请求协议头
	 */
	private Map<String,String> header = new HashMap();

	private CrawlerContentType contentType = CrawlerContentType.HTML;

	private String requestType = "application/x-www-form-urlencoded";

	/**
	 * 支持添加自定义cookie
	 */
	private Set<CrawlerCookie> crawlerCookies = new HashSet<>();

	public void incrReqCount(){
		this.currentReqCount +=1;
	}

	public CrawlerRequest setCrawlerName(String crawlerName) {
		this.crawlerName = crawlerName;
		return this;
	}
	public CrawlerRequest setRequestType(String requestType) {
		this.requestType = requestType;
		return this;
	}

	public CrawlerRequest setUrl(String url) {
		this.url = url;
		return this;
	}

	public CrawlerRequest setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
		return this;
	}

	public CrawlerRequest setParams(Map<String, String> params) {
		this.params = params;
		return this;
	}

	public CrawlerRequest setCookieTransfer(boolean cookieTransfer) {
		isCookieTransfer = cookieTransfer;
		return this;
	}

	public CrawlerRequest setMeta(Map<String, Object> meta) {
		this.meta = meta;
		return this;
	}

	public <T,A> CrawlerRequest setCallBackFunc(CrawlerCallback<T,A> callBackFunc) {
		this.callBackFunc = callBackFunc;
		return this;
	}

	public CrawlerRequest setStop(boolean stop) {
		this.stop = stop;
		return this;
	}

	public CrawlerRequest setMaxReqCount(int maxReqCount) {
		this.maxReqCount = maxReqCount;
		return this;
	}

	public CrawlerRequest setCurrentReqCount(int currentReqCount) {
		this.currentReqCount = currentReqCount;
		return this;
	}

	public CrawlerRequest setUseUserAgent(boolean useUserAgent) {
		this.useUserAgent = useUserAgent;
		return this;
	}

	public CrawlerRequest setHeader(Map<String, String> header) {
		this.header = header;
		return this;
	}

	public CrawlerRequest setUnRepeat(boolean unRepeat) {
		isUnRepeat = unRepeat;
		return this;
	}

	public CrawlerRequest setContentType(CrawlerContentType contentType) {
		this.contentType = contentType;
		return this;
	}

	public CrawlerRequest setCrawlerCookies(Set<CrawlerCookie> crawlerCookies) {
		this.crawlerCookies = crawlerCookies;
		return this;
	}
	public CrawlerRequest setHasJsonBody(boolean hasJsonBody) {
		this.hasJsonBody = hasJsonBody;
		return this;
	}

	public HttpHost getProxy() {
		return proxy;
	}

	public CrawlerRequest setProxy(HttpHost proxy) {
		this.proxy = proxy;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		CrawlerRequest that = (CrawlerRequest) o;

		return new EqualsBuilder()
				.append(stop, that.stop)
				.append(maxReqCount, that.maxReqCount)
				.append(currentReqCount, that.currentReqCount)
				.append(isUnRepeat, that.isUnRepeat)
				.append(useUserAgent, that.useUserAgent)
				.append(isCookieTransfer, that.isCookieTransfer)
				.append(crawlerName, that.crawlerName)
				.append(url, that.url)
				.append(httpMethod, that.httpMethod)
				.append(params, that.params)
				.append(meta, that.meta)
				.append(callBackFunc, that.callBackFunc)
				.append(header, that.header)
				.append(contentType, that.contentType)
				.append(crawlerCookies, that.crawlerCookies)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(crawlerName)
				.append(url)
				.append(httpMethod)
				.append(params)
				.append(meta)
				.append(callBackFunc)
				.append(stop)
				.append(maxReqCount)
				.append(currentReqCount)
				.append(isUnRepeat)
				.append(useUserAgent)
				.append(isCookieTransfer)
				.append(header)
				.append(contentType)
				.append(crawlerCookies)
				.toHashCode();
	}


}
