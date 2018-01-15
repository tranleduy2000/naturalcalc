/*******************************************************************************
 * microMathematics Plus - Extended visual calculator
 * *****************************************************************************
 * Copyright (C) 2014-2017 Mikhail Kulesh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.duy.natural.calc.calculator.calcbutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.duy.natural.calc.calculator.settings.CalculatorSetting;
import com.mkulesh.micromath.utils.ViewUtils;
import com.nstudio.calc.casio.R;

import java.util.Arrays;

public class CalcImageButton extends AppCompatImageView implements ICalcButton {
    private final boolean[] mEnabledModes = new boolean[Category.values().length];
    private String code = null;
    private String shortCut = null;
    private Category[] categories = null;
    private CalculatorSetting mSetting;

    public CalcImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        enableAll();
        setup(context);
    }

    public CalcImageButton(Context context) {
        super(context);
        enableAll();
        setup(context);

    }

    public CalcImageButton(Context context, int shortCutId, int descriptionId, String code) {
        super(context);
        setup(context);
        initWithParameter(shortCutId, descriptionId, code);
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
        ViewUtils.setImageButtonColorAttr(getContext(), this,
                isEnabled() ? R.attr.colorMicroMathIcon : R.attr.colorPrimaryDark);
    }

    private void setup(Context context) {
        mSetting = new CalculatorSetting(context);
    }

    public String getCategoryCode() {
        return code;
    }

    public String getShortCut() {
        return shortCut;
    }

    public Category[] getCategories() {
        return categories;
    }

    public void setCategories(Category[] categories) {
        this.categories = categories;
    }

    private void enableAll() {
        Arrays.fill(mEnabledModes, true);
    }

    public void setEnabled(Category category, boolean value) {
        mEnabledModes[category.ordinal()] = value;
        super.setEnabled(true);
        super.setFocusable(true);
        for (boolean shouldEnable : mEnabledModes) {
            if (!shouldEnable) {
                super.setEnabled(false);
                super.setFocusable(false);
                break;
            }
        }
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
            if (v != null) {
                v.vibrate(mSetting.getVibrateStrength());
            }
        }
    }

}
