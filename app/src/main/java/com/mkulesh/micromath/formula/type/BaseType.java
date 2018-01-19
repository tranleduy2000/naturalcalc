/*
 * Copyright (c) 2018 by Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mkulesh.micromath.formula.type;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.nstudio.calc.casio.R;


public enum BaseType implements FunctionTermType {
    EQUATION(R.drawable.ic_equation, R.string.math_new_equation, R.id.btn_add_assign),
    RESULT(R.drawable.ic_result, R.string.math_new_result, R.id.btn_add_row),
    TERM(R.drawable.ic_new_term, R.string.math_new_term, R.id.btn_add_term);
    @DrawableRes
    private final int imageId;
    @StringRes
    private final int descriptionId;
    @IdRes
    private final int viewId;

    BaseType(@DrawableRes int imageId, @StringRes int descriptionId, @IdRes int viewId) {
        this.imageId = imageId;
        this.descriptionId = descriptionId;
        this.viewId = viewId;
    }

    public int getViewId() {
        return viewId;
    }

    public int getImageId() {
        return imageId;
    }

    @NonNull
    @Override
    public String getCode() {
        return name().toLowerCase();
    }

    public int getDescriptionId() {
        return descriptionId;
    }
}
