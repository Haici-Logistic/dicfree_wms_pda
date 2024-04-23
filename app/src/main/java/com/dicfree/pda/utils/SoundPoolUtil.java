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

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.dicfree.pda.R;

import java.util.HashMap;
import java.util.Map;

public class SoundPoolUtil {
	private volatile static SoundPoolUtil mInstance;
	private SoundPool mSoundPool;
	private Context mContext;
	public static SoundPoolUtil getInstance() {
		if (mInstance == null) {
			synchronized (SoundPoolUtil.class) {
				mInstance = new SoundPoolUtil();
			}
		}
		return mInstance;
	}
	private static HashMap<String, Integer> spMap= new HashMap<String, Integer>();;
	private static float volumnRatio =1;

	// 声音资源关键字
	public static final String SOUND_RACE_OK = "raceok";
	public static final String OFF_SHELF_SUCCESS = "OffShelfSuccess";
	public static final String ON_SHELF_SUCCESS = "OnShelfSuccess";
	public static final String WRONG_SKU = "WrongSKU";
	public static final String WRONG_SHELF = "WrongShelf";
	public static final String PRINT_SUCCESS = "PrintSuccess";
	public static final String PRINT_FAILURE = "PrintFailure";
	public static final Map<String,Integer> sound_map = new HashMap<String,Integer>(){{
		put(SOUND_RACE_OK,R.raw.raceok);
		put(OFF_SHELF_SUCCESS,R.raw.offshelfsuccess);
		put(ON_SHELF_SUCCESS,R.raw.onshelfsuccess);
		put(WRONG_SHELF,R.raw.wrongshelf);
		put(WRONG_SKU,R.raw.wrongsku);
		put(PRINT_SUCCESS,R.raw.allocationprintsuccess);
		put(PRINT_FAILURE,R.raw.allocationprintfailure);
	}};

	/**
	 * 需要先初始化再调用playSound
	 */
	public void initSoundPool(Context context) {
		mContext = context;
		if (Build.VERSION.SDK_INT >= 21) {
			SoundPool.Builder builder = new SoundPool.Builder();
			// 传入最多播放音频数量,
			builder.setMaxStreams(1);
			// AudioAttributes是一个封装音频各种属性的方法
			AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
			// 设置音频流的合适的属性
			attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
			// 加载一个AudioAttributes
			builder.setAudioAttributes(attrBuilder.build());
			mSoundPool = builder.build();
		} else {
			/**
			 * 第一个参数：SoundPool对象的最大并发流数
			 * 第二个参数：AudioManager中描述的音频流类型
			 * 第三个参数：采样率转换器的质量。 目前没有效果。 使用0作为默认值。
			 */
			mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		}
		sound_map.forEach((key,id)->{
			spMap.put(key, mSoundPool.load(mContext,id, 1));
		});
	}

	private static int mPresentPlayId;
	private static String TAG = "soundpool";

	/**
	 * 播放声音音轨
	 *
	 * @param s
	 */
	public  void soundPoll(String s) {
		soundPoll(s,0);
	}
	/**
	 * 播放声音音轨
	 * 
	 * @param s
	 * @param loop
	 */
	public  void soundPoll(String s, int loop) {
		LogUtils.dTag(TAG, "音效:" + s);
		if (mSoundPool != null && spMap.containsKey(s) && spMap.get(s) != null) {
			mPresentPlayId = mSoundPool.play(spMap.get(s), // 声音资源
					volumnRatio, // 左声道
					volumnRatio, // 右声道
					1, // 优先级，0最低
					loop, // 循环次数，0是不循环，-1是永远循环
					1); // 回放速度，0.5-2.0之间。1为正常速度
		} else {
			Log.e(TAG, "请先初始化音轨!!!");
		}
	}
}
