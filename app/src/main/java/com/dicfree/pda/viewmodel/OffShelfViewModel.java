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

import static com.dicfree.pda.base.BaseCodeConstants.STATUS_CODE_SUCCESS;
import static com.dicfree.pda.base.BaseCodeConstants.STATUS_MESSAGE_SUCCESS_VALUE;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dicfree.pda.base.BaseConsumer;
import com.dicfree.pda.base.BaseViewModel;
import com.dicfree.pda.base.RxScheduler;
import com.dicfree.pda.base.bean.BaseResponseBean;
import com.dicfree.pda.base.bean.IBaseResponse;
import com.dicfree.pda.base.http.HttpManager;
import com.dicfree.pda.model.ServerApi;
import com.dicfree.pda.model.bean.CollectionUndoInfo;
import com.dicfree.pda.model.bean.OffShelfReqParams;
import com.dicfree.pda.model.bean.OffShelfUndoBean;
import com.dicfree.pda.model.bean.OffShelfUndoEarlyBean;

import java.util.ArrayList;
import java.util.List;

import autodispose2.AutoDispose;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Function3;

public class OffShelfViewModel extends BaseViewModel {
    private static final String TAG="OffShelfViewModel";
    private final MutableLiveData<OffShelfUndoEarlyBean> infoMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<OffShelfUndoBean>> offShelfUndoList = new MutableLiveData<>();
    private final MutableLiveData<Integer> countInfo = new MutableLiveData<>();
    private OffShelfUndoEarlyBean.ShelfInfo lockShelfInfo;
    private final MutableLiveData<Boolean> offShelfState = new MutableLiveData<>();
    ServerApi serverApi;
    public OffShelfViewModel(){
        serverApi= HttpManager.getInstance().getServiceApi(ServerApi.class);
    }

    public MutableLiveData<OffShelfUndoEarlyBean> getInfoMutableLiveData() {
        return infoMutableLiveData;
    }

    public MutableLiveData<Integer> getCountInfo() {
        return countInfo;
    }

    public MutableLiveData<List<OffShelfUndoBean>> getOffShelfUndoList() {
        return offShelfUndoList;
    }

    public MutableLiveData<Boolean> getOffShelfState() {
        return offShelfState;
    }
    /**
     * @return
     */
    private Observable<BaseResponseBean<OffShelfUndoEarlyBean>> getOffShelfUndoEarliestList(Integer id) {
        return serverApi.getOffShelfUndoEarliestList(id)
                .onErrorReturn(throwable -> new BaseResponseBean<OffShelfUndoEarlyBean>(STATUS_CODE_SUCCESS, new OffShelfUndoEarlyBean()))
                .compose(RxScheduler.io_mains())
                .flatMap((Function<BaseResponseBean<OffShelfUndoEarlyBean>, ObservableSource<BaseResponseBean<OffShelfUndoEarlyBean>>>) resp -> {
                    return Observable.just(resp);
                });
    }

    /**
     * @return
     */
    private Observable<BaseResponseBean<List<CollectionUndoInfo>>> getCollectionUndoList() {
        return serverApi.getCollectionUndoList()
                .onErrorReturn(throwable -> new BaseResponseBean<>(STATUS_CODE_SUCCESS, new ArrayList<>()))
                .compose(RxScheduler.io_mains())
                .flatMap((Function<BaseResponseBean<List<CollectionUndoInfo>>, ObservableSource<BaseResponseBean<List<CollectionUndoInfo>>>>) resp -> {
                    return Observable.just(resp);
                });
    }


    /**
     * @return
     */
    private Observable<BaseResponseBean<Integer>> getOffShelfUndoCount() {
        return serverApi.getOffShelfUndoCount()
                .onErrorReturn(throwable -> new BaseResponseBean<Integer>(STATUS_CODE_SUCCESS, 0))
                .compose(RxScheduler.io_mains())
                .flatMap((Function<BaseResponseBean<Integer>, ObservableSource<BaseResponseBean<Integer>>>) resp -> {
                    countInfo.postValue(resp.getData());
                    return Observable.just(resp);
                });
    }


    public void initData(Integer id){
        showLoading(true);
        if(null ==id){
            serverApi.getOffShelfUndoList().compose(RxScheduler.io_mains())
                    .concatMap((Function<BaseResponseBean<List<OffShelfUndoBean>>, ObservableSource<BaseResponseBean<OffShelfUndoEarlyBean>>>) listBaseResponseBean -> {
                        if (listBaseResponseBean.isSuccess()&&listBaseResponseBean.getData()!=null&&!listBaseResponseBean.getData().isEmpty()) {

                            return Observable.create((ObservableOnSubscribe<BaseResponseBean<OffShelfUndoEarlyBean>>) emitter -> {
                                Observable.zip(
                                                getOffShelfUndoEarliestList(listBaseResponseBean.getData().get(0).getId()),
                                                getOffShelfUndoCount(),
                                                (BiFunction<BaseResponseBean<OffShelfUndoEarlyBean>, BaseResponseBean<Integer>, BaseResponseBean<OffShelfUndoEarlyBean>>) (offShelfUndoEarlyResponseBean,countBean) -> {
                                                    return offShelfUndoEarlyResponseBean;
                                                }).compose(RxScheduler.io_mains())
                                        .to(AutoDispose.autoDisposable(OffShelfViewModel.this))//解决内存泄漏
                                        .subscribe(new BaseConsumer<BaseResponseBean<OffShelfUndoEarlyBean>>() {
                                            @Override
                                            public void onBizSucceed(BaseResponseBean<OffShelfUndoEarlyBean> resp) {
                                                emitter.onNext(resp);
                                            }

                                            @Override
                                            public void onBizFailed(IBaseResponse error) {
                                                emitter.onError(new Error());
                                            }
                                        });
                            });
                        } else {
                            return Observable.just( new BaseResponseBean<>(STATUS_CODE_SUCCESS, new OffShelfUndoEarlyBean()));
                        }
                    }).to(AutoDispose.autoDisposable(this))
                    .subscribe(new BaseConsumer<BaseResponseBean<OffShelfUndoEarlyBean>>(){
                        @Override
                        public void onBizSucceed(BaseResponseBean<OffShelfUndoEarlyBean> bean) {
                            showLoading(false);
                            if(bean.isSuccess()&&bean.getData()!=null){
                                infoMutableLiveData.setValue(bean.getData());
                            }else{
                                infoMutableLiveData.setValue(null);
                            }
                        }

                        @Override
                        public void onBizFailed(IBaseResponse error) {
                            showLoading(false);
                            ToastUtils.showLong(error.getStatusMessage());
                        }
                    });

        }else{
            getOffShelfUndoEarliestList(id).compose(RxScheduler.io_mains())
                    .to(AutoDispose.autoDisposable(this))
                    .subscribe(new BaseConsumer<BaseResponseBean<OffShelfUndoEarlyBean>>() {
                        @Override
                        public void onBizSucceed(BaseResponseBean<OffShelfUndoEarlyBean> bean) {
                            if(bean.isSuccess()&&bean.getData()!=null){
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
    }

    public void getOffShelfList() {
        showLoading(true);
        serverApi.getOffShelfUndoList()
                .compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<List<OffShelfUndoBean>>>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<List<OffShelfUndoBean>> bean) {
                        if(bean.isSuccess()&&bean.getData()!=null&&!bean.getData().isEmpty()){
                            offShelfUndoList.setValue(bean.getData());
                        }else{
                            offShelfUndoList.setValue(null);
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
     * 判断扫码货架号并不在本波次待分拣的名单内
     * @param scanShelfCode
     * @return
     */
    public OffShelfUndoEarlyBean.ShelfInfo checkContainScanShelfCode(String scanShelfCode){
        boolean isContain = false;
        OffShelfUndoEarlyBean.ShelfInfo lockShelfInfo =null;
        OffShelfUndoEarlyBean offShelfUndoEarlyBean =infoMutableLiveData.getValue();
        if(offShelfUndoEarlyBean!=null&&offShelfUndoEarlyBean.getShelfList()!=null&&offShelfUndoEarlyBean.getShelfList().size()>0){
            for(OffShelfUndoEarlyBean.ShelfInfo shelfInfo:offShelfUndoEarlyBean.getShelfList()){
                if(shelfInfo.getShelfNo().equals(scanShelfCode)){
                    isContain = true;
                    lockShelfInfo = shelfInfo;
                    break;
                }
            }
        }
        return  lockShelfInfo;
    }

    /**
     * 判断扫码货物并不在本波次待分拣的名单内
     * @param scanCode
     * @return
     */
    public boolean checkContainScanProductCode(String shelfCode,String scanCode){
        LogUtils.dTag("checkContainScanProductCode",scanCode);
        OffShelfUndoEarlyBean.ShelfInfo lockShelfInfo = checkContainScanShelfCode(shelfCode);
        boolean isContain = false;
        if(lockShelfInfo!=null&&lockShelfInfo.getProductDeliveryOrderItemList()!=null&&lockShelfInfo.getProductDeliveryOrderItemList().size()>0){
            for (OffShelfUndoEarlyBean.ProductDeliveryOrderItem orderItem:lockShelfInfo.getProductDeliveryOrderItemList()){
                if(orderItem.getProductSkuCode().contains(scanCode)){
                    isContain = true;
                    break;
                }
            }
        }
        return  isContain;
    }

    /**
     * 下架
     */
    public void offShelf(Integer id,String shelfNo,String productCode){
        showLoading(true);
        serverApi.offShelf(id,shelfNo,productCode)
                .compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<String >>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<String> bean) {
                        showLoading(false);
                        offShelfState.setValue(bean.getData().equals(STATUS_MESSAGE_SUCCESS_VALUE));
                    }

                    @Override
                    public void onBizFailed(IBaseResponse error) {
                        ToastUtils.showLong(error.getStatusMessage());
                        showLoading(false);
                        offShelfState.setValue(false);
                    }
                });
    }

    public static class OffShelfModelFactory implements ViewModelProvider.Factory {
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(OffShelfViewModel.class)) {
                return (T) new OffShelfViewModel();
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }
}