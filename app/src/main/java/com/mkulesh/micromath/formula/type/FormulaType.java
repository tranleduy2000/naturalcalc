package com.mkulesh.micromath.formula.type;

import android.support.annotation.Nullable;

import java.util.Locale;

public enum FormulaType {
    EQUATION,
    RESULT,
    PLOT_FUNCTION,
    PLOT_CONTOUR,
    TEXT_FRAGMENT,
    IMAGE_FRAGMENT;

    @Nullable
    public static FormulaType getFormulaType(String code) {
        try {
            return FormulaType.valueOf(code.toUpperCase(Locale.ENGLISH));
        } catch (Exception ex) {
            // nothing to do
        }
        return null;
    }
}