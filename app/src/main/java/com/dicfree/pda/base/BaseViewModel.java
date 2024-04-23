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

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.LogUtils;

import java.io.Serializable;

public class BaseViewModel extends AutoDisposeViewModel{
    protected final String TAG = getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    private MutableLiveData<Boolean> ldLoading = new MutableLiveData<>();
    private MutableLiveData<BaseEmptyEntity> ldEmpty = new MutableLiveData<>();
    protected LifecycleOwner owner;

    @MainThread
    protected void showLoading(Boolean loading) {
        LogUtils.dTag(TAG, "showLoading(" + loading + ")");
        this.ldLoading.setValue(loading);
    }

    @MainThread
    protected void showEmpty(BaseEmptyEntity entity) {
        LogUtils.dTag(TAG, "showEmpty(" + entity + ")");
        this.ldEmpty.setValue(entity);
    }

    protected void observeLoading(@NonNull LifecycleOwner owner, @NonNull Observer<Boolean> observer) {
        this.owner = owner;
        ldLoading.observe(owner, observer);
    }

    protected void removeLoadingObserver(@NonNull Observer<Boolean> observer) {
        ldLoading.removeObserver(observer);
    }

    public void observeEmpty(@NonNull LifecycleOwner owner, @NonNull Observer<BaseEmptyEntity> observer) {
        this.owner = owner;
        ldEmpty.observe(owner, observer);
    }

    public void removeEmptyObserver(@NonNull Observer<BaseEmptyEntity> observer) {
        ldEmpty.removeObserver(observer);
    }

    @Override
    protected void onCleared() {
        LogUtils.dTag(TAG, "onCleared()");
        super.onCleared();
    }

    public static class BaseEmptyEntity implements Serializable {
        private String title;
        private String message;
        private int picResource;
        private FunctionReload functionReload;
        private FunctionBack functionBack;

        public BaseEmptyEntity(String title, String message, int picResource) {
            this.title = title;
            this.message = message;
            this.picResource = picResource;
        }

        public BaseEmptyEntity(String title, String message, FunctionReload functionReload) {
            this.title = title;
            this.message = message;
            this.functionReload = functionReload;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getPicResource() {
            return picResource;
        }

        public void setPicResource(int picResource) {
            this.picResource = picResource;
        }

        public FunctionReload getFunctionReload() {
            return functionReload;
        }

        public void setFunctionReload(FunctionReload functionReload) {
            this.functionReload = functionReload;
        }

        public FunctionBack getFunctionBack() {
            return functionBack;
        }

        public void setFunctionBack(FunctionBack functionBack) {
            this.functionBack = functionBack;
        }
    }


    public interface FunctionReload {
        void reload();
    }

    public interface FunctionBack {
        void back();
    }
}
