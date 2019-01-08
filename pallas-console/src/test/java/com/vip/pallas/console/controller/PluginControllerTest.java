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

package com.vip.pallas.console.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vip.pallas.console.BaseControllerTest;
import com.vip.pallas.console.vo.ClusterVO;
import com.vip.pallas.console.vo.PluginAction;
import com.vip.pallas.console.vo.RemovePlugin;
import com.vip.pallas.mybatis.entity.PluginRuntime;
import com.vip.pallas.mybatis.entity.PluginUpgrade;
import com.vip.pallas.mybatis.repository.PluginRuntimeRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PluginControllerTest extends BaseControllerTest {
    private static String clusterId = null;
    private static Long id = null;
    private static String pluginName = "ivyTestPlugin";
    private static String pluginVersion = "1.0.0";
    private static String packagePath = "";
    private static PluginAction action = new PluginAction();
    private static Long removeId = null;

    @Autowired
    private PluginRuntimeRepository runtimeRepository;

    @Test
    public void test1nsertCluster()throws Exception {
        ClusterVO clusterVO = insertClusterToTable();
        clusterId = clusterVO.getClusterId();
    }

    @Test
    public void test21FieUpload() throws Exception {

    	String path = BaseControllerTest.class.getClassLoader().getResource("").getPath() + "request/ivyTestPlugin-1.0.0.zip";
        Map resultMap = uploadFile("/plugin/upgrade/fileUpload.json?clusterId=" + clusterId + "&pluginName=" + pluginName + "&pluginVersion=" + pluginVersion, path);
        assertThat(resultMap).containsEntry("status", HttpStatus.OK.value());
        packagePath = (String)resultMap.get("data");
        assertThat(packagePath).isNotEmpty();

    }

    @Test
    public void test22AddPlugin() throws Exception {
        PluginUpgrade pluginUpgrade = new PluginUpgrade();
        pluginUpgrade.setClusterId(clusterId);
        pluginUpgrade.setPluginName(pluginName);
        pluginUpgrade.setPluginVersion(pluginVersion);
        pluginUpgrade.setPluginType(0);
        pluginUpgrade.setNote("it is a test");
        pluginUpgrade.setPackagePath(packagePath);
        assertThat(callRestApi("/plugin/upgrade/add.json", JSON.toJSONString(pluginUpgrade)));
        //再次插入报500
        // assertThat(callRestApi("/plugin/upgrade/add.json", JSON.toJSONString(pluginUpgrade))).containsEntry("status", 500);
    }

    @Test
    public void test23UpgradeList() throws Exception {
        //get update id

        Map resultMap = callGetApi("/plugin/upgrade/list.json?currentPage=1&pageSize=100&pluginName=" + pluginName);
        JSONArray jsonArray = ((JSONObject) resultMap.get("data")).getJSONArray("list");
        id = jsonArray.getJSONObject(0).getLong("id");
        assertThat(id).isGreaterThanOrEqualTo(1L);
        action.setPluginUpgradeId(id);
    }

    @Test
    public void test24DownloadAction() throws Exception {
        action.setAction("download");
        assertThat(callRestApi("/plugin/upgrade/action.json", JSON.toJSONString(action))).isNull();
    }

    @Test
    public void test25Update() throws Exception {
        action.setAction("upgrade");
        assertThat(callRestApi("/plugin/upgrade/action.json", JSON.toJSONString(action))).isNull();
    }

    @Test
    public void test26MarkComplete() throws Exception {
        action.setAction("done");
        assertThat(callRestApi("/plugin/upgrade/action.json", JSON.toJSONString(action))).isNull();
    }

    @Test
    public void test27Recall() throws Exception{
        test22AddPlugin();
        test23UpgradeList();
        action.setAction("recall");
        assertThat(callRestApi("/plugin/upgrade/action.json", JSON.toJSONString(action))).isNull();
    }

    @Test
    public void test28Deny() throws Exception {
        test22AddPlugin();
        test23UpgradeList();
        action.setAction("deny");
        assertThat(callRestApi("/plugin/upgrade/action.json", JSON.toJSONString(action))).isNull();
    }

    @Test
    public void test41PluginStateList() throws Exception {
        Map resultMap = callGetApi("/plugin/runtime/list.json?currentPage=1&pageSize=10&pluginName" + pluginName);
        JSONArray jsonArray = ((JSONObject) resultMap.get("data")).getJSONArray("list");
        removeId = jsonArray.getJSONObject(0).getLong("id");
        assertThat(removeId).isGreaterThanOrEqualTo(1L);
    }

    @Test
    public void test42remove() throws Exception {
        RemovePlugin removePlugin = new RemovePlugin();
        removePlugin.setClusterId(clusterId);
        removePlugin.setPluginName(pluginName);
        removePlugin.setPluginUpgradeId(removeId);
        removePlugin.setPluginVersion("");
        assertThat(callRestApi("/plugin/remove.json", JSON.toJSONString(removePlugin))).isNull();

        //delete plugin_runtime
        List<PluginRuntime> availableNodes = runtimeRepository.findByClusterAndPluginName(clusterId, pluginName);
        for(PluginRuntime pluginRuntime : availableNodes) {
            runtimeRepository.deleteByPrimaryKey(pluginRuntime.getId());
        }

    }

    @AfterClass
    public static void cleanData() throws Exception{
        assertThat(callRestApi("/cluster/delete/id.json", "{\"clusterId\": \"" + clusterId + "\"}")).isNull();
    }
}