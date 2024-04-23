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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dicfree.pda.R;
import com.dicfree.pda.databinding.OnshelfUndoListItemBinding;
import com.dicfree.pda.databinding.OnshelfUndoListSubItemBinding;
import com.dicfree.pda.model.bean.OffShelfUndoEarlyBean;
import com.dicfree.pda.model.bean.OnShelfUndoBean;

import java.util.List;

public class OffShelfListAdapter extends RecyclerView.Adapter<BindingViewHolder>{


    private final Context context;
    private List<OffShelfUndoEarlyBean.ShelfInfo> list;
    private String scanShelfNo="";

    private OnItemClickListener mOnItemClickListener;

    private int selectItemPosition =0;

    public OffShelfListAdapter(Context context, List<OffShelfUndoEarlyBean.ShelfInfo> list){
        this.context = context;
        this.list = list;
    }

    public OffShelfUndoEarlyBean.ShelfInfo getItemData(int position){
        if (list == null||position>list.size()) {
            return null;
        }
        return list.get(position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setScanShelfNo(String scanShelfNo) {
        this.scanShelfNo = scanShelfNo;
        notifyDataSetChanged();
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
    public void onBindViewHolder(@NonNull BindingViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        OnshelfUndoListItemBinding binding = (OnshelfUndoListItemBinding) holder.getBinding();
        OffShelfUndoEarlyBean.ShelfInfo shelfInfo =list.get(position);
        if(scanShelfNo.equals(shelfInfo.getShelfNo())||position==selectItemPosition){
            binding.cvBg.setCardBackgroundColor(context.getColor(R.color.color_warning));
        }else{
            binding.cvBg.setCardBackgroundColor(context.getColor(R.color.color_727272));
        }
        binding.tvOrderNo.setText(shelfInfo.getShelfNo());
        binding.llSub.removeAllViews();
        if(shelfInfo.getProductDeliveryOrderItemList()!=null&&!shelfInfo.getProductDeliveryOrderItemList().isEmpty()){
            for (OffShelfUndoEarlyBean.ProductDeliveryOrderItem deliveryOrderItem:shelfInfo.getProductDeliveryOrderItemList()){
                OnshelfUndoListSubItemBinding undoListSubItemBinding =OnshelfUndoListSubItemBinding.inflate(LayoutInflater.from(context));
                undoListSubItemBinding.tvProductCode.setText(deliveryOrderItem.getProductSkuCode());
                undoListSubItemBinding.tvCount.setText(String.valueOf(deliveryOrderItem.getOffShelfCount()));
                undoListSubItemBinding.tvCount1.setText("/"+deliveryOrderItem.getTotalCount());
                binding.llSub.addView(undoListSubItemBinding.getRoot());
            }
        }
        binding.getRoot().setOnClickListener(v -> {
            selectItemPosition = position;
            notifyDataSetChanged();
            if(mOnItemClickListener!=null){
                mOnItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void updateList(List<OffShelfUndoEarlyBean.ShelfInfo> list){
        this.selectItemPosition = 0;
        this.list = list;
        notifyDataSetChanged();
    }

}
