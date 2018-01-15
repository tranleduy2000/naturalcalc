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
package com.duy.natural.calc.calculator.calcbutton.button;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.duy.common.utils.DLog;
import com.duy.natural.calc.calculator.keyboard.OnCalcButtonClickListener;
import com.mkulesh.micromath.formula.type.ActionType;
import com.mkulesh.micromath.formula.type.BaseType;
import com.mkulesh.micromath.formula.type.FunctionType;
import com.mkulesh.micromath.formula.views.FormulaTermComparatorView;
import com.mkulesh.micromath.formula.views.FormulaTermFunctionView;
import com.mkulesh.micromath.formula.views.FormulaTermIntervalView;
import com.mkulesh.micromath.formula.views.FormulaTermLoopView;
import com.mkulesh.micromath.formula.views.FormulaTermOperatorView;
import com.mkulesh.micromath.formula.views.FormulaTermView;
import com.mkulesh.micromath.formula.views.TermField;
import com.mkulesh.micromath.utils.ClipboardManager;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.widgets.CalcEditText;
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
    private final ArrayList<ArrayList<ICalcButton>> mPaletteBlock = new ArrayList<>();
    private final ViewGroup mPaletteLayout;
    //    private final CustomEditText mHiddenInput;
    private String mLastHiddenInput = "";

    public CalcButtonManager(Context context, ViewGroup parent, OnCalcButtonClickListener listener) {
        mContext = context;
        mListener = listener;
        mPaletteLayout = parent;

//        mHiddenInput = parent.findViewById(R.id.hidden_edit_text);
//        mHiddenInput.setChangeListener(this, this);
//        mHiddenInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        enableHiddenInput(false);

        for (int i = 0; i < Category.values().length; i++) {
            mPaletteBlock.add(new ArrayList<ICalcButton>());
        }

        // list operations
        for (int i = 0; i < BaseType.values().length; i++) {
            final BaseType baseType = BaseType.values()[i];
            View view = parent.findViewById(baseType.getViewId());
            if (view instanceof ICalcButton) {
                if (baseType.getImageId() != NO_BUTTON) {
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
        }


      /*  ArrayList<Descriptor> descriptors = new ArrayList<>();
        descriptors.addAll(Arrays.asList(ActionType.values()));
        descriptors.addAll(Arrays.asList(BaseType.values()));
        descriptors.addAll(Arrays.asList(BasicSymbolType.values()));
        descriptors.addAll(Arrays.asList(ComparatorType.values()));
        descriptors.addAll(Arrays.asList(FunctionType.values()));
        descriptors.addAll(Arrays.asList(LoopType.values()));
        descriptors.addAll(Arrays.asList(OperatorType.values()));*/

        for (int i = 0; i < ActionType.values().length; i++) {
            final ActionType actionType = ActionType.values()[i];
            View view = parent.findViewById(actionType.getViewId());
            if (view instanceof ICalcButton) {
                ICalcButton button = (ICalcButton) view;
                button.initWithParameter(View.NO_ID, NO_BUTTON, actionType.toString());
                button.setCategories(new Category[]{Category.NONE});
            }
        }

        FormulaTermIntervalView.addToPalette(context, parent, new Category[]{Category.TOP_LEVEL_TERM});
        FormulaTermOperatorView.addToPalette(parent, new Category[]{Category.CONVERSION});
        FormulaTermFunctionView.addToPalette(parent, new Category[]{Category.CONVERSION});
        FormulaTermLoopView.addToPalette(parent, new Category[]{Category.CONVERSION});
        FormulaTermComparatorView.addToPalette(parent, new Category[]{Category.COMPARATOR});
        TermField.addToPalette(parent, new Category[]{Category.NONE});

        // prepare all buttons
        addButtonEvent(parent);
    }

    private void addButtonEvent(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ICalcButton) {
                ICalcButton calcButton = (ICalcButton) child;
                if ((calcButton).getCategories() != null) {
                    for (Category category : (calcButton).getCategories()) {
                        mPaletteBlock.get(category.ordinal()).add(calcButton);
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
        updateButtonsColor();
    }

    /**
     * This procedure is used to enable/disable palette buttons related to a formula term
     */
    public void setPaletteBlockEnabled(Category t, boolean enabled) {
     /*   for (ICalcButton b : mPaletteBlock.get(t.ordinal())) {
            b.setEnabled(t, enabled);
        }
        updateButtonsColor();*/
    }

    /**
     * Procedure sets the background color for all buttons depending on enabled status
     */
    private void updateButtonsColor() {
        for (int i = 0; i < mPaletteLayout.getChildCount(); i++) {
            if (!(mPaletteLayout.getChildAt(i) instanceof CalcImageButton)) {
                continue;
            }
            CalcImageButton b = (CalcImageButton) mPaletteLayout.getChildAt(i);
            final boolean isEnabled = b.isEnabled() && mPaletteLayout.isEnabled();
            int color = isEnabled ? R.attr.colorMicroMathIcon : R.attr.colorPrimaryDark;
            ViewUtils.setImageButtonColorAttr(mContext, b, color);
        }
    }

    @Override
    public void onClick(View view) {
        if (DLog.DEBUG) DLog.d(TAG, "onClick() called with: view = [" + view + "]");
        if (view instanceof ICalcButton && mListener != null) {
            final ICalcButton calcButton = (ICalcButton) view;
            mListener.onButtonPressed(view, calcButton.getCategoryCode());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view instanceof ICalcButton) {
            return ViewUtils.showButtonDescription(mContext, view);
        }
        return false;
    }

    public void enableHiddenInput(boolean hiddenInputEnabled) {
//        mHiddenInput.setTextWatcher(false);
//        final int newVis = hiddenInputEnabled ? View.VISIBLE : View.GONE;
//        if (mHiddenInput.getVisibility() != newVis) {
//            mHiddenInput.setVisibility(newVis);
//        }
//        if (mHiddenInput.getVisibility() != View.GONE) {
//            mLastHiddenInput = "";
//            mHiddenInput.setText(mLastHiddenInput);
//            mHiddenInput.requestFocus();
//            mHiddenInput.setTextWatcher(true);
//        }
    }

    @Override
    public void beforeTextChanged(String text, boolean isManualInput) {
        // empty
    }

    @Override
    public void onTextChanged(String text, boolean isManualInput) {
        if (text == null || mListener == null) {
            mLastHiddenInput = null;
            return;
        }

        if (mLastHiddenInput != null && mLastHiddenInput.equals(text)) {
            return;
        }

        mLastHiddenInput = text;

        if (ClipboardManager.isFormulaObject(text)) {
//            mHiddenInput.setTextWatcher(false);
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
//            mHiddenInput.setTextWatcher(false);
            mListener.onButtonPressed(null, text);
            return;
        }

        for (int i = 0; i < mPaletteLayout.getChildCount(); i++) {
            if (mPaletteLayout.getChildAt(i) instanceof CalcImageButton) {
                CalcImageButton button = (CalcImageButton) mPaletteLayout.getChildAt(i);
                if (button.isEnabled() && button.getCategoryCode() != null && button.getCategoryCode().equalsIgnoreCase(code)) {
//                    mHiddenInput.setTextWatcher(false);
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
    public int onGetNextFocusId(CalcEditText owner, FocusType focusType) {
        return R.id.container_display;
    }
}
