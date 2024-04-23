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
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dicfree.pda.R;
import com.dicfree.pda.adapter.OnShelfUndoListAdapter;
import com.dicfree.pda.base.BaseActivityWithLoading;
import com.dicfree.pda.databinding.ActivityOnshelfUndoLayoutBinding;
import com.dicfree.pda.viewmodel.OnShelfUndoViewModel;

import java.util.ArrayList;

/**
 * @Author: shengcaiwang
 * @Description:待上架的列表
 * @Date: 2023/10/29
 */
public class OnShelfUndoListActivity extends BaseActivityWithLoading<OnShelfUndoViewModel> implements View.OnClickListener {
    private ActivityOnshelfUndoLayoutBinding binding;
    private OnShelfUndoListAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        viewModel.initData();
    }

    private void initView(){
        binding = ActivityOnshelfUndoLayoutBinding.inflate(getLayoutInflater());
        binding.includeTitle.tvTitle.setText(R.string.onslelf_undo_list_title);
        binding.includeTitle.imgRight.setOnClickListener(this);
        binding.includeTitle.rlRightLayout.setVisibility(View.VISIBLE);
        binding.includeTitle.imgRight.setImageResource(R.mipmap.ic_delete);
        setContentView(binding.getRoot());
        adapter = new OnShelfUndoListAdapter(this,new ArrayList<>());
        binding.undoRvList.setLayoutManager(new LinearLayoutManager(this));
        binding.undoRvList.setAdapter(adapter);
        viewModel.getInfoMutableLiveData().observe(this,data->{
            if(data!=null){
                adapter.updateList(data);
            }else{
                binding.includeEmpty.getRoot().setVisibility(View.VISIBLE);
                binding.includeEmpty.tvEmptyTips.setText("There is no onshelf undo list");
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
    protected OnShelfUndoViewModel onCreateViewModel() {
        return new ViewModelProvider(this, new OnShelfUndoViewModel.OnShelfUndoViewModelFactory()).get(OnShelfUndoViewModel.class);
    }
}
