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

import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by owen on 12/7/2017.
 */
public class HttpUtilTest extends TestCase {

    @Test
    public void testHttpGet() throws Exception {
        HttpUtil util = new HttpUtil();
        Map<String, Object> params = new HashMap<String, Object>();

        Map<String, Object> res = util.httpGet("http://10.199.205.98:9200/_cat/indices", params, false);
        assertEquals("{}", res.toString());

        res = util.httpGet("http://10.199.205.98:9200/_cat/indices", params, true);
        assertTrue(res.size() > 0);
    }
}