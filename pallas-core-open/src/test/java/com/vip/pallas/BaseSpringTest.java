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

package com.vip.pallas;

import java.io.InputStream;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-service-test.xml" })
@Ignore
public class BaseSpringTest extends TestCase {
	static {
		System.setProperty("saturn.rest.url", "http://saturn.vip.vip.com/rest/v1");		
		System.setProperty("VIP_OSP_LOCAL_PROXY", "10.199.172.98:2080");
		System.setProperty("VIP_OSP_REMOTE_PROXY", "10.199.172.98:2080");
	}
	
	private static final Logger logger = LoggerFactory.getLogger(BaseSpringTest.class);

	protected String getResourceContent(String resourcePath) {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
		try {
			return IOUtils.toString(is, Charsets.UTF_8);
		} catch (Exception e) {
			logger.error("BaseSpringTest.getResourceContent,e:" + e);
		} finally {
			IOUtils.closeQuietly(is);
		}
		return null;
	}
}