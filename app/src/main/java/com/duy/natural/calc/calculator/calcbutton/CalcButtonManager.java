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

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.duy.common.utils.DLog;
import com.duy.natural.calc.calculator.keyboard.OnCalcButtonClickListener;
import com.mkulesh.micromath.formula.type.ActionType;
import com.mkulesh.micromath.formula.type.BaseType;
import com.mkulesh.micromath.formula.type.BasicSymbolType;
import com.mkulesh.micromath.formula.type.FormulaTermType;
import com.mkulesh.micromath.formula.type.ComparatorType;
import com.mkulesh.micromath.formula.type.FunctionTrigger;
import com.mkulesh.micromath.formula.type.FunctionType;
import com.mkulesh.micromath.formula.type.IntervalType;
import com.mkulesh.micromath.formula.type.LoopType;
import com.mkulesh.micromath.formula.type.OperatorType;
import com.mkulesh.micromath.formula.views.FormulaTermView;
import com.mkulesh.micromath.utils.ClipboardManager;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.widgets.FormulaEditText;
import com.mkulesh.micromath.widgets.OnFocusChangedListener;
import com.mkulesh.micromath.widgets.OnTextChangeListener;
import com.nstudio.calc.casio.R;

import java.util.ArrayList;


public class CalcButtonManager implements OnClickListener, OnLongClickListener,
        OnTextChangeListener, OnFocusChangedListener {
    public static final int NO_BUTTON = -1;
    private static final String TAG = "CalcButtonManager";
    private final Context mContext;
    private final OnCalcButtonClickListener mListener;
    private final ArrayList<ArrayList<ICalcButton>> mButtonCategory = new ArrayList<>();
    private final ViewGroup mPaletteLayout;
    @Nullable
    private FormulaEditText mHiddenInput;
    private String mLastHiddenInput = "";

    public CalcButtonManager(Context context, ViewGroup parent, OnCalcButtonClickListener listener) {
        mContext = context;
        mListener = listener;
        mPaletteLayout = parent;

        mHiddenInput = parent.findViewById(R.id.hidden_edit_text);
        if (mHiddenInput != null) {
            mHiddenInput.setChangeListener(this, this);
            mHiddenInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }

        enableHiddenInput(false);

        for (int i = 0; i < Category.values().length; i++) {
            mButtonCategory.add(new ArrayList<ICalcButton>());
        }

        setupActionButton(parent);
        setupIntervalButton(parent);
        setupOperatorButton(parent);
        setupFunctionButton(parent);
        setupLoopFunctionButton(parent);
        setupComparatorButton(parent);
        setupBasicButton(parent);

        // prepare all buttons
        addButtonEvent(parent);
    }

    private void setupActionButton(ViewGroup parent) {
        for (BaseType baseType : BaseType.values()) {
            View view = parent.findViewById(baseType.getViewId());
            if (view instanceof ICalcButton) {
                if (baseType == BaseType.TERM) {
                    ICalcButton button = (ICalcButton) view;
                    button.initWithParameter(R.string.formula_term_separator, baseType.getDescriptionId(), baseType.toString());
                    button.setCategories(new Category[]{Category.NEW_TERM, Category.CONVERSION});
                } else {
                    ICalcButton button = (ICalcButton) view;
                    button.initWithParameter(NO_BUTTON, baseType.getDescriptionId(), baseType.toString());
                }
            }
        }
        for (ActionType actionType : ActionType.values()) {
            View view = parent.findViewById(actionType.getViewId());
            if (view instanceof ICalcButton) {
                ICalcButton button = (ICalcButton) view;
                button.initWithParameter(View.NO_ID, actionType.getDescriptionId(), actionType.toString());
            }
        }
    }

    private void setupIntervalButton(ViewGroup viewGroup) {
        for (IntervalType intervalType : IntervalType.values()) {
            View view = viewGroup.findViewById(intervalType.getViewId());
            if (view instanceof ICalcButton) {
                ICalcButton button = (ICalcButton) view;
                button.initWithParameter(intervalType.getSymbolId(),
                        intervalType.getDescriptionId(),
                        intervalType.getCode());
                button.setCategories(new Category[]{Category.TOP_LEVEL_TERM});
            }
        }
    }

    private void setupBasicButton(ViewGroup viewGroup) {
        for (FormulaTermType type : BasicSymbolType.values()) {
            View view = viewGroup.findViewById(type.getViewId());
            if (view instanceof ICalcButton) {
                ((ICalcButton) view).initWithParameter(CalcButtonManager.NO_BUTTON,
                        type.getDescriptionId(), type.getCode());
            }
        }

    }

    private void setupComparatorButton(ViewGroup parent) {
        for (int i = 0; i < ComparatorType.values().length; i++) {
            final ComparatorType type = ComparatorType.values()[i];
            View view = parent.findViewById(type.getViewId());
            if (view instanceof ICalcButton) {
                ICalcButton p = (ICalcButton) view;
                p.initWithParameter(type.getSymbolId(), type.getDescriptionId(), type.getCode());
                p.setCategories(new Category[]{Category.COMPARATOR});
            }
        }
    }

    private void setupLoopFunctionButton(ViewGroup parent) {
        for (int i = 0; i < LoopType.values().length; i++) {
            final LoopType type = LoopType.values()[i];
            int id = type.getViewId();
            View view = parent.findViewById(id);
            if (view instanceof ICalcButton) {
                ICalcButton p = (ICalcButton) view;
                p.initWithParameter(type.getSymbolId(), type.getDescriptionId(), type.getCode());
                p.setCategories(new Category[]{Category.CONVERSION});
            }
        }
    }

    private void setupFunctionButton(ViewGroup parent) {
        for (int i = 0; i < FunctionType.values().length; i++) {
            final FunctionType type = FunctionType.values()[i];
            int shortCuId = CalcButtonManager.NO_BUTTON;
            for (FunctionTrigger functionTrigger : FunctionTrigger.values()) {
                if (functionTrigger.getFunctionType() == type) {
                    shortCuId = functionTrigger.getCodeId();
                }
            }
            View view = parent.findViewById(type.getViewId());
            if (view instanceof ICalcButton) {
                ICalcButton b = (ICalcButton) view;
                b.initWithParameter(shortCuId, type.getDescriptionId(), type.getCode());
                b.setCategories(new Category[]{Category.CONVERSION});
            }
        }
    }

    private void setupOperatorButton(ViewGroup parent) {
        for (int i = 0; i < OperatorType.values().length; i++) {
            final OperatorType type = OperatorType.values()[i];
            int viewId = type.getViewId();
            View view = parent.findViewById(viewId);
            if (view instanceof ICalcButton) {
                ICalcButton p = (ICalcButton) view;
                p.initWithParameter(type.getSymbolId(), type.getDescriptionId(), type.getCode());
                p.setCategories(new Category[]{Category.CONVERSION});
            }
        }
    }

    private void addButtonEvent(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ICalcButton) {
                ICalcButton calcButton = (ICalcButton) child;
                if ((calcButton).getCategories() != null) {
                    for (Category category : (calcButton).getCategories()) {
                        mButtonCategory.get(category.ordinal()).add(calcButton);
                    }
                }
                child.setOnLongClickListener(this);
                child.setOnClickListener(this);
            } else if (child instanceof ViewGroup) {
                addButtonEvent((ViewGroup) child);
            }
        }
    }

    /**
     * This procedure is used to enable/disable whole palette
     */
    public void setEnabled(boolean enabled) {
        mPaletteLayout.setEnabled(enabled);
        updateButtonsColor(mPaletteLayout);
    }

    /**
     * This procedure is used to enable/disable palette buttons related to a formula term
     */
    public void setPaletteBlockEnabled(Category category, boolean enabled) {
        if (DLog.DEBUG)
            DLog.d(TAG, "setPaletteBlockEnabled() called with: category = [" + category + "], enabled = [" + enabled + "]");
        for (ICalcButton calcButton : mButtonCategory.get(category.ordinal())) {
            calcButton.setEnabled(category, enabled);
        }
        updateButtonsColor(mPaletteLayout);
    }

    /**
     * Procedure sets the background color for all buttons depending on enabled status
     */
    private void updateButtonsColor(ViewGroup viewGroup) {
        int[] attrs = {R.attr.colorButtonEnabled, R.attr.colorButtonDisabled};
        Resources.Theme theme = mContext.getTheme();
        TypedArray ta = theme.obtainStyledAttributes(attrs);

        int disableColor = mContext.getResources().getColor(ta.getResourceId(0, R.color.gray500));
        @StyleableRes int enableColorIndex = 1;
        int enableColor = mContext.getResources().getColor(ta.getResourceId(enableColorIndex, R.color.white));
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (!(child instanceof ICalcButton)) {
                if (child instanceof ViewGroup) {
                    updateButtonsColor((ViewGroup) child);
                }
                continue;
            }
            ICalcButton b = (ICalcButton) child;
            final boolean isEnabled = b.isEnabled() && viewGroup.isEnabled();
            int color = isEnabled ? enableColor : disableColor;
            b.setTint(color);
        }
        ta.recycle();
    }

    @Override
    public void onClick(View view) {
        if (view instanceof ICalcButton && mListener != null) {
            final ICalcButton calcButton = (ICalcButton) view;
            String categoryCode = calcButton.getCategoryCode();
            if (categoryCode != null) {
                mListener.onButtonPressed(view, categoryCode);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        return view instanceof ICalcButton && ViewUtils.showButtonDescription(mContext, view);
    }

    public void enableHiddenInput(boolean hiddenInputEnabled) {
        if (mHiddenInput == null) return;
        mHiddenInput.setTextWatcher(false);
        final int newVis = hiddenInputEnabled ? View.VISIBLE : View.GONE;
        if (mHiddenInput.getVisibility() != newVis) {
            mHiddenInput.setVisibility(newVis);
        }
        if (mHiddenInput.getVisibility() != View.GONE) {
            mLastHiddenInput = "";
            mHiddenInput.setText(mLastHiddenInput);
            mHiddenInput.requestFocus();
            mHiddenInput.setTextWatcher(true);
        }
    }

    @Override
    public void beforeTextChanged(String text, boolean isManualInput) {
        // empty
    }

    @Override
    public void onTextChanged(String text, boolean isManualInput) {
        if (mHiddenInput == null) {
            return;
        }
        if (text == null || mListener == null) {
            mLastHiddenInput = null;
            return;
        }

        if (mLastHiddenInput != null && mLastHiddenInput.equals(text)) {
            return;
        }

        mLastHiddenInput = text;

        if (ClipboardManager.isFormulaObject(text)) {
            mHiddenInput.setTextWatcher(false);
            mListener.onButtonPressed(null, text);
            return;
        }

        final String termSep = mContext.getResources().getString(R.string.formula_term_separator);
        final String code = (termSep.equals(text)) ? BaseType.TERM.toString() : FormulaTermView.getOperatorCode(
                mContext, text, true);
        if (code == null) {
            return;
        }

        if (FunctionType.FUNCTION_LINK.toString().equalsIgnoreCase(code)) {
            mHiddenInput.setTextWatcher(false);
            mListener.onButtonPressed(null, text);
            return;
        }

        for (int i = 0; i < mPaletteLayout.getChildCount(); i++) {
            if (mPaletteLayout.getChildAt(i) instanceof CalcImageButton) {
                CalcImageButton button = (CalcImageButton) mPaletteLayout.getChildAt(i);
                if (button.isEnabled() && button.getCategoryCode() != null && button.getCategoryCode().equalsIgnoreCase(code)) {
                    mHiddenInput.setTextWatcher(false);
                    mListener.onButtonPressed(null, button.getCategoryCode());
                    break;
                }
            }
        }
    }

    @Override
    public void onSizeChanged() {
        // empty
    }

    @Override
    public int onGetNextFocusId(FormulaEditText owner, FocusType focusType) {
        return R.id.container_display;
    }
}
