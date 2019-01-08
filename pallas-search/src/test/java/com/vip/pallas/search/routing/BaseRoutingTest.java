package com.vip.pallas.search.routing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;

import com.vip.pallas.search.BasePallasSearchTest;
import com.vip.pallas.search.service.PallasCacheFactory;
import com.vip.pallas.search.utils.JdbcUtil;

public abstract class BaseRoutingTest extends BasePallasSearchTest {

	abstract void setTestTargetGroup() throws SQLException;

	@Before
	public void zBeforeTest() throws SQLException {
		cleanTestTargetGroup();
		setTestTargetGroup();

		cleanTestIndexRouting();
		setTestIndexRouting();

		waitCacheRefreshed();
	}

	protected void cleanTestIndexRouting() throws SQLException {
		 executeUpdate("delete from index_routing where index_name = 'vfeature'");
	}

	protected void cleanTestTargetGroup() throws SQLException {
		executeUpdate("delete from index_routing_target_group where index_name = 'vfeature'");
	}

	protected void setTestIndexRouting() throws SQLException {
		String sql = "insert into `pallas_console`.`index_routing` (`index_id`, `index_name`, `routings_info`, `create_time`, `update_time`, `is_deleted`) values ('4543', 'vfeature', '[{\"name\":\"matchall\",\"conditionRelation\":\"AND\",\"conditions\":[{\"paramType\":\"header\",\"paramName\":\"business_code\",\"paramValue\":\"ittest\",\"exprOp\":\"=\"}],\"targetGroups\":[{\"id\":" +
				getTestTargetGroupId() + ",\"weight\":1}],\"enable\":true}]', '2017-11-06 17:50:53', '2017-11-17 19:25:55', '0');";

		executeUpdate(sql);
	}

	protected int getTestTargetGroupId() throws SQLException {
		Connection connection = getConnection();

		String sql = "select id from index_routing_target_group where index_name = 'vfeature'";

		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery(sql);

		PreparedStatement pstmt = null;

		try{
			if(rs.next()) {
				return rs.getInt(1);
			}
		}finally {
			JdbcUtil.free(connection, pstmt, rs);
			st.close();
		}

		return -1;
	}

	private Connection getConnection() throws SQLException {
		return JdbcUtil.getConnection(
				"jdbc:mysql://10.199.203.185:3306/pallas_console?useUnicode=true&amp;characterEncoding=utf-8&amp;allowMultiQueries=true;",
				"root", "123456");
	}

	protected void executeUpdate(String sql) throws SQLException {
		Connection connection = getConnection();

		PreparedStatement pstmt = null;

		try{
			pstmt = connection.prepareStatement(sql);
			pstmt.executeUpdate();
			pstmt.close();
		}finally {
			JdbcUtil.free(connection, pstmt, null);
		}
	}

	protected static void waitCacheRefreshed(){
		PallasCacheFactory.getCacheService().invalidateCache();
	}
}
