//package com.vip.pallas.search.cache;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.Test;
//
//import com.alibaba.fastjson.JSONObject;
//import com.vip.pallas.search.BasePallasSearchTest;
//
//public class RoutingCacheTest {
//	static String cluster = System.currentTimeMillis() + "";
//
//	@Test
//	public void testCache() throws IOException {
//		System.setProperty("VIP_PALLAS_CONSOLE_REST_URL", "http://192.168.200.216/pallas");
//		System.setProperty("pallas.stdout", "true");
//		try {
//			RoutingCache routingCache = new RoutingCache();
//			Map<String, Object> beforeInsertCluster = (Map<String, Object>) ((HashMap)(routingCache.fetchData(RoutingCache.CLUSTER_NODE_LIST))).get(RoutingCache.CLUSTER_NODE_LIST);
//			JSONObject jo = new JSONObject();
//			jo.fluentPut("httpAddress", "192.168.200.78:9200").fluentPut("clientAddress", "192.168.200.78:9300").fluentPut("clusterId", cluster).fluentPut("description", "whatever");
//			String addClusterResult = BasePallasSearchTest.callRestApiAndReturnString("192.168.200.216", 80, "/pallas/cluster/add.json", jo.toJSONString());
//			assertThat(addClusterResult).containsIgnoringCase("ok");
//
//			Map<String, Object> afterInsertCluster =  (Map<String, Object>) ((HashMap)(routingCache.fetchData(RoutingCache.CLUSTER_NODE_LIST))).get(RoutingCache.CLUSTER_NODE_LIST);
//			assertThat(beforeInsertCluster.size() + 1).isEqualTo(afterInsertCluster.size());
//		} finally {
//			// delete the cluster
//			String deleteClusterResult = BasePallasSearchTest.callRestApiAndReturnString("192.168.200.216", 80, "/pallas/cluster/delete/id.json", "{\"clusterId\":"+cluster+"}");
//			assertThat(deleteClusterResult).containsIgnoringCase("ok");
//		}
//	}
//}
