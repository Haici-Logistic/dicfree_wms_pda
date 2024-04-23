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

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dicfree.pda.base.BaseConsumer;
import com.dicfree.pda.base.BaseViewModel;
import com.dicfree.pda.base.RxScheduler;
import com.dicfree.pda.base.bean.BaseResponseBean;
import com.dicfree.pda.base.bean.IBaseResponse;
import com.dicfree.pda.base.http.HttpManager;
import com.dicfree.pda.model.ServerApi;
import com.dicfree.pda.model.bean.DeviceInfo;
import com.dicfree.pda.model.bean.LoginData;
import com.dicfree.pda.model.bean.MainMergeInfo;
import com.dicfree.pda.model.bean.OnShelfUndoBean;
import com.dicfree.pda.utils.CacheManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import autodispose2.AutoDispose;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

public class MainViewModel extends BaseViewModel {
    private static final String TAG="MainViewModel";
    private final MutableLiveData<MainMergeInfo> infoMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> onShelfState = new MutableLiveData<>();
    ServerApi serverApi;
    public MainViewModel(){
        serverApi= HttpManager.getInstance().getServiceApi(ServerApi.class);
    }

    public MutableLiveData<MainMergeInfo> getInfoMutableLiveData() {
        return infoMutableLiveData;
    }

    public MutableLiveData<Boolean> getOnShelfState() {
        return onShelfState;
    }

    public void initData(){
        showLoading(true);
        getDeviceInfo().concatMap((Function<BaseResponseBean<DeviceInfo>, ObservableSource<BaseResponseBean<MainMergeInfo>>>) deviceInfoBean->{
                    if(deviceInfoBean.isSuccess()){
                        CacheManager.getInstance().saveDeviceInfo(deviceInfoBean.getData());
                        DeviceInfo deviceInfo = deviceInfoBean.getData();
                        return serverApi.login(deviceInfo.getCode(),deviceInfo.getKey()).concatMap((Function<BaseResponseBean<LoginData>, ObservableSource<BaseResponseBean<MainMergeInfo>>>) loginBean->{
                            if(loginBean.isSuccess()){
                                CacheManager.getInstance().saveTokenData(loginBean.getData());
                                return serverApi.getOnShelfUndoCount().concatMap((Function< BaseResponseBean<Integer>, ObservableSource<BaseResponseBean<MainMergeInfo>>>) countBean->{
                                    MainMergeInfo mainMergeInfo = new MainMergeInfo();
                                    if(countBean.isSuccess()){
                                        mainMergeInfo.setCount(countBean.getData());
                                        mainMergeInfo.setState(STATUS_CODE_SUCCESS);
                                    }
                                    return Observable.just(new BaseResponseBean(STATUS_CODE_SUCCESS,mainMergeInfo));
                                });
                            }else{
                                return Observable.just(new BaseResponseBean(deviceInfoBean.getStatusCode(),deviceInfoBean.getStatusMessage()));
                            }
                        });
                    }else{
                        return Observable.just(new BaseResponseBean(deviceInfoBean.getStatusCode(),deviceInfoBean.getStatusMessage()));
                    }
                }).compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<MainMergeInfo>>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<MainMergeInfo> bean) {
                        showLoading(false);
                        infoMutableLiveData.setValue(bean.getData());
                    }

                    @Override
                    public void onBizFailed(IBaseResponse error) {
                        showLoading(false);
                        MainMergeInfo mainMergeInfo = new MainMergeInfo(error.getStatusCode(),error.getStatusMessage());
                        infoMutableLiveData.setValue(mainMergeInfo);
                    }
                });
    }

    /**
     * 产品上架
     * @param productCode
     * @param shiftNo
     */
    public void submit(String productCode,String shiftNo){
        showLoading(true);
        serverApi.sumitOnShelf(productCode,shiftNo).delay(1*1000, TimeUnit.MILLISECONDS)
                .compose(RxScheduler.io_mains())
                .to(AutoDispose.autoDisposable(this))
                .subscribe(new BaseConsumer<BaseResponseBean<String>>() {
                    @Override
                    public void onBizSucceed(BaseResponseBean<String> bean) {
                        showLoading(false);
                        onShelfState.setValue(true);
                    }

                    @Override
                    public void onBizFailed(IBaseResponse error) {
                        ToastUtils.showLong(error.getStatusMessage());
                        showLoading(false);
                    }
                });
    }

    /**
     * 获取设备信息
     * @return
     */
    private Observable<BaseResponseBean<DeviceInfo>> getDeviceInfo(){
        String macAddress = DeviceUtils.getMacAddress();//"b4:29:3d:75:0c:f8";//;
        if (!CacheManager.getInstance().isGetDeviceInfo()) {
            return serverApi.getLoginInfo(macAddress)
                    .compose(RxScheduler.io_mains())
                    .flatMap((Function<BaseResponseBean<DeviceInfo>, ObservableSource<BaseResponseBean<DeviceInfo>>>) resp -> {
                        return Observable.just(resp);
                    });
        } else {
            return Observable.create(new ObservableOnSubscribe<BaseResponseBean<DeviceInfo>>() {
                        @Override
                        public void subscribe(ObservableEmitter<BaseResponseBean<DeviceInfo>> emitter) {
                            emitter.onNext(new BaseResponseBean<DeviceInfo>(STATUS_CODE_SUCCESS, CacheManager.getInstance().getDeviceInfo()) );
                        }
                    }).compose(RxScheduler.io_mains())
                    .flatMap((Function<BaseResponseBean<DeviceInfo>, ObservableSource<BaseResponseBean<DeviceInfo>>>) resp -> {
                        return Observable.just(resp);
                    });

        }
    }
    public static class MainViewModelFactory implements ViewModelProvider.Factory {
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(MainViewModel.class)) {
                return (T) new MainViewModel();
            } else {
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }
    }
}