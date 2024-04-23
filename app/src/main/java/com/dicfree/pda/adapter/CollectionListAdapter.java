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
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dicfree.pda.R;
import com.dicfree.pda.databinding.CollectionUndoListItemBinding;
import com.dicfree.pda.databinding.OffshelfUndoListItemBinding;
import com.dicfree.pda.model.bean.CollectionUndoInfo;
import com.dicfree.pda.model.bean.OffShelfUndoBean;

import java.util.List;

public class CollectionListAdapter extends RecyclerView.Adapter<BindingViewHolder>{


    private final Context context;
    private List<CollectionUndoInfo> list;
    private OnItemClickListener itemClickListener;
    public CollectionListAdapter(Context context, List<CollectionUndoInfo> list, OnItemClickListener onItemClickListener){
        this.context = context;
        this.list = list;
        this.itemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        CollectionUndoListItemBinding binding =CollectionUndoListItemBinding.inflate(layoutInflater,parent,false);
        BindingViewHolder viewHolder = new BindingViewHolder(binding.getRoot());
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        CollectionUndoListItemBinding binding = (CollectionUndoListItemBinding) holder.getBinding();
        CollectionUndoInfo collectionUndoInfo =list.get(position);
        binding.cvBg.setCardBackgroundColor(context.getColor(R.color.color_727272));
        binding.tvOrderNo.setText(collectionUndoInfo.getCollectionAreaCode());
        binding.tvCount.setText(collectionUndoInfo.getUniqueNo());
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void updateList(List<CollectionUndoInfo> list){
        this.list = list;
        notifyDataSetChanged();
    }

}
