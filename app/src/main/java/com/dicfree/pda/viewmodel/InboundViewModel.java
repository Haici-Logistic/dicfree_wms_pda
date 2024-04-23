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
package com.dicfree.pda.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.ToastUtils;
import com.dicfree.pda.base.BaseConsumer;
import com.dicfree.pda.base.BaseViewModel;
import com.dicfree.pda.base.RxScheduler;
import com.dicfree.pda.base.bean.BaseResponseBean;
import com.dicfree.pda.base.bean.IBaseResponse;
import com.dicfree.pda.base.http.HttpManager;
import com.dicfree.pda.model.ServerApi;
import com.dicfree.pda.model.bean.OrderInfo;
import com.dicfree.pda.model.bean.ProductInfo;
import com.dicfree.pda.model.bean.SnInboundRequest;

import java.util.ArrayList;

import autodispose2.AutoDispose;

public class InboundViewModel extends BaseViewModel {
    private static final String TAG="InboundViewModel";
    ServerApi serverApi;

    private MutableLiveData<ProductInfo> productInfoLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> bindLiveData = new MutableLiveData<>();


    public InboundViewModel(){
        serverApi= HttpManager.getInstance().getServiceApi(ServerApi.class);
    }

    public MutableLiveData<ProductInfo> getProductInfoLiveData() {
        return productInfoLiveData;
    }

    public MutableLiveData<Boolean> getBindLiveData() {
        return bindLiveData;
    }

    public void checkWithProductCode(String productCode){
        showLoading(true);
        serverApi.snInboundInit(productCode)
                .compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<ProductInfo>>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<ProductInfo> bean) {
                        showLoading(false);
                        productInfoLiveData.setValue(bean.getData());
                    }
                    @Override
                    public void onBizFailed(IBaseResponse error) {
                        ToastUtils.showLong(error.getStatusMessage());
                        showLoading(false);
                    }
                });
    }



    public void bindProductCode(String productCode,String weight,String height,String length,String width,boolean quality){
        showLoading(true);
        SnInboundRequest snInboundRequest = new SnInboundRequest();
        snInboundRequest.setWeight(weight);
        snInboundRequest.setHeight(height);
        snInboundRequest.setQuality(quality);
        snInboundRequest.setWidth(width);
        snInboundRequest.setLength(length);
        serverApi.snInbound(productCode,snInboundRequest)
                .compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<String>>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<String> bean) {
                        showLoading(false);
                        bindLiveData.setValue(true);
                    }
                    @Override
                    public void onBizFailed(IBaseResponse error) {
                        ToastUtils.showLong(error.getStatusMessage());
                        showLoading(false);
                    }
                });
    }

    public void bindWayBillAndWeight(String wayBill,String weight){
        showLoading(true);
        serverApi.bindWayBillAndWeigh(wayBill,weight)
                .compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<String>>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<String> bean) {
                        showLoading(false);
                        bindLiveData.setValue(true);
                    }
                    @Override
                    public void onBizFailed(IBaseResponse error) {
                        ToastUtils.showLong(error.getStatusMessage());
                        showLoading(false);
                    }
                });
    }

    public static class InboundViewModelFactory implements ViewModelProvider.Factory {
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(InboundViewModel.class)) {
                return (T) new InboundViewModel();
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }

}