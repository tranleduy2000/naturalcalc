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
 * Supported loop types
 */
public enum LoopType implements FormulaTermType {
    SUMMATION(R.string.formula_loop_summation, R.string.math_loop_summation, R.id.btn_sum) {
        @Override
        public String toString() {
            return "Sum";
        }
    },
    PRODUCT(R.string.formula_loop_product, R.string.math_loop_product, R.id.btn_product) {
        @Override
        public String toString() {
            return "Product";
        }
    },
    INTEGRAL(R.string.formula_loop_integral, R.string.math_loop_integral, R.id.btn_integral) {
        @Override
        public String toString() {
            return "Int";
        }
    },
    DERIVATIVE(R.string.formula_loop_derivative, R.string.math_loop_derivative, R.id.btn_derivative) {
        @Override
        public String toString() {
            return "D";
        }
    };

    private final int symbolId;
    private final int descriptionId;
    private final int viewId;
    private final String lowerCaseName;

    LoopType(int symbolId, int descriptionId, @IdRes int viewId) {
        this.symbolId = symbolId;
        this.descriptionId = descriptionId;
        this.viewId = viewId;
        this.lowerCaseName = name().toLowerCase(Locale.ENGLISH);
    }

    public int getViewId() {
        return viewId;
    }

    public int getSymbolId() {
        return symbolId;
    }

    public int getDescriptionId() {
        return descriptionId;
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
        return TermType.LOOP;
    }
}