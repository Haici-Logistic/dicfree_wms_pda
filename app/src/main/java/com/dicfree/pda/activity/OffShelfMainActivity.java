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

import static com.dicfree.pda.utils.SoundPoolUtil.OFF_SHELF_SUCCESS;
import static com.dicfree.pda.utils.SoundPoolUtil.WRONG_SHELF;
import static com.dicfree.pda.utils.SoundPoolUtil.WRONG_SKU;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ToastUtils;
import com.dicfree.pda.R;
import com.dicfree.pda.adapter.OffShelfListAdapter;
import com.dicfree.pda.adapter.OnItemClickListener;
import com.dicfree.pda.base.BaseActivityWithLoading;
import com.dicfree.pda.databinding.ActivityOffshelfMainLayoutBinding;
import com.dicfree.pda.model.bean.BarcodeScanResult;
import com.dicfree.pda.utils.SoundPoolUtil;
import com.dicfree.pda.utils.StringUtils;
import com.dicfree.pda.viewmodel.OffShelfViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 波次下架主页
 */
public class OffShelfMainActivity extends BaseActivityWithLoading<OffShelfViewModel> implements View.OnClickListener {
    private ActivityOffshelfMainLayoutBinding binding;
    private OffShelfListAdapter adapter;
    private String curUniqueNo;
    private Integer id= null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        viewModel.getInfoMutableLiveData().observe(this,offShelfUndoEarlyBean->{
            if(offShelfUndoEarlyBean!=null){
                id = offShelfUndoEarlyBean.getId();
                if(offShelfUndoEarlyBean.getShelfList()!=null&&!offShelfUndoEarlyBean.getShelfList().isEmpty()||TextUtils.isEmpty(offShelfUndoEarlyBean.getCollectionAreaCode())){
                    if(offShelfUndoEarlyBean.getShelfList()!=null&&!offShelfUndoEarlyBean.getShelfList().isEmpty()){
                        binding.includeTitle.rlRightLayout.setVisibility(View.VISIBLE);
                        binding.includeEmpty.getRoot().setVisibility(View.GONE);
                        curUniqueNo = offShelfUndoEarlyBean.getUniqueNo();
                        adapter.updateList(offShelfUndoEarlyBean.getShelfList());
                        binding.tvWaveCode.setText(curUniqueNo);
                        binding.rlWaveLayout.setVisibility(View.GONE);
                        binding.tvShelfCode.setText(offShelfUndoEarlyBean.getShelfList().get(0).getShelfNo());
                        binding.tvProductCode.setText("");
                    }else{
                        binding.includeEmpty.getRoot().setVisibility(View.VISIBLE);
                        binding.includeEmpty.tvEmptyTips.setText("There is no product wave offshelf");
                    }
                }else {
                    binding.includeTitle.rlRightLayout.setVisibility(View.GONE);
                    binding.contentLayout.setVisibility(View.GONE);
                    binding.rlWaveLayout.setVisibility(View.VISIBLE);
                    binding.tvShelfNum.setText(offShelfUndoEarlyBean.getCollectionAreaCode());
                }
            }
        });
        viewModel.getCountInfo().observe(this,countBean->{
            if(countBean!=null&&countBean>0){
                binding.includeTitle.tvCount.setVisibility(View.VISIBLE);
                binding.includeTitle.tvCount.setText(String.valueOf(countBean));
            }
        });
        viewModel.getOffShelfState().observe(this,state->{
            if (state){
                SoundPoolUtil.getInstance().soundPoll(OFF_SHELF_SUCCESS);
                viewModel.initData(id);
            }
        });
        viewModel.initData(id);
    }

    private void initView(){
        binding = ActivityOffshelfMainLayoutBinding.inflate(getLayoutInflater());
        binding.includeTitle.tvTitle.setText(R.string.offshelf_title);
        binding.includeTitle.imgRight.setOnClickListener(this);
        binding.includeTitle.rlRightLayout.setVisibility(View.VISIBLE);
        binding.tvNext.setOnClickListener(this);
        setContentView(binding.getRoot());
        adapter = new OffShelfListAdapter(this, new ArrayList<>());
        binding.rvOffshelfList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOffshelfList.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            if(adapter.getItemData(position)!=null){
                binding.tvShelfCode.setText(adapter.getItemData(position).getShelfNo());
                binding.tvProductCode.setText("");
            }

        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_next:
                id = null;
                viewModel.initData(id);
                break;
            case R.id.img_right:
                Intent intent = new Intent(this,OffShelfUndoListActivity.class);
                intent.putExtra("data",curUniqueNo);
                startActivityForResult(intent,0x01);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMode(BarcodeScanResult scanResult) {
        if(!TextUtils.isEmpty(scanResult.getQrData())){
            /*if(StringUtils.isShelfCode(scanResult.getQrData())){
                if(!viewModel.checkContainScanShelfCode(scanResult.getQrData())){
                    SoundPoolUtil.getInstance().soundPoll(WRONG_SHELF);
                }else{
                    adapter.setScanShelfNo(scanResult.getQrData());
                }
                binding.tvShelfCode.setText(scanResult.getQrData());
            }else{*/
                if(!TextUtils.isEmpty(binding.tvShelfCode.getText())){
                    binding.tvProductCode.setText(scanResult.getQrData());
                    if(!viewModel.checkContainScanProductCode(binding.tvShelfCode.getText().toString(),scanResult.getQrData())){
                        SoundPoolUtil.getInstance().soundPoll(WRONG_SKU);
                    }else{
                        viewModel.offShelf(id,binding.tvShelfCode.getText().toString(),scanResult.getQrData());
                    }
                }
//            }
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
    protected OffShelfViewModel onCreateViewModel() {
        return new ViewModelProvider(this, new OffShelfViewModel.OffShelfModelFactory()).get(OffShelfViewModel.class);
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
                    Integer selectId = data.getIntExtra("selectId",0);
                    viewModel.initData(selectId);
                    break;
            }
        }
    }
}