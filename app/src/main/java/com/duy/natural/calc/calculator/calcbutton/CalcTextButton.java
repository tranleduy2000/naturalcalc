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
package com.duy.natural.calc.calculator.calcbutton;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.duy.common.utils.DLog;
import com.duy.natural.calc.calculator.settings.CalculatorSetting;

import java.util.Arrays;

public class CalcTextButton extends AppCompatTextView implements ICalcButton {
    private static final String TAG = "CalcTextButton";
    private final boolean[] enabled = new boolean[Category.values().length];
    private String code = null;
    private String shortCut = null;
    private Category[] categories = null;
    private CalculatorSetting mSetting;

    public CalcTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
        enableAll();
    }

    public CalcTextButton(Context context) {
        super(context);
        setup(context);
        enableAll();
    }

    public CalcTextButton(Context context, int shortCutId, int descriptionId, String code) {
        super(context);
        setup(context);
        initWithParameter(shortCutId, descriptionId, code);
    }

    private void setup(Context context) {
        mSetting = new CalculatorSetting(context);
    }

    public void initWithParameter(int shortCutId, int descriptionId, String code) {
        if (shortCutId != CalcButtonManager.NO_BUTTON) {
            shortCut = getContext().getResources().getString(shortCutId);
        }
        if (descriptionId != CalcButtonManager.NO_BUTTON) {
            String description = getContext().getResources().getString(descriptionId);
            if (shortCut != null) {
                description += " ('";
                description += shortCut;
                description += "')";
            }
            setContentDescription(description);
            setLongClickable(true);
        }
        this.code = code;
        enableAll();
    }

    public String getCategoryCode() {
        return code;
    }

    public String getShortCut() {
        return shortCut;
    }

    @Override
    public Category[] getCategories() {
        return categories;
    }

    public void setCategories(Category[] categories) {
        this.categories = categories;
    }

    private void enableAll() {
        Arrays.fill(enabled, true);
    }

    @Override
    public void setEnabled(Category t, boolean value) {
        if (DLog.DEBUG)
            DLog.d(TAG, "setEnabled() " + code + " called with: t = [" + t + "], value = [" + value + "]");
        enabled[t.ordinal()] = value;
        super.setEnabled(true);
        for (boolean en : enabled) {
            if (!en) {
                super.setEnabled(false);
                break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            vibrate();
        }
        return super.onTouchEvent(event);
    }

    private void vibrate() {
        if (mSetting.isUseVibrate()) {
            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(mSetting.getVibrateStrength());
        }
    }
}
