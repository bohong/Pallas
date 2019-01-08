package com.vip.pallas.search.routing;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.vip.pallas.utils.IPUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RoutingWithNormalLevelTest extends BaseRoutingTest {

	void setTestTargetGroup() throws SQLException {
		String sql = "insert into `pallas_console`.`index_routing_target_group` (`index_id`, `index_name`, `name`, `nodes_info`, `clusters_info`, `state`, `cluster_level`) values ('4543', 'vfeature', 'ittest-normal', '[{\"cluster\":\"test-rpm.api.vip.com\",\"name\":\"10.199.205.97\",\"address\":\"10.199.205.97\",\"weight\":1,\"state\":0},{\"cluster\":\"test-rpm.api.vip.com\",\"name\":\"10.199.205.97\",\"address\":\"10.199.205.97\",\"weight\":1,\"state\":1},{\"cluster\":\"test-rpm.api.vip.com\",\"name\":\"10.199.205.97\",\"address\":\"10.199.205.97\",\"weight\":1,\"state\":1},{\"cluster\":\"test-rpm.api.vip.com\",\"name\":\"10.199.205.97\",\"address\":\"10.199.205.97\",\"weight\":1,\"state\":1}]', '[]', 0, 0);";
		executeUpdate(sql);
	}

	@Test
	public void testWithNormalLevel() throws Exception {
		Map<String, String> header = new HashMap<>();
		header.put("business_code", "ittest");

		String requestBody = "{\n" +
                "  \"id\": \"vfeature_list_by_ids\",\n" +
                "  \"params\": {\n" +
                "    \"id\": {\n" +
                "      \"list\": [1]\n" +
                "    }\n" +
                "  }\n" +
                "}";

		assertThat(callRestApiAndReturnResponse(IPUtils.localIp4Str(), SERVER_PORT, "/vfeature/_search/template", header, requestBody).getStatusLine().getStatusCode()).isEqualTo(200);
	}

	@Test
	public void testRoutingNotMatchIndex() throws Exception {
		Map<String, String> header = new HashMap<>();
		header.put("business_code", "ittest");

		String requestBody = "{\n" +
				"  \"id\": \"vfeature_list_by_ids\",\n" +
				"  \"params\": {\n" +
				"    \"id\": {\n" +
				"      \"list\": [1]\n" +
				"    }\n" +
				"  }\n" +
				"}";

		assertThat(callRestApiAndReturnResponse(IPUtils.localIp4Str(), SERVER_PORT, "/vfeature0/_search/template", header, requestBody).getStatusLine().getStatusCode()).isEqualTo(503);
	}

	@Test
	public void testRoutingNotMatchHeader() throws Exception {
		Map<String, String> header = new HashMap<>();
		header.put("business_code", "ittest123");

		String requestBody = "{\n" +
				"  \"id\": \"vfeature_list_by_ids\",\n" +
				"  \"params\": {\n" +
				"    \"id\": {\n" +
				"      \"list\": [1]\n" +
				"    }\n" +
				"  }\n" +
				"}";

		assertThat(callRestApiAndReturnResponse(IPUtils.localIp4Str(), SERVER_PORT, "/vfeature/_search/template", header, requestBody).getStatusLine().getStatusCode()).isEqualTo(503);
	}
}
