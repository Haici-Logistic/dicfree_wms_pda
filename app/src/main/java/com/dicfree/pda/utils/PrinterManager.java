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

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.dicfree.pda.R;
import com.dicfree.pda.adapter.OnItemClickListener;
import com.dicfree.pda.adapter.SelectDeviceListAdapter;
import com.dicfree.pda.base.BaseDialogFragment;
import com.dicfree.pda.databinding.ActivityBasketMainLayoutBinding;
import com.dicfree.pda.databinding.DialogDeviceListLayoutBinding;
import com.dicfree.pda.model.bean.BluetoothParameter;
import com.dicfree.pda.view.CommonLoadingDialog;
import com.gprinter.bean.PrinterDevices;
import com.gprinter.command.LabelCommand;
import com.gprinter.io.BluetoothPort;
import com.gprinter.io.EthernetPort;
import com.gprinter.io.PortManager;
import com.gprinter.io.SerialPort;
import com.gprinter.io.UsbPort;
import com.gprinter.utils.CallbackListener;
import com.gprinter.utils.Command;
import com.gprinter.utils.ConnMethod;
import com.gprinter.utils.SDKUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * @Author: shengcaiwang
 * @Description:打印机业务
 * @Date: 2023/11/4
 */
public class PrinterManager implements CallbackListener{
    private static final String TAG="PrinterManager";
    public static PortManager portManager=null;

    private volatile static PrinterManager mInstance;
    private PermissionUtils permissionUtils;

    private List<BluetoothParameter> bluetoothParameterList= new ArrayList<>();

    /**
     * 设备蓝牙开关状态
     */
    private boolean bluetoothState = false;
    private  PrinterDevices device;
    private BluetoothAdapter mBluetoothAdapter;
    CommonLoadingDialog dialog;
    private boolean isConnect =false;
    /**
     * 未执行的打印任务队列
     */
    private final LinkedList<Vector<Byte>> pendingQueue = new LinkedList<>();


    Dialog selectDeviceDialog;

    private AppCompatActivity mActivity;

    private ConnectCallBack mConnectCallBack;
    public static PrinterManager getInstance() {
        if (mInstance == null) {
            synchronized (PrinterManager.class) {
                mInstance = new PrinterManager();
            }
        }
        return mInstance;
    }

    private final BroadcastReceiver mFindBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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
                for (BluetoothParameter p:bluetoothParameterList) {
                    if (p.getBluetoothMac().equals(parameter.getBluetoothMac())){//防止重复添加
                        return;
                    }
                    parameter.setPair(device.getBondState() == BluetoothDevice.BOND_BONDED);
                }
                if(!TextUtils.isEmpty(device.getName())&&device.getName().startsWith("SMK")){
                    bluetoothParameterList.add(parameter);
                    connect(parameter.getBluetoothMac());
                    if(dialog!=null&&!dialog.isDismissed()){
                        dialog.updateLoadingText(R.string.connecting);
                    }
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                LogUtils.dTag(TAG,"bluetoothParameterList.size="+bluetoothParameterList.size());
                if(mActivity==null&&!mBluetoothAdapter.isEnabled())return;
                if(bluetoothParameterList.isEmpty()){
                    ToastUtils.showLong(R.string.search_no_devices);
                    if(dialog!=null&&!dialog.isDismissed()){
                        dialog.dismissAllowingStateLoss();
                    }
//                    return;
                }
//                if(bluetoothParameterList.size()==1){
//                    connect(bluetoothParameterList.get(0).getBluetoothMac());
//                    if(dialog!=null&&!dialog.isDismissed()){
//                        dialog.updateLoadingText(R.string.connecting);
//                    }
//                }else{
//                    showSelectDeviceDialog();
//                }
            }else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int bluetooth_state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                bluetoothState = bluetooth_state==BluetoothAdapter.STATE_ON;
                if(!bluetoothState){
                    isConnect = false;
                }else{
                    if(mActivity!=null&&!mActivity.isFinishing()&&mBluetoothAdapter.isEnabled()){
                        scanDevices(mActivity);
                    }
                }
            }
        }
    };

    private PrinterManager(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothState = mBluetoothAdapter.isEnabled();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // Register for broadcasts when discovery has finished
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        Utils.getApp().registerReceiver(mFindBlueToothReceiver, filter);
    }

    /**
     * 初始化打印机相关的权限
     * @param activity
     */
    public void initPermission(Activity activity){
        if(null ==permissionUtils){
            permissionUtils = new PermissionUtils(activity);
        }
        permissionUtils.requestPermissions(activity.getString(R.string.permission),
                new PermissionUtils.PermissionListener(){
                    @Override
                    public void doAfterGrand(String... permission) {

                    }
                    @Override
                    public void doAfterDenied(String... permission) {
                        for (String p:permission) {
                            switch (p){
                                case Manifest.permission.READ_EXTERNAL_STORAGE:
                                    ToastUtils.showLong(activity.getString(R.string.no_read));
                                    break;
                                case Manifest.permission.ACCESS_FINE_LOCATION:
                                    ToastUtils.showLong(activity.getString(R.string.no_permission));
                                    break;
                            }
                        }
                    }
                },  Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void connect(String mac){
        LogUtils.dTag(TAG, "connect=====>"+SDKUtils.bytesToHexString(mac.getBytes()));
        device =new PrinterDevices.Build()
                .setContext(Utils.getApp())
                .setConnMethod(ConnMethod.BLUETOOTH)
                .setMacAddress(mac)
                .setCommand(Command.TSC)
                .setCallbackListener(this)
                .build();
        connect(device);
    }

    /**
     * 检测链接状态
     * @return
     */
    public boolean checkConnect(){
        return  isConnect;
//        if(isConnect){
//            try{
//                if(portManager!=null&&portManager.getConnectStatus()){
//                    isConnect = true;
//                }
//            }catch (Exception e){
//                isConnect = false;
//            }
//            return  isConnect;
//        }else{
//            return  false;
//        }
    }

    public void connectPrint(AppCompatActivity activity,ConnectCallBack connectCallBack){
        LogUtils.dTag(TAG,"connectPrint======>"+activity.getLocalClassName());
        this.mActivity = activity;
        this.mConnectCallBack = connectCallBack;
        if(checkConnect()){
            mConnectCallBack.onConnect(true);
            return;
        }
        //蓝牙未打开则先打开蓝牙
        if(!bluetoothState){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Utils.getApp().startActivity(enableIntent);
        }else{
            if(device==null){
                scanDevices(mActivity);
            }else{
                if(portManager!=null&&!portManager.getConnectStatus()){
                    connect(device);
                    showLoadingDialog(activity,R.string.search);
                }else{
                    if(mConnectCallBack!=null){
                        mConnectCallBack.onConnect(true);
                    }
                }
            }
        }
    }
    private void  send2Print(AppCompatActivity activity,List<String> datas,boolean isQrCode){

    }

    public void send2Print(AppCompatActivity activity,String content,boolean isQrCode){
        send2Print(activity,getLabelData(content,isQrCode));
    }

    /**
     *
     */
    private boolean send2Print(AppCompatActivity activity, Vector<Byte> data)  {
        this.mActivity = activity;
        //蓝牙未打开则先打开蓝牙
        if(!bluetoothState){
            pendingQueue.offer(data);
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Utils.getApp().startActivity(enableIntent);
            return false;
        }
        //蓝牙打印对象为空 则去扫描蓝牙
        if(device==null){
            pendingQueue.offer(data);
            scanDevices(mActivity);
            return false;
        }

        if(portManager!=null&&!portManager.getConnectStatus()){
            pendingQueue.offer(data);
            connect(device);
            showLoadingDialog(activity,R.string.search);
            return false;
        }
        Command command = portManager.getCommand();
        try {
            if(portManager.getPrinterStatus(command)!=0){
                pendingQueue.offer(data);
                connect(device);
                return  false;
            }
            sendDataToPrinter(data);
        }catch (Exception e){
            LogUtils.dTag(TAG,"send2Print===error:",e);
        }
        return true;
    }

    public void sendPrintData(String data,boolean isQrData){
        try {
            sendDataToPrinter(getLabelData(data,isQrData));
        }catch (Exception e){
            LogUtils.dTag(TAG,"send2Print===error:",e);
        }
    }
    private void scanDevices(AppCompatActivity activity){
        mBluetoothAdapter.startDiscovery();
        showLoadingDialog(activity,R.string.search);
    }

    private void showLoadingDialog(AppCompatActivity activity,int loadingMsgId){
        dialog = new CommonLoadingDialog(loadingMsgId);
        dialog.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CommonBlurDialog);
        dialog.setCancelable(false);
        dialog.show(activity.getSupportFragmentManager(),"");
    }
    /**
     * 连接
     * @param devices
     */
    public  void connect(final PrinterDevices devices){
        LogUtils.dTag(TAG,"connect======>"+devices.getMacAddress());
        isConnect =false;
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                if (portManager!=null) {//先close上次连接
                    portManager.closePort();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }
                }
                if (devices!=null) {
                    switch (devices.getConnMethod()) {
                        case BLUETOOTH://蓝牙
                            portManager = new BluetoothPort(devices);
                            portManager.openPort();
                            break;
                        case USB://USB
                            portManager = new UsbPort(devices);
                            portManager.openPort();
                            break;
                        case WIFI://WIFI
                            portManager = new EthernetPort(devices);
                            portManager.openPort();
                            break;
                        case SERIALPORT://串口
                            portManager=new SerialPort(devices);
                            portManager.openPort();
                            break;
                        default:
                            break;
                    }
                }

            }
        });
    }
    /**
     * 发送数据到打印机 字节数据
     * @param vector
     * @return true发送成功 false 发送失败
     * 打印机连接异常或断开发送时会抛异常，可以捕获异常进行处理
     */
    public static boolean sendDataToPrinter(byte [] vector) throws IOException {
        if (portManager==null){
            return false;
        }
        return portManager.writeDataImmediately(vector);
    }

    /**
     * 获取打印机状态
     * @param printerCommand 打印机命令 ESC为小票，TSC为标签 ，CPCL为面单
     * @return 返回值常见文档说明
     * @throws IOException
     */
    public static int getPrinterState(Command printerCommand, long delayMillis)throws IOException {
        return portManager.getPrinterStatus(printerCommand);
    }

    /**
     * 获取打印机电量
     * @return
     * @throws IOException
     */
    public static int getPower() throws IOException {
        return portManager.getPower();
    }
    /**
     * 获取打印机指令
     * @return
     */
    public static Command getPrinterCommand(){
        return portManager.getCommand();
    }

    /**
     * 设置使用指令
     * @param printerCommand
     */
    public static void setPrinterCommand(Command printerCommand){
        if (portManager==null){
            return;
        }
        portManager.setCommand(printerCommand);
    }

    /**
     * 发送数据到打印机 指令集合内容
     * @param vector
     * @return true发送成功 false 发送失败
     * 打印机连接异常或断开发送时会抛异常，可以捕获异常进行处理
     */
    public static boolean sendDataToPrinter(Vector<Byte> vector) throws IOException {
        if (portManager==null){
            return false;
        }
        return portManager.writeDataImmediately(vector);
    }
    /**
     * 关闭连接
     * @return
     */
    public static void close(){
        if (portManager!=null){
            portManager.closePort();
            portManager=null;
        }
    }

    @Override
    public void onConnecting() {
        LogUtils.dTag(TAG,"onConnecting");
    }

    @Override
    public void onCheckCommand() {
        LogUtils.dTag(TAG,"onCheckCommand");
    }

    @Override
    public void onSuccess(PrinterDevices printerDevices) {
        LogUtils.dTag(TAG,"onSuccess====>"+printerDevices.getMacAddress());
        isConnect = true;
        if(dialog!=null&&!dialog.isDismissed()){
            dialog.dismissAllowingStateLoss();
        }
        if(mConnectCallBack!=null){
            mConnectCallBack.onConnect(true);
        }
        if(!pendingQueue.isEmpty()){
            Vector<Byte> data = pendingQueue.poll();
            send2Print(mActivity,data);
        }
    }

    @Override
    public void onReceive(byte[] bytes) {
        LogUtils.dTag(TAG,"onReceive====>");
    }

    @Override
    public void onFailure() {
        LogUtils.dTag(TAG,"onFailure====>");
        device  = null;
        if(dialog!=null&&!dialog.isDismissed()){
            dialog.dismissAllowingStateLoss();
        }
        ToastUtils.showLong(R.string.printer_connect_fail);
        isConnect = false;
        if(mConnectCallBack!=null){
            mConnectCallBack.onConnect(false);
        }
    }

    @Override
    public void onDisconnect() {
        LogUtils.dTag(TAG,"onDisconnect====>");
        ToastUtils.showLong(R.string.printer_disconnect);
        if(!pendingQueue.isEmpty()){
            pendingQueue.clear();
        }
        isConnect = false;
        device = null;
    }

    /**
     * 标签打印测试页
     *
     * @return
     */
    public  Vector<Byte> getLabelData(String data,boolean isQRCode) {
        LabelCommand tsc = new LabelCommand();
        // 设置标签尺寸宽高，按照实际尺寸设置 单位mm
        tsc.addUserCommand("\r\n");
        tsc.addSize(40, 30);
        // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0 单位mm
        tsc.addGap(3);
        //设置纸张类型为黑标，发送BLINE 指令不能同时发送GAP指令
//        tsc.addBline(2);
        // 设置打印方向
        tsc.addDirection(LabelCommand.DIRECTION.FORWARD, LabelCommand.MIRROR.NORMAL);
        // 设置原点坐标
        tsc.addReference(0, 0);
        //设置浓度
        tsc.addDensity(LabelCommand.DENSITY.DNESITY4);
        // 撕纸模式开启
        tsc.addTear(LabelCommand.RESPONSE_MODE.ON);
        // 清除打印缓冲区
        tsc.addCls();
        if(isQRCode){
            //绘制二维码
            tsc.addBitmap(50,10,180,QRUtils.createQRCodeBitmap(data,180,180,"4"));
            // 绘制简体中文

            tsc.addText(data.length()>10?50:100, 180, LabelCommand.FONTTYPE.SIMPLIFIED_24_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1, data);
        }else{
            // 绘制一维条码
            tsc.add1DBarcode(40, 80, LabelCommand.BARCODETYPE.CODE128, 80, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, data);
        }
        // 打印标签
        tsc.addPrint(1, 1);
        // 打印标签后 蜂鸣器响
        tsc.addSound(2, 100);
        //开启钱箱
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        Vector<Byte> datas = tsc.getCommand();
        // 发送数据
        return datas;
    }

    private void showSelectDeviceDialog(){
        if(dialog!=null&&!dialog.isDismissed()){
            dialog.dismissAllowingStateLoss();
        }
        DialogDeviceListLayoutBinding binding = DialogDeviceListLayoutBinding.inflate(mActivity.getLayoutInflater());
        selectDeviceDialog = new Dialog(mActivity, R.style.myDialogIsScale);
        SelectDeviceListAdapter deviceListAdapter = new SelectDeviceListAdapter(mActivity, bluetoothParameterList, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectDeviceDialog.dismiss();
                connect(bluetoothParameterList.get(position).getBluetoothMac());
                showLoadingDialog(mActivity,R.string.connecting);
            }
        });
        binding.imClose.setOnClickListener(v -> selectDeviceDialog.dismiss());
        binding.undoRvList.setAdapter(deviceListAdapter);
        binding.undoRvList.setLayoutManager(new LinearLayoutManager(mActivity));
        Window window = selectDeviceDialog.getWindow();
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        int[] wh = DisplayUtil.getDeviceWH(mActivity);
        windowParams.x = wh[0];
        windowParams.y = 0;
        // 控制dialog停放位置
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_menu_animStyle);
        //        window.setAttributes(windowParams);
        selectDeviceDialog.setContentView(binding.getRoot());
        selectDeviceDialog.setCanceledOnTouchOutside(false);
        // 最终决定dialog的大小,实际由contentView确定了
        selectDeviceDialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        try {
            selectDeviceDialog.show();
        } catch (Exception e) {
        }
    }

    /**
     * 连接回调
     */
    public interface  ConnectCallBack{
        public void onConnect(boolean result);
    }
}
