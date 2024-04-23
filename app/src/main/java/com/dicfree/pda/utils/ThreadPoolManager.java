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

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Circle on 2019/9/17.
 * 线程池管理类
 */

public class ThreadPoolManager {
    String TAG=ThreadPoolManager.class.getSimpleName();
    private static  ThreadPoolManager threadPoolManager=null;

    public static  ThreadPoolManager getInstance(){
        if (threadPoolManager == null) {
            threadPoolManager =new ThreadPoolManager();
        }
        return threadPoolManager;
    }
    //线程安全
    private LinkedBlockingDeque<Runnable> mQueue=new LinkedBlockingDeque<>();

    private ThreadPoolExecutor mThreadPoolExecutor;
    //创建线程池
    private ThreadPoolManager(){
        mThreadPoolExecutor=new ThreadPoolExecutor(1, 100, 15, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                addTask(r);
            }
        });
        new Thread(coreTread).start();
//        mThreadPoolExecutor.execute(coreTread);//执行核心线程池
    }


    //将请求添加到队列中
    public void addTask(Runnable runnable){
        Log.e(TAG,runnable.getClass().getSimpleName());
        if (runnable!=null){
            try {
                mQueue.put(runnable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void addTopTask(Runnable runnable){
        if (runnable!=null){
            try {
                mQueue.putFirst(runnable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //创建核心线程
    public Runnable coreTread=new Runnable() {
        Runnable runn=null;
        @Override
        public void run() {
            while (true) {
                try {
                    runn = mQueue.take();
                    synchronized(this){
                        mThreadPoolExecutor.execute(runn);//执行线程
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 结束线程池
     */
    public void stopThreadPool() {
        if (mThreadPoolExecutor != null) {
            mThreadPoolExecutor.shutdown();
            mQueue.clear();
            mThreadPoolExecutor = null;
            threadPoolManager = null;
        }
    }
}
