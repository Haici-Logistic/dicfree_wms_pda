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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.dicfree.pda.model.bean.BluetoothParameter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @Author: shengcaiwang
 * @Description:称重管理类
 * @Date: 2024/1/12
 */
public class WeightManager {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String TAG="WeightManager";
    private volatile static WeightManager mInstance;

    private boolean connectState = false;

    public static WeightManager getInstance() {
        if (mInstance == null) {
            synchronized (WeightManager.class) {
                mInstance = new WeightManager();
            }
        }
        return mInstance;
    }

    private Context mContext;

    private WeightListen mWeightListen;

    private BluetoothAdapter mBluetoothAdapter;

    /**
     * 设备蓝牙开关状态
     */
    private boolean bluetoothState = false;

    private BluetoothDevice mBluetoothDevice;

    private BluetoothSocket mBluetoothSocket;

    private InputStream mInputStream;

    private OutputStream mOutputStream;

    private String macAddress;
    private boolean isConnecting = false;

    private  boolean receiveDataState = false;
    public WeightListen getWeightListen() {
        return mWeightListen;
    }

    public void setWeightListen(WeightListen mWeightListen) {
        this.mWeightListen = mWeightListen;
    }


    public void init(Context context){
        this.mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter!=null){
            if(mBluetoothAdapter.isEnabled()){
                mBluetoothAdapter.startDiscovery();
            }else{
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mContext.startActivity(enableBtIntent);
            }
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // Register for broadcasts when discovery has finished
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mFindBlueToothReceiver, filter);
    }
    private final BroadcastReceiver mFindBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.dTag(TAG,"onReceive===>"+intent.getAction());
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed
                // already
                BluetoothParameter parameter = new BluetoothParameter();
                int rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);//获取蓝牙信号强度
                if (device != null && device.getName() != null) {
                    parameter.setBluetoothName(device.getName());
                } else {
                    parameter.setBluetoothName("unKnow");
                }
                parameter.setBluetoothMac(device.getAddress());
                parameter.setBluetoothStrength(rssi+ "");
                LogUtils.dTag(TAG,"\nBlueToothName:\t"+device.getName()+"\nMacAddress:\t"+device.getAddress()+"\nrssi:\t"+rssi);
                if(!TextUtils.isEmpty(device.getName())&&device.getName().contains("HC-06")){
                    macAddress =device.getAddress();
                    new ConnectThread(macAddress).start();
                    mBluetoothAdapter.cancelDiscovery();
                }
            }
        }
    };

    /**
     * 检测连接状态 如果没有连接 则去连接
     */
    public void checkConnectState(){
        receiveDataState =true;
        if(!isConnectState()){
            if(TextUtils.isEmpty(macAddress)){
                if(mBluetoothAdapter!=null){
                    if(mBluetoothAdapter.isEnabled()){
                        mBluetoothAdapter.startDiscovery();
                    }else{
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        mContext.startActivity(enableBtIntent);
                    }
                }
            }else{
                if(!isConnecting){
                    new ConnectThread(macAddress).start();
                }
            }
        }else{
            // 连接成功后，启动接收数据的线程
            Thread thread = new Thread(new ReceiveDataThread());
            thread.start();
        }
    }

    public void pauseReceiveData(){
        receiveDataState  =false;
    }
    private class ConnectThread extends Thread{
        private String macAddress;
        public ConnectThread(String mac){
            this.macAddress = mac;
        }
        @Override
        public void run() {
            super.run();
            isConnecting = true;
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(macAddress);
            if(mBluetoothDevice==null)return;
            // 只有通过启动该程序打开蓝牙才能获取设备UUID，该UUID与SERVICE_CHARACTERISTIC一致
            // 若已知悉设备SPP服务UUID则注释下面两行。
            String  SERVICE_CHARACTERISTIC = mBluetoothDevice.getUuids()[0].toString();
            LogUtils.dTag(TAG, "deviceUUID:"+SERVICE_CHARACTERISTIC);

            // 建立蓝牙连接
            UUID uuid = UUID.fromString(SERVICE_CHARACTERISTIC); // SPP通信的UUID

            try {
                // 蓝牙权限已经被授予，可以连接蓝牙，建立通信套接字
                mBluetoothSocket= mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mBluetoothSocket.connect();
                // 定义输入输出流
                mInputStream = mBluetoothSocket.getInputStream();
                mOutputStream = mBluetoothSocket.getOutputStream();
                LogUtils.dTag(TAG, "Connected to device: " + mBluetoothDevice.getName());
                connectState =true;
                if(mWeightListen!=null){
                    mWeightListen.onConnectState(true);
                }
                isConnecting = false;
                // 连接成功后，启动接收数据的线程
                Thread thread = new Thread(new ReceiveDataThread());
                thread.start();
                // 发送数据
            } catch (IOException e) {
                isConnecting = false;
                if(mWeightListen!=null){
                    mWeightListen.onConnectState(false);
                }
                LogUtils.dTag(TAG, "Failed to connect: " + e.getMessage());
                e.printStackTrace();
                // 连接或读取数据失败，关闭连接并进行错误处理
                try {
                    mBluetoothSocket.close();
                } catch (IOException e2) {
                    LogUtils.dTag(TAG, "Error closing Bluetooth socket: " + e2.getMessage());
                }
            }
        }
    }
    private class ReceiveDataThread implements Runnable {
        @Override
        public void run() {
            byte[] arrayOfByte1 = new byte[1024];
            byte[] arrayOfByte2;
            while (receiveDataState) {
                try {
                    int i = mInputStream.read(arrayOfByte1);
                    arrayOfByte2 = new byte[i];
                    System.arraycopy(arrayOfByte1,0,arrayOfByte2,0,i);
                    if(i>0){
                        for (int j = 0; j < arrayOfByte1.length; j++){
                            arrayOfByte1[j] = 0;
                        }
                        String data = new String(arrayOfByte2).replace("+","").replace("=","").replace("kg","").trim();
                        if(mWeightListen!=null){
                            mWeightListen.onWeightReceive(data);
                        }
                    }

                } catch (IOException e) {
                    LogUtils.dTag(TAG, "Error reading data: " + e.getMessage());
                    if(mWeightListen!=null){
                        mWeightListen.onConnectState(false);
                    }
                    connectState =false;
                    try {
                        mBluetoothSocket.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                }
            }
        }
    }

    public boolean isConnectState() {
        return connectState;
    }

    public interface WeightListen{
        public void onConnectState(boolean status);

        public void onWeightReceive(String weight);
    }
}
