package com.senjyouhara.crawler.base;

import com.senjyouhara.crawler.ThreadPool.ThreadManager;
import com.senjyouhara.crawler.exception.CrawlerThreadException;
import com.senjyouhara.crawler.model.CrawlerCookie;
import com.senjyouhara.crawler.model.CrawlerProcess;
import com.senjyouhara.crawler.model.CrawlerRequest;
import com.senjyouhara.crawler.queue.QueueManager;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Log4j2
public abstract class AbstractCrawler implements BaseCrawler {

	protected String crawlerName;

	private CrawlerRequest oldRequest;

	public String getCrawlerName() {
		return crawlerName;
	}

	public void setCrawlerName(String crawlerName) {
		this.crawlerName = crawlerName;
	}

	protected List<CrawlerRequest> requestList = new ArrayList<>();

	private String currentUserAgent;

	public String getCurrentUserAgent() {
		return currentUserAgent;
	}

	public void setCurrentUserAgent(String currentUserAgent) {
		this.currentUserAgent = currentUserAgent;
	}

	@Autowired
	protected QueueManager queueManager;
	@Autowired
	protected ThreadManager threadManager;


	protected String[] userAgent = new String[]{
			"Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.43 Safari/537.31",
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.60 Safari/537.17",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1309.0 Safari/537.17",
			"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.2; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)",
			"Mozilla/5.0 (Windows; U; MSIE 7.0; Windows NT 6.0; en-US)",
			"Mozilla/5.0 (Windows; U; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)",
			"Mozilla/6.0 (Windows NT 6.2; WOW64; rv:16.0.1) Gecko/20121011 Firefox/16.0.1",
			"Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:15.0) Gecko/20100101 Firefox/15.0.1",
			"Mozilla/5.0 (Windows NT 6.2; WOW64; rv:15.0) Gecko/20120910144328 Firefox/15.0.2",
			"Mozilla/5.0 (Windows; U; Windows NT 6.1; rv:2.2) Gecko/20110201",
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9a3pre) Gecko/20070330",
			"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; en-US; rv:1.9.2.13; ) Gecko/20101203",
			"Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14",
			"Opera/9.80 (X11; Linux x86_64; U; fr) Presto/2.9.168 Version/11.50",
			"Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; de) Presto/2.9.168 Version/11.52",
			"Mozilla/5.0 (Windows; U; Win 9x 4.90; SG; rv:1.9.2.4) Gecko/20101104 Netscape/9.1.0285",
			"Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O; en-US; rv:1.8.1.7pre) Gecko/20070815 Firefox/2.0.0.6 Navigator/9.0b3",
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.12) Gecko/20080219 Firefox/2.0.0.12 Navigator/9.0.0.6"
	};

	public AbstractCrawler() {
		currentUserAgent = getUserAgent();
	}

	public String getUserAgent() {
		int i = RandomUtils.nextInt(0, userAgent.length);
		return userAgent[i];
	}

	@Override
	public List<CrawlerRequest> startRequests() {
		return null;
	}

	public void addRequest(CrawlerRequest crawlerRequest) {

		if (oldRequest != null) {
//			cookie 传递
			if (oldRequest.isCookieTransfer()) {
				List<CrawlerCookie> cookies = crawlerRequest.getCrawlerCookies();
				List<CrawlerCookie> oldCookies = oldRequest.getCrawlerCookies();
				ArrayList<CrawlerCookie> mergeCookies = new ArrayList<>(oldCookies);
				if (cookies != null) {
					mergeCookies.addAll(cookies);
				}
				crawlerRequest.setCrawlerCookies(mergeCookies);
			}

//			元数据传递
			if (oldRequest.getMeta() != null) {
				Map<String, Object> meta = crawlerRequest.getMeta();
				Map<String, Object> oldMeta = oldRequest.getMeta();
				HashMap<String, Object> map = new HashMap<>();

				if (meta != null) {
					map.putAll(meta);
				}
				if (oldMeta != null) {
					map.putAll(oldMeta);
				}
			}
		}
		crawlerRequest.setCrawlerName(crawlerName);


		CrawlerProcess crawlerProcess = new CrawlerProcess(crawlerName, crawlerRequest);
		List<CrawlerRequest> queueAll = queueManager.getAll(crawlerName);
		if (!requestList.contains(crawlerRequest)) {
			requestList.add(crawlerRequest);
		}
		
		queueManager.add(crawlerName, crawlerRequest);
		threadManager.add(crawlerName, crawlerProcess);
		oldRequest = crawlerRequest;
	}

	//	开始产生请求队列 然后执行请求
	public final void start() {

		if (startUrls() != null && startUrls().length > 0) {
			for (String s : startUrls()) {
				addRequest(RequestBuild.build(s, AbstractCrawler::responseHandler));
			}
		} else {
			List<CrawlerRequest> crawlerRequests = startRequests();
			if (crawlerRequests != null) {
				for (CrawlerRequest c : crawlerRequests) {
					addRequest(c);
				}
			}
		}
	}

	//	开启线程执行请求
	public final void startRequest() {
		try {
			threadManager.invock(crawlerName);
		} catch (Exception e) {
			log.error(e);
			throw new CrawlerThreadException("The " + crawlerName + " run faild");
		}
	}


	@Override
	public void errorRequest(CrawlerRequest crawlerRequest) {
		log.error("The request is error, request name [ {} ], request : {}", crawlerName, crawlerRequest);
	}
}
