package com.senjyouhara.crawler.http;

import com.senjyouhara.core.config.ApplicationContextHolder;
import com.senjyouhara.crawler.annotation.Crawler;
import com.senjyouhara.crawler.base.AbstractCrawler;
import com.senjyouhara.crawler.base.CrawlerCallback;
import com.senjyouhara.crawler.confg.CrawlerContext;
import com.senjyouhara.crawler.enums.CrawlerBodyType;
import com.senjyouhara.crawler.enums.CrawlerHttpType;
import com.senjyouhara.crawler.exception.CrawlerNotFoundCustomHttpInstance;
import com.senjyouhara.crawler.http.httpclient.DefaultHttpClient;
import com.senjyouhara.crawler.http.okhttp.DefaultOkHttp;
import com.senjyouhara.crawler.model.CrawlerRequest;
import com.senjyouhara.crawler.model.CrawlerResponse;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.beans.BeansException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Slf4j
public class CrawlerProcess implements Runnable {

	private Pattern metaRefresh = Pattern.compile("<(?:META|meta|Meta)\\s+(?:HTTP-EQUIV|http-equiv)\\s*=\\s*\"refresh\".*(?:url|URL)=(\\S*)\".*/?>");

	private String crawlerName;

	private CrawlerRequest crawlerRequest;

	private AbstractCrawler baseCrawler;

	private int count = 0;

	public CrawlerProcess(String crawlerName, CrawlerRequest crawlerRequest) {
		this.crawlerName = crawlerName;
		this.crawlerRequest = crawlerRequest;
	}

	@Override
	public void run() {
		if(crawlerRequest.isStop()){
			return;
		}

		baseCrawler = CrawlerContext.getCrawler(crawlerName);
		Crawler annotation = baseCrawler.getClass().getAnnotation(Crawler.class);
		AbstractHttpDownload bean = null;
		try {
			bean = ApplicationContextHolder.getBean(AbstractHttpDownload.class);
		}catch (BeansException e){
			e.getStackTrace();
		}

		AbstractHttpDownload downloader;
		if (CrawlerHttpType.HTTP_CLIENT.val().equals(annotation.httpType().val())) {
			downloader = new DefaultHttpClient(crawlerName);
		} else if (CrawlerHttpType.OK_HTTP.val().equals(annotation.httpType().val())) {
			downloader = new DefaultOkHttp(crawlerName);
		}else{
			if(bean == null){
				throw new CrawlerNotFoundCustomHttpInstance("The corresponding [ AbstractHttpDownload ] object could not be found in the bean ");
			}
			downloader = bean;
		}

		try {
			CrawlerResponse process = downloader.process(crawlerRequest);
			if (StringUtils.isNotBlank(process.getContent()) && CrawlerBodyType.TEXT.equals(process.getBodyType())) {
				Matcher mm = metaRefresh.matcher(process.getContent());
				int refreshCount = 0;

				while (mm.find() && refreshCount < 3) {
					String nextUrl = mm.group(1).replaceAll("'", "");
					log.info("nextUrl : {}", nextUrl);
					process = downloader.metaRefresh(nextUrl);
					mm = metaRefresh.matcher(process.getContent());
					refreshCount += 1;
				}
			}

			doLambdaCallback(crawlerRequest, process, annotation);

		} catch (Exception e) {
			log.error("error", e);

			if(crawlerRequest.getMaxReqCount() > 0){
				if (crawlerRequest.getCurrentReqCount() <= crawlerRequest.getMaxReqCount()) {
					crawlerRequest.incrReqCount();
					run();
					log.error("Request process error,req will go into queue again,url={},maxReqCount={},currentReqCount={}", crawlerRequest.getUrl(), crawlerRequest.getMaxReqCount(), crawlerRequest.getCurrentReqCount());
				} else if (crawlerRequest.getCurrentReqCount() > crawlerRequest.getMaxReqCount() && crawlerRequest.getMaxReqCount() > 0) {
					baseCrawler.errorRequest(crawlerRequest);
				}
			}else {
				if (count++ <= annotation.tryCount()) {
					run();
					log.error("Request process error,req will go into queue again,url={},maxReqCount={},currentReqCount={}", crawlerRequest.getUrl(), crawlerRequest.getMaxReqCount(), crawlerRequest.getCurrentReqCount());
				} else {
					baseCrawler.errorRequest(crawlerRequest);
				}
			}

		}

	}

	private void doLambdaCallback(CrawlerRequest crawlerRequest, CrawlerResponse process, Crawler annotation) throws InterruptedException {

		CrawlerCallback callBackFunc = crawlerRequest.getCallBackFunc();
		if (callBackFunc == null) {
			log.info("can not find callback function");
			return;
		}

		if(annotation.delay() > 0){
				TimeUnit.SECONDS.sleep(annotation.delay());
		}

		callBackFunc.call(baseCrawler, process);

	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;
		CrawlerProcess that = (CrawlerProcess) o;

		return new EqualsBuilder()
				.append(crawlerName, that.crawlerName)
				.append(crawlerRequest, that.crawlerRequest)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(crawlerName)
				.append(crawlerRequest)
				.toHashCode();
	}
}
