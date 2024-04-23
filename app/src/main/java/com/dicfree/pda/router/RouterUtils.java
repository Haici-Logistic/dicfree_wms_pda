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
package com.dicfree.pda.router;
//
//
//import android.app.Activity;
//import android.os.Bundle;
//
//import com.alibaba.android.arouter.launcher.ARouter;
//import com.blankj.utilcode.util.ActivityUtils;
//import com.blankj.utilcode.util.Utils;
//
//import java.io.Serializable;
//
//public class RouterUtils  {
//    /**
//     * 基础的router
//     */
//    public static Object navigation(String path) {
//        return ARouter.getInstance().build(path).navigation(ActivityUtils.getTopActivity());
//    }
//
//
//    /***
//     * 携带bundle参数
//     */
//    public static Object navigation(String path, Bundle bundle) {
//        return ARouter.getInstance().build(path).with(bundle).navigation(Utils.getApp());
//    }
//
//    /***
//     * 携带序列化参数
//     */
//    public static Object navigation(String path, Serializable bean) {
//        return ARouter.getInstance().build(path).withSerializable("bean", bean).navigation(ActivityUtils.getTopActivity());
//    }
//
//    /***
//     * 携带序列化参数，code
//     */
//    public static void navigation(String path, int RequestCode, Activity activity, Serializable bean) {
//        ARouter.getInstance().build(path).withSerializable("bean", bean).navigation(activity, RequestCode);
//    }
//}
