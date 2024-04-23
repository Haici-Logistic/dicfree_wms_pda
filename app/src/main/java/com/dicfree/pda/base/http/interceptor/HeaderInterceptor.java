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
package com.dicfree.pda.base.http.interceptor;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;
import com.dicfree.pda.base.http.HttpLogger;
import com.dicfree.pda.base.http.token.DefaultRefreshTokenApi;
import com.dicfree.pda.base.http.token.IRefreshTokenApi;
import com.dicfree.pda.utils.CacheManager;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import kotlin.Pair;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;


public class HeaderInterceptor implements Interceptor {

    @NonNull
    private IRefreshTokenApi refreshTokenApi;
    private static final String TAG = HeaderInterceptor.class.getSimpleName();

    public HeaderInterceptor() {
        this.refreshTokenApi = new DefaultRefreshTokenApi();
    }

    public HeaderInterceptor(@NonNull IRefreshTokenApi refreshTokenApi) {
        this.refreshTokenApi = refreshTokenApi;
    }


    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<
        builder.addHeader("Accept", "application/json; charset=utf-8");
        String token = CacheManager.getInstance().getAccessToken();
        if (!TextUtils.isEmpty(token)&&!token.startsWith("Bearer ")) {
            token = "Bearer " + token;
        }
        if(!TextUtils.isEmpty(token)){
            builder.addHeader("Authorization", token);
        }

//        String projectKey = BaseConfigManager.getInstance().getCurrentFamily().getProjectGateway().getProjectKey();
//        String ts = String.valueOf(System.currentTimeMillis());
//        String sign = hmacSHA256(signSource(request, ts), projectKey);
//        builder.addHeader("x-pateo-timestamp", ts);
//        builder.addHeader("x-pateo-signature", sign);
        request = builder.build();
        showMessage(request);
        return chain.proceed(request);
    }

    private void showMessage(Request request) {
        if (null != request.url().encodedQuery()) {
            Headers headers = request.headers();
            for (Pair<? extends String, ? extends String> header : headers) {
                LogUtils.iTag(HttpLogger.TAG, "headers:"+header);
            }
            for (String key : request.url().queryParameterNames()) {
                String value = request.url().queryParameter(key);
                LogUtils.iTag(HttpLogger.TAG, "queryParams:"+key+"="+value);
            }
        }
    }

    /**
     * x-pateo-signature source string
     *
     * @param request the Request
     * @return String
     */
    private String signSource(Request request, String ts) {
        String method = request.method().toUpperCase();
        String path = request.url().encodedPath();
        String contentMD5 = "";

        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        Map<String, String> sourceMap = new TreeMap();
        //query参数
        if (null != request.url().encodedQuery()) {
            for (String key : request.url().queryParameterNames()) {
                sourceMap.put(key, request.url().queryParameter(key));
            }
        }

        if (null != request.body()) {
            //form body数据参与签名
            if (null != request.body() && null != request.body().contentType() && "application/x-www-form-urlencoded".equals(request.body().contentType().toString()) && request.body() instanceof FormBody) {
                FormBody body = (FormBody) request.body();
                for (int index = 0; index < body.size(); index++) {
                    sourceMap.put(body.encodedName(index), body.encodedValue(index));
                }
            }
            //json body 取md5
            if (null != request.body() && null != request.body().contentType() && request.body().contentType().toString().contains("application/json")) {
                try {
                    final Request copy = request.newBuilder().build();
                    final Buffer buffer = new Buffer();
                    copy.body().writeTo(buffer);
                    byte[] bodyBytes = buffer.readByteArray();
                    contentMD5 = EncryptUtils.encryptMD5ToString(bodyBytes).toLowerCase();
                } catch (final IOException e) {
                }
            }
        }

        String source = method + "&" + path + "&" + "x-pateo-timestamp=" + ts;
        for (Map.Entry<String, String> s : sourceMap.entrySet()) {
            String key = s.getKey();
            String value = s.getValue();
            source = source + "&" + key + "=" + value;
        }
        if (!TextUtils.isEmpty(contentMD5)) {
            source = source + "&contentMD5=" + contentMD5;
        }
        Log.i(this.getClass().getSimpleName(), "source=" + source);
        return source;
    }

    /**
     * Convert string to HmacSHA256
     *
     * @param source,key
     * @return String
     */
    private String hmacSHA256(String source, String key) {
        String signature = EncryptUtils.encryptHmacSHA256ToString(source, key).toLowerCase();
        Log.i(this.getClass().getSimpleName(), "key=" + key + ",signature=" + signature);
        return signature;
    }
}