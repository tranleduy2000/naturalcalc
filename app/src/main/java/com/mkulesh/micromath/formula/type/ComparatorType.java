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

import android.support.annotation.NonNull;

import com.nstudio.calc.casio.R;

import java.util.Locale;

/**
 * Supported comparators
 */
public enum ComparatorType implements ButtonDescriptor {
    EQUAL(R.string.formula_comparator_equal, R.string.math_comparator_equal) {
        @Override
        public String toString() {
            return "==";
        }
    },
    NOT_EQUAL(R.string.formula_comparator_not_equal, R.string.math_comparator_not_equal) {
        @Override
        public String toString() {
            return "=|";
        }
    },
    LESS(R.string.formula_comparator_less, R.string.math_comparator_less) {
        @Override
        public String toString() {
            return "<";
        }
    },
    LESS_EQUAL(R.string.formula_comparator_less_eq, R.string.math_comparator_less_eq) {
        @Override
        public String toString() {
            return "<=";
        }
    },
    GREATER(R.string.formula_comparator_greater, R.string.math_comparator_greater) {
        @Override
        public String toString() {
            return ">";
        }
    },
    GREATER_EQUAL(
            R.string.formula_comparator_greater_eq,
            R.string.math_comparator_greater_eq) {
        @Override
        public String toString() {
            return ">=";
        }
    },
    COMPARATOR_AND(R.string.formula_comparator_and, R.string.math_comparator_and) {
        @Override
        public String toString() {
            return "&&";
        }
    },
    COMPARATOR_OR(R.string.formula_comparator_or, R.string.math_comparator_or) {
        @Override
        public String toString() {
            return "||";
        }
    };

    private final int symbolId;
    private final int descriptionId;
    private final String lowerCaseName;

    ComparatorType(int symbolId, int descriptionId) {
        this.symbolId = symbolId;
        this.descriptionId = descriptionId;
        this.lowerCaseName = name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public int getViewId() {
        return 0;
    }

    public int getSymbolId() {
        return symbolId;
    }

    public int getDescriptionId() {
        return descriptionId;
    }

    @NonNull
    public String getLowerCaseName() {
        return lowerCaseName;
    }
}