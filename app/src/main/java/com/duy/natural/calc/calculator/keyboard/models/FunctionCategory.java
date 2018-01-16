package com.duy.natural.calc.calculator.keyboard.models;

import android.support.annotation.StringRes;

import com.mkulesh.micromath.formula.type.FunctionType;

import java.util.ArrayList;

/**
 * Created by Duy on 1/16/2018.
 */

public final class FunctionCategory {
    private final int titleId;
    private final ArrayList<FunctionType> functionTypes = new ArrayList<>();

    public FunctionCategory(@StringRes int titleId) {
        this.titleId = titleId;
    }

    public int getTitleId() {
        return titleId;
    }

    public ArrayList<FunctionType> getFunctionTypes() {
        return functionTypes;
    }

    public void add(FunctionType functionType) {
        functionTypes.add(functionType);
    }
}
