package com.mkulesh.micromath.formula.type;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Created by Duy on 1/14/2018.
 */

public interface FunctionTermType {
    @StringRes
    int getDescriptionId();

    @IdRes
    int getViewId();

    @NonNull
    String getCode();
}
