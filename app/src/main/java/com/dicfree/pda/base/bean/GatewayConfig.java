/**
 * Copyright 2024 Wuhan Haici Technology Co., Ltd 
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dicfree.pda.base.bean;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class GatewayConfig {
    private String desc = "";
    private String projectId;
    private String projectKey;
    private String baseUrl;
    private String fileUpLoadUrl;
    private String serverCA;
    private String clientCA;
    private String clientKey;
    private List<String> pathList;

    public GatewayConfig() {
        this.projectId = "";
        this.projectKey = "";
        this.baseUrl = "";
        this.fileUpLoadUrl = "";
        this.serverCA = "";
        this.clientCA = "";
        this.clientKey = "";
        this.pathList = new ArrayList();
    }

    public String getDesc() {
        return this.desc;
    }

    public GatewayConfig setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public GatewayConfig setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public String getProjectKey() {
        return this.projectKey;
    }

    public GatewayConfig setProjectKey(String projectKey) {
        this.projectKey = projectKey;
        return this;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public GatewayConfig setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public String getServerCA() {
        return this.serverCA;
    }

    public GatewayConfig setServerCA(String serverCA) {
        this.serverCA = serverCA;
        return this;
    }

    public String getClientCA() {
        return this.clientCA;
    }

    public GatewayConfig setClientCA(String clientCA) {
        this.clientCA = clientCA;
        return this;
    }

    public String getClientKey() {
        return this.clientKey;
    }

    public GatewayConfig setClientKey(String clientKey) {
        this.clientKey = clientKey;
        return this;
    }

    public String getFileUpLoadUrl() {
        return fileUpLoadUrl;
    }

    public GatewayConfig setFileUpLoadUrl(String fileUpLoadUrl) {
        this.fileUpLoadUrl = fileUpLoadUrl;
        return this;
    }

    public List<String> getPathList() {
        return this.pathList;
    }

    public GatewayConfig setPathList(List<String> pathList) {
        this.pathList = pathList;
        return this;
    }

    public String toJson() {
        return (new Gson()).toJson(this);
    }
}
