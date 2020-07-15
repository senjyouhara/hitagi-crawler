package com.senjyouhara.example.schedule;


import com.senjyouhara.crawler.confg.CrawlerContext;
import com.senjyouhara.example.reptile.Bilibili;
import com.senjyouhara.example.reptile.Doki8;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Log4j2
@Component
public class DemoSchedule {


	// 每天凌晨一点执行
	@Scheduled(cron = "0 0 1 * * ? ")
	public void demo(){
		CrawlerContext.run(Doki8.class);
	}


}
