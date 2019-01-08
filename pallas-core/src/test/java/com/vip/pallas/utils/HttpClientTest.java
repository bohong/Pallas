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

package com.vip.pallas.utils;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Created by owen on 12/7/2017.
 */
public class HttpClientTest extends TestCase {

    @Test
    public void testGet() throws Exception {

        String res = HttpClient.httpGet("http://10.199.173.109:9200/_cat");
        assertTrue(res.contains("/_cat/nodes"));
    }

    @Test
    public void testPost() throws Exception {
        String res = HttpClient.httpPost("http://10.199.173.109:9200/sales/item/_search/template", "{\n" +
                "  \"id\": \"sales_sales\"\n" +
                " \n" +
                "}");
        System.out.println(res);
        assertTrue(res.contains("\"hits\":"));
    }

    @Test
    public void testDelete() throws Exception {
        HttpClient.httpPost("http://10.199.173.109:9200/sales/item/aaa", "{}");
        String res = HttpClient.httpDelete("http://10.199.173.109:9200/sales/item/aaa");
        assertTrue(res.contains("{\"found\":true"));

    }
}