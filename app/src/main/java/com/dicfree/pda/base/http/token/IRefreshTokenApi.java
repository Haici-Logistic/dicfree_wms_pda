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
import com.dicfree.pda.model.bean.LoginData;

public interface IRefreshTokenApi {
    /***
     * 同步刷新token接口
     * @return
     */
    BaseResponseBean<LoginData> refreshTokenSync();

    /**
     * 判断是否刷新token url
     * @param url
     * @return
     */
    boolean isRefreshUrl(String url);
}