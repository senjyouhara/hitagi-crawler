package com.senjyouhara.example.reptile;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.senjyouhara.commons.mail.config.MailProperty;
import com.senjyouhara.commons.mail.utils.MailUtils;
import com.senjyouhara.core.date.DateUtils;
import com.senjyouhara.crawler.annotation.Crawler;
import com.senjyouhara.crawler.base.AbstractCrawler;
import com.senjyouhara.crawler.base.RequestBuild;
import com.senjyouhara.crawler.enums.CrawlerHttpType;
import com.senjyouhara.crawler.enums.HttpMethod;
import com.senjyouhara.crawler.model.CrawlerRequest;
import com.senjyouhara.crawler.model.CrawlerResponse;
import com.senjyouhara.example.entity.UserEntity;
import com.senjyouhara.example.mapper.UserMapper;
import lombok.extern.log4j.Log4j2;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Log4j2
@Crawler(httpType= CrawlerHttpType.HTTP_CLIENT)
public class Doki8 extends AbstractCrawler {

	private String host = "www.doki8.com";

	@Autowired
	private UserMapper userMapper;

	@Override
	public String[] startUrls() {
		return null;
	}

	@Override
	public List<CrawlerRequest> startRequests() {
		List<CrawlerRequest> requests = new LinkedList<>();

		LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(UserEntity::getType, "doki8");
		List<UserEntity> userEntities = userMapper.selectList(wrapper);

		for (UserEntity user : userEntities) {
			CrawlerRequest start = RequestBuild.build("http://"+ host +"/wp-login.php", Doki8::responseHandler);
			Map<String,String> params = new HashMap<>();
			Map<String,Object> meta = new HashMap<>();
			meta.put("username",user.getUsername());
			meta.put("password",user.getPassword());
			meta.put("exceptionEmail",user.getEmail());
			params.put("log",user.getUsername());
			params.put("pwd",user.getPassword());
			params.put("rememberme","forever");
			params.put("wp-submit","登录");
			start.setHttpMethod(HttpMethod.POST);
			start.setParams(params);
			requests.add(start);
		}
		return requests;
	}


	@Override
	public void responseHandler(CrawlerResponse crawlerResponse) {
		Map<String, Object> meta = crawlerResponse.getMeta();
		System.out.println(crawlerResponse.getRealUrl() + "----url");
		String content = crawlerResponse.getContent();
		JXDocument document = crawlerResponse.document();
		JXNode jxNode = document.selNOne("//div[@id='login_error']");
		if(jxNode != null){
			String s = jxNode.toString();
			System.out.println(s);
			HashMap<String, Object> map = new HashMap<>();
			map.put("username", meta.get("username"));
			map.put("email",  meta.get("exceptionEmail"));
			map.put("time", DateUtils.dateFormat());
			map.put("errorTips", s);
			sendEmail(map, "doki8自动登录通知", "email_m1.html");
		}

	}

	private void sendEmail(HashMap<String,Object> info, String title, String filePath) {

		MailProperty mailProperty = new MailProperty();
		mailProperty.setToEmail(info.get("email").toString());
		mailProperty.setEmailTitle(title);
		mailProperty.setEmailFilePath(filePath);
		mailProperty.setData(info);
		try {
			MailUtils.sendEmail(mailProperty);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
