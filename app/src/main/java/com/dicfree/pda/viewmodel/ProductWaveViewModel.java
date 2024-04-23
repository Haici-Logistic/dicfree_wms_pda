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
import com.dicfree.pda.model.bean.CollectionUndoInfo;
import com.dicfree.pda.model.bean.OffShelfUndoEarlyBean;

import java.util.List;

import autodispose2.AutoDispose;

public class ProductWaveViewModel extends BaseViewModel {
    private static final String TAG="ProductWaveViewModel";
    private final MutableLiveData<List<CollectionUndoInfo>> infoMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> countState = new MutableLiveData<>();
    private final MutableLiveData<Boolean> collectDoneState = new MutableLiveData<>();
    ServerApi serverApi;
    public ProductWaveViewModel(){
        serverApi= HttpManager.getInstance().getServiceApi(ServerApi.class);
    }

    public MutableLiveData<List<CollectionUndoInfo>> getInfoMutableLiveData() {
        return infoMutableLiveData;
    }

    public MutableLiveData<Integer> getCountState() {
        return countState;
    }

    public MutableLiveData<Boolean> getCollectDoneState() {
        return collectDoneState;
    }

    public void getCollectionUndoInfo(){
        showLoading(true);
        serverApi.getCollectionUndoList()
                .compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<List<CollectionUndoInfo>>>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<List<CollectionUndoInfo>> bean) {
                        if(bean.isSuccess()&&bean.getData()!=null&&!bean.getData().isEmpty()){
                            infoMutableLiveData.setValue(bean.getData());
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

    public void getCollectionUndoCount(){
        serverApi.getCollectionUndoCount()
                .compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<Integer>>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<Integer> bean) {
                        if(bean.isSuccess()&&bean.getData()!=null){
                            countState.setValue(bean.getData());
                        }else{
                            countState.setValue(0);
                        }
                    }

                    @Override
                    public void onBizFailed(IBaseResponse error) {
                        ToastUtils.showLong(error.getStatusMessage());
                    }
                });
    }

    public void getCollectionOffShelfUndoList(){
        showLoading(true);
        serverApi.getCollectionOffShelfUndoList()
                .compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<List<CollectionUndoInfo>>>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<List<CollectionUndoInfo>> bean) {
                        if(bean.isSuccess()&&bean.getData()!=null&&!bean.getData().isEmpty()){
                            infoMutableLiveData.setValue(bean.getData());
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
     * 判断扫码货物并不在本波次待分拣的名单内
     * @param scanCode
     * @return
     */
    public boolean checkContainScanAreaCode(String scanCode){
        boolean isContain = false;
        List<CollectionUndoInfo> collectionUndoInfos = infoMutableLiveData.getValue();
        if(collectionUndoInfos!=null&&collectionUndoInfos.size()>0){
            for (CollectionUndoInfo collectionUndoInfo:collectionUndoInfos){
                if(collectionUndoInfo.getCollectionAreaCode().contains(scanCode)){
                    isContain = true;
                    break;
                }
            }
        }
        return  isContain;
    }

    public String getUniqueNo(String scanCode){
        String uniqueNo="";
        List<CollectionUndoInfo> collectionUndoInfos = infoMutableLiveData.getValue();
        if(collectionUndoInfos!=null&&collectionUndoInfos.size()>0){
            for (CollectionUndoInfo collectionUndoInfo:collectionUndoInfos){
                if(collectionUndoInfo.getCollectionAreaCode().contains(scanCode)){
                    uniqueNo = collectionUndoInfo.getUniqueNo();
                    break;
                }
            }
        }
        return  uniqueNo;
    }

    public void collectionDone(String collectionAreaCode){
        showLoading(true);
        serverApi.collectionDone(collectionAreaCode)
                .compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<String>>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<String> bean) {
                        showLoading(false);
                        collectDoneState.setValue(true);
                    }

                    @Override
                    public void onBizFailed(IBaseResponse error) {
                        ToastUtils.showLong(error.getStatusMessage());
                        showLoading(false);
                        collectDoneState.setValue(false);
                    }
                });
    }
    public static class ProductWaveViewModelFactory implements ViewModelProvider.Factory {
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ProductWaveViewModel.class)) {
                return (T) new ProductWaveViewModel();
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }
}