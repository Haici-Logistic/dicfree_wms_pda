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
package com.dicfree.pda.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.dicfree.pda.model.bean.BarcodeScanResult;

import org.greenrobot.eventbus.EventBus;

/**
 * @Author: shengcaiwang
 * @Description: 扫码管理类
 * @Date: 2023/10/29
 */
public class ScanBarcodeManager {
    private static final String TAG="ScanBarcodeManager";
    private static final ScanBarcodeManager sInstance = new ScanBarcodeManager();
    private final static String SCAN_ACTION = "urovo.rcv.message";
    public static ScanBarcodeManager getInstance(){
        return sInstance;
    }
    private ScanBarcodeManager(){
    }

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SCAN_ACTION.equals(intent.getAction())) {
                byte[] barcode = intent.getByteArrayExtra("barocode");
                int barocodelen = intent.getIntExtra("length", 0);
                byte temp = intent.getByteExtra("barcodeType", (byte) 0);
                String barcodeStr = new String(barcode, 0, barocodelen);
                EventBus.getDefault().post(new BarcodeScanResult(barcodeStr));
                LogUtils.dTag(TAG,"onReceive------->scanResult:"+barcodeStr);
            }
        }
    };

    public void init(){
        LogUtils.dTag(TAG,"init");
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        Utils.getApp().registerReceiver(mScanReceiver, filter);
    }

    public void onPause(){
        LogUtils.dTag(TAG,"onPause");
        Utils.getApp().unregisterReceiver(mScanReceiver);
    }

    private String bytesToHexString(byte[] arr) {
        String s = "[]";
        if (arr != null) {
            s = "[";
            for (int i = 0; i < arr.length; i++) {
                s += "0x" + Integer.toHexString(arr[i]) + ", ";
            }
            s = s.substring(0, s.length() - 2) + "]";
        }
        return s;
    }

}
