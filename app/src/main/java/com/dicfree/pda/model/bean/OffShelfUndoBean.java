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

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: shengcaiwang
 * @Description:
 * @Date: 2023/10/29
 */
public class OffShelfUndoBean implements Serializable {
    /**
     *   "id": 2,
     *             "uniqueNo": "W1699954200142",
     *             "totalSnCount": 17
     */

    private int id;

    private String uniqueNo;

    private int totalSnCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUniqueNo() {
        return uniqueNo;
    }

    public void setUniqueNo(String uniqueNo) {
        this.uniqueNo = uniqueNo;
    }

    public int getTotalSnCount() {
        return totalSnCount;
    }

    public void setTotalSnCount(int totalSnCount) {
        this.totalSnCount = totalSnCount;
    }
}
