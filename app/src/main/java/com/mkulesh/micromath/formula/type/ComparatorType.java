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
 * Supported comparators
 */
public enum ComparatorType implements FormulaTermType {
    EQUAL(R.string.formula_comparator_equal, R.string.math_comparator_equal, R.id.btn_equal) {
        @Override
        public String toString() {
            return "==";
        }
    },
    NOT_EQUAL(R.string.formula_comparator_not_equal, R.string.math_comparator_not_equal, R.id.btn_not_equal) {
        @Override
        public String toString() {
            return "=|";
        }
    },
    LESS(R.string.formula_comparator_less, R.string.math_comparator_less, R.id.btn_less) {
        @Override
        public String toString() {
            return "<";
        }
    },
    LESS_EQUAL(R.string.formula_comparator_less_eq, R.string.math_comparator_less_eq, R.id.btn_less_equal) {
        @Override
        public String toString() {
            return "<=";
        }
    },
    GREATER(R.string.formula_comparator_greater, R.string.math_comparator_greater, R.id.btn_greater) {
        @Override
        public String toString() {
            return ">";
        }
    },
    GREATER_EQUAL(
            R.string.formula_comparator_greater_eq,
            R.string.math_comparator_greater_eq, R.id.btn_greater_equal) {
        @Override
        public String toString() {
            return ">=";
        }
    },
    COMPARATOR_AND(R.string.formula_comparator_and, R.string.math_comparator_and, R.id.btn_and) {
        @Override
        public String toString() {
            return "&&";
        }
    },
    COMPARATOR_OR(R.string.formula_comparator_or, R.string.math_comparator_or, R.id.btn_or) {
        @Override
        public String toString() {
            return "||";
        }
    };

    private final int symbolId;
    private final int descriptionId;
    private final String lowerCaseName;
    private int viewId;

    ComparatorType(int symbolId, int descriptionId, @IdRes int viewId) {
        this.symbolId = symbolId;
        this.descriptionId = descriptionId;
        this.viewId = viewId;
        this.lowerCaseName = name().toLowerCase(Locale.ENGLISH);
    }

    @Override
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
        return TermType.COMPARATOR;
    }

    @NonNull
    @Override
    public String getLowerCaseName() {
        return lowerCaseName;
    }
}