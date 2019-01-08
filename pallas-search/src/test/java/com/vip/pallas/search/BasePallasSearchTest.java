package com.vip.pallas.search;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.BeforeClass;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vip.pallas.search.filter.base.AbstractFilterContext;
import com.vip.pallas.search.launch.Startup;
import com.vip.pallas.utils.IPUtils;
import com.vip.pallas.utils.PallasBasicProperties;


public abstract class BasePallasSearchTest extends MockLocalHttpServerService {

	private static final String IT_TEST_CLUSTER = "it-test";
	protected static CloseableHttpClient httpClient = HttpClientBuilder.create().setMaxConnPerRoute(64).build();
	protected static int SERVER_PORT = 0;
	protected static Thread SERVER_THREAD;
	protected static boolean started  = false;
	protected static AbstractFilterContext backupRouteFilterContext;
	
	static {
		PallasBasicProperties.REFRESH_AFTER_WRITE_DURATION = 5;
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				BasePallasSearchTest.shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }));
	}
	
	@BeforeClass
	public static void setUpPs() throws Exception {
		if (!started) {
			SERVER_PORT = getNewPort();
			initSysEnv();
			startPS();
			waitUntilServerStarted();
			started = true;
		}
	}

	public static void shutdown() throws IOException {
		if (SERVER_THREAD != null) {
			System.out.println("start to shutdown the server...");
			SERVER_THREAD.interrupt();
			System.out.println("server interrupted.");
			System.out.println("start to delete all the it-test cluster records in table search_server.");
			deleteAllIttestClusterRecords();
		}
	}
	
	private static void waitUntilServerStarted() throws InterruptedException {
		Thread.sleep(500);
		int i = 200;
		while (!serverListening(SERVER_PORT) || i > 0) {
			try {
				Thread.sleep(20);
				i--;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!serverListening(SERVER_PORT)) {
			System.out.println("server started at port:" + SERVER_PORT + " failed. exit.");
			System.exit(-1);
		}
		System.out.println("server started at port:" + SERVER_PORT);
	}
	private static void deleteAllIttestClusterRecords() throws IOException {
		JSONObject jo = new JSONObject();
		jo.fluentPut("currentPage", 1).fluentPut("pageSize", 100).fluentPut("selectedCluster", IT_TEST_CLUSTER);
		String listStr = callRestApiAndReturnString("10.199.203.185", 80, "/pallas/ss/find.json?currentPage=1&pageSize=100&selectedCluster=" + IT_TEST_CLUSTER, "");
	    String regEx = "\"id\":(\\d+)";
	    Pattern pattern = Pattern.compile(regEx);
	    Matcher matcher = pattern.matcher(listStr);
	    while(matcher.find()) {
	    	try {
	    		String id = matcher.group(1);
				callRestApiAndReturnString("10.199.203.185", 80, "/pallas/ss/delete.json", "{\"id\":" + id + " }");
	    		System.out.println("delete searchServer where id = " + id);
	    	} catch (Exception e) {
				e.printStackTrace();
			}
	    }		
	}
	public static boolean serverListening(int port) {
		Socket s = null;
		try {
			s = new Socket(IPUtils.localIp4Str(), port);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private static void startPS() {
		SERVER_THREAD = new Thread(() -> {
            try {
                Startup.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
		SERVER_THREAD.start();
	}
	
	public static void initSysEnv() throws IOException {
		System.setProperty("pallas.search.port", SERVER_PORT + "");
		System.setProperty("pallas.console.upload_url", "http://pallas.vip.vip.com/pallas/ss/upsert.json");
		System.setProperty("pallas.stdout", "true");
		System.setProperty("pallas.search.cluster", IT_TEST_CLUSTER);
	}
	
	private static int getNewPort() throws IOException {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}
	@SuppressWarnings("rawtypes")
	public static Map callRestApi(String url, String requestBody) throws IOException{
		return JSON.parseObject(callRestApiAndReturnString(url, requestBody), Map.class);
	}
	public static CloseableHttpResponse callRestApiAndReturnResponse(String host, int port, String url, String requestBody) throws IOException{
		HttpHost target = new HttpHost(host, port);
		HttpPost request = new HttpPost(url);
		request.setEntity(new StringEntity(requestBody, ContentType.create("application/json", "UTF-8")));
		return httpClient.execute(target, request);
	}
	public static CloseableHttpResponse callRestApiAndReturnResponse(String host, int port, String url, Map<String, String> header, String requestBody) throws IOException{
		HttpHost target = new HttpHost(host, port);
		HttpPost request = new HttpPost(url);
		if(header != null) header.forEach((k, v) -> request.setHeader(k, v));
		request.setEntity(new StringEntity(requestBody, ContentType.create("application/json", "UTF-8")));
		return httpClient.execute(target, request);
	}
	public static String callRestApiAndReturnString(String host, int port, String url, String requestBody) throws IOException{
		return inputStream2String(callRestApiAndReturnResponse(host, port, url, requestBody).getEntity().getContent());
	}
	public static String callRestApiAndReturnString(String host, int port, String url, Map<String, String> header, String requestBody) throws IOException{
		return inputStream2String(callRestApiAndReturnResponse(host, port, url, header, requestBody).getEntity().getContent());
	}

	public static CloseableHttpResponse callRestApiAndReturnResponse(String url, String requestBody) throws IOException{
		return callRestApiAndReturnResponse(IPUtils.localIp4Str(), SERVER_PORT, url, requestBody);
	}
	public static String callRestApiAndReturnString(String url, String requestBody) throws IOException{
		return callRestApiAndReturnString(IPUtils.localIp4Str(), SERVER_PORT, url, requestBody);
	}
	public static String inputStream2String(InputStream is) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		is.close();
		return baos.toString();
	}
	
}
