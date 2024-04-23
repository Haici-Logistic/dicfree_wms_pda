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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dicfree.pda.R;
import com.dicfree.pda.adapter.CollectionListAdapter;
import com.dicfree.pda.adapter.OffShelfUndoListAdapter;
import com.dicfree.pda.base.BaseActivityWithLoading;
import com.dicfree.pda.databinding.ActivityOffshelfUndoLayoutBinding;
import com.dicfree.pda.databinding.ActivityProductWaveLayoutBinding;
import com.dicfree.pda.databinding.ActivityWaveLocationUndoLayoutBinding;
import com.dicfree.pda.model.bean.CollectionUndoInfo;
import com.dicfree.pda.model.bean.OnShelfUndoBean;
import com.dicfree.pda.viewmodel.OffShelfViewModel;
import com.dicfree.pda.viewmodel.ProductWaveViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shengcaiwang
 * @Description:未完成的波次数量列表
 * @Date: 2023/10/29
 */
public class WaveUndoListActivity extends BaseActivityWithLoading<ProductWaveViewModel> implements View.OnClickListener {
    private ActivityWaveLocationUndoLayoutBinding binding;
    private CollectionListAdapter adapter;
    private List<CollectionUndoInfo> collectionUndoInfoList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        viewModel.getCollectionOffShelfUndoList();
    }

    private void initView(){
        binding = ActivityWaveLocationUndoLayoutBinding.inflate(getLayoutInflater());
        binding.includeTitle.tvTitle.setText(R.string.product_wave_locaton_list_title);
        binding.includeTitle.rlRightLayout.setVisibility(View.VISIBLE);
        binding.includeTitle.imgRight.setOnClickListener(this);
        binding.includeTitle.imgRight.setImageResource(R.mipmap.ic_delete);
        setContentView(binding.getRoot());
        adapter = new CollectionListAdapter(this,new ArrayList<>(), position -> {
            Intent intent = new Intent();
            intent.putExtra("data",collectionUndoInfoList.get(position).getId());
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
        binding.rvWaveList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvWaveList.setAdapter(adapter);
        viewModel.getInfoMutableLiveData().observe(this,data->{
            if(data!=null){
                collectionUndoInfoList =data;
                adapter.updateList(collectionUndoInfoList);
            }else{
                binding.includeEmpty.getRoot().setVisibility(View.VISIBLE);
                binding.includeEmpty.tvEmptyTips.setText("There is no wave collection undo list");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_right:
                this.finish();
                break;
        }
    }
    @Override
    protected ProductWaveViewModel onCreateViewModel() {
        return new ViewModelProvider(this, new ProductWaveViewModel.ProductWaveViewModelFactory()).get(ProductWaveViewModel.class);
    }
}
