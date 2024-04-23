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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.dicfree.pda.R;
import com.dicfree.pda.base.BaseActivityWithLoading;
import com.dicfree.pda.databinding.ActivityBasketMainLayoutBinding;
import com.dicfree.pda.model.bean.BarcodeScanResult;
import com.dicfree.pda.viewmodel.BasketViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

//@Route(path = RouterConstants.MAIN_ACTIVITY)
public class BasketMainActivity extends BaseActivityWithLoading<BasketViewModel> implements View.OnClickListener {
    private ActivityBasketMainLayoutBinding binding;
    private String uniqueNo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        viewModel.getInfoMutableLiveData().observe(this,orderInfos -> {
            Intent intent = new Intent(BasketMainActivity.this,BasketDetailActivity.class);
            intent.putExtra("uniqueNo",uniqueNo);
            startActivity(intent);
        });
    }

    private void initView(){
        binding = ActivityBasketMainLayoutBinding.inflate(getLayoutInflater());
        binding.includeTitle.tvTitle.setText(R.string.basket_main_title);
        binding.tvNext.setOnClickListener(this);
        setContentView(binding.getRoot());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_next:
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMode(BarcodeScanResult scanResult) {
        if(!TextUtils.isEmpty(scanResult.getQrData())){
            uniqueNo = scanResult.getQrData();
            binding.tvWaveNo.setText(uniqueNo);
            viewModel.initData(uniqueNo);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected BasketViewModel onCreateViewModel() {
        return new ViewModelProvider(this, new BasketViewModel.BasketViewModelFactory()).get(BasketViewModel.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}