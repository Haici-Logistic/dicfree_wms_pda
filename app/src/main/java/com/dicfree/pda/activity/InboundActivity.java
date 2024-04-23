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

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dicfree.pda.R;
import com.dicfree.pda.base.BaseActivityWithLoading;
import com.dicfree.pda.databinding.ActivityRukuMainLayoutBinding;
import com.dicfree.pda.model.bean.BarcodeScanResult;
import com.dicfree.pda.utils.WeightManager;
import com.dicfree.pda.viewmodel.InboundViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @Author: shengcaiwang
 * @Description:
 * @Date: 2024/1/10
 */
public class InboundActivity extends BaseActivityWithLoading<InboundViewModel> implements View.OnClickListener, WeightManager.WeightListen {
    private String productCode;
    private ActivityRukuMainLayoutBinding binding;

    private boolean quality= true;

    private String lastWeight;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }
    private void initView(){
        binding = ActivityRukuMainLayoutBinding.inflate(getLayoutInflater());
        binding.includeTitle.tvTitle.setText("Inbound");
        binding.includeTitle.imgRight.setOnClickListener(this);
        binding.tvHege.setOnClickListener(this);
        binding.tvBuhege.setOnClickListener(this);
        binding.tvInventory.setOnClickListener(this);
        binding.includeTitle.rlRightLayout.setVisibility(View.VISIBLE);
        binding.includeTitle.imgRight.setImageResource(R.mipmap.ic_delete);
        setContentView(binding.getRoot());
        binding.tvConnectStatus.setText(WeightManager.getInstance().isConnectState()?"Online":"Offline");
        WeightManager.getInstance().setWeightListen(this);
    }

    private void initData(){
        viewModel.getProductInfoLiveData().observe(this,productInfo -> {
            if(!StringUtils.isEmpty(productInfo.getHeight())){
                binding.tvHeigh.setText(String.valueOf(productInfo.getHeight()));
            }

            if(!StringUtils.isEmpty(productInfo.getLength())){
                binding.tvLength.setText(String.valueOf(productInfo.getLength()));
            }
            if(!StringUtils.isEmpty(productInfo.getWidth())){
                binding.tvWidth.setText(String.valueOf(productInfo.getWidth()));
            }
            binding.tvRemain.setText((productInfo.getInboundCount()+1)+"/"+productInfo.getTotalCount());
        });
        viewModel.getBindLiveData().observe(this,result -> {
            if(result){
                quality =true;
                binding.tvBuhege.setBackground(getDrawable(R.drawable.gray_6_roundcorner_bg));
                binding.tvHege.setBackground(getDrawable(R.drawable.green_6_roundcorner_bg));
                binding.tvProductCode.setText("");
                binding.tvWeight.setText("");
                binding.tvHeigh.setText("");
                binding.tvLength.setText("");
                binding.tvWidth.setText("");
                binding.tvRemain.setText("--/--");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_hege:
                quality =true;
                binding.tvBuhege.setBackground(getDrawable(R.drawable.gray_6_roundcorner_bg));
                binding.tvHege.setBackground(getDrawable(R.drawable.green_6_roundcorner_bg));
                break;
            case R.id.tv_buhege:
                quality =false;
                binding.tvBuhege.setBackground(getDrawable(R.drawable.red_shape_6_corner_bg));
                binding.tvHege.setBackground(getDrawable(R.drawable.gray_6_roundcorner_bg));
                break;
            case R.id.tv_inventory:
                if(TextUtils.isEmpty(binding.tvProductCode.getText())){
                    ToastUtils.showLong("Product Code is empty");
                    return;
                }
                if(TextUtils.isEmpty(binding.tvWidth.getText())){
                    ToastUtils.showLong("Width is empty");
                    return;
                }
                if(TextUtils.isEmpty(binding.tvLength.getText())){
                    ToastUtils.showLong("Length is empty");
                    return;
                }
                if(TextUtils.isEmpty(binding.tvWeight.getText())){
                    ToastUtils.showLong("Weight is empty");
                    return;
                }
                if(TextUtils.isEmpty(binding.tvHeigh.getText())){
                    ToastUtils.showLong("Height is empty");
                    return;
                }
                viewModel.bindProductCode(productCode,
                        binding.tvWeight.getText().toString(),
                        binding.tvHeigh.getText().toString(),
                        binding.tvLength.getText().toString(),
                        binding.tvWidth.getText().toString(),
                        quality
                        );
                break;
            case R.id.img_right:
                this.finish();
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMode(BarcodeScanResult scanResult) {
        if(!TextUtils.isEmpty(scanResult.getQrData())){
            productCode = scanResult.getQrData();
            binding.tvProductCode.setText(productCode);
            viewModel.checkWithProductCode(productCode);
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
