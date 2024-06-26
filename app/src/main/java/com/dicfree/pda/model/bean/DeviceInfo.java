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
package com.dicfree.pda.model.bean;

/**
 * @Author: shengcaiwang
 * @Description:
 * @Date: 2023/10/29
 */
public class DeviceInfo {
    /**
     * 设备唯一编码 暂时用mac地址
     */
    private String code;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 密码
     */
    private String key;

    private String shelfAreaCode;

    public DeviceInfo() {

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getShelfAreaCode() {
        return shelfAreaCode;
    }

    public void setShelfAreaCode(String shelfAreaCode) {
        this.shelfAreaCode = shelfAreaCode;
    }
}
