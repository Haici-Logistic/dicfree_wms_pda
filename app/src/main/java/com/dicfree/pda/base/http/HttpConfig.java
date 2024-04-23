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
package com.dicfree.pda.base.http;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Interceptor;
import retrofit2.CallAdapter;


public class HttpConfig {
    private boolean showLog;
    private String baseUrl;
    private String serverCA;
    private String clientCA;
    private String clientKey;
    private int connectTimeout;
    private int readTimeout;
    private int writeTimeout;
    private String pinnerPattern;
    private List<Interceptor> interceptorList = new ArrayList<>(10);
    private List<Interceptor> networkInterceptorList = new ArrayList<>(10);
    private List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>(2);

    public HttpConfig() {
        init();
    }

    private HttpConfig init(){
        showLog = true;
        baseUrl = "";
        serverCA = "";
        clientCA = "";
        clientKey = "";
        connectTimeout = 10000;
        readTimeout = 10000;
        writeTimeout = 10000;
        interceptorList.clear();
        networkInterceptorList.clear();
        callAdapterFactories.clear();
        return this;
    }

    public HttpConfig addInterceptor(Interceptor interceptor){
        if (null != interceptor) {
            interceptorList.add(interceptor);
        }
        return this;
    }

    public HttpConfig addNetworkInterceptor(Interceptor interceptor){
        if (null != interceptor) {
            networkInterceptorList.add(interceptor);
        }
        return this;
    }

    public HttpConfig addCallAdapter(CallAdapter.Factory factory){
        if (null != callAdapterFactories) {
            callAdapterFactories.add(factory);
        }
        return this;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public HttpConfig setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public String getServerCA() {
        return serverCA;
    }

    public HttpConfig setServerCA(String serverCA) {
        this.serverCA = serverCA;
        return this;
    }

    public String getClientCA() {
        return clientCA;
    }

    public HttpConfig setClientCA(String clientCA) {
        this.clientCA = clientCA;
        return this;
    }

    public String getClientKey() {
        return clientKey;
    }

    public HttpConfig setClientKey(String clientKey) {
        this.clientKey = clientKey;
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public HttpConfig setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public HttpConfig setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public HttpConfig setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public List<Interceptor> getInterceptorList() {
        return interceptorList;
    }

    public List<Interceptor> getNetworkInterceptorList() {
        return networkInterceptorList;
    }

    public List<CallAdapter.Factory> getCallAdapterFactories() {
        return callAdapterFactories;
    }

    public String getPinnerPattern() {
        return pinnerPattern;
    }

    public HttpConfig setPinnerPattern(String pinnerPattern) {
        this.pinnerPattern = pinnerPattern;
        return this;
    }

    public boolean isShowLog() {
        return showLog;
    }

    public HttpConfig setShowLog(boolean showLog) {
        this.showLog = showLog;
        return this;
    }
}