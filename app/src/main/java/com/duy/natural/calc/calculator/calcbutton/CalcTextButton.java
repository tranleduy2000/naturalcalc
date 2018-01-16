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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.duy.common.utils.DLog;
import com.duy.natural.calc.calculator.settings.CalculatorSetting;
import com.duy.natural.calc.calculator.utils.FontManager;
import com.nstudio.calc.casio.R;

import java.util.Arrays;

public class CalcTextButton extends AppCompatTextView implements ICalcButton {
    private static final String TAG = "CalcTextButton";
    private final boolean[] enabledModes = new boolean[Category.values().length];
    private String code = null;
    private String shortCut = null;
    private Category[] categories = null;
    private CalculatorSetting mSetting;

    public CalcTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
        enableAll();
    }

    public CalcTextButton(Context context) {
        super(context);
        setup(context, null);
        enableAll();
    }

    public CalcTextButton(Context context, int shortCutId, int descriptionId, String code) {
        super(context);
        setup(context, null);
        initWithParameter(shortCutId, descriptionId, code);
    }

    private void setup(Context context, AttributeSet attrs) {
        mSetting = new CalculatorSetting(context);
        setTypeface(FontManager.getFontFromAsset(context, "Roboto-Light.ttf"));
        if (attrs != null) {
            TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CalcTextButton, -1, -1);
            code = ta.getString(R.styleable.CalcTextButton_code);
            if (getText().length() == 0) {
                setText(code);
            }
            shortCut = ta.getString(R.styleable.CalcTextButton_shortCutId);

            String description = ta.getString(R.styleable.CalcTextButton_descriptionId);
            description += " ('";
            description += shortCut;
            description += "')";
            setContentDescription(description);
            setLongClickable(true);
        }
    }

    @Override
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
        if (getText().length() == 0) {
            setText(code);
        }
        enableAll();
    }

    @Override
    public void setTint(int color) {
        setTextColor(color);
    }

    @Nullable
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
        Arrays.fill(enabledModes, true);
    }

    @Override
    public void setEnabled(Category category, boolean value) {
        enabledModes[category.ordinal()] = value;
        setEnabled(true);
        setFocusable(true);
        for (boolean shouldEnable : enabledModes) {
            if (!shouldEnable) {
                setEnabled(false);
                setFocusable(false);
                break;
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (DLog.DEBUG) DLog.d(TAG, "setEnabled() called with: enabled = [" + enabled + "]");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isEnabled()) vibrate();
        }
        return super.onTouchEvent(event);
    }

    private void vibrate() {
        if (mSetting.isUseVibrate()) {
            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(mSetting.getVibrateStrength());
        }
    }

    public String getType() {
        Object tag = getTag();
        if (tag != null)
            return tag.toString();
        else {
            return "";
        }
    }
}
