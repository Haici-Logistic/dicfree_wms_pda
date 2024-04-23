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

import java.io.Serializable;

/**
 * @Author: shengcaiwang
 * @Description:
 * @Date: 2023/11/19
 */
public class OrderInfo implements Serializable {
    private int id;
    /**
     *  OO(一品一件)/OM(一品多件)/MM(多品多件)
     */
    private String type;
    private String waybill;

    private String basketNo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWaybill() {
        return waybill;
    }

    public void setWaybill(String waybill) {
        this.waybill = waybill;
    }

    public String getBasketNo() {
        return basketNo;
    }

    public void setBasketNo(String basketNo) {
        this.basketNo = basketNo;
    }
}
