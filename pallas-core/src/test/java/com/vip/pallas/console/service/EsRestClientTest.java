/**
 * Copyright 2019 vip.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.vip.pallas.console.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.Ignore;
import org.junit.Test;

import com.vip.pallas.utils.ElasticRestClient;
import com.vip.pallas.utils.JsonUtil;

@Ignore
public class EsRestClientTest {

	@Test
	public void testMain(){
		
	}
	
	public static void main(String[] args) throws Exception {
		/*
		 * for (int i = 0;i<10;i++) { new Thread(new GetCountTask()).start(); }
		 */

		RestClient restClient = ElasticRestClient.build("http://192.168.200.78:9200");
		Response response = restClient.performRequest("GET", "_cluster/settings?flat_settings=true",
				Collections.singletonMap("pretty", "false"));
		String responseStr = IOUtils.toString(response.getEntity().getContent(), Charset.forName("UTF-8"));
		System.out.println("before::" + responseStr);
		String nodeIp = "192.168.200.8";
		Map<String, Object> result = JsonUtil.readValue(responseStr, Map.class);
		try {

			String ipList = StringUtils.substringBetween(responseStr, "\"cluster.routing.allocation.exclude._ip\":\"",
					"\"");
			if (StringUtils.isNotEmpty(ipList)) {
				List<String> asList = Arrays.asList(ipList.split(","));
				List<String> excludedList = new ArrayList<String>(asList);  
				excludedList.remove(nodeIp);
				ipList = StringUtils.join(asList, ",");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(-1);
	}
}

class GetCountTask implements Runnable {
	public GetCountTask() {
	}

	@Override
	public void run() {
		RestClient restClient = ElasticRestClient.build("10.199.205.97:9200,10.199.205.98:9200");
		while (true) {
			try {
				Response response = restClient.performRequest("GET", "/sales_query_index_100008/item/_search",
						Collections.singletonMap("pretty", "true"));
				String responseStr = IOUtils.toString(response.getEntity().getContent(), Charset.forName("UTF-8"));
				Map<String, Object> result = JsonUtil.readValue(responseStr, Map.class);
				System.out.println(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}