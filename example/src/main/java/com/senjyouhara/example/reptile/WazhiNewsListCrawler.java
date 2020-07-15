//package com.senjyouhara.example.reptile;
//
//import cn.wanghaomiao.seimi.annotation.Crawler;
//import cn.wanghaomiao.seimi.def.BaseSeimiCrawler;
//import cn.wanghaomiao.seimi.http.HttpMethod;
//import cn.wanghaomiao.seimi.struct.Request;
//import cn.wanghaomiao.seimi.struct.Response;
//import com.senjyouhara.core.reg.PatternUtils;
//import lombok.extern.log4j.Log4j2;
//import org.apache.commons.lang3.StringUtils;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.seimicrawler.xpath.JXDocument;
//import org.seimicrawler.xpath.exception.XpathSyntaxErrorException;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.io.*;
//import java.util.*;
//
//
//@Log4j2
//@Crawler
//public class WazhiNewsListCrawler extends BaseSeimiCrawler {
//
//
//	@Override
//	public String[] startUrls() {
//		return null;
//	}
//
//	@Override
//	public List<Request> startRequests() {
//		List<Request> requests = new LinkedList<>();
//		Request start = Request.build("http://www.wazhi.com.cn/SchoolManage/NewsDispatcher?SchoolId=1168&action=gotoschool",WazhiNewsListCrawler::start);
//		Map<String,String> params = new HashMap<>();
//		params.put("Host", "www.wazhi.com.cn");
//		params.put("Origin", "http://www.wazhi.com.cn");
//		params.put("Referer", "http://www.wazhi.com.cn/SchoolManage/NewsDispatcher?SchoolId=1168&action=gotoschool");
//		params.put("User-Agent", getUserAgent());
//		start.setHttpMethod(HttpMethod.POST);
//		start.setParams(params);
//		requests.add(start);
//		return requests;
//	}
//
//	@Override
//	public void start(Response response) {
//
//		for(int j = 0; j < Math.ceil(1168D / 6);j++){
//			push(Request.build("http://www.wazhi.com.cn/SchoolManage/NewsList?iscommonuse=gotoschool&pageId="+(j+1)+"&SchoolId=1168",WazhiNewsListCrawler::minePage).setParams(response.getParams()));
//		}
////		log.info("====: {}", Math.ceil(1168D / 6));
//		log.info(response.getContent());
////		HashMap<String, String> stringObjectHashMap = new HashMap<>();
////		stringObjectHashMap.put("Host", "www.wazhi.com.cn");
////		stringObjectHashMap.put("Origin", "http://www.wazhi.com.cn");
////		stringObjectHashMap.put("Referer", "http://www.wazhi.com.cn/SchoolManage/NewsDispatcher?SchoolId=1168&action=gotoschool");
////		stringObjectHashMap.put("User-Agent", getUserAgent());
////		JXDocument doc = response.document();
////		try {
////			List<Object> urls = doc.sel("//newsinfos");
////			logger.info("{}", urls.size());
////			logger.info("{}", urls);
////			for (Object s:urls){
////				push(Request.build(s.toString(),Basic::getTitle));
////			}
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
//	}
//
//	public void detailHandler(Response response) {
//		Map<String, Object> params = response.getMeta();
//		log.info(" detailHandler: {}", params);
//		JXDocument doc = response.document();
//		String content = "";
//		String date = "";
//		String title = "";
//		Long access = 0L;
//		try {
//
//			List<Object> titls = doc.sel("//h3[@align='center']/text()");
//
//			if(titls.size() > 0){
//				title = titls.get(0).toString();
//			}
//
//
//			String a = "";
//			List<Object> dates = doc.sel("//h5[@align='right']");
//			logger.info("detailHandler dates:{}", dates);
//			if(dates.size() > 0){
//				for(Object e : dates){
//					Element ele = (Element) e;
//					a = ele.outerHtml();
//				}
//			}
//
//			if(StringUtils.isNoneBlank(a)){
//
//				List<String> matcher = PatternUtils.matcher(a, "[0-9-]+ [0-9:]+");
//				if(matcher.size() > 0){
//					date = matcher.get(0);
//				}
//
//				matcher = PatternUtils.matcher(a, "(?<=浏览量：)[0-9]+");
//				if(matcher.size() > 0){
//					access = Long.valueOf(matcher.get(0));
//				}
//
//			}
//
//			List<Object> sel = doc.sel("//div[@class='single-left-grid']");
//			logger.info("detailHandler uname:{}", sel);
//			if(sel.size() > 0){
//				for(Object e : sel){
//					Element ele = (Element) e;
//					ele.select(".single-bottom").remove();
//					ele.select(".buttonupload").remove();
////					List<Node> nodes = ele.childNodes();
////					Elements imgs = ele.select("img");
////					for (int i = 0; i < imgs.size(); i++) {
////						String src = imgs.get(i).absUrl("src");
//////						/image/1168/20200419/20200419114800161.jpg
////						imgs.get(i).attr("src", src.replace("http://www.wazhi.com.cn//school", ""));
////						//连接url
////						URL url1 = new URL(src);
////						URLConnection uri=url1.openConnection();
////						//获取数据流
////						InputStream is=uri.getInputStream();
////						//获取后缀名
////						String imageName = src.substring(src.lastIndexOf("/") + 1,src.length());
////						//写入数据流
////						OutputStream os = new FileOutputStream(new File("d:/data/app/upload/image/2020-02/", imageName));
////
////						byte[] buf = new byte[1024];
////						int p=0;
////						while((p=is.read(buf))!=-1){
////							os.write(buf, 0, p);
////						}
////						os.close();
////					}
//					Elements remove = ele.select(" h3[align='center']").remove();
//					Elements remove2 = ele.select(" h5[align='right']").remove();
//					Elements remove3 = ele.select(" div[class='single-bottom']").remove();
//					Elements remove4 = ele.select(" p[class='segcotent']").remove();
//
//					content = ele.outerHtml();
//				}
//			}
//
////			articleService.saveEntity(title,content);
//
//			File file = new File("d:/data/" + params.get("id") + ".txt");
//
//			FileOutputStream fileOutputStream = new FileOutputStream(file);
//			fileOutputStream.write(content.getBytes());
//
//			fileOutputStream.close();
//
//			FileOutputStream propertyOutputStream = new FileOutputStream(new File("d:/data/" + params.get("id") + ".property"));
//
//			Properties properties = new Properties();
//			properties.setProperty("title",title);
//			properties.setProperty("access",access + "");
//			properties.store(propertyOutputStream,"shuruliu");
//
//		} catch (XpathSyntaxErrorException | FileNotFoundException e) {
//			logger.debug(e.getMessage(),e);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void minePage(Response response){
//		if(response == null || response.getContent() == null)
//				return ;
//		JXDocument doc = response.document();
//		ArrayList<String> strings = new ArrayList<>();
//		try {
//			List<Object> sel = doc.sel("//newsinfors/newsinfor/id/text()");
//			logger.info("uname:{}", sel);
//			if(sel.size() > 0){
//				for(Object e : sel){
//					String o = (String) e;
//					strings.add(o);
//					Map<String, Object> map = new HashMap<>();
//					map.put("id",o);
//					push(Request.build("http://www.wazhi.com.cn/SchoolManage/NewsDispatcher?NewsId="+ o +"&SchoolId=1168&action=singlenews",WazhiNewsListCrawler::detailHandler).setMeta(map));
//				}
//			}
//		} catch (XpathSyntaxErrorException e) {
//			logger.debug(e.getMessage(),e);
//		}
//	}
//}
