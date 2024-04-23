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
import com.dicfree.pda.model.bean.OnShelfUndoBean;

import java.util.List;

import autodispose2.AutoDispose;

public class OnShelfUndoViewModel extends BaseViewModel {
    private static final String TAG="OnShelfUndoViewModel";
    private final MutableLiveData<List<OnShelfUndoBean>> infoMutableLiveData = new MutableLiveData<>();
    ServerApi serverApi;
    public OnShelfUndoViewModel(){
        serverApi= HttpManager.getInstance().getServiceApi(ServerApi.class);
    }

    public MutableLiveData<List<OnShelfUndoBean>> getInfoMutableLiveData() {
        return infoMutableLiveData;
    }

    public void initData(){
        showLoading(true);
        serverApi.getOnShelfUndoList()
                .compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<List<OnShelfUndoBean>>>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<List<OnShelfUndoBean>> bean) {
                        if(bean.isSuccess()&&bean.getData()!=null&&!bean.getData().isEmpty()){
//                            List<OnShelfUndoBean.ArrivalOrderItem> orderItemList = new ArrayList<>();
//                            for(OnShelfUndoBean onShelfUndoBean:bean.getData()){
//                                for (OnShelfUndoBean.ArrivalOrderItem arrivalOrderItem:onShelfUndoBean.getMallArrivalOrderItemList()){
//                                    arrivalOrderItem.setThirdOrderNo(onShelfUndoBean.getThirdOrderNo());
//                                    orderItemList.add(arrivalOrderItem);
//                                }
//                            }
                            List<OnShelfUndoBean> onShelfUndoBeans = bean.getData();
                            infoMutableLiveData.setValue(onShelfUndoBeans);
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

    public static class OnShelfUndoViewModelFactory implements ViewModelProvider.Factory {
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(OnShelfUndoViewModel.class)) {
                return (T) new OnShelfUndoViewModel();
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }
}