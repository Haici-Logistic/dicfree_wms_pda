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


public interface BaseCodeConstants {
    String STATUS_MESSAGE_KEY="errorMessage";
    String STATUS_MESSAGE_SUCCESS_KEY="errorMessage";
    String STATUS_MESSAGE_SUCCESS_VALUE="success";
    String STATUS_DATA_KEY="data";
    String STATUS_CODE_KEY="errorCode";
    String STATUS_CODE_SUCCESS = "200";
    String STATUS_CODE_FAIL = "201";
    String STATUS_CODE_DEVICE_NOT_REGISTER="wms_error_device_pda_not_found";//设备没有注册
    String STATUS_CODE_ACCESS_TOKEN_INVALID = "invalid_token";   //accessToken凭证无效
    String STATUS_CODE_NOT_LOGIN="NOT_LOGIN_OR_LOGIN_ERROR";//需要重新登录
    String STATUS_CODE_PASSWORD_ERROR="certificate_failed";//密码错误

    String ERROR_NO_NETWORK = "error_no_network";
    String ERROR_TIMEOUT = "error_timeout";
    String ERROR_BUSY = "error_busy";


}
