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
package com.dicfree.pda.base;

import com.blankj.utilcode.util.SPUtils;
import com.dicfree.pda.base.bean.BaseResponseBean;
import com.dicfree.pda.base.bean.BuildEnv;
import com.dicfree.pda.base.bean.GatewayFamily;
import com.dicfree.pda.base.bean.IBaseResponse;

import java.util.HashMap;
import java.util.Map;

public final class BaseConfigManager {


    private static final BaseConfigManager sInstance = new BaseConfigManager();
    public static BaseConfigManager getInstance(){
        return sInstance;
    }

    //网关环境Map
    private Map<BuildEnv, GatewayFamily> gatewayFamily = new HashMap<>();
    //错误信息Map
    private BaseErrorConverter errorConverter;
//    private Map<String, BaseResponseBean<Object>> errorMap = new HashMap<>();

    private BaseConfigManager(){}

    public void setGatewayFamily(Map<BuildEnv, GatewayFamily> gatewayFamily) {
        this.gatewayFamily = (null == gatewayFamily)? new HashMap<>() : gatewayFamily;
    }

    public Map<BuildEnv, GatewayFamily> getGatewayFamily() {
        return gatewayFamily;
    }

    public GatewayFamily getCurrentFamily() {
        BuildEnv env = BuildEnv.PRO;
        String envStr = SPUtils.getInstance().getString(BuildEnv.class.getSimpleName());
        try {
            env = BuildEnv.valueOf(envStr);
        } catch (Exception e) {
            env = BuildEnv.DEV;
        }

        GatewayFamily family = gatewayFamily.get(env);
        return (null == family)? new GatewayFamily() : family;
    }

    public void setErrorConverter(BaseErrorConverter converter) {
        this.errorConverter = converter;
    }

//    public void setErrorMap(Map<String, BaseResponseBean<Object>> errorMap) {
//        this.errorMap = (null == errorMap)? new HashMap<>() : errorMap;
//    }

    public IBaseResponse getError(String statusCode, String statusMessage) {
        if (null != errorConverter) {
            return errorConverter.getError(statusCode, statusMessage);
        }
        return new BaseResponseBean<>(statusCode, statusMessage);
    }

    public IBaseResponse getError(IBaseResponse resp) {
        if (null != errorConverter) {
            return errorConverter.getError(resp.getStatusCode(), resp.getStatusMessage());
        }
        return resp;
    }


    public interface BaseErrorConverter {
        IBaseResponse getError(String statusCode, String statusMessage);
    }
}
