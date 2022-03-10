package com.senjyouhara.crawler.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.senjyouhara.core.date.DateUtils;
import com.senjyouhara.core.json.JsonUtil;
import com.senjyouhara.core.ssl.MyX509TrustManager;
import com.senjyouhara.crawler.model.CrawlerCookie;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @date 2018/12/20
 */
@Log4j2
public class HttpUtil {

	/**
	 * 是否开启服务端HTTPS证书校验
	 */
	private static boolean enabled = true;
	/**
	 * 是否发送客户端证书
	 */
	private static boolean clientCert = true;
	/**
	 * 是否支持eureka的HTTPS注册
	 */
	private static boolean eureka = true;
	/**
	 * CA根证书密钥库文件
	 */
	private static String caRootCertKeyStore;
	/**
	 * CA根证书密钥库密码
	 */
	private static String caRootCertPassword;
	/**
	 * 客户端证书库文件
	 */
	private static String clientCertKeyStore;
	/**
	 * 客户端证书库密码
	 */
	private static String clientCertPassword;
	/**
	 * 建立连接的超时时间
	 */
	private static int connectTimeout = 20000;
	/**
	 * 连接不够用的等待时间
	 */
	private static int requestTimeout = 20000;
	/**
	 * 每次请求等待返回的超时时间
	 */
	private static int socketTimeout = 30000;
	/**
	 * 每个主机最大连接数
	 */
	private static int defaultMaxPerRoute = 100;
	/**
	 * 最大连接数
	 */
	private static int maxTotalConnections = 300;
	/**
	 * 连接保持活跃的时间（Keep-Alive）
	 */
	private static int defaultKeepAliveTimeMillis = 20000;
	/**
	 * 空闲连接的生存时间
	 */
	private static int closeIdleConnectionWaitTimeSecs = 30;


	private HttpUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * get
	 *
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doGet(String host, String path,
	                                 Map<String, String> headers,
	                                 Map<String, String> querys) throws IOException {
		return doGet(host, path, headers, querys, null);
	}

	public static HttpResponse doGet(String host, String path,
	                                 Map<String, String> headers,
	                                 Map<String, String> querys,
	                                 HttpHost httpHost) throws IOException {

		if (StringUtils.isBlank(path)) {
			path = "/";
		}
		if (headers == null) {
			headers = new HashMap<>();
		}
		if (querys == null) {
			querys = new HashMap<>();
		}

		HttpClient httpClient = wrapClient(host);
		HttpGet request = new HttpGet(buildUrl(host, path, querys));
		for (Map.Entry<String, String> e : headers.entrySet()) {
			request.addHeader(e.getKey(), e.getValue());
		}

		if (httpHost != null) {
			request.setConfig(RequestConfig.custom()
					.setProxy(httpHost)
					.build());
		}

		return httpClient.execute(request);
	}

	/**
	 * post form
	 *
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @param bodys
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doPost(String host, String path,
	                                  Map<String, String> headers,
	                                  Map<String, String> querys,
	                                  Map<String, String> bodys)
			throws Exception {
		return doPost(host, path, headers, querys, bodys, null);
	}

	public static HttpResponse doPost(String host, String path,
	                                  Map<String, String> headers,
	                                  Map<String, String> querys,
	                                  Map<String, String> bodys,
	                                  HttpHost httpHost)
			throws Exception {

		if (headers == null) {
			headers = new HashMap<>();
		}
		if (querys == null) {
			querys = new HashMap<>();
		}

		HttpClient httpClient = wrapClient(host);

		HttpPost request = new HttpPost(buildUrl(host, path, querys));
		for (Map.Entry<String, String> e : headers.entrySet()) {
			request.addHeader(e.getKey(), e.getValue());
		}

		if (bodys != null) {
			List<NameValuePair> nameValuePairList = new ArrayList<>();

			for (String key : bodys.keySet()) {
				nameValuePairList.add(new BasicNameValuePair(key, bodys.get(key)));
			}
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
			formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
			request.setEntity(formEntity);
		}

		if (httpHost != null) {
			request.setConfig(RequestConfig.custom()
					.setProxy(httpHost)
					.build());
		}

		return httpClient.execute(request);
	}

	/**
	 * Post String
	 *
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doPost(String host, String path,
	                                  Map<String, String> headers,
	                                  Map<String, String> querys,
	                                  String body)
			throws Exception {
		return doPost(host, path, headers, querys, body, null);
	}

	public static HttpResponse doPost(String host, String path,
	                                  Map<String, String> headers,
	                                  Map<String, String> querys,
	                                  String body, HttpHost httpHost)
			throws Exception {


		if (headers == null) {
			headers = new HashMap<>();
		}
		if (querys == null) {
			querys = new HashMap<>();
		}

		HttpClient httpClient = wrapClient(host);

		HttpPost request = new HttpPost(buildUrl(host, path, querys));
		for (Map.Entry<String, String> e : headers.entrySet()) {
			request.addHeader(e.getKey(), e.getValue());
		}
		if (StringUtils.isNotBlank(body)) {
			request.setEntity(new StringEntity(body, "utf-8"));
		}
		if (httpHost != null) {
			request.setConfig(RequestConfig.custom()
					.setProxy(httpHost)
					.build());
		}
		return httpClient.execute(request);
	}

	/**
	 * Post stream
	 *
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doPost(String host, String path,
	                                  Map<String, String> headers,
	                                  Map<String, String> querys,
	                                  byte[] body)
			throws Exception {
		return doPost(host, path, headers, querys, body, null);
	}

	public static HttpResponse doPost(String host, String path,
	                                  Map<String, String> headers,
	                                  Map<String, String> querys,
	                                  byte[] body, HttpHost httpHost)
			throws Exception {

		if (headers == null) {
			headers = new HashMap<>();
		}
		if (querys == null) {
			querys = new HashMap<>();
		}

		HttpClient httpClient = wrapClient(host);

		HttpPost request = new HttpPost(buildUrl(host, path, querys));
		for (Map.Entry<String, String> e : headers.entrySet()) {
			request.addHeader(e.getKey(), e.getValue());
		}

		if (body != null) {
			request.setEntity(new ByteArrayEntity(body));
		}

		if (httpHost != null) {
			request.setConfig(RequestConfig.custom()
					.setProxy(httpHost)
					.build());
		}

		return httpClient.execute(request);
	}

	/**
	 * Put String
	 *
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doPut(String host, String path,
	                                 Map<String, String> headers,
	                                 Map<String, String> querys,
	                                 String body)
			throws Exception {
		return doPut(host, path, headers, querys, body, null);
	}

	public static HttpResponse doPut(String host, String path,
	                                 Map<String, String> headers,
	                                 Map<String, String> querys,
	                                 String body, HttpHost httpHost)
			throws Exception {


		if (headers == null) {
			headers = new HashMap<>();
		}
		if (querys == null) {
			querys = new HashMap<>();
		}

		HttpClient httpClient = wrapClient(host);

		HttpPut request = new HttpPut(buildUrl(host, path, querys));
		for (Map.Entry<String, String> e : headers.entrySet()) {
			request.addHeader(e.getKey(), e.getValue());
		}

		if (StringUtils.isNotBlank(body)) {
			request.setEntity(new StringEntity(body, "utf-8"));
		}

		if (httpHost != null) {
			request.setConfig(RequestConfig.custom()
					.setProxy(httpHost)
					.build());
		}

		return httpClient.execute(request);
	}

	/**
	 * Put stream
	 *
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doPut(String host, String path,
	                                 Map<String, String> headers,
	                                 Map<String, String> querys,
	                                 byte[] body)
			throws Exception {
		return doPut(host, path, headers, querys, body, null);
	}

	public static HttpResponse doPut(String host, String path,
	                                 Map<String, String> headers,
	                                 Map<String, String> querys,
	                                 byte[] body, HttpHost httpHost)
			throws Exception {

		if (headers == null) {
			headers = new HashMap<>();
		}
		if (querys == null) {
			querys = new HashMap<>();
		}

		HttpClient httpClient = wrapClient(host);

		HttpPut request = new HttpPut(buildUrl(host, path, querys));
		for (Map.Entry<String, String> e : headers.entrySet()) {
			request.addHeader(e.getKey(), e.getValue());
		}

		if (body != null) {
			request.setEntity(new ByteArrayEntity(body));
		}
		if (httpHost != null) {
			request.setConfig(RequestConfig.custom()
					.setProxy(httpHost)
					.build());
		}

		return httpClient.execute(request);
	}

	/**
	 * Delete
	 *
	 * @param host
	 * @param path
	 * @param headers
	 * @param querys
	 * @return
	 * @throws Exception
	 */
	public static HttpResponse doDelete(String host, String path,
	                                    Map<String, String> headers,
	                                    Map<String, String> querys)
			throws Exception {
		return doDelete(host, path, headers, querys, null);
	}

	public static HttpResponse doDelete(String host, String path,
	                                    Map<String, String> headers,
	                                    Map<String, String> querys, HttpHost httpHost)
			throws Exception {

		if (headers == null) {
			headers = new HashMap<>();
		}
		if (querys == null) {
			querys = new HashMap<>();
		}

		HttpClient httpClient = wrapClient(host);

		HttpDelete request = new HttpDelete(buildUrl(host, path, querys));
		for (Map.Entry<String, String> e : headers.entrySet()) {
			request.addHeader(e.getKey(), e.getValue());
		}
		if (httpHost != null) {
			request.setConfig(RequestConfig.custom()
					.setProxy(httpHost)
					.build());
		}
		return httpClient.execute(request);
	}


	private static String buildUrl(String host, String path, Map<String, String> querys) {
		StringBuilder sbUrl = new StringBuilder();
		sbUrl.append(host);
		if (!StringUtils.isBlank(path)) {
			sbUrl.append(path);
		}
		if (null != querys) {
			StringBuilder sbQuery = new StringBuilder();
			for (Map.Entry<String, String> query : querys.entrySet()) {
				if (0 < sbQuery.length()) {
					sbQuery.append("&");
				}
				if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
					sbQuery.append(query.getValue());
				}
				if (!StringUtils.isBlank(query.getKey())) {
					sbQuery.append(query.getKey());
					if (!StringUtils.isBlank(query.getValue())) {
						sbQuery.append("=");
						try {
							sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
							throw new RuntimeException("querys参数url编码失败");
						}
					}
				}
			}
			if (0 < sbQuery.length()) {
				sbUrl.append("?").append(sbQuery);
			}
		}

		return sbUrl.toString();
	}

	private static HttpClient wrapClient(String host) {
		HttpClient httpClient = HttpClientBuilder.create().build();
		if (host.startsWith("https://")) {
			return sslClient();
		}

		return httpClient;
	}

	private static X509TrustManager getX509TrustManager()
			throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		// 加载服务端信任根证书库
		KeyStore trustKeyStore = KeyStore.getInstance("PKCS12");
		trustKeyStore.load(new FileInputStream(
				caRootCertKeyStore), caRootCertPassword.toCharArray());
		// 初始化服务端信任证书管理器
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(trustKeyStore);
		TrustManager[] trustManagers = tmf.getTrustManagers();
		return (X509TrustManager) trustManagers[0];
	}


	public static PoolingHttpClientConnectionManager poolingConnectionManager(SSLContext sslContext) {
		SSLConnectionSocketFactory sslsf;
		try {
			HostnameVerifier hostnameVerifier = new TrustAnyHostnameVerifier();
			sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
		} catch (Exception e) {
			log.error("Pooling Connection Manager Initialisation failure");
			throw new RuntimeException("Pooling Connection Manager Initialisation failure", e);
		}
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				.<ConnectionSocketFactory>create()
				.register("https", sslsf)
				.register("http", new PlainConnectionSocketFactory())
				.build();

		PoolingHttpClientConnectionManager poolingConnectionManager
				= new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		poolingConnectionManager.setMaxTotal(maxTotalConnections);  //最大连接数
		poolingConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);  //同路由并发数
		return poolingConnectionManager;
	}

	public static ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
		return (response, httpContext) -> {
			HeaderElementIterator it = new BasicHeaderElementIterator
					(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
			while (it.hasNext()) {
				HeaderElement he = it.nextElement();
				String param = he.getName();
				String value = he.getValue();
				if (value != null && param.equalsIgnoreCase("timeout")) {
					return Long.parseLong(value) * 1000;
				}
			}
			return defaultKeepAliveTimeMillis;
		};
	}

	public static CloseableHttpClient httpClient(SSLContext sslContext) {
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(requestTimeout)
				.setConnectTimeout(connectTimeout)
				.setSocketTimeout(socketTimeout).build();

		return HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.setConnectionManager(poolingConnectionManager(sslContext))
				.setKeepAliveStrategy(connectionKeepAliveStrategy())
				.setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
				.build();
	}


	private static CloseableHttpClient sslClient() {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new SecureRandom());
			return httpClient(ctx);
		} catch (KeyManagementException ex) {
			throw new RuntimeException(ex);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}


	/**
	 * 发送https请求
	 *
	 * @param requestUrl    请求地址
	 * @param requestMethod 请求方式（GET、POST）
	 * @param outputStr     提交的数据
	 * @return JSONObject(通过JSONObject.get ( key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = {new MyX509TrustManager()};
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod(requestMethod);

			// 当outputStr不为null时向输出流写数据
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				// 注意编码格式
				outputStream.write(outputStr.getBytes(StandardCharsets.UTF_8));
				outputStream.close();
			}

			// 从输入流读取返回内容
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}

			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			jsonObject = JSONUtil.parseObj(buffer.toString());
		} catch (ConnectException ce) {
			log.error("连接超时：{}", ce);
		} catch (Exception e) {
			log.error("https请求异常：{}", e);
		}
		return jsonObject;
	}


	/**
	 * 向指定 URL 发送GET方法的请求
	 *
	 * @param url   发送请求的 URL
	 * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param) {
		return sendGet(url, param, StandardCharsets.UTF_8);
	}

	/**
	 * 向指定 URL 发送GET方法的请求
	 *
	 * @param url         发送请求的 URL
	 * @param param       请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @param contentType 编码类型
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendGet(String url, String param, Charset contentType) {
		StringBuilder result = new StringBuilder();
		BufferedReader in = null;
		try {
			String urlNameString = url + "?" + param;
			log.info("sendGet - {}", urlNameString);
			URL realUrl = new URL(urlNameString);
			URLConnection connection = realUrl.openConnection();
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.connect();
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), contentType));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			log.info("recv - {}", result);
		} catch (ConnectException e) {
			log.error("调用HttpUtils.sendGet ConnectException, url=" + url + ",param=" + param, e);
		} catch (SocketTimeoutException e) {
			log.error("调用HttpUtils.sendGet SocketTimeoutException, url=" + url + ",param=" + param, e);
		} catch (IOException e) {
			log.error("调用HttpUtils.sendGet IOException, url=" + url + ",param=" + param, e);
		} catch (Exception e) {
			log.error("调用HttpsUtil.sendGet Exception, url=" + url + ",param=" + param, e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception ex) {
				log.error("调用in.close Exception, url=" + url + ",param=" + param, ex);
			}
		}
		return result.toString();
	}

	public static String sendSSLPost(String url, String param) {
		StringBuilder result = new StringBuilder();
		String urlNameString = url + "?" + param;
		try {
			log.info("sendSSLPost - {}", urlNameString);
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new SecureRandom());
			URL console = new URL(urlNameString);
			HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Accept-Charset", "utf-8");
			conn.setRequestProperty("contentType", "utf-8");
			conn.setDoOutput(true);
			conn.setDoInput(true);

			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String ret = "";
			while ((ret = br.readLine()) != null) {
				if (ret != null && !ret.trim().equals("")) {
					result.append(new String(ret.getBytes("ISO-8859-1"), "utf-8"));
				}
			}
			log.info("recv - {}", result);
			conn.disconnect();
			br.close();
		} catch (ConnectException e) {
			log.error("调用HttpUtils.sendSSLPost ConnectException, url=" + url + ",param=" + param, e);
		} catch (SocketTimeoutException e) {
			log.error("调用HttpUtils.sendSSLPost SocketTimeoutException, url=" + url + ",param=" + param, e);
		} catch (IOException e) {
			log.error("调用HttpUtils.sendSSLPost IOException, url=" + url + ",param=" + param, e);
		} catch (Exception e) {
			log.error("调用HttpsUtil.sendSSLPost Exception, url=" + url + ",param=" + param, e);
		}
		return result.toString();
	}

	private static class TrustAnyTrustManager implements X509TrustManager {
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			log.info(">>>>>>>>>>>>>> sslContext getAcceptedIssuers 00000000000000000 start ...");
//			X509TrustManager x509TrustManager = getX509TrustManager();
//			return x509TrustManager.getAcceptedIssuers();
			return new X509Certificate[]{};
		}

		@Override
		public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
			log.info(">>>>>>>>>>>>>> sslContext checkClientTrusted 111111111111 start ...");
		}

		@Override
		public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
			log.info(">>>>>>>>>>>>>> sslContext checkServerTrusted 222222222222222 start ...");
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			try {
				Certificate[] certs = session.getPeerCertificates();
				X509Certificate x509 = (X509Certificate) certs[0];
			} catch (SSLPeerUnverifiedException e) {
				return false;
			}
			return true;
		}
	}

	public static String getSha1(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f'};
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
			mdTemp.update(str.getBytes("UTF-8"));

			byte[] md = mdTemp.digest();
			int j = md.length;
			char buf[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
				buf[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(buf);
		} catch (Exception e) {
			return null;
		}
	}

	public static List<CrawlerCookie> cookieParse(List<String> list){
		List<CrawlerCookie> cookies = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			String[] split = s.split(";");
			if(split.length > 0){
				String s1 = split[0];
				String[] split1 = s1.split("=");
				if(split1.length == 2){
					LocalDateTime expires = null;
					if(split.length >= 2){
						String s2 = split[1];
						String[] split2 = s2.split("=");
						if(split2.length == 2){
							if(split2[0].trim().equals("expires")){
								String pattern = "EEE, dd-MMM-yyyy HH:mm:ss zzz";
								SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.US);
								try {
									Date date = format.parse(split2[1]);
									expires = DateUtils.dateToLocalDate(date);
								} catch (ParseException e) {
									e.printStackTrace();
									log.error("日期解析出错： {}",split2[1] );
								}
							}
						}
					}
					CrawlerCookie crawlerCookie = new CrawlerCookie(split1[0],split1[1], expires);
					cookies.add(crawlerCookie);
				}
			}
		}
		return cookies;
	}

	public static String cookie2String(Set<CrawlerCookie> set){

		StringBuilder cookies = new StringBuilder();
		if (set != null && set.size() > 0) {
			List<CrawlerCookie> crawlerCookies = set.stream().distinct().collect(Collectors.toList());
			for (int i = 0; i < crawlerCookies.size(); i++) {
				CrawlerCookie crawlerCookie = crawlerCookies.get(i);
				cookies.append(crawlerCookie.getName()).append("=").append(crawlerCookie.getValue());
				if (set.size() - 1 != i) {
					cookies.append("; ");
				}
			}
		}
		return cookies.toString();
	}
}
