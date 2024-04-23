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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class BaseDialogFragment extends DialogFragment {
    private boolean dismissed = true;

    public boolean isDismissed() {
        return dismissed;
    }


    @Override
    public int show(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        dismissed = false;
        return super.show(transaction, tag);
    }

    @Override
    public void showNow(@NonNull FragmentManager manager, @Nullable String tag) {
        dismissed = false;
        super.showNow(manager, tag);
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        dismissed = false;
        super.show(manager, tag);
    }

    @Override
    public void dismissAllowingStateLoss() {
        dismissed = true;
        super.dismissAllowingStateLoss();
    }

    @Override
    public void dismiss() {
        dismissed = true;
        super.dismiss();
    }
}
