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

import static com.dicfree.pda.base.BaseCodeConstants.STATUS_CODE_DEVICE_NOT_REGISTER;
import static com.dicfree.pda.utils.SoundPoolUtil.ON_SHELF_SUCCESS;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.dicfree.pda.R;
import com.dicfree.pda.base.BaseActivityWithLoading;
import com.dicfree.pda.databinding.ActivityIndexLayoutBinding;
import com.dicfree.pda.databinding.ActivityMainLayoutBinding;
import com.dicfree.pda.model.bean.BarcodeScanResult;
import com.dicfree.pda.utils.PrinterManager;
import com.dicfree.pda.utils.SoundPoolUtil;
import com.dicfree.pda.utils.StringUtils;
import com.dicfree.pda.utils.WeightManager;
import com.dicfree.pda.view.ComponentCommonDialog;
import com.dicfree.pda.viewmodel.MainViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//@Route(path = RouterConstants.MAIN_ACTIVITY)
public class IndexActivity extends BaseActivityWithLoading<MainViewModel> implements View.OnClickListener {
    private ActivityIndexLayoutBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WeightManager.getInstance().init(this);
        initView();
        viewModel.initData();
        viewModel.getInfoMutableLiveData().observe(this,mainMergeInfo->{
            if(mainMergeInfo.getState().equals(STATUS_CODE_DEVICE_NOT_REGISTER)){
                ComponentCommonDialog.Companion.show(this, "温馨提示", "您的设备号"+ DeviceUtils.getMacAddress()+"还没有注册,请联系管理员添加!", "取消", "确定",
                        (dialog, view) -> {
                            dialog.dismiss();
                        }, (dialog, view) -> {
                            dialog.dismiss();
                        }, Gravity.CENTER,false,false);
            }
        });
    }

    private void initView(){
        binding = ActivityIndexLayoutBinding.inflate(getLayoutInflater());
        binding.includeTitle.tvTitle.setText(R.string.index_title);
        binding.includeTitle.imgRight.setOnClickListener(this);
        binding.tvOffshelf.setOnClickListener(this);
        binding.tvBasket.setOnClickListener(this);
        binding.tvCollection.setOnClickListener(this);
        binding.tvOnshefl.setOnClickListener(this);
        binding.tvInventory.setOnClickListener(this);
        binding.tvOutbound.setOnClickListener(this);
        binding.tvLogShare.setOnClickListener(this);
        setContentView(binding.getRoot());
        PrinterManager.getInstance().initPermission(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_log_share:
                logShare(this);
                break;
            case R.id.tv_inventory:
                startActivity(new Intent(this,InboundActivity.class));
                break;
            case R.id.tv_outbound:
                startActivity(new Intent(this,OutboundActivity.class));
                break;
            case R.id.tv_collection:
                startActivity(new Intent(this,ProductWaveActivity.class));
                break;
            case R.id.tv_basket:
                startActivity(new Intent(this,BasketMainActivity.class));
                break;
            case R.id.tv_offshelf:
                startActivity(new Intent(this,OffShelfMainActivity.class));
                break;
            case R.id.tv_onshefl:
                startActivity(new Intent(this,MainActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected MainViewModel onCreateViewModel() {
        return new ViewModelProvider(this, new MainViewModel.MainViewModelFactory()).get(MainViewModel.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    /**
     * 日志分享
     * @param context
     */
    public static void logShare(Context context) {
        try {
            File logPath = context.getApplicationContext().getExternalFilesDir("logcat");
            File zipFile = new File(logPath, "logcat.zip");
            if (zipFile.exists()) {
                zipFile.delete();
            }
            File[] files = logPath.listFiles();
            List<String> paths = new ArrayList<>();
            for (File file : files) {
                paths.add(file.getPath());
            }
            ZipUtils.zipFiles(paths, zipFile.getPath());
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".share.log_provider", zipFile);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("application/zip");
            Intent chooser = Intent.createChooser(intent, "Log Share");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooser);
        } catch (Exception e) {
            LogUtils.d(e.getMessage());
        }
    }
}


