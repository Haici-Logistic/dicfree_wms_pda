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
package com.dicfree.pda;

import static com.dicfree.pda.base.BaseCodeConstants.ERROR_BUSY;
import static com.dicfree.pda.base.BaseCodeConstants.ERROR_NO_NETWORK;
import static com.dicfree.pda.base.BaseCodeConstants.ERROR_TIMEOUT;
import static com.dicfree.pda.base.BaseCodeConstants.STATUS_CODE_ACCESS_TOKEN_INVALID;
import static com.dicfree.pda.base.BaseCodeConstants.STATUS_CODE_NOT_LOGIN;

import android.util.Log;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.dicfree.pda.base.BaseApplication;
import com.dicfree.pda.base.BaseConfigManager;
import com.dicfree.pda.base.bean.BaseResponseBean;
import com.dicfree.pda.base.bean.BuildEnv;
import com.dicfree.pda.base.bean.GatewayConfig;
import com.dicfree.pda.base.bean.GatewayFamily;
import com.dicfree.pda.base.http.HttpConfig;
import com.dicfree.pda.base.http.HttpManager;
import com.dicfree.pda.base.http.interceptor.HeaderInterceptor;
import com.dicfree.pda.base.http.interceptor.NetworkMonitorInterceptor;
import com.dicfree.pda.base.http.interceptor.TokenInterceptor;
import com.dicfree.pda.base.http.monitor.LiveNetworkMonitor;
import com.dicfree.pda.utils.CacheManager;
import com.dicfree.pda.utils.ScanBarcodeManager;
import com.dicfree.pda.utils.SoundPoolUtil;
import com.dicfree.pda.utils.WeightManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

//import com.alibaba.android.arouter.launcher.ARouter;

/**
 * @Author: shengcaiwang
 * @Description:
 * @Date: 2023/10/29
 */
public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        ScanBarcodeManager.getInstance().init();
        SoundPoolUtil.getInstance().initSoundPool(this);
        CacheManager.getInstance();
        GatewayConfig currentGateway = BaseConfigManager.getInstance().getCurrentFamily().getProjectGateway();
        HttpManager.getInstance().initDefaultConfig(new HttpConfig().setShowLog(AppUtils.isAppDebug())
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(new NetworkMonitorInterceptor(new LiveNetworkMonitor(this)))
                .addInterceptor(new TokenInterceptor())
                .addCallAdapter(RxJava3CallAdapterFactory.create())
                .setBaseUrl(currentGateway.getBaseUrl()));

    }


    @NonNull
    @Override
    protected Map<BuildEnv, GatewayFamily> getGatewayFamily() {
        HashMap<BuildEnv, GatewayFamily> family = new HashMap<>();
        family.put(BuildEnv.DEV, new GatewayFamily().setEnv(BuildEnv.DEV).setProjectGateway(new GatewayConfig().setBaseUrl(ProjectConstants.Perf.PROJECT_BASE_URL)));
        family.put(BuildEnv.TEST, new GatewayFamily().setEnv(BuildEnv.TEST).setProjectGateway(new GatewayConfig().setBaseUrl(ProjectConstants.Uat.PROJECT_BASE_URL)));
        family.put(BuildEnv.PRO, new GatewayFamily().setEnv(BuildEnv.PRO).setProjectGateway(new GatewayConfig().setBaseUrl(ProjectConstants.Pro.PROJECT_BASE_URL)));
        return family;
    }

    @NonNull
    @Override
    protected BaseConfigManager.BaseErrorConverter getErrorConverter() {
        return (statusCode, statusMessage) -> {
            if (ERROR_NO_NETWORK.equals(statusCode)) {
                return new BaseResponseBean(statusCode, getString(R.string.error_no_network));
            } else if (ERROR_TIMEOUT.equals(statusCode)) {
                return new BaseResponseBean(statusCode, getString(R.string.error_timeout));
            } else if (ERROR_BUSY.equals(statusCode)) {
                return new BaseResponseBean(statusCode, getString(R.string.error_busy));
            } else if (STATUS_CODE_ACCESS_TOKEN_INVALID.equals(statusCode)        //accessToken凭证无效
                    || STATUS_CODE_NOT_LOGIN.equals(statusCode)     //用户已登出或在其它设备已登录，本凭证已失效，请重新登录
            ) {
                return new BaseResponseBean(statusCode, getString(R.string.error_token_expired));
            }
            return new BaseResponseBean<>(statusCode, statusMessage);
        };
    }
}
