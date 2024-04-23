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

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.blankj.utilcode.util.Utils;


/**
 * @author chenjiaming1 dp(dip): device independent pixels(设备独立像素).
 *         不同设备有不同的显示效果,这个和设备硬件有关，一般我们为了支持WVGA、HVGA和QVGA 推荐使用这个，不依赖像素。
 *         dp也就是dip，这个和sp基本类似。如果设置表示长度、高度等属性时可以使用dp
 *         或sp。但如果设置字体，需要使用sp。dp是与密度无关，sp除了与密度无关外
 *         ，还与scale无关。如果屏幕密度为160，这时dp和sp和px是一
 *         样的。1dp=1sp=1px，但如果使用px作单位，如果屏幕大小不变（
 *         假设还是3.2寸），而屏幕密度变成了320。那么原来TextView的宽度
 *         设成160px，在密度为320的3.2寸屏幕里看要比在密度为160的3
 *         .2寸屏幕上看短了一半。但如果设置成160dp或160sp的话。系统会自动 将width属性值设置成320px的。也就是160 * 320
 *         / 160。其中320 / 160可称为密度比例因子。也就是说，如果使用dp和sp，系统会根据屏幕密度的变化自动进行转换。 px:
 *         pixels(像素). 不同设备显示效果相同，一般我们HVGA代表320x480像素，这个用的比较多。 pt:
 *         point，是一个标准的长度单位，1pt＝1/72英寸，用于印刷业，非常简单易用； sp: scaled pixels(放大像素).
 *         主要用于字体显示best for textsize。
 */
public class DisplayUtil {

    private static final Context mContext = Utils.getApp();

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * 
     * @param pxValue
     * @param  （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        return px2dip(mContext, pxValue);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * 
     * @param dipValue
     * @param  （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int dip2px(float dipValue) {
        return dip2px(mContext, dipValue);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * 
     * @param pxValue
     * @param  （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int px2sp(float pxValue) {
        return px2sp(mContext, pxValue);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * 
     * @param spValue
     * @param  （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int sp2px(float spValue) {
        return sp2px(mContext, spValue);
    }

    public static int getDisplayWidth() {
        return getDisplayWidth(mContext);
    }

    // 获取 设备的Width
    public static int getDisplayWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    // 获取 设备的Width &Hight
    public static int[] getDeviceWH(Context context) {
        int[] wh = new int[2];
        int w = 0;
        int h = 0;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        w = dm.widthPixels;
        h = dm.heightPixels;
        wh[0] = w;
        wh[1] = h;
        return wh;
    }

    // 获取 设备的Hight
    public static int getDeviceHeight() {
        return getDeviceHeight(mContext);
    }

    // 获取 设备的Hight
    public static int getDeviceHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int getDeviceRealHeight(Activity context){
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        int deviceRealHeightPixels = outMetrics.heightPixels;
        return deviceRealHeightPixels;
    }
    
    /**
     * 获取状态栏的高度
     * @param context
     * @return状态栏的高度
     */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}



}
