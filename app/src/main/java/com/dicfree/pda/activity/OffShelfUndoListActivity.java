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
import com.dicfree.pda.adapter.OffShelfUndoListAdapter;
import com.dicfree.pda.adapter.OnItemClickListener;
import com.dicfree.pda.adapter.OnShelfUndoListAdapter;
import com.dicfree.pda.base.BaseActivityWithLoading;
import com.dicfree.pda.databinding.ActivityOffshelfUndoLayoutBinding;
import com.dicfree.pda.databinding.ActivityOnshelfUndoLayoutBinding;
import com.dicfree.pda.model.bean.OffShelfUndoBean;
import com.dicfree.pda.model.bean.OnShelfUndoBean;
import com.dicfree.pda.viewmodel.OffShelfViewModel;
import com.dicfree.pda.viewmodel.OnShelfUndoViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shengcaiwang
 * @Description:待下架的波次列表
 * @Date: 2023/10/29
 */
public class OffShelfUndoListActivity extends BaseActivityWithLoading<OffShelfViewModel> implements View.OnClickListener {
    private ActivityOffshelfUndoLayoutBinding binding;
    private OffShelfUndoListAdapter adapter;
    private List<OffShelfUndoBean> onShelfUndoBeanList;
    private String curUniqueNo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        curUniqueNo = getIntent().getStringExtra("data");
        initView();
        viewModel.getOffShelfList();
    }

    private void initView(){
        binding = ActivityOffshelfUndoLayoutBinding.inflate(getLayoutInflater());
        binding.includeTitle.tvTitle.setText(R.string.offslelf_undo_list_title);
        binding.includeTitle.imgRight.setOnClickListener(this);
        binding.includeTitle.imgRight.setImageResource(R.mipmap.ic_delete);
        binding.includeTitle.rlRightLayout.setVisibility(View.VISIBLE);
        setContentView(binding.getRoot());
        adapter = new OffShelfUndoListAdapter(this, curUniqueNo,new ArrayList<>(), position -> {
            Intent intent = new Intent();
            intent.putExtra("selectId",onShelfUndoBeanList.get(position).getId());
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
        binding.undoRvList.setLayoutManager(new LinearLayoutManager(this));
        binding.undoRvList.setAdapter(adapter);
        viewModel.getOffShelfUndoList().observe(this,data->{
            if(data!=null){
                onShelfUndoBeanList =data;
                adapter.updateList(onShelfUndoBeanList);
            }else{
                binding.includeEmpty.getRoot().setVisibility(View.VISIBLE);
                binding.includeEmpty.tvEmptyTips.setText("There is no offshelf undo list");
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
    protected OffShelfViewModel onCreateViewModel() {
        return new ViewModelProvider(this, new OffShelfViewModel.OffShelfModelFactory()).get(OffShelfViewModel.class);
    }
}
