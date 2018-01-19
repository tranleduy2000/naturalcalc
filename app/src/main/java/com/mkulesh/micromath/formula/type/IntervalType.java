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

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nstudio.calc.casio.R;

import java.util.Locale;

/**
 * Supported functions
 */
public enum IntervalType implements FormulaTermType {
    EQUIDISTANT_INTERVAL(
            R.string.formula_quidistant_interval,
            R.drawable.p_equidistant_interval,
            R.string.math_equidistant_interval, R.id.btn_inverval);

    private final int symbolId;
    private final int imageId;
    private final int descriptionId;
    private final String lowerCaseName;
    private int viewId;

    IntervalType(int symbolId, int imageId, int descriptionId, @IdRes int viewId) {
        this.symbolId = symbolId;
        this.imageId = imageId;
        this.descriptionId = descriptionId;
        this.viewId = viewId;
        this.lowerCaseName = name().toLowerCase(Locale.ENGLISH);
    }

    public int getSymbolId() {
        return symbolId;
    }

    public int getImageId() {
        return imageId;
    }

    public int getDescriptionId() {
        return descriptionId;
    }

    @Override
    public int getViewId() {
        return viewId;
    }

    @NonNull
    public String getCode() {
        return lowerCaseName;
    }

    @NonNull
    @Override
    public String getLowerCaseName() {
        return lowerCaseName;
    }

    @Nullable
    @Override
    public TermType getType() {
        return TermType.INTERVAL;
    }
}