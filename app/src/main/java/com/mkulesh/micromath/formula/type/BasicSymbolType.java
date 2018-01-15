package com.mkulesh.micromath.formula.type;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.nstudio.calc.casio.R;

import java.util.Locale;

import static com.mkulesh.micromath.formula.button.CalcButtonManager.NO_BUTTON;

/**
 * Created by Duy on 1/14/2018.
 */

public enum BasicSymbolType implements ButtonDescriptor {
    ONE(NO_BUTTON, R.id.btn_one),
    TWO(NO_BUTTON, R.id.btn_two),
    THREE(NO_BUTTON, R.id.btn_three),
    FOUR(NO_BUTTON, R.id.btn_four),
    FIVE(NO_BUTTON, R.id.btn_five),
    SIX(NO_BUTTON, R.id.btn_six),
    SEVEN(NO_BUTTON, R.id.btn_seven),
    EIGHT(NO_BUTTON, R.id.btn_eight),
    NINE(NO_BUTTON, R.id.btn_nine),
    ZERO(NO_BUTTON, R.id.btn_zero),
    DECIMAL(NO_BUTTON, R.id.btn_decimal),
    PI(R.string.math_constant_pi, R.id.btn_const_pi) {
        @Override
        public String toString() {
            return "Pi";
        }
    },
    E(R.string.math_constant_e, R.id.btn_const_e) {
        @Override
        public String toString() {
            return "E";
        }
    },
    I(R.string.math_constant_i, R.id.btn_const_i) {
        @Override
        public String toString() {
            return "I";
        }
    },
    VAR_X(R.string.math_constant_x, R.id.btn_var_x) {
        @Override
        public String toString() {
            return "x";
        }
    }, VAR_Y(R.string.math_constant_y, R.id.btn_var_y) {
        @Override
        public String toString() {
            return "y";
        }
    };

    private static final String NUMBER = "1234567890.";
    private final int viewId;
    private int descriptionId;

    BasicSymbolType(@StringRes int descriptionId, @IdRes int viewId) {
        this.descriptionId = descriptionId;
        this.viewId = viewId;
    }

    @Nullable
    public static BasicSymbolType getNumberType(String code) {
        try {
            return BasicSymbolType.valueOf(code.toUpperCase(Locale.ENGLISH));
        } catch (Exception ex) {
            // nothing to do
        }
        return null;
    }

    public int getViewId() {
        return viewId;
    }

    @NonNull
    public String getLowerCaseName() {
        return name().toLowerCase(Locale.US);
    }

    @Override
    public int getDescriptionId() {
        return descriptionId;
    }

    @Override
    public String toString() {
        return String.valueOf(NUMBER.charAt(ordinal()));
    }
}
