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
public class OnShelfUndoBean  implements Serializable {
    private String thirdOrderNo;

    @SerializedName("productArrivalOrderItemList")
    private List<ArrivalOrderItem> productArrivalOrderItemList;

    public String getThirdOrderNo() {
        return thirdOrderNo;
    }

    public void setThirdOrderNo(String thirdOrderNo) {
        this.thirdOrderNo = thirdOrderNo;
    }


    public List<ArrivalOrderItem> getProductArrivalOrderItemList() {
        return productArrivalOrderItemList;
    }

    public void setProductArrivalOrderItemList(List<ArrivalOrderItem> productArrivalOrderItemList) {
        this.productArrivalOrderItemList = productArrivalOrderItemList;
    }

    public static class ArrivalOrderItem{
        private String thirdOrderNo;
        private int id;
        private int onShelfCount;
        private int totalCount;
        private String productCode;

        public String getThirdOrderNo() {
            return thirdOrderNo;
        }

        public void setThirdOrderNo(String thirdOrderNo) {
            this.thirdOrderNo = thirdOrderNo;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public int getOnShelfCount() {
            return onShelfCount;
        }

        public void setOnShelfCount(int onShelfCount) {
            this.onShelfCount = onShelfCount;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    }
}
