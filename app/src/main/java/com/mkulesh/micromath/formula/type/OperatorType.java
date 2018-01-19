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
 * Supported operators
 */
public enum OperatorType implements FormulaTermType {
    PLUS(R.string.formula_operator_plus, R.string.math_operator_plus, R.id.btn_plus) {
        @Override
        public String toString() {
            return "+";
        }
    },
    MINUS(R.string.formula_operator_minus, R.string.math_operator_minus, R.id.btn_minus) {
        @Override
        public String toString() {
            return "-";
        }
    },
    MULTIPLY(R.string.formula_operator_mult, R.string.math_operator_mult, R.id.btn_mul) {
        @Override
        public String toString() {
            return "*";
        }
    },
    FRACTION(R.string.formula_operator_divide, R.string.math_operator_divide, R.id.btn_fraction) {
        @Override
        public String toString() {
            return "/";
        }
    },
    DIVIDE_SLASH(R.string.formula_operator_divide_slash, R.string.math_operator_divide_slash, R.id.btn_div_splash) {
        @Override
        public String toString() {
            return "/";
        }
    };

    private final int symbolId;
    private final int descriptionId;
    private final int viewId;
    private final String lowerCaseName;

    OperatorType(int symbolId, int descriptionId, @IdRes int viewId) {
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

    @Nullable
    @Override
    public TermType getType() {
        return TermType.OPERATOR;
    }

    @NonNull
    @Override
    public String getLowerCaseName() {
        return lowerCaseName;
    }
}