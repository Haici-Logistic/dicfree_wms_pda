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

import com.dicfree.pda.base.http.convert.MyGsonConverterFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpManager {
    private static HttpManager sInstance = new HttpManager();
    private final Map<String, Object> apiMap = new ConcurrentHashMap<>();
    private Retrofit defaultRetrofit;
    private HttpConfig defaultConfig;

    public static HttpManager getInstance(){
        return sInstance;
    }

    private HttpManager(){}

    public void initDefaultConfig(HttpConfig config) {
        this.defaultConfig = config;
    }

    public HttpConfig getDefaultConfig() {
        return defaultConfig;
    }

    private Retrofit buildRetrofit(HttpConfig config) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(config.getWriteTimeout(), TimeUnit.MILLISECONDS);

        for (Interceptor interceptor : config.getNetworkInterceptorList()){
            builder.addNetworkInterceptor(interceptor);
        }
        for (Interceptor interceptor : config.getInterceptorList()){
            builder.addInterceptor(interceptor);
        }
        if (config.isShowLog()) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logInterceptor);
        }
        builder.retryOnConnectionFailure(true);


        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl(config.getBaseUrl())
                .addConverterFactory(MyGsonConverterFactory.create());
        for (CallAdapter.Factory factory : config.getCallAdapterFactories()){
            retrofitBuilder.addCallAdapterFactory(factory);
        }

        return retrofitBuilder.build();
    }

    public <T> T getServiceApi(Class<T> clazz) {
//        if (SERVICE_MAP.containsKey(clazz.getName())) {
//            return (T) SERVICE_MAP.get(clazz.getName());
//        } else {
//            if (null == retrofit){
//                retrofit = buildRetrofit();
//            }
//            T service = retrofit.create(clazz);
//            SERVICE_MAP.put(clazz.getName(), service);
//            return service;
//        }

        T obj = (T) apiMap.get(clazz.toString());
        if (obj != null) {
            return obj;
        }
        synchronized (apiMap) {
            obj = (T) apiMap.get(clazz.toString());
            if (obj == null) {
                if (null == defaultRetrofit){
                    defaultRetrofit = buildRetrofit(defaultConfig);
                }
                obj = defaultRetrofit.create(clazz);
                apiMap.put(clazz.toString(), obj);
            }
        }
        return obj;
    }

    ///清除所有的apiMap
    public void clear() {
        defaultRetrofit = null;
        apiMap.clear();
    }

}
