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


import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class BaseActivityWithVm<V extends BaseViewModel> extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    protected V viewModel;
    BaseDialogFragment loadingDialog;
    AtomicInteger loadingRefCount = new AtomicInteger(0);
    Observer<Boolean> loadingObserver = loading -> {
        LogUtils.dTag(TAG , "loading=" + loading + ",loadingRefCount="+loadingRefCount.get());
        if (this != ActivityUtils.getTopActivity()) {
            return;
        }
        if (null == loading) {
            return;
        }

        int count = loadingRefCount.get();
        if (loading) {
            if (count < 0) {
                loadingRefCount.set(0);
                count = loadingRefCount.get();
            }
            int newCount = loadingRefCount.incrementAndGet();
            LogUtils.dTag(TAG , "loading=true, count="+ count + "=>" + newCount
                    + ", (null == loadingDialog)? = " + (null ==loadingDialog)
                    + ", loadingDialog.isDismissed()="+((null == loadingDialog)?null:loadingDialog.isDismissed()));
            if (count == 0) {
                if (null != loadingDialog) {
                    loadingDialog.show(getSupportFragmentManager(), "");
                }
            }
            LogUtils.dTag(TAG , ".......loadingDialog.isDismissed()="+((null == loadingDialog)?null:loadingDialog.isDismissed()));
        } else {
            if (count >= 1) {
                int newCount = loadingRefCount.decrementAndGet();
                LogUtils.dTag(TAG , "loading=false, count="+ count + "=>" + newCount
                        + ", (null == loadingDialog)? = " + (null ==loadingDialog)
                        + ", loadingDialog.isDismissed()="+((null == loadingDialog)?null:loadingDialog.isDismissed()));
                if (count <= 1) {
                    if (null != loadingDialog && !loadingDialog.isDismissed()) {
                        loadingDialog.dismissAllowingStateLoss();
                    }
                }
                LogUtils.dTag(TAG , ".......loadingDialog.isDismissed()="+((null == loadingDialog)?null:loadingDialog.isDismissed()));
            }
        }
    };

    private ArrayList<BaseActivityOnTouchListener> onTouchListeners = new ArrayList<BaseActivityOnTouchListener>();
    private BaseActivityOnBackListener onBackListener;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.dTag(TAG, "lifecycle=>onCreate(),"+((null == savedInstanceState)?"savedInstanceState=null":"savedInstanceState!=null"));
        super.onCreate(savedInstanceState);
        viewModel = onCreateViewModel();
        loadingDialog = onCreateLoadingDialog();
        if (null != viewModel) {
            LogUtils.dTag(TAG, "onCreate(), viewModel.observeLoading(this, loadingObserver)");
            viewModel.observeLoading(this, loadingObserver);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        getLifecycle().addObserver(new LifecycleEventObserver(){
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                LogUtils.dTag(TAG, "lifecycle=>onStateChanged" + event.name());
            }
        });
    }

    @Override
    public void onBackPressed() {
        LogUtils.dTag(TAG, "onBackPressed()");
        if (null == onBackListener || !onBackListener.onBackPressed()) {
            KeyboardUtils.hideSoftInput(this);
            int count = getSupportFragmentManager().getBackStackEntryCount();

            if (count == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        LogUtils.dTag(TAG, "onSaveInstanceState(), outState = " + outState);
        if (null != loadingDialog && !loadingDialog.isDismissed()) {
            LogUtils.dTag(TAG, "onSaveInstanceState(), loadingDialog.dismiss()");
            loadingDialog.dismissAllowingStateLoss();
//            loadingDialog = null;
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        LogUtils.dTag(TAG, "onConfigurationChanged(), newConfig = " + newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        LogUtils.dTag(TAG, "lifecycle=>onStart()");
        super.onStart();
    }

    @Override
    protected void onResume() {
        LogUtils.dTag(TAG, "lifecycle=>onResume()");
        super.onResume();
    }

    @Override
    protected void onStop() {
        LogUtils.dTag(TAG, "lifecycle=>onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtils.dTag(TAG, "lifecycle=>onDestroy(), isChangingConfigurations="+isChangingConfigurations());
        if (null != viewModel) {
            LogUtils.dTag(TAG, "onDestroy(), viewModel.removeLoadingObserver(loadingObserver)");
            viewModel.removeLoadingObserver(loadingObserver);
            viewModel.showLoading(null);
        }
        loadingRefCount.set(0);
//        if (null != loadingDialog && loadingDialog.isVisible()) {
//            LogUtils.dTag(TAG, "onDestroy(), loadingDialog.dismissAllowingStateLoss()");
//            loadingDialog.dismissAllowingStateLoss();
//        }
        super.onDestroy();
    }

    /***
     * 获取viewModel
     */
    protected abstract V onCreateViewModel();

    /***
     * 获取loading对话框，默认无
     */
    protected BaseDialogFragment onCreateLoadingDialog() {
        return null;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (BaseActivityOnTouchListener listener : onTouchListeners) {
            listener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void registerOnTouchListener(BaseActivityOnTouchListener listener) {
        onTouchListeners.add(listener);
    }

    public void unregisterOnTouchListener(BaseActivityOnTouchListener listener) {
        onTouchListeners.remove(listener);
    }

    public void registerOnBackListener(BaseActivityOnBackListener listener) {
        onBackListener = listener;
    }

    public void unregisterOnBackListener(BaseActivityOnBackListener listener) {
        onBackListener = null;
    }


    public interface BaseActivityOnTouchListener {
        boolean onTouch(MotionEvent ev);
    }

    public interface BaseActivityOnBackListener {
        boolean onBackPressed();
    }
}
