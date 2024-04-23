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
import java.util.List;

/**
 * @Author: shengcaiwang
 * @Description:
 * @Date: 2023/10/29
 */
public class OffShelfUndoEarlyBean implements Serializable {
    private int id;
    private String uniqueNo;
    private int totalSnCount;
    private List<ShelfInfo> shelfList;
    private List<CollectionUndoInfo> collectionUndoInfoList;
    private String collectionAreaCode;

    public String getCollectionAreaCode() {
        return collectionAreaCode;
    }

    public void setCollectionAreaCode(String collectionAreaCode) {
        this.collectionAreaCode = collectionAreaCode;
    }

    public List<CollectionUndoInfo> getCollectionUndoInfoList() {
        return collectionUndoInfoList;
    }

    public void setCollectionUndoInfoList(List<CollectionUndoInfo> collectionUndoInfoList) {
        this.collectionUndoInfoList = collectionUndoInfoList;
    }

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

    public List<ShelfInfo> getShelfList() {
        return shelfList;
    }

    public void setShelfList(List<ShelfInfo> shelfList) {
        this.shelfList = shelfList;
    }

    public static class ShelfInfo{
        private String shelfNo;
        private List<ProductDeliveryOrderItem> productDeliveryOrderItemList;

        public String getShelfNo() {
            return shelfNo;
        }

        public void setShelfNo(String shelfNo) {
            this.shelfNo = shelfNo;
        }

        public List<ProductDeliveryOrderItem> getProductDeliveryOrderItemList() {
            return productDeliveryOrderItemList;
        }

        public void setProductDeliveryOrderItemList(List<ProductDeliveryOrderItem> productDeliveryOrderItemList) {
            this.productDeliveryOrderItemList = productDeliveryOrderItemList;
        }
    }

    public static class  ProductDeliveryOrderItem{

        private String productSkuCode;

        private int offShelfCount;

        private int  totalCount;

        public String getProductSkuCode() {
            return productSkuCode;
        }

        public void setProductSkuCode(String productSkuCode) {
            this.productSkuCode = productSkuCode;
        }

        public int getOffShelfCount() {
            return offShelfCount;
        }

        public void setOffShelfCount(int offShelfCount) {
            this.offShelfCount = offShelfCount;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    }
}
