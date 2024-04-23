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

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dicfree.pda.base.BaseConsumer;
import com.dicfree.pda.base.BaseViewModel;
import com.dicfree.pda.base.RxScheduler;
import com.dicfree.pda.base.bean.BaseResponseBean;
import com.dicfree.pda.base.bean.IBaseResponse;
import com.dicfree.pda.base.http.HttpManager;
import com.dicfree.pda.model.ServerApi;
import com.dicfree.pda.model.bean.OrderInfo;

import java.util.ArrayList;
import java.util.List;

import autodispose2.AutoDispose;

public class BasketViewModel extends BaseViewModel {
    private static final String TAG="BasketViewModel";
    private final MutableLiveData<ArrayList<OrderInfo>> infoMutableLiveData = new MutableLiveData<>();
    ServerApi serverApi;
    private final  MutableLiveData<String> bindBasketLiveData = new MutableLiveData<>();
    public BasketViewModel(){
        serverApi= HttpManager.getInstance().getServiceApi(ServerApi.class);
    }

    public MutableLiveData<ArrayList<OrderInfo>> getInfoMutableLiveData() {
        return infoMutableLiveData;
    }

    public MutableLiveData<String> getBindBasketLiveData() {
        return bindBasketLiveData;
    }

    public void initData(String uniqueNo){
        showLoading(true);
        serverApi.getOrderList(uniqueNo)
                .compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<ArrayList<OrderInfo>>>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<ArrayList<OrderInfo>> bean) {
                        if(bean.isSuccess()&&bean.getData()!=null&&!bean.getData().isEmpty()){
                            ArrayList<OrderInfo> fitterList = new ArrayList<>();
                            for(OrderInfo orderInfo:bean.getData()){
                                if(StringUtils.isEmpty(orderInfo.getBasketNo())){
                                    fitterList.add(orderInfo);
                                }
                            }
                            infoMutableLiveData.setValue(fitterList);
                        }else{
                            infoMutableLiveData.setValue(null);
                        }
                        showLoading(false);
                    }

                    @Override
                    public void onBizFailed(IBaseResponse error) {
                        ToastUtils.showLong(error.getStatusMessage());
                        showLoading(false);
                    }
                });
    }

    /**
     * 判断扫码的数据是否Waybill
     * @param data
     * @return
     */
    public boolean checkIsWaybillNo(String data){
        boolean flag = false;
        ArrayList<OrderInfo> orderInfos = infoMutableLiveData.getValue();
        for (OrderInfo orderInfo:orderInfos){
            if(orderInfo.getWaybill().equals(data)){
                flag = true;
                break;
            }
        }
        return  flag;
    }

    public void orderBindBasket(String basketNo,String waybill){
        showLoading(true);
        serverApi.orderBindBasket(basketNo,waybill)
                .compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<String>>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<String> bean) {
                        showLoading(false);
                        bindBasketLiveData.setValue(waybill);
                    }
                    @Override
                    public void onBizFailed(IBaseResponse error) {
                        ToastUtils.showLong(error.getStatusMessage());
                        showLoading(false);
                    }
                });
    }

    public static class BasketViewModelFactory implements ViewModelProvider.Factory {
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(BasketViewModel.class)) {
                return (T) new BasketViewModel();
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }

}