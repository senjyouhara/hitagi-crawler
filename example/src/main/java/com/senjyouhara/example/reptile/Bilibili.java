package com.senjyouhara.example.reptile;

import com.senjyouhara.crawler.annotation.Crawler;
import com.senjyouhara.crawler.base.AbstractCrawler;
import com.senjyouhara.crawler.base.RequestBuild;
import com.senjyouhara.crawler.model.CrawlerResponse;
import lombok.extern.log4j.Log4j2;
import org.seimicrawler.xpath.JXDocument;

import java.util.List;

@Log4j2
@Crawler
public class Bilibili extends AbstractCrawler {
	@Override
	public String[] startUrls() {
		return new String[]{"https://www.bilibili.com/"};
	}

	@Override
	public void responseHandler(CrawlerResponse crawlerResponse) {
		String content1 = crawlerResponse.getContent();
//		System.out.println(content1);
		JXDocument content = crawlerResponse.document();
		List<Object> sel = content.sel("//div[@class='recommend-box']//div[@class='info-box']//a");

		sel.forEach(v->{
			String s = v.toString();

//			System.out.println(s);
			JXDocument jxDocument = JXDocument.create(s);
			Object name = jxDocument.selOne("//p[@class='up']/text()");
			Object href = jxDocument.selOne("//a/@href");

			System.out.println("name == " + name + "__" + "href ==" + href);

			addRequest(RequestBuild.build("https:" + href.toString(),Bilibili::titleDetail));

		});

	}

	public void titleDetail(CrawlerResponse crawlerResponse) {

		JXDocument document = crawlerResponse.document();
		Object o = document.selOne("//div[@id='viewbox_report']/h1");

		JXDocument jxDocument = JXDocument.create(o.toString());
		Object videoTips = jxDocument.selOne("//a/text()");
		Object title = jxDocument.selOne("//span/text()");

		System.out.println("videoTips : " + videoTips + " , title = " + title);

	}

	}
