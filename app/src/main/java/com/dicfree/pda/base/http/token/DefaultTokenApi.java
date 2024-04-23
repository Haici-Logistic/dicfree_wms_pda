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


import com.dicfree.pda.base.bean.BaseResponseBean;
import com.dicfree.pda.model.bean.DeviceInfo;
import com.dicfree.pda.model.bean.LoginData;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DefaultTokenApi {
    /***
     * 登录
     */
    @POST("/api/sso/pda/login")
    Call<BaseResponseBean<LoginData>> login(@Query("username")String username, @Query("password") String password);


    /***
     * 通过code获取账户密码相关信息
     */
    @POST("/api/pda/pmall/devicePda/info")
    Call<BaseResponseBean<DeviceInfo>> getLoginInfo(@Query("code")String code);
}

