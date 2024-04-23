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

import static com.dicfree.pda.utils.SoundPoolUtil.PRINT_FAILURE;
import static com.dicfree.pda.utils.SoundPoolUtil.PRINT_SUCCESS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dicfree.pda.R;
import com.dicfree.pda.adapter.CollectionListAdapter;
import com.dicfree.pda.base.BaseActivityWithLoading;
import com.dicfree.pda.databinding.ActivityOffshelfMainLayoutBinding;
import com.dicfree.pda.databinding.ActivityProductWaveLayoutBinding;
import com.dicfree.pda.model.bean.BarcodeScanResult;
import com.dicfree.pda.utils.PrinterManager;
import com.dicfree.pda.utils.SoundPoolUtil;
import com.dicfree.pda.utils.StringUtils;
import com.dicfree.pda.viewmodel.OffShelfViewModel;
import com.dicfree.pda.viewmodel.ProductWaveViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 打印波次集货标签
 */
public class ProductWaveActivity extends BaseActivityWithLoading<ProductWaveViewModel> implements View.OnClickListener {
    private ActivityProductWaveLayoutBinding binding;
    private CollectionListAdapter adapter;
    private String collectionAreaCode;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        viewModel.getCollectionUndoInfo();
        viewModel.getCollectionUndoCount();
        viewModel.getInfoMutableLiveData().observe(this,collectionUndoInfo->{
            if(collectionUndoInfo!=null){
                binding.includeEmpty.getRoot().setVisibility(View.GONE);
                adapter.updateList(collectionUndoInfo);
            }else{
                binding.includeEmpty.getRoot().setVisibility(View.VISIBLE);
                binding.includeEmpty.tvEmptyTips.setText("There is no product wave collection");
            }
        });
        viewModel.getCountState().observe(this,count->{
            binding.includeTitle.tvCount.setText(String.valueOf(count));
            binding.includeTitle.tvCount.setVisibility(count>0?View.VISIBLE:View.GONE);
        });

        viewModel.getCollectDoneState().observe(this,result->{
            if(result){
//                PrinterManager.getInstance().connectPrint(this, result1 -> {
//                    binding.tvConnectStatus.setText(result1?"OnLine":"Offline");
//                    if(result1){
//                        PrinterManager.getInstance().sendPrintData("",true);
//                        SoundPoolUtil.getInstance().soundPoll(PRINT_SUCCESS);
//                    }else{
//                        SoundPoolUtil.getInstance().soundPoll(PRINT_FAILURE);
//                    }
//                });
            }
        });
        PrinterManager.getInstance().connectPrint(this, result1 -> {
            binding.tvConnectStatus.setText(result1?"OnLine":"Offline");
        });
    }

    private void initView(){
        binding = ActivityProductWaveLayoutBinding.inflate(getLayoutInflater());
        binding.includeTitle.tvTitle.setText(R.string.product_wave_locaton_title);
        binding.includeTitle.imgRight.setOnClickListener(this);
        binding.includeTitle.rlRightLayout.setVisibility(View.VISIBLE);
        adapter = new CollectionListAdapter(this,new ArrayList<>(),null);
        binding.rvCollectionList.setAdapter(adapter);
        binding.rvCollectionList.setLayoutManager(new LinearLayoutManager(this));
        setContentView(binding.getRoot());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_next:
                break;
            case R.id.img_right:
                startActivityForResult(new Intent(this,WaveUndoListActivity.class),0x01);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMode(BarcodeScanResult scanResult) {
        if(!TextUtils.isEmpty(scanResult.getQrData())){
            collectionAreaCode = scanResult.getQrData();
            binding.tvLocationCode.setText(collectionAreaCode);
            if(viewModel.checkContainScanAreaCode(collectionAreaCode)){
                PrinterManager.getInstance().connectPrint(this, result1 -> {
                    binding.tvConnectStatus.setText(result1?"OnLine":"Offline");
                    if(result1){
                        PrinterManager.getInstance().sendPrintData(viewModel.getUniqueNo(collectionAreaCode),true);
                        SoundPoolUtil.getInstance().soundPoll(PRINT_SUCCESS);
                        viewModel.collectionDone(collectionAreaCode);
                    }else{
                        SoundPoolUtil.getInstance().soundPoll(PRINT_FAILURE);
                    }
                });
            }else{
                SoundPoolUtil.getInstance().soundPoll(PRINT_FAILURE);
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
    protected ProductWaveViewModel onCreateViewModel() {
        return new ViewModelProvider(this, new ProductWaveViewModel.ProductWaveViewModelFactory()).get(ProductWaveViewModel.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case 0x01:
                    break;
            }
        }
    }
}