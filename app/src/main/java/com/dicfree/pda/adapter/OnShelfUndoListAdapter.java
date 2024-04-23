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
package com.dicfree.pda.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dicfree.pda.R;
import com.dicfree.pda.databinding.OnshelfUndoListItemBinding;
import com.dicfree.pda.databinding.OnshelfUndoListSubItemBinding;
import com.dicfree.pda.model.bean.OnShelfUndoBean;

import java.util.List;

public class OnShelfUndoListAdapter extends RecyclerView.Adapter<BindingViewHolder>{


    private final Context context;
    private List<OnShelfUndoBean> list;

    public OnShelfUndoListAdapter(Context context,List<OnShelfUndoBean> list){
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        OnshelfUndoListItemBinding binding =OnshelfUndoListItemBinding.inflate(layoutInflater,parent,false);
        BindingViewHolder viewHolder = new BindingViewHolder(binding.getRoot());
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, int position) {
        OnshelfUndoListItemBinding binding = (OnshelfUndoListItemBinding) holder.getBinding();
        OnShelfUndoBean onShelfUndoBean =list.get(position);
        binding.getRoot().setBackground(context.getDrawable(R.drawable.white_shape_6_corner_bg));
        binding.tvOrderNo.setText(onShelfUndoBean.getThirdOrderNo());
        if(onShelfUndoBean.getProductArrivalOrderItemList()!=null&&!onShelfUndoBean.getProductArrivalOrderItemList().isEmpty()){
            for (OnShelfUndoBean.ArrivalOrderItem arrivalOrderItem:onShelfUndoBean.getProductArrivalOrderItemList()){
                if(arrivalOrderItem.getTotalCount() ==arrivalOrderItem.getOnShelfCount()){
                    continue;
                }
                OnshelfUndoListSubItemBinding undoListSubItemBinding =OnshelfUndoListSubItemBinding.inflate(LayoutInflater.from(context));
                undoListSubItemBinding.tvProductCode.setText(arrivalOrderItem.getProductCode());
                undoListSubItemBinding.tvCount.setText(String.valueOf(arrivalOrderItem.getTotalCount()-arrivalOrderItem.getOnShelfCount()));
                undoListSubItemBinding.tvCount1.setText("/"+arrivalOrderItem.getTotalCount());
                binding.llSub.addView(undoListSubItemBinding.getRoot());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void updateList(List<OnShelfUndoBean> list){
        this.list = list;
        notifyDataSetChanged();
    }

}
