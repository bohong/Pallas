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
public class RoutingWithClusterLevelTest extends BaseRoutingTest {

	void setTestTargetGroup() throws SQLException {
		String sql = "insert into `pallas_console`.`index_routing_target_group` (`index_id`, `index_name`, `name`, `nodes_info`, `clusters_info`, `state`, `cluster_level`) values ('4543', 'vfeature', 'ittest-cluster', '[]', '[{\"cluster\":\"test-rpm.api.vip.com\",\"name\":\"test-rpm.api.vip.com\",\"address\":\"test-rpm.api.vip.com\",\"weight\":0,\"state\":0}]', 0, 1);";
		executeUpdate(sql);
	}

	@Test
	public void testWithClusterLevel() throws Exception {
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
}
