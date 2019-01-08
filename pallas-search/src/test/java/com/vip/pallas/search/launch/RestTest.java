package com.vip.pallas.search.launch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.vip.pallas.search.BasePallasSearchTest;

public class RestTest extends BasePallasSearchTest {
	
	@Test
	public void testUpdateRouting() throws Exception {
		assertThat(callRestApiAndReturnString("/_py/update_routing", "")).isEqualTo("rules updated.");
	}
	
	@Test
	public void test503() throws Exception {
		assertThat(callRestApiAndReturnResponse("/url/not_exist", "").getStatusLine().getStatusCode()).isGreaterThanOrEqualTo(400);
	}
	

}
