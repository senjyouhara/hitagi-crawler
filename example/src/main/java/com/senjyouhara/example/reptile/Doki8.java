package com.senjyouhara.example.reptile;

import com.senjyouhara.crawler.annotation.Crawler;
import com.senjyouhara.crawler.base.AbstractCrawler;
import com.senjyouhara.crawler.base.RequestBuild;
import com.senjyouhara.crawler.enums.HttpMethod;
import com.senjyouhara.crawler.model.CrawlerRequest;
import com.senjyouhara.crawler.model.CrawlerResponse;
import org.seimicrawler.xpath.JXDocument;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Crawler
public class Doki8 extends AbstractCrawler {

	@Value("${doki.username}")
	private String username;

	@Value("${doki.password}")
	private String password;

	@Override
	public String[] startUrls() {
		return null;
	}

	@Override
	public List<CrawlerRequest> startRequests() {
		List<CrawlerRequest> requests = new LinkedList<>();
		CrawlerRequest start = RequestBuild.build("http://www.doki8.com/wp-login.php", Doki8::responseHandler);
		Map<String,String> params = new HashMap<>();
		params.put("log",username);
		params.put("pwd",password);
		params.put("rememberme","forever");
		params.put("wp-submit","登录");
		start.setHttpMethod(HttpMethod.POST);
		start.setParams(params);
		requests.add(start);
		return requests;
	}


	@Override
	public void responseHandler(CrawlerResponse crawlerResponse) {
		System.out.println(crawlerResponse.getRealUrl() + "----url");
	}

}
