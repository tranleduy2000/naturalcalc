package com.duy.natural.calc.calculator.keyboard;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by Duy on 1/14/2018.
 */

public interface OnCalcButtonClickListener {

    void onButtonPressed(@Nullable View view, @NonNull String code);
}
