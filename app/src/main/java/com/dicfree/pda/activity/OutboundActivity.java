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
package com.dicfree.pda.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.ToastUtils;
import com.dicfree.pda.R;
import com.dicfree.pda.base.BaseActivityWithLoading;
import com.dicfree.pda.databinding.ActivityChukuMainLayoutBinding;
import com.dicfree.pda.databinding.ActivityRukuMainLayoutBinding;
import com.dicfree.pda.model.bean.BarcodeScanResult;
import com.dicfree.pda.utils.WeightManager;
import com.dicfree.pda.viewmodel.BasketViewModel;
import com.dicfree.pda.viewmodel.InboundViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @Author: shengcaiwang
 * @Description:
 * @Date: 2024/1/10
 */
public class OutboundActivity extends BaseActivityWithLoading<InboundViewModel> implements View.OnClickListener, WeightManager.WeightListen {
    private String wayBill;
    private ActivityChukuMainLayoutBinding binding;
    private String lastWeight;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        WeightManager.getInstance().checkConnectState();
        viewModel.getBindLiveData().observe(this,result -> {
            if(result){
                binding.tvProductCode.setText("");
                binding.tvWeight.setText("");
            }
        });
    }
    private void initView(){
        binding = ActivityChukuMainLayoutBinding.inflate(getLayoutInflater());
        binding.includeTitle.tvTitle.setText("Outbound weighing");
        binding.includeTitle.imgRight.setOnClickListener(this);
        binding.includeTitle.rlRightLayout.setVisibility(View.VISIBLE);
        binding.includeTitle.imgRight.setImageResource(R.mipmap.ic_delete);
        binding.tvConfrim.setOnClickListener(this);
        setContentView(binding.getRoot());
        binding.tvConnectStatus.setText(WeightManager.getInstance().isConnectState()?"Online":"Offline");
        WeightManager.getInstance().setWeightListen(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_confrim:
                if(TextUtils.isEmpty(binding.tvProductCode.getText())){
                    ToastUtils.showLong("WayBill is empty,please scan first!");
                    return;
                }
                if(TextUtils.isEmpty(binding.tvWeight.getText())){
                    ToastUtils.showLong("Weight is empty");
                    return;
                }
                viewModel.bindWayBillAndWeight(wayBill,binding.tvWeight.getText().toString());
                break;
            case R.id.img_right:
                this.finish();
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMode(BarcodeScanResult scanResult) {
        if(!TextUtils.isEmpty(scanResult.getQrData())){
            wayBill = scanResult.getQrData();
            binding.tvProductCode.setText(wayBill);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        WeightManager.getInstance().checkConnectState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        WeightManager.getInstance().pauseReceiveData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected InboundViewModel onCreateViewModel() {
        return new ViewModelProvider(this, new InboundViewModel.InboundViewModelFactory()).get(InboundViewModel.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onConnectState(boolean status) {
        runOnUiThread(() -> binding.tvConnectStatus.setText(status?"Online":"Offline"));
    }

    @Override
    public void onWeightReceive(String weight) {
        runOnUiThread(() -> {
            if(TextUtils.isEmpty(lastWeight)||!lastWeight.equals(weight)){
                binding.tvWeight.setText(weight);
                lastWeight =weight;
            }
        });
    }
}
