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
package com.dicfree.pda.view;

import static android.view.animation.Animation.INFINITE;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dicfree.pda.R;
import com.dicfree.pda.base.BaseDialogFragment;

public class CommonLoadingDialog extends BaseDialogFragment {
    private String message;
    private int loadingResId;
    private int messageId;
    private ImageView ivLoading;
    private TextView tvLoading;
    private ObjectAnimator animator;

    public CommonLoadingDialog() {
    }

    public CommonLoadingDialog(String message, int loadingResId) {
        this.message = message;
        this.loadingResId = loadingResId;
    }
    public CommonLoadingDialog(String message) {
        this.message = message;
    }

    public CommonLoadingDialog(int messageId) {
        this.messageId = messageId;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void updateLoadingText(int id){
        tvLoading.setText(id);
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null || dialog.getWindow() == null){
            return;
        }
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams =window.getAttributes();
        windowParams.dimAmount = 0.0f; //将Window周边设置透明为0.0
        window.setAttributes(windowParams);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cm_layout_loading_dialog, container, false);
        ivLoading = view.findViewById(R.id.cm_iv_loading);
        tvLoading = view.findViewById(R.id.cm_tv_loading);
        FrameLayout form = view.findViewById(R.id.cm_fmlyt_loading);
        if (loadingResId > 0) {
            ivLoading.setImageResource(loadingResId);
        }
        if(!TextUtils.isEmpty(message)){
            tvLoading.setText(message);
        }
        if (messageId > 0) {
            tvLoading.setText(messageId);
        }
        form.setOnClickListener(v -> {
            if (isCancelable()) {
                dismiss();
            }
        });
        animator = ObjectAnimator.ofFloat(ivLoading, "rotation", 0, 360);
        animator.setDuration(2000);
        animator.setRepeatCount(INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        animator.cancel();
    }

    public interface OnDialogBtnClickListener {
        void onClickLeft(View v);
        void onClickRight(View v);
    }
}
