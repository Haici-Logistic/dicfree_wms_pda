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
package com.dicfree.pda.model;

import com.dicfree.pda.base.bean.BaseResponseBean;
import com.dicfree.pda.model.bean.CollectionUndoInfo;
import com.dicfree.pda.model.bean.DeviceInfo;
import com.dicfree.pda.model.bean.LoginData;
import com.dicfree.pda.model.bean.OffShelfReqParams;
import com.dicfree.pda.model.bean.OffShelfUndoBean;
import com.dicfree.pda.model.bean.OffShelfUndoEarlyBean;
import com.dicfree.pda.model.bean.OnShelfUndoBean;
import com.dicfree.pda.model.bean.OrderInfo;
import com.dicfree.pda.model.bean.ProductInfo;
import com.dicfree.pda.model.bean.SnInboundRequest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @Author: shengcaiwang
 * @Description:
 * @Date: 2023/10/29
 */
public interface ServerApi {
    /***
     * 通过code获取账户密码相关信息
     */
    @POST("/api/pda/pmall/devicePda/info")
    Observable<BaseResponseBean<DeviceInfo>> getLoginInfo(@Query("code")String code);

    /***
     * 登录
     */
    @POST("/api/sso/pda/login")
    Observable<BaseResponseBean<LoginData>> login(@Query("username")String username, @Query("password") String password);

    /**
     *
     * @return
     */
    @POST("/api/pda/productArrivalOrder/onShelfUndoListAll")
    Observable<BaseResponseBean<List<OnShelfUndoBean>>> getOnShelfUndoList();

    /**
     *上架
     * @return
     */
    @POST("/api/pda/productArrivalOrder/snOnShelf")
    Observable<BaseResponseBean<String>> sumitOnShelf(@Query("productCode")String productCode, @Query("shelfNo") String shiftNo);

    /**
     * 获取待上架的商品
     * @return
     */
    @POST("/api/pda/productArrivalOrder/onShelfUndoListCount")
    Observable<BaseResponseBean<Integer>> getOnShelfUndoCount();

    /**
     * 未完成的集货任务统计
     * @return
     */
    @POST("/api/pda/productWaveTask/offShelfUndoCount")
    Observable<BaseResponseBean<Integer>> getOffShelfUndoCount();


    /**
     * 未完成的集货任务列表
     * @return
     */
    @POST("/api/pda/productWaveTask/offShelfUndoList")
    Observable<BaseResponseBean<List<OffShelfUndoBean>>> getOffShelfUndoList();


    /**
     * 最早未完成的波次任务
     * @return
     */
    @POST("/api/pda/productWaveTask/offShelfUndoDetail")
    Observable<BaseResponseBean<OffShelfUndoEarlyBean>> getOffShelfUndoEarliestList(@Query("id")Integer id);


    /**
     * 商品下架
     * @return
     */
    @POST("/api/pda/productWaveTask/snOffShelf")
    Observable<BaseResponseBean<String>> offShelf(@Query("id") Integer id,@Query("shelfNo") String shelfNo,@Query("productCode") String productCode);


    /**
     * 已完成下架但未集货的波次列表
     * @return
     */
    @POST("/api/pda/productWaveTask/collectionUndoList")
    Observable<BaseResponseBean<List<CollectionUndoInfo>>> getCollectionUndoList();

    /**
     * 已完成下架但未集货的波次数量
     * @return
     */
    @POST("/api/pda/productWaveTask/collectionOffShelfUndoCount")
    Observable<BaseResponseBean<Integer>> getCollectionUndoCount();

    /**
     *波次集货完成
     */
    @POST("/api/pda/productWaveTask/collectionDone")
    Observable<BaseResponseBean<String>> collectionDone(@Query("collectionAreaCode")String collectionAreaCode);


    /**
     * 未完成下架且未集货的波次列表
     * @return
     */
    @POST("/api/pda/productWaveTask/collectionOffShelfUndoList")
    Observable<BaseResponseBean<List<CollectionUndoInfo>>> getCollectionOffShelfUndoList();


    /**
     * 按顺序获取波次订单列表
     * @return
     */
    @POST("/api/pda/productWaveTask/basketInit")
    Observable<BaseResponseBean<ArrayList<OrderInfo>>> getOrderList(@Query("uniqueNo")String uniqueNo);


    /**
     * 出库订单绑定篮子
     * @return
     */
    @POST("/api/pda/productWaveTask/basketBind")
    Observable<BaseResponseBean<String>> orderBindBasket(@Query("basketNo")String basketNo,@Query("waybill") String waybill);


    /**
     * 查询商品长宽高信息
     * @return
     */
    @POST("/api/pda/productArrivalOrder/snInboundInit")
    Observable<BaseResponseBean<ProductInfo>> snInboundInit(@Query("productCode")String productCode);


    /**
     * 更新长宽高
     * @return
     */
    @POST("/api/pda/productArrivalOrder/snInbound")
    Observable<BaseResponseBean<String>> snInbound(@Query("productCode")String productCode, @Body SnInboundRequest request);


    /**
     * 更新出库订单的重量
     * @return
     */
    @POST("/api/pda/productWaveTask/weighing")
    Observable<BaseResponseBean<String>> bindWayBillAndWeigh(@Query("waybill")String waybill,@Query("weight")String weight);
}
