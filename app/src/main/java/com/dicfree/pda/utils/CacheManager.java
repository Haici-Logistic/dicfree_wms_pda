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
package com.dicfree.pda.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;
import com.dicfree.pda.model.bean.DeviceInfo;
import com.dicfree.pda.model.bean.LoginData;
public class CacheManager {
    private static final CacheManager sInstance = new CacheManager();
    private LoginData tokenData;
    private long localMillis;
    private DeviceInfo  deviceInfo;
    public static CacheManager getInstance(){
        return sInstance;
    }

    private CacheManager(){
        try{
            String json = SPUtils.getInstance().getString(LoginData.class.getSimpleName());
            tokenData = GsonUtils.fromJson(json, LoginData.class);
        }catch (Exception e){
            tokenData = new LoginData();
        }
        try {
            String deviceJson = SPUtils.getInstance().getString(LoginData.class.getSimpleName());
            deviceInfo = GsonUtils.fromJson(deviceJson, DeviceInfo.class);
        }catch (Exception e){
            deviceInfo = new DeviceInfo();
        }
        localMillis = 0;
    }

    /***
     * 获取accessToken
     */
    public String getAccessToken() {
        if (null == tokenData||tokenData.getAccessToken() ==null) {
            return "";
        }
        return tokenData.getAccessToken().getTokenValue();
    }

    /***
     * 获取refreshToken
     */
    public String getRefreshToken() {
        if (null == tokenData||tokenData.getRefreshToken()==null) {
            return "";
        }
        return tokenData.getRefreshToken().getTokenValue();
    }

    /***
     * 是否需要刷新token,${REFRESH_TOKEN_VALID_MILLIS}毫秒内不重新刷
     */
    public boolean isNeedRefresh() {
        long REFRESH_TOKEN_VALID_MILLIS = 5000;
        return (System.currentTimeMillis() - localMillis > REFRESH_TOKEN_VALID_MILLIS);
    }

    /***
     * 保存token数据
     */
    public void saveTokenData(String accessToken, String refreshToken) {

        if (null == tokenData) {
            tokenData = new LoginData(accessToken, refreshToken);
        } else {
            tokenData = tokenData.copyWith(accessToken, refreshToken);
        }
        SPUtils.getInstance().put(LoginData.class.getSimpleName(), GsonUtils.toJson(tokenData));
        localMillis = System.currentTimeMillis();
    }
    /***
     * 保存token数据
     */
    public void saveTokenData(LoginData.TokenInfo accessToken, LoginData.TokenInfo refreshToken) {
        saveTokenData(new LoginData(accessToken, refreshToken));
    }

    /***
     * 保存token数据
     */
    public void saveTokenData(LoginData loginData) {
        this.tokenData =loginData;
        SPUtils.getInstance().put(LoginData.class.getSimpleName(), GsonUtils.toJson(tokenData));
        localMillis = System.currentTimeMillis();
    }


    /***
     * 判断是否登录
     */
    public boolean isLogin() {
        return (!TextUtils.isEmpty(getAccessToken()) && getAccessToken().length() > 10
                && !TextUtils.isEmpty(getRefreshToken()) && getRefreshToken().length() > 10
                );
    }

    /**
     * 保存设备信息
     * @param deviceInfo
     */
    public void saveDeviceInfo(DeviceInfo deviceInfo){
        this.deviceInfo =deviceInfo;
        SPUtils.getInstance().put(DeviceInfo.class.getSimpleName(), GsonUtils.toJson(deviceInfo));
    }

    public boolean isGetDeviceInfo(){
        return deviceInfo!=null&& !TextUtils.isEmpty(deviceInfo.getKey());
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void clear(){
        deviceInfo = new DeviceInfo();
        tokenData = new LoginData();
        SPUtils.getInstance().put(DeviceInfo.class.getSimpleName(), "");
        SPUtils.getInstance().put(LoginData.class.getSimpleName(), "");
    }
}
