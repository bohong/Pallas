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

package com.vip.pallas.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.vip.pallas.BaseSpringTest;
import com.vip.pallas.service.ElasticSearchService;

public class ElasticSearchServiceImplTest extends BaseSpringTest {
	
	@Autowired
	private ElasticSearchService elasticSearchService;
	private static final String INDEX_NAME = "saturn_index_it";
	private static Long versionId = 99999l;
	
	@Test
	public void testInterfaces() throws Exception {
		boolean isIndexExisted = elasticSearchService.isExistIndex(INDEX_NAME, versionId);
		assertThat(isIndexExisted).isEqualTo(true);
		
		Long dataCount = elasticSearchService.getDataCount(INDEX_NAME, versionId);
		assertThat(dataCount).isGreaterThanOrEqualTo(0l);
		
		String indexInfo = elasticSearchService.getIndexInfo(INDEX_NAME, versionId);
		assertThat(indexInfo).isNotNull();
		
		String mapping = elasticSearchService.genMappingJsonByVersionId(versionId);
		assertThat(mapping).isNotNull();
		System.out.println(mapping);

		boolean result = elasticSearchService.excludeOneNode("10.199.205.97:9200", "10.199.205.98");
		assertTrue(result);

		result = elasticSearchService.includeOneNode("10.199.205.97:9200", "10.199.205.98");
		assertTrue(result);

		List<String> avalableNodeIps = elasticSearchService.getAvalableNodeIps("10.199.205.97:9200");
		assertTrue(avalableNodeIps.size() > 0);

	}
	
	@Test
	public void testDeleteByQuery() {
		System.out.println(elasticSearchService.cancelDeleteByQueryTask(103653l, "big_desk_103653", "start"));
	}

}