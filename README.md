### 一个爬虫库

使用springboot实现爬取数据功能，目前只完成了基本核心功能，其他功能待日后完成

该项目使用注解驱动编程


#### 开启功能
```
在启动项目标注该注解
@EnabledCrawler
@SpringBootApplication
public class Application {
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(Application.class,args);
	}
}
```

#### 定义爬虫逻辑
需要继承自抽象类 ```AbstractCrawler``` 然后标记该注解```@Crawler```该类就是一个爬虫类了，只要标记了该注解就会被自动扫描到，不需要再加```@Component```，
```$xslt
@Crawler
public class Bilibili extends AbstractCrawler {
}
```


#### 如何创建请求
在爬虫类里使用该方式可以创建请求，最后一个参数是个执行请求回来的该请求的一个方法，可以进行请求递归传递
```
addRequest(RequestBuild.build("url”,Bilibili::titleDetail));
```




#### 如何使用
启动方式有2种，主要通过```CrawlerContext.run```该静态方法执行爬虫相关逻辑，一种是传入爬虫类名开始执行，一种是传入爬虫类名的小驼峰命名来执行。

```$xslt
CrawlerContext.run("bilibili");
or
CrawlerContext.run(Bilibili.class);

```

在内部会对所有的Request请求进行缓存，以便多次执行。每个类会为其分配默认20个线程进行执行
