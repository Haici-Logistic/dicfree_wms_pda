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


import com.blankj.utilcode.util.LogUtils;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;

public class RxRetryHandler implements Function<Observable<Throwable>, ObservableSource<?>> {
    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Throwable {
        return throwableObservable.flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {

            try {
//                if (throwable instanceof RefreshTokenExpiredException) {
//                    TokenManager.getInstance().clear();
//                    LogUtils.d("RxRetryHandler, launch login");
//                    BaseRouterUtils.navigation(MAIN_HOME_ACTIVITY);
//                }
                return Observable.error(throwable);
            } catch (OutOfMemoryError error) {
                return Observable.error(throwable);
            }
        });
    }
}
