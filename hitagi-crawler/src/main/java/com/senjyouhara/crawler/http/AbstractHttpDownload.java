package com.senjyouhara.crawler.http;

import com.senjyouhara.core.date.DateUtils;
import com.senjyouhara.crawler.enums.CrawlerHttpType;
import com.senjyouhara.crawler.model.CrawlerCookie;
import com.senjyouhara.crawler.model.CrawlerRequest;
import com.senjyouhara.crawler.model.CrawlerResponse;
import com.senjyouhara.crawler.utils.HttpUtil;
import lombok.extern.log4j.Log4j2;
import okhttp3.Cookie;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.exception.XpathSyntaxErrorException;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public abstract class AbstractHttpDownload implements HttpDownload {

	protected String crawlerName;

	protected CrawlerHttpType crawlerHttpType = CrawlerHttpType.OK_HTTP;


	public AbstractHttpDownload(String crawlerName) {
		this.crawlerName = crawlerName;
	}


	protected String getFirstElementStr(List<Object> list, String val){
		if (CollectionUtils.isEmpty(list)){
			return val;
		}
		return list.get(0).toString();
	}

	protected String parseCharset(String target){
		final Pattern charsetPattern = Pattern.compile("charset=([1-9a-zA-Z-]+)$");
		Matcher matcher = charsetPattern.matcher(target);
		if (matcher.find()){
			return matcher.group(1);
		}
		return "";
	}

	protected String renderRealCharset(String content) throws XpathSyntaxErrorException {
		String charset;
		JXDocument doc = JXDocument.create(content);
		charset = getFirstElementStr(doc.sel("//meta[@charset]/@charset"),"").trim();
		if (StringUtils.isBlank(charset)){
			charset = getFirstElementStr(doc.sel("//meta[@http-equiv='charset']/@content"),"").trim();
		}
		if (StringUtils.isBlank(charset)){
			String ct = StringUtils.join(doc.sel("//meta[@http-equiv='Content-Type']/@content|//meta[@http-equiv='content-type']/@content"),";").trim();
			charset = parseCharset(ct.toLowerCase());
		}
		return StringUtils.isNotBlank(charset)?charset:"UTF-8";
	}

	protected CrawlerResponse handlerResponse(CrawlerRequest crawlerRequest, List<String> setCookies) {
		CrawlerResponse crawlerResponse = new CrawlerResponse();
		crawlerResponse.setHttpType(crawlerHttpType);
		crawlerResponse.setUrl(crawlerRequest.getUrl());
		crawlerResponse.setRealUrl(crawlerRequest.getUrl());
		crawlerResponse.setRequest(crawlerRequest);
		crawlerResponse.setMeta(crawlerRequest.getMeta());

		log.info("setCookies : {}", setCookies);

		List<CrawlerCookie> crawlerCookies = HttpUtil.cookieParse(setCookies);
		Set<CrawlerCookie> newCrawlerCookies = new HashSet<>();

		if(crawlerRequest.getCrawlerCookies() != null && crawlerRequest.getCrawlerCookies().size() > 0){
			newCrawlerCookies.addAll(crawlerRequest.getCrawlerCookies());
		}

		if(crawlerCookies.size() > 0){
			newCrawlerCookies.addAll(crawlerCookies);
		}
		crawlerResponse.setCrawlerCookies(newCrawlerCookies);

		return crawlerResponse;
	}

	}
