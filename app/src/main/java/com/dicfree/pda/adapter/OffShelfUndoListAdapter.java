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
import com.dicfree.pda.databinding.OffshelfUndoListItemBinding;
import com.dicfree.pda.databinding.OnshelfUndoListItemBinding;
import com.dicfree.pda.databinding.OnshelfUndoListSubItemBinding;
import com.dicfree.pda.model.bean.OffShelfUndoBean;
import com.dicfree.pda.model.bean.OnShelfUndoBean;

import java.util.List;

public class OffShelfUndoListAdapter extends RecyclerView.Adapter<BindingViewHolder>{


    private final Context context;
    private List<OffShelfUndoBean> list;
    private OnItemClickListener itemClickListener;
    private String curUniqueNo;
    public OffShelfUndoListAdapter(Context context, String curUniqueNo,List<OffShelfUndoBean> list,OnItemClickListener onItemClickListener){
        this.context = context;
        this.list = list;
        this.curUniqueNo = curUniqueNo;
        this.itemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        OffshelfUndoListItemBinding binding =OffshelfUndoListItemBinding.inflate(layoutInflater,parent,false);
        BindingViewHolder viewHolder = new BindingViewHolder(binding.getRoot());
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        OffshelfUndoListItemBinding binding = (OffshelfUndoListItemBinding) holder.getBinding();
        OffShelfUndoBean offShelfUndoBean =list.get(position);
        if(curUniqueNo.equals(offShelfUndoBean.getUniqueNo())){
            binding.cvBg.setCardBackgroundColor(context.getColor(R.color.color_theme));
        }else{
            binding.cvBg.setCardBackgroundColor(context.getColor(R.color.color_727272));
        }
        binding.tvOrderNo.setText(offShelfUndoBean.getUniqueNo());
        binding.tvCount.setText(String.valueOf(offShelfUndoBean.getTotalSnCount()));
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener!=null){
                    itemClickListener.onItemClick(position);
                }
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

    public void updateList(List<OffShelfUndoBean> list){
        this.list = list;
        notifyDataSetChanged();
    }

}
