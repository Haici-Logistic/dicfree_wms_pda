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

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.dicfree.pda.base.bean.BaseResponseBean;
import com.dicfree.pda.base.bean.IBaseResponse;
import com.dicfree.pda.base.http.token.DefaultRefreshTokenApi;
import com.dicfree.pda.base.http.token.IRefreshTokenApi;
import com.dicfree.pda.base.http.token.RefreshTokenExpiredException;
import com.dicfree.pda.model.bean.LoginData;
import com.dicfree.pda.utils.CacheManager;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
public class TokenInterceptor implements Interceptor {
    private static final Object refreshLock = new Object();

    @NonNull
    private IRefreshTokenApi refreshApi;

    public TokenInterceptor() {
        this.refreshApi = new DefaultRefreshTokenApi();
    }

    public TokenInterceptor(@NonNull IRefreshTokenApi refreshApi) {
        this.refreshApi = refreshApi;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (refreshApi.isRefreshUrl(request.url().toString())) {
            return response;
        }
        String attemptRefreshToken = request.header("_attemptRefreshToken");

        try {
            if (null != response.body() && null == attemptRefreshToken) {
                long byteCount = response.body().contentLength();
                if (byteCount < 0) {
                    byteCount = Long.MAX_VALUE;
                }
                ResponseBody responseBody = response.peekBody(byteCount);
                String jsonString = responseBody.string();
                IBaseResponse baseResponseBean = GsonUtils.fromJson(jsonString, BaseResponseBean.class);
                if (baseResponseBean.isTokenExpired()) {
                    CacheManager.getInstance().clear();
                    synchronized (refreshLock) {
                        if (CacheManager.getInstance().isNeedRefresh()) {
                            BaseResponseBean<LoginData> tokenResp = refreshApi.refreshTokenSync();
                            if (tokenResp.isSuccess() && null != tokenResp.getData()) {
                                CacheManager.getInstance().saveTokenData(
                                        tokenResp.getData().getAccessToken(),
                                        tokenResp.getData().getRefreshToken());
                            } else if (tokenResp.isTokenExpired()) {
                                ///refreshToken失效，抛出异常，拉起登录页面
                                LogUtils.d("RefreshTokenExpiredException,"+chain.request().url()+" refreshToken expired=>"+tokenResp.getStatusCode());
                                throw new RefreshTokenExpiredException(baseResponseBean);
                            }
                        }
                        String token = CacheManager.getInstance().getAccessToken();
                        if (TextUtils.isEmpty(token)) {
                            token = "access token is empty";
                        }
                        if (!token.startsWith("Bearer ")) {
                            token = "Bearer " + token;
                        }

                        response.close();
                        Request newRequest = request.newBuilder()
                                .removeHeader("Authorization")
                                .removeHeader("_attemptRefreshToken")
                                .addHeader("Authorization",  token)
                                .addHeader("_attemptRefreshToken", String.valueOf(1))
                                .build();
                        return chain.proceed(newRequest);

                    }
                }
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return response;
    }
}
