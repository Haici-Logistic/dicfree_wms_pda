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
package com.dicfree.pda.base;


import static com.dicfree.pda.base.BaseCodeConstants.ERROR_BUSY;
import static com.dicfree.pda.base.BaseCodeConstants.ERROR_NO_NETWORK;
import static com.dicfree.pda.base.BaseCodeConstants.ERROR_TIMEOUT;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.dicfree.pda.base.bean.IBaseResponse;
import com.dicfree.pda.base.http.monitor.NoNetworkException;
import com.dicfree.pda.base.http.token.RefreshTokenExpiredException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public abstract class BaseConsumer<T extends IBaseResponse> implements Observer<T> {

    public static class BaseThrowable extends Throwable{
        private String code;
        private String showMessage;

        public BaseThrowable(String code,String showMessage){
            this.code = code;
            this.showMessage = showMessage;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getShowMessage() {
            return showMessage;
        }

        public void setShowMessage(String showMessage) {
            this.showMessage = showMessage;
        }

    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onError(Throwable e) {
        LogUtils.d("BaseConsumer#onError(" + e + ")");
        if (e instanceof ConnectException || e instanceof java.net.SocketTimeoutException) {
            IBaseResponse error = BaseConfigManager.getInstance()
                    .getError(ERROR_TIMEOUT, ERROR_TIMEOUT);
//            error.setData(e);
            onBizFailed(error);
        } else if (e instanceof NoNetworkException) {
            IBaseResponse error = BaseConfigManager.getInstance()
                    .getError(ERROR_NO_NETWORK, ERROR_NO_NETWORK);
//            error.setData(e);
            onBizFailed(error);
        } else if (e instanceof HttpException) {
            try {
                Response<?> response = ((HttpException) e).response();
                ResponseBody body = response.errorBody();
                String errorStr = body.string();
                Type type = ((ParameterizedType) Objects.requireNonNull(getClass().getGenericSuperclass())).getActualTypeArguments()[0];
                IBaseResponse baseResponse = GsonUtils.fromJson(errorStr, type);
                IBaseResponse error = BaseConfigManager.getInstance()
                        .getError(baseResponse.getStatusCode(), baseResponse.getStatusMessage());
//                error.setData(e);
                onBizFailed(error);
            } catch (Exception e1) {
                IBaseResponse error = BaseConfigManager.getInstance()
                        .getError(ERROR_BUSY, ERROR_BUSY);
//                error.setData(e);
                onBizFailed(error);
            }
        } else if (e instanceof RefreshTokenExpiredException) {
            IBaseResponse error = BaseConfigManager.getInstance().getError(((RefreshTokenExpiredException) e).busiResp());
            onBizFailed(error);
        } else if (e instanceof BaseThrowable){
            IBaseResponse error = BaseConfigManager.getInstance()
                    .getError(((BaseThrowable) e).getCode(), ((BaseThrowable) e).getShowMessage());
//            error.setData(e);
            onBizFailed(error);
        } else {
            IBaseResponse error = BaseConfigManager.getInstance()
                    .getError(ERROR_BUSY, ERROR_BUSY);
//            error.setData(e);
            onBizFailed(error);
        }
    }


    @Override
    public void onNext(@NonNull T t) {
        LogUtils.d("BaseConsumer#onNext(" + GsonUtils.toJson(t) + ")");
        if (t.isSuccess()) {
            onBizSucceed(t);
        } else {
            IBaseResponse error = BaseConfigManager.getInstance()
                    .getError(t.getStatusCode(), t.getStatusMessage());
            onBizFailed(error);
        }
    }

    @Override
    public void onComplete() {

    }

    public abstract void onBizSucceed(T bean);

    public abstract void onBizFailed(IBaseResponse error);
}
