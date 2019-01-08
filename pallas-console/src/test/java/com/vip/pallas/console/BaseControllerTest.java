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

package com.vip.pallas.console;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vip.pallas.console.vo.ClusterVO;
import com.vip.pallas.console.vo.IndexVO;
import com.vip.pallas.console.vo.PageResultVO;
import com.vip.pallas.console.vo.ResultVO;
import com.vip.pallas.mybatis.entity.Cluster;
import com.vip.pallas.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("development")
@RunWith(SpringRunner.class)
@SpringBootTest(classes =  ConsoleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseControllerTest {
	
	protected static Logger logger = LoggerFactory.getLogger("ITLOGGER");

	static {
		//System.setProperty("pallas.stdout", "true");
		System.setProperty("spring.profiles.active", "development");
		System.setProperty("pallas.db.profiles.active", "h2");
		System.setProperty("embedded.elasticsearch.download.directory", getDownloadDir());

	}
	static {
		try {
			launchES();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Autowired
	private TestRestTemplate template;

	private static TestRestTemplate restTemplate;

	@PostConstruct
	public void onPostConstruct()  {
		restTemplate = this.template;
	}

    @Test
    public void healthCheckTest() {
    }

	@SuppressWarnings("rawtypes")
	public static Map callRestApi(String url, String requestBody) throws IOException{
		return JSON.parseObject(callRestApiAndReturnString(url, requestBody), Map.class);
	}

	public static String callRestApiAndReturnString(String url, String requestBody) throws IOException{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_UTF8_VALUE));
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, new HttpEntity<>(requestBody, headers), String.class);
		return responseEntity.getBody();
	}

	public static Map uploadFile(String url, String filePath) throws IOException{
		FileSystemResource resource = new FileSystemResource(new File(filePath));
		MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
		param.add("file", resource);
		return JSON.parseObject(restTemplate.postForObject(url, param, String.class), Map.class);
	}

	@SuppressWarnings("rawtypes")
	public static Map callGetApi(String url) throws IOException{
		return JSON.parseObject(callGetApiAsString(url), Map.class);
	}
	
	public static <T> ResultVO<T> callGetApi(String url, Class<T> c) throws IOException {

		String bodyJsonString = callGetApiAsString(url);
		if(StringUtils.isEmpty(bodyJsonString)) {
			return new ResultVO<>();
		}

		ResultVO<T> resultVO = new ResultVO<>();
		JSONObject jsonObject = JSONObject.parseObject(bodyJsonString);
		resultVO.setStatus(jsonObject.getInteger("status"));
		resultVO.setMessage(jsonObject.getString("message"));
		if(StringUtils.isNotBlank(jsonObject.getString("data"))) {
			resultVO.setData(JSON.parseObject(jsonObject.getString("data"), c));
		}

		return resultVO;
	}

	public static String callGetApiAsString(String url) {
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
		return responseEntity.getBody();
	}

    public static String readFile(String filePath){
        StringBuilder result = new StringBuilder();
        try{
            String s = null;
            BufferedReader br = new BufferedReader(new FileReader(BaseControllerTest.class.getResource("/").getPath() + filePath));
            while((s = br.readLine())!=null){
                result.append(System.lineSeparator()+s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }

	protected ClusterVO insertClusterToTable()  throws Exception{

		ClusterVO clusterVO = ObjectJsonUtils.getClusterVO();
		Cluster cluster = null;

		assertThat(callRestApi("/cluster/add.json", JsonUtil.toJson(clusterVO))).isNull();  //首次插入
		//assertThat(callRestApi("/cluster/add.json", JsonUtil.toJson(clusterVO))).containsEntry("status", 500);  //再次插入

		ResultVO resultVO = callGetApi("/cluster/id.json?clusterId=" + clusterVO.getClusterId(), Cluster.class);
		assertThat(resultVO.getStatus()).isEqualTo(HttpStatus.OK.value());
		cluster = (Cluster)resultVO.getData();
		assertThat(cluster.getId()).isNotNull();

		clusterVO.setId(cluster.getId());
		return clusterVO;
	}

	protected IndexVO insertIndexToTable() throws Exception{

		ClusterVO clusterVO = insertClusterToTable();

		IndexVO indexVO = ObjectJsonUtils.getIndexVO();
		indexVO.setClusterId(clusterVO.getClusterId());

		assertThat(callRestApi("/index/add.json", JsonUtil.toJson(indexVO))).isNull();  //首次插入

		ResultVO<PageResultVO> resultVO = callGetApi("/index/page.json?indexName=" + indexVO.getIndexName() + "&clusterId=" + clusterVO.getClusterId(), PageResultVO.class);
		assertThat(resultVO.getStatus()).isEqualTo(HttpStatus.OK.value());

		PageResultVO<JSONObject> pageResultVO = resultVO.getData();
		assertThat(pageResultVO.getPageCount()).isEqualTo(1);
		assertThat(pageResultVO.getTotal()).isEqualTo(1L);
		assertThat(pageResultVO.getList().size()).isEqualTo(1);

		indexVO.setIndexId(pageResultVO.getList().get(0).getLong("id"));

		return indexVO;
	}

	public static void launchES() throws IOException, InterruptedException {
//		IndexSettings indexSettings = IndexSettings.builder()
//				.withType("item", getSystemResourceAsStream("product_comment-mapping.json"))
//				.withSettings(getSystemResourceAsStream("product_comment-settings.json"))
//				.build();

		EmbeddedElastic embeddedElastic = EmbeddedElastic.builder()
				.withElasticVersion("5.5.2")
				.withSetting(PopularProperties.CLUSTER_NAME, "pallas-test-cluster")
				.withSetting(PopularProperties.HTTP_PORT, 9200)
				.withEsJavaOpts("-Xms256m -Xmx256m -Dfile.encoding=UTF-8 -Des.insecure.allow.root=true")
				//.withIndex("product_comment", indexSettings)
				.withStartTimeout(30, TimeUnit.SECONDS)
				.withDownloadDirectory(new File(System.getProperty("embedded.elasticsearch.download.directory")))
				.withInstallationDirectory(new File(System.getProperty("java.io.tmpdir"), "embedded-elasticsearch-dir1"))
				.build()
				.start();

		//PallasEmbeddedElastic.withTemplate("product_comment_product_comment_search", IOUtils.toString(getSystemResourceAsStream("product_comment_search")));

		//JSONArray objects = JSON.parseArray(IOUtils.toString(getSystemResourceAsStream("product_comment-data.json"), UTF_8));

		//embeddedElastic.index("product_comment", "item", objects.stream().map(object -> object.toString()).collect(toList()));

		EmbeddedElastic.builder()
				.withElasticVersion("5.5.2")
				.withSetting(PopularProperties.CLUSTER_NAME, "pallas-test-cluster")
				.withSetting(PopularProperties.HTTP_PORT, 9210)
				.withEsJavaOpts("-Xms256m -Xmx256m -Dfile.encoding=UTF-8 -Des.insecure.allow.root=true")
				.withStartTimeout(30, TimeUnit.SECONDS)
				.withDownloadDirectory(new File(System.getProperty("embedded.elasticsearch.download.directory")))
				.withInstallationDirectory(new File(System.getProperty("java.io.tmpdir"), "embedded-elasticsearch-dir2"))
				.build()
				.start();

		EmbeddedElastic.builder()
				.withElasticVersion("5.5.2")
				.withSetting(PopularProperties.CLUSTER_NAME, "pallas-test-cluster")
				.withSetting(PopularProperties.HTTP_PORT, 9220)
				.withEsJavaOpts("-Xms256m -Xmx256m -Dfile.encoding=UTF-8 -Des.insecure.allow.root=true")
				.withStartTimeout(30, TimeUnit.SECONDS)
				.withDownloadDirectory(new File(System.getProperty("embedded.elasticsearch.download.directory")))
				.withInstallationDirectory(new File(System.getProperty("java.io.tmpdir"), "embedded-elasticsearch-dir3"))
				.build()
				.start();
	}

	private static String getDownloadDir(){
		String elasticFilePath = BaseControllerTest.class.getClassLoader().getResource("elasticsearch").getPath();
		File file = new File(elasticFilePath + "/elasticsearch-5.5.2.zip");

		if((file.exists())){
			return elasticFilePath;
		}else{
			Pattern pattern = Pattern.compile("file:(.*)/pallas-demo.jar!/elasticsearch");
			Matcher matcher = pattern.matcher(elasticFilePath);

			if (matcher.find()) {
				return matcher.group(1) + "/elasticsearch";
			}
		}
		return System.getProperty("java.io.tmpdir");
	}
}