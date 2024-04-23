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

import static com.dicfree.pda.base.BaseCodeConstants.STATUS_CODE_DEVICE_NOT_REGISTER;
import static com.dicfree.pda.utils.SoundPoolUtil.ON_SHELF_SUCCESS;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.dicfree.pda.R;
import com.dicfree.pda.base.BaseActivityWithLoading;
import com.dicfree.pda.databinding.ActivityMainLayoutBinding;
import com.dicfree.pda.model.bean.BarcodeScanResult;
import com.dicfree.pda.utils.PrinterManager;
import com.dicfree.pda.utils.ScanBarcodeManager;
import com.dicfree.pda.utils.SoundPoolUtil;
import com.dicfree.pda.utils.StringUtils;
import com.dicfree.pda.view.ComponentCommonDialog;
import com.dicfree.pda.viewmodel.MainViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

//@Route(path = RouterConstants.MAIN_ACTIVITY)
public class MainActivity extends BaseActivityWithLoading<MainViewModel> implements View.OnClickListener {
    private ActivityMainLayoutBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        viewModel.getInfoMutableLiveData().observe(this,mainMergeInfo->{
            if(mainMergeInfo.getState().equals(STATUS_CODE_DEVICE_NOT_REGISTER)){
                ComponentCommonDialog.Companion.show(this, "温馨提示", "您的设备号"+ DeviceUtils.getMacAddress()+"还没有注册,请联系管理员添加!", "取消", "确定",
                        (dialog, view) -> {
                            dialog.dismiss();
                        }, (dialog, view) -> {
                            dialog.dismiss();
                        }, Gravity.CENTER,false,false);
            }else{
                if(mainMergeInfo.getCount()>0){
                    binding.includeTitle.tvCount.setVisibility(View.VISIBLE);
                    binding.includeTitle.tvCount.setText(String.valueOf(mainMergeInfo.getCount()));
                }else{
                    binding.includeTitle.tvCount.setVisibility(View.GONE);
                    ToastUtils.showLong(mainMergeInfo.getMessage());
                }
            }
        });
        viewModel.getOnShelfState().observe(this,state->{
            if(state){
//                binding.tvShelfCode.setText("");
                binding.tvProductCode.setText("");
                SoundPoolUtil.getInstance().soundPoll(ON_SHELF_SUCCESS);
            }
        });
    }

    private void initView(){
        binding = ActivityMainLayoutBinding.inflate(getLayoutInflater());
        binding.includeTitle.tvTitle.setText(R.string.main_title);
        binding.includeTitle.imgRight.setOnClickListener(this);
        binding.includeTitle.rlRightLayout.setVisibility(View.VISIBLE);
        binding.tvScan.setOnClickListener(this);
        binding.tvOffshelf.setOnClickListener(this);
        binding.tvBasket.setOnClickListener(this);
        binding.tvCollection.setOnClickListener(this);
        setContentView(binding.getRoot());
        PrinterManager.getInstance().initPermission(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_collection:
                startActivity(new Intent(this,ProductWaveActivity.class));
                break;
            case R.id.tv_basket:
                startActivity(new Intent(this,BasketMainActivity.class));
                break;
            case R.id.tv_offshelf:
                startActivity(new Intent(this,OffShelfMainActivity.class));
                break;
            case R.id.tv_scan:
                PrinterManager.getInstance().send2Print(this,"1234567890",true);
                break;
            case R.id.img_right:
                startActivity(new Intent(this,OnShelfUndoListActivity.class));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMode(BarcodeScanResult scanResult) {
        if(!TextUtils.isEmpty(scanResult.getQrData())){
            if(StringUtils.isShelfCode(scanResult.getQrData())){
                binding.tvShelfCode.setText(scanResult.getQrData());
            }else{
                binding.tvProductCode.setText(scanResult.getQrData());
            }
            if(!TextUtils.isEmpty(binding.tvShelfCode.getText())&&!TextUtils.isEmpty(binding.tvProductCode.getText())){
                viewModel.submit(binding.tvProductCode.getText().toString(),binding.tvShelfCode.getText().toString());
            }
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
    protected MainViewModel onCreateViewModel() {
        return new ViewModelProvider(this, new MainViewModel.MainViewModelFactory()).get(MainViewModel.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}