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
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ToastUtils;
import com.dicfree.pda.R;
import com.dicfree.pda.adapter.BasketListAdapter;
import com.dicfree.pda.adapter.OnItemClickListener;
import com.dicfree.pda.base.BaseActivityWithLoading;
import com.dicfree.pda.databinding.ActivityBasketDetailLayoutBinding;
import com.dicfree.pda.model.bean.BarcodeScanResult;
import com.dicfree.pda.model.bean.OrderInfo;
import com.dicfree.pda.utils.PrinterManager;
import com.dicfree.pda.viewmodel.BasketViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Route(path = RouterConstants.MAIN_ACTIVITY)
public class BasketDetailActivity extends BaseActivityWithLoading<BasketViewModel> implements View.OnClickListener,OnItemClickListener {
    private ActivityBasketDetailLayoutBinding binding;
    private List<OrderInfo> showOrderInfos;
    private String uniqueNo;
    Map<String, List<OrderInfo>> orderMaps = new HashMap<>();
    private String showType="OO";
    BasketListAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uniqueNo = getIntent().getStringExtra("uniqueNo");
        viewModel.initData(uniqueNo);
        initView();
        initData();
        PrinterManager.getInstance().connectPrint(this, result1 -> {
            binding.tvConnectStatus.setText(result1?"OnLine":"Offline");
        });
    }

    private void initData(){
        viewModel.getBindBasketLiveData().observe(this,waybill->{
            binding.tvBillNo.setText("");
            binding.tvBasketNo.setText("");
            List<OrderInfo> nextOrderInfos = new ArrayList<>();
            for(OrderInfo orderInfo:showOrderInfos){
                if(!orderInfo.getWaybill().equals(waybill)){
                    nextOrderInfos.add(orderInfo);
                }
            }
            if(nextOrderInfos.size()>0){
                adapter.updateList(nextOrderInfos);
            }else{
                if(showType.equals("OO")){
                    if(orderMaps.get("OM").size()>0){
                        showType ="OM";
                        nextOrderInfos.addAll(orderMaps.get("OM"));
                    }else if(orderMaps.get("MM").size()>0){
                        nextOrderInfos.addAll(orderMaps.get("MM"));
                        showType ="MM";
                    }
                }else if(showType.equals("OM")){
                    showType ="MM";
                    nextOrderInfos.addAll(orderMaps.get("MM"));
                }
                if(nextOrderInfos.size()>0){
                    showOrderInfos.clear();
                    showOrderInfos.addAll(nextOrderInfos);
                    showOrderInfo();
                    binding.contentLayout.setVisibility(View.GONE);
                    binding.rlWaveLayout.setVisibility(View.VISIBLE);
                }else{
                    adapter.updateList(nextOrderInfos);
                }
            }
        });
        viewModel.getInfoMutableLiveData().observe(this,orderInfos -> {
            orderMaps.clear();
            if(orderInfos!=null&&!orderInfos.isEmpty()){
                List<OrderInfo> oooderInos= new ArrayList<>();
                List<OrderInfo> omoderInos= new ArrayList<>();
                List<OrderInfo> mmoderInos= new ArrayList<>();
                for(OrderInfo orderInfo:orderInfos){
                    if(orderInfo.getType().equals("OO")){
                        oooderInos.add(orderInfo);
                    }else if(orderInfo.getType().equals("OM")){
                        omoderInos.add(orderInfo);
                    }else if(orderInfo.getType().equals("MM")){
                        mmoderInos.add(orderInfo);
                    }
                }
                orderMaps.put("OO",oooderInos);
                orderMaps.put("OM",omoderInos);
                orderMaps.put("MM",mmoderInos);
                if(orderMaps.get("OO").size()>0){
                    showType="OO";
                    showOrderInfos = orderMaps.get("OO");
                }else if(orderMaps.get("OM").size()>0){
                    showType="OM";
                    showOrderInfos = orderMaps.get("OM");
                }else if(orderMaps.get("MM").size()>0){
                    showOrderInfos = orderMaps.get("MM");
                    showType="MM";
                }
                showOrderInfo();
            }
        });
    }

    private void showOrderInfo(){
        binding.tvNum.setText(String.valueOf(showOrderInfos.size()));
        if(showType.equals("OO")){
            binding.includeTitle.tvTitle.setText(getResources().getString(R.string.basket_main_title)+"(OO)");
            binding.tvDes.setText("One product one piece");
        }else if(showType.equals("OM")){
            binding.includeTitle.tvTitle.setText(getResources().getString(R.string.basket_main_title)+"(OM)");
            binding.tvDes.setText("One product with multiple pieces");
        }else if(showType.equals("MM")){
            binding.includeTitle.tvTitle.setText(getResources().getString(R.string.basket_main_title)+"(MM)");
            binding.tvDes.setText("Multiple products with multiple pieces");
        }
        if(adapter==null){
            adapter = new BasketListAdapter(this, showOrderInfos, this);
            binding.rvOffshelfList.setAdapter(adapter);
            binding.rvOffshelfList.setLayoutManager(new LinearLayoutManager(this));
        }else{
            adapter.updateList(showOrderInfos);
        }
    }
    private void initView(){
        binding = ActivityBasketDetailLayoutBinding.inflate(getLayoutInflater());
        binding.includeTitle.tvTitle.setText(R.string.basket_main_title);
        binding.tvStart.setOnClickListener(this);
        binding.tvWaveCode.setText(uniqueNo);
        binding.contentLayout.setVisibility(View.GONE);
        binding.rlWaveLayout.setVisibility(View.VISIBLE);
        setContentView(binding.getRoot());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_start:
                //打印所有的单号
                if(PrinterManager.getInstance().checkConnect()){
                    for(OrderInfo orderInfo:showOrderInfos){
                        PrinterManager.getInstance().sendPrintData(orderInfo.getWaybill(),true);
                    }
                }else{
                    PrinterManager.getInstance().connectPrint(this, result -> {
                        if(result){
                            for(OrderInfo orderInfo:showOrderInfos){
                                PrinterManager.getInstance().sendPrintData(orderInfo.getWaybill(),true);
                            }
                        }
                    });
                }
                binding.contentLayout.setVisibility(View.VISIBLE);
                binding.rlWaveLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMode(BarcodeScanResult scanResult) {
        if(!TextUtils.isEmpty(scanResult.getQrData())){
            if(TextUtils.isEmpty(binding.tvBillNo.getText())){
                if(viewModel.checkIsWaybillNo(scanResult.getQrData())){
                    binding.tvBillNo.setText(scanResult.getQrData());
                    adapter.setScanNo(scanResult.getQrData());
                }else{
                    ToastUtils.showLong("Wrong waybill");
                }
            }else{
                if(viewModel.checkIsWaybillNo(scanResult.getQrData())){
                    binding.tvBillNo.setText(scanResult.getQrData());
                    adapter.setScanNo(scanResult.getQrData());
                }else{
                    binding.tvBasketNo.setText(scanResult.getQrData());
                    viewModel.orderBindBasket(binding.tvBasketNo.getText().toString(),binding.tvBillNo.getText().toString());
                }
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
    protected BasketViewModel onCreateViewModel() {
        return new ViewModelProvider(this, new BasketViewModel.BasketViewModelFactory()).get(BasketViewModel.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemClick(int position) {
        if(PrinterManager.getInstance().checkConnect()){
            PrinterManager.getInstance().sendPrintData(showOrderInfos.get(position).getWaybill(),true);
        }else{
            PrinterManager.getInstance().connectPrint(this, result -> {
                if(result){
                    PrinterManager.getInstance().sendPrintData(showOrderInfos.get(position).getWaybill(),true);
                }
            });
        }
    }
}