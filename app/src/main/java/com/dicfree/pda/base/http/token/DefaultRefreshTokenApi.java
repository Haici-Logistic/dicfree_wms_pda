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
package com.dicfree.pda.base.http.token;


import static com.dicfree.pda.base.BaseCodeConstants.ERROR_BUSY;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.dicfree.pda.base.BaseConfigManager;
import com.dicfree.pda.base.bean.BaseResponseBean;
import com.dicfree.pda.base.bean.IBaseResponse;
import com.dicfree.pda.base.http.HttpLogger;
import com.dicfree.pda.base.http.HttpManager;
import com.dicfree.pda.model.bean.DeviceInfo;
import com.dicfree.pda.model.bean.LoginData;
import com.dicfree.pda.utils.CacheManager;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


public class DefaultRefreshTokenApi implements IRefreshTokenApi {

    @Override
    public BaseResponseBean<LoginData> refreshTokenSync() {
        LogUtils.dTag(HttpLogger.TAG,"DefaultRefreshTokenApi========>"+ DeviceUtils.getMacAddress());
        Call<BaseResponseBean<DeviceInfo>> loginInfoCall = HttpManager.getInstance().getServiceApi(DefaultTokenApi.class).getLoginInfo(DeviceUtils.getMacAddress());
        try {
            Response<BaseResponseBean<DeviceInfo>> response = loginInfoCall.execute();
            BaseResponseBean<DeviceInfo> body = response.body();
            if(body!=null&&body.isSuccess()&&body.getData()!=null){
                DeviceInfo deviceInfo =body.getData();
                Call<BaseResponseBean<LoginData>> call = HttpManager.getInstance().getServiceApi(DefaultTokenApi.class).login(deviceInfo.getCode(),deviceInfo.getKey());
                Response<BaseResponseBean<LoginData>> loginRespose = call.execute();
                BaseResponseBean<LoginData> loginBody = loginRespose.body();
                if (null != loginBody) {
                    return loginBody;
                } else if (null != response.errorBody()){
                    ResponseBody errorBody = response.errorBody();
                    String errorStr = errorBody.string();
                    Type type = new TypeToken<BaseResponseBean<LoginData>>(){}.getType();
                    return GsonUtils.fromJson(errorStr, type);
                }
            }else{
                ResponseBody errorBody = response.errorBody();
                String errorStr = errorBody.string();
                Type type = new TypeToken<BaseResponseBean<LoginData>>(){}.getType();
                return GsonUtils.fromJson(errorStr, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        IBaseResponse error = BaseConfigManager.getInstance().getError(ERROR_BUSY, ERROR_BUSY);
        return new BaseResponseBean<>(error.getStatusCode(), error.getStatusMessage(), null);
    }

    @Override
    public boolean isRefreshUrl(String url) {
        if (null == url) {
            return false;
        }
        return( url.endsWith("/sso/pda/login")|| url.endsWith("/pda/pmall/devicePda/info"))&& CacheManager.getInstance().isLogin();
    }
}
