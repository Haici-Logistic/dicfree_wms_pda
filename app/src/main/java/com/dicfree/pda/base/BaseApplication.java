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
package com.dicfree.pda.base;

import android.app.Application;

import androidx.annotation.NonNull;

//import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.dicfree.pda.base.bean.BuildEnv;
import com.dicfree.pda.base.bean.GatewayFamily;

import java.util.ArrayList;
import java.util.Map;

public abstract class BaseApplication extends Application {
    private Boolean isMainProcess;
    private static final String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        SPUtils.getInstance().put(BuildEnv.class.getSimpleName(), BuildEnv.PRO.name());
        if (!isMainProcess()) {
            return;
        }
        Utils.init(this);
        initConfig();
        initLog();
        initCrash();
//        ARouter.init(this); // 尽可能早，推荐在Application中初始化
    }

    /**
     * 初始化配置数据，包含网关、错误转换信息等等
     */
    private void initConfig() {
        BaseConfigManager.getInstance().setGatewayFamily(getGatewayFamily());
        BaseConfigManager.getInstance().setErrorConverter(getErrorConverter());
    }

    @NonNull
    protected abstract Map<BuildEnv, GatewayFamily> getGatewayFamily();

    @NonNull
    protected abstract BaseConfigManager.BaseErrorConverter getErrorConverter();

    protected void initLog() {
        String env = BaseConfigManager.getInstance().getCurrentFamily().getEnv().name();
        LogUtils.Config config = LogUtils.getConfig().setLogSwitch(AppUtils.isAppDebug())// 设置 log 总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(AppUtils.isAppDebug())// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置 log 全局标签，默认为空
                // 当全局标签不为空时，我们输出的 log 全部为该 tag，
                // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
                .setLogHeadSwitch(false)// 设置 log 头信息开关，默认为开
                .setLog2FileSwitch(AppUtils.isAppDebug())// 打印 log 时是否存到文件的开关，默认关
                .setDir(Utils.getApp().getApplicationContext().getExternalFilesDir("logcat"))// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix("app")// 当文件前缀为空时，默认为"util"，即写入文件为"util-yyyy-MM-dd$fileExtension"
                .setFileExtension(".log")// 设置日志文件后缀
                .setBorderSwitch(false)// 输出日志是否带边框开关，默认开
                .setSingleTagSwitch(true)// 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
                .setConsoleFilter(LogUtils.V)// log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
                .setFileFilter(LogUtils.V)// log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
                .setStackDeep(1)// log 栈深度，默认为 1
                .setStackOffset(0)// 设置栈偏移，比如二次封装的话就需要设置，默认为 0
                .setSaveDays(3)// 设置日志可保留天数，默认为 -1 表示无限时长
                // 新增 ArrayList 格式化器，默认已支持 Array, Throwable, Bundle, Intent 的格式化输出
                .addFormatter(new LogUtils.IFormatter<ArrayList>() {
                    @Override
                    public String format(ArrayList arrayList) {
                        return "LogUtils Formatter ArrayList { " + arrayList.toString() + " }";
                    }
                }).addFileExtraHead("ExtraKey", "ExtraValue");
        LogUtils.i(config.toString());
    }

    private void initCrash() {
        String env = BaseConfigManager.getInstance().getCurrentFamily().getEnv().name();
        CrashUtils.init(Utils.getApp().getApplicationContext().getExternalFilesDir("logcat"), crashInfo -> {
            crashInfo.addExtraHead("extraKey", "extraValue");
            LogUtils.e(crashInfo.toString());
        });
    }


    public boolean isMainProcess() {
        if (isMainProcess == null) isMainProcess = ProcessUtils.isMainProcess();
        return isMainProcess;
    }
}
