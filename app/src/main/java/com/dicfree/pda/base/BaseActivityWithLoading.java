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

import androidx.fragment.app.DialogFragment;

import com.dicfree.pda.R;
import com.dicfree.pda.view.CommonLoadingDialog;


public abstract class BaseActivityWithLoading<V extends BaseViewModel> extends BaseActivityWithVm<V> {

    @Override
    protected BaseDialogFragment onCreateLoadingDialog() {
        BaseDialogFragment dialog = new CommonLoadingDialog();
        dialog.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CommonBlurDialog);
        dialog.setCancelable(false);
        return dialog;
    }
}
