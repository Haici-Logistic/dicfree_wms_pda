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

import com.blankj.utilcode.util.LogUtils;
import com.dicfree.pda.R;
import com.dicfree.pda.databinding.BasketListItemBinding;
import com.dicfree.pda.databinding.OnshelfUndoListItemBinding;
import com.dicfree.pda.databinding.OnshelfUndoListSubItemBinding;
import com.dicfree.pda.model.bean.OffShelfUndoEarlyBean;
import com.dicfree.pda.model.bean.OrderInfo;
import com.dicfree.pda.utils.PrinterManager;

import java.util.List;

public class BasketListAdapter extends RecyclerView.Adapter<BindingViewHolder>{
    private final Context context;
    private List<OrderInfo> list;
    private String scanNo="";
    private OnItemClickListener itemClickListener;
    public BasketListAdapter(Context context, List<OrderInfo> list,OnItemClickListener onItemClickListener){
        this.context = context;
        this.list = list;
        this.itemClickListener = onItemClickListener;
    }

    public void setScanNo(String scanNo) {
        this.scanNo = scanNo;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        BasketListItemBinding binding =BasketListItemBinding.inflate(layoutInflater,parent,false);
        BindingViewHolder viewHolder = new BindingViewHolder(binding.getRoot());
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        BasketListItemBinding binding = (BasketListItemBinding) holder.getBinding();
        OrderInfo orderInfo =list.get(position);
        if(scanNo.equals(orderInfo.getWaybill())){
            binding.rlMainLayout.setBackgroundColor(context.getColor(R.color.color_theme));
        }else{
            binding.rlMainLayout.setBackgroundColor(context.getColor(R.color.white));
        }
        binding.tvBasketNo.setText(orderInfo.getWaybill());
        binding.tvPrint.setOnClickListener(v -> {
            LogUtils.dTag("BasketListAdapter","onItemClick====>"+position);
//         if(itemClickListener!=null){
//             itemClickListener.onItemClick(position);
//         }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void updateList(List<OrderInfo> list){
        this.list = list;
        notifyDataSetChanged();
    }

}
