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

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vip.pallas.utils.HttpClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext-service-test.xml"})
@Ignore
public class HttpClientTest {

    @Test
    public void testCreateSaturnJob() throws Exception{
		try {
			String url = "http://10.199.172.93:8080/saturn/rest/v1/yfb-saturn-java.vip.vip.com/jobs";
			
			String content = "{     \"jobName\": \"DemoShellJob\",      \"description\": \"shell作业例子\",      \"jobConfig\": {         \"channelName\": \"\",          \"cron\": \"0/5 * * * * ?\",          \"jobClass\": \"\",          \"jobParameter\": \"\",          \"jobType\": \"SHELL_JOB\",          \"loadLevel\": 1,         \"localMode\": false,         \"pausePeriodDate\": \"\",          \"pausePeriodTime\": \"\",          \"preferList\": \"@yfb-saturn-java.vip.vip.com-xptest1\",          \"queueName\": \"\",          \"shardingItemParameters\": \"0=echo 0,1=echo 1\",         \"shardingTotalCount\": 2,          \"timeout4AlarmSeconds\": 0,          \"timeoutSeconds\": 0,          \"useDispreferList\": true,         \"useSerial\": false,         \"jobDegree\": 0,          \"dependencies\": \"\"     } }";
			String result = HttpClient.httpPost(url, content );
			
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}