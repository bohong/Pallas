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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class GenPerformaceFile {

	static String REQUEST = "/merchandise_front_index/_search/template@@{\"id\":\"merchandise_front_index_upshelf_query\","
			+ "\"params\":{\"now_in_str\":\"%s\",\"vspuid\":{\"list\":[%s]},\"platform\":\"%s\",\"site\":\"%s\",\"mshow\":1}}\r\n";

	static String[] SITE_ARRAY = { "VIP_NH", "VIP_BJ", "VIP_CD", "VIP_HZ", "VIP_SH" };

	static int[] PLATFORM_ARRAY = { 1, 2 };

	public static void main(String[] args) throws IOException {
		List<String> spuList = FileUtils.readLines(new File("/Users/xueshuting/Downloads/mfi.csv"));
		File output = new File("/Users/xueshuting/Downloads/mfi-pt.txt");
		int spuListSize = spuList.size();
		Random rt = new Random(spuListSize);
		for (int i = 0; i < 2000000; i++) {
			int platformIdx = rt.nextInt(spuListSize) % 2;
			int siteIdx = rt.nextInt(5);
			int platform = PLATFORM_ARRAY[platformIdx];
			String site = SITE_ARRAY[siteIdx];
			String spuId = spuList.get(rt.nextInt(spuListSize));
			String nowStr = "2018-06-05 15:40:12";
			String out = String.format(REQUEST, nowStr, spuId, platform, site);
			FileUtils.write(output, out, true);
			
		}
	}
}