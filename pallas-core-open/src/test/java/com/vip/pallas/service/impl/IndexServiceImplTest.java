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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.vip.pallas.BaseSpringTest;
import com.vip.pallas.exception.PallasException;
import com.vip.pallas.mybatis.entity.DataSource;
import com.vip.pallas.mybatis.entity.Index;
import com.vip.pallas.mybatis.entity.Page;
import com.vip.pallas.service.IndexService;

public class IndexServiceImplTest extends BaseSpringTest {
	
	@Autowired
	private IndexService indexService;
	
	private static final String INDEX_NAME = "in-" + System.currentTimeMillis();
	private static long indexId = 0;
	@Test
	public void testSelectPage() throws PallasException {
		Page<Index> page = new Page<>();
		page.setPageNo(1);
		page.setPageSize(10);
		List<Index> findPage = indexService.findPage(page, null, null);
		assertThat(findPage.size()).isGreaterThanOrEqualTo(0);
	}
	@Test
	public void testCrud() throws PallasException {
		try {
			Index index = new Index();
	
			DataSource dataSource1 = new DataSource();
			dataSource1.setIp("10.199.203.185");
			dataSource1.setPort("3306");
			dataSource1.setUsername("root");
			dataSource1.setPassword("123456");
			dataSource1.setDbname("pallas_console");
			dataSource1.setTableName("index");
			
			DataSource dataSource2 = new DataSource();
			dataSource2.setIp("10.199.203.185");
			dataSource2.setPort("3306");
			dataSource2.setUsername("root");
			dataSource2.setPassword("123456");
			dataSource2.setDbname("pallas_console");
			dataSource2.setTableName("index2");
			
			index.setDescription("one");
			index.setClusterName("pallas");
			List<DataSource> dataSourceList = new ArrayList<>();
			dataSourceList.add(dataSource1);
			dataSourceList.add(dataSource2);
	
			index.setDataSourceList(dataSourceList);
			index.setIndexName(INDEX_NAME);
			
			// insert
			indexService.insert(index, dataSourceList);
			assertNotNull(index.getId());
			
			indexId = index.getId();
			
			// query
			Index findByClusterNameAndIndexName = indexService.findByClusterNameAndIndexName(index.getClusterName(), INDEX_NAME);
			assertNotNull(findByClusterNameAndIndexName);
			
			// query
			Index indexFromDb = indexService.findById(indexId);
			assertNotNull(indexFromDb);
			assertThat(indexFromDb.getDataSourceList().size()).isEqualTo(2);
			
			// update
			indexFromDb.setDescription("two");
	
			DataSource dataSource3 = new DataSource();
			dataSource3.setIp("10.199.203.185");
			dataSource3.setPort("3306");
			dataSource3.setUsername("root");
			dataSource3.setPassword("123456");
			dataSource3.setDbname("pallas_console");
			dataSource3.setTableName("index3");
			
			List<DataSource> dataSourceListFromDB = indexFromDb.getDataSourceList();
			dataSourceListFromDB.add(dataSource3);
			dataSourceListFromDB.get(0).setDescription("it test");
			
			indexService.update(indexFromDb, dataSourceListFromDB, false);
			
			Index indexFromDbAfterUpdate = indexService.findById(indexId);
			assertEquals("two", indexFromDbAfterUpdate.getDescription());
			assertThat(indexFromDbAfterUpdate.getDataSourceList().size()).isEqualTo(3);
			
			
			// findAll
			List<Index> findAll = indexService.findAll();
			assertThat(findAll.size()).isGreaterThan(0);
			
			// findPage
			Page<Index> page = new Page<>();
			page.setPageNo(1);
			page.setPageSize(10);
			List<Index> findPage = indexService.findPage(page, INDEX_NAME, "pallas");
			assertThat(findPage.size()).isEqualTo(1);
		} finally {
			// delete 
			indexService.deleteById(indexId);
			Index indexFromDbAfterDelete = indexService.findById(indexId);
			assertNull(indexFromDbAfterDelete);
		}
	}
	

}