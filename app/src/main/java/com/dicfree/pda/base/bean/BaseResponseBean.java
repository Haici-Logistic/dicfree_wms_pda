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

import static com.dicfree.pda.base.BaseCodeConstants.STATUS_CODE_ACCESS_TOKEN_INVALID;
import static com.dicfree.pda.base.BaseCodeConstants.STATUS_CODE_DEVICE_NOT_REGISTER;
import static com.dicfree.pda.base.BaseCodeConstants.STATUS_CODE_FAIL;
import static com.dicfree.pda.base.BaseCodeConstants.STATUS_CODE_NOT_LOGIN;
import static com.dicfree.pda.base.BaseCodeConstants.STATUS_CODE_PASSWORD_ERROR;
import static com.dicfree.pda.base.BaseCodeConstants.STATUS_CODE_SUCCESS;

import java.io.Serializable;


public class BaseResponseBean<T> implements IBaseResponse, Serializable {

    private String errorCode;
    private  String errorMessage;

    protected T data;//数据

    public BaseResponseBean(IBaseResponse resp) {
        if (null == resp) {
            this.errorCode = "";
            this.errorMessage = "";
        } else {
            this.errorCode = resp.getStatusCode();
            this.errorMessage = resp.getStatusMessage();
        }
    }

    public BaseResponseBean(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
    public BaseResponseBean(String errorCode, T data) {
        this.errorCode = errorCode;
        this.data =data;
    }

    public BaseResponseBean(String errorCode, String errorMessage, T data) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean isSuccess() {
        return STATUS_CODE_SUCCESS.equals(errorCode);
    }

    @Override
    public boolean isTokenExpired() {
        return (STATUS_CODE_ACCESS_TOKEN_INVALID.equals(errorCode)  //accessToken凭证无效
                || STATUS_CODE_NOT_LOGIN.equals(errorCode)	 //accessToken凭证已经过期
                ||STATUS_CODE_PASSWORD_ERROR.equals(errorCode)//
        );
    }

    @Override
    public String getStatusCode() {
        return errorCode;
    }

    @Override
    public String getStatusMessage() {
        return errorMessage;
    }


    @Override
    public boolean isDeviceRegister() {
        return STATUS_CODE_DEVICE_NOT_REGISTER.equals(errorCode);
    }
}
