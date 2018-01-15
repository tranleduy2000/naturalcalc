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
package com.mkulesh.micromath.formula.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.duy.common.utils.DLog;
import com.mkulesh.micromath.formula.IArgumentHolder;
import com.duy.natural.calc.calculator.calcbutton.button.Category;
import com.duy.natural.calc.calculator.calcbutton.button.ICalcButton;
import com.duy.natural.calc.calculator.evaluator.CalculateTask;
import com.duy.natural.calc.calculator.evaluator.CalculateTask.CancelException;
import com.mkulesh.micromath.formula.type.LoopType;
import com.mkulesh.micromath.formula.type.TermType;
import com.mkulesh.micromath.math.CalculatedValue;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.widgets.CalcEditText;
import com.mkulesh.micromath.widgets.CalcTextView;
import com.mkulesh.micromath.widgets.ScaledDimensions;
import com.nstudio.calc.casio.R;

import java.util.ArrayList;

import static com.mkulesh.micromath.formula.type.LoopType.INTEGRAL;
import static com.mkulesh.micromath.widgets.CalcTextView.SymbolType.SUMMATION;

public class FormulaTermLoopView extends FormulaTermView implements IArgumentHolder {
    private static final String SYMBOL_LAYOUT_TAG = "SYMBOL_LAYOUT_TAG";
    private static final String MIN_VALUE_LAYOUT_TAG = "MIN_VALUE_LAYOUT_TAG";
    private static final String MAX_VALUE_LAYOUT_TAG = "MAX_VALUE_LAYOUT_TAG";
    private static final String TAG = "FormulaTermLoopView";
    //    private final LoopCalculator loopCalculator = new LoopCalculator();
    // Attention: this is not thread-safety declaration!
    private final CalculatedValue minValue = new CalculatedValue(), maxValue = new CalculatedValue(),
            calcVal = new CalculatedValue(), argValue = new CalculatedValue();
    /**
     * Private attributes
     */
    private LoopType mLoopType = null;
    private LinearLayout symbolLayout = null, minValueLayout = null, maxValueLayout = null;
    private TermField mIndexTerm = null;
    private TermField mMinValueTerm = null;
    private TermField mMaxValueTerm = null;
    private TermField mArgTerm = null;
    private boolean useBrackets = false;


    public FormulaTermLoopView(TermField owner, LinearLayout layout, String code, int index) throws Exception {
        super(owner.getFormulaRoot(), layout, owner.mTermDepth);
        setParentField(owner);
        onCreate(code, index, owner.bracketsType);
    }

    public FormulaTermLoopView(Context context) {
        super();
    }

    public FormulaTermLoopView(Context context, AttributeSet attrs) {
        super();
    }

    public static LoopType getLoopType(Context context, String code) {
        LoopType retValue = null;
        for (LoopType f : LoopType.values()) {
            if (code.equals(f.getLowerCaseName())
                    || code.contains(context.getResources().getString(f.getSymbolId()))) {
                retValue = f;
                break;
            }
        }
        return retValue;
    }

    /**
     * Add palette buttons for this term
     */
    public static void addToPalette(ViewGroup parent, Category[] categories) {
        for (int i = 0; i < LoopType.values().length; i++) {
            final LoopType type = LoopType.values()[i];
            int id = type.getViewId();
            View view = parent.findViewById(id);
            if (view instanceof ICalcButton) {
                ICalcButton p = (ICalcButton) view;
                p.initWithParameter(type.getSymbolId(), type.getDescriptionId(), type.getLowerCaseName());
                p.setCategories(categories);
            }
        }
    }

    @Override
    public CalculatedValue.ValueType getValue(CalculateTask thread, CalculatedValue outValue) throws CancelException {
        return outValue.invalidate(CalculatedValue.ErrorType.TERM_NOT_READY);
    }

    @NonNull
    @Override
    public String toExpressionString() {
        String arg = mArgTerm.toExpressionString();
        String var = mIndexTerm.toExpressionString();
        switch (mLoopType) {
            case PRODUCT:
            case INTEGRAL:
            case SUMMATION: {
                String min = mMinValueTerm.toExpressionString();
                String max = mMaxValueTerm.toExpressionString();
                return String.format("%s(%s,{%s,%s,%s})", mLoopType.toString(), arg, var, min, max);
            }
            case DERIVATIVE:
                return String.format("D(%s)", arg + "," + var);
        }
        return null;
    }

    @Override
    public TermType getTermType() {
        return TermType.LOOP;
    }

    @Override
    public String getTermCode() {
        return getLoopType().getLowerCaseName();
    }

    @Override
    public boolean isContentValid(ValidationPassType type) {
        if (DLog.DEBUG) DLog.d(TAG, "isContentValid() called with: type = [" + type + "]");

        boolean isValid = true;
        switch (type) {
            case VALIDATE_SINGLE_FORMULA:
                isValid = super.isContentValid(type);
                break;
            case VALIDATE_LINKS:
                isValid = super.isContentValid(type);
                break;
        }
        if (DLog.DEBUG) DLog.d(TAG, "isContentValid() returned: " + isValid);
        return isValid;
    }

    @Override
    protected CalcTextView initializeSymbol(CalcTextView v) {
        if (v.getText() != null) {
            String t = v.getText().toString();
            if (t.equals(getContext().getResources().getString(R.string.formula_operator_key))) {
                switch (mLoopType) {
                    case SUMMATION:
                        v.prepare(SUMMATION, getFormulaRoot().getFormulaList().getActivity(),
                                this);
                        v.setText("S..");
                        break;
                    case PRODUCT:
                        v.prepare(CalcTextView.SymbolType.PRODUCT, getFormulaRoot().getFormulaList().getActivity(), this);
                        v.setText("S..");
                        break;
                    case INTEGRAL:
                        v.prepare(CalcTextView.SymbolType.INTEGRAL, getFormulaRoot().getFormulaList().getActivity(), this);
                        v.setText("S..");
                        break;
                    case DERIVATIVE:
                        v.prepare(CalcTextView.SymbolType.HOR_LINE, getFormulaRoot().getFormulaList().getActivity(), this);
                        break;
                }
            } else if (t.equals(getContext().getResources().getString(R.string.formula_left_bracket_key))) {
                v.prepare(CalcTextView.SymbolType.LEFT_BRACKET, getFormulaRoot().getFormulaList().getActivity(), this);
                v.setText("."); // this text defines view width/height
            } else if (t.equals(getContext().getResources().getString(R.string.formula_right_bracket_key))) {
                v.prepare(CalcTextView.SymbolType.RIGHT_BRACKET, getFormulaRoot().getFormulaList().getActivity(),
                        this);
                v.setText("."); // this text defines view width/height
            } else if (t.equals(getContext().getResources().getString(R.string.formula_loop_diff))) {
                v.prepare(CalcTextView.SymbolType.TEXT, getFormulaRoot().getFormulaList().getActivity(), this);
            }
        }
        return v;
    }

    @Override
    protected CalcEditText initializeTerm(CalcEditText v, LinearLayout l) {
        final int addDepth = (mLoopType == INTEGRAL || mLoopType == LoopType.DERIVATIVE) ? 0 : 3;
        if (v.getText() != null) {
            if (v.getText().toString().equals(getContext().getResources().getString(R.string.formula_max_value_key))) {
                mMaxValueTerm = addTerm(getFormulaRoot(), l, -1, v, this, addDepth);
            } else if (v.getText().toString()
                    .equals(getContext().getResources().getString(R.string.formula_min_value_key))) {
                mMinValueTerm = addTerm(getFormulaRoot(), l, -1, v, this, addDepth);
            } else if (v.getText().toString().equals(getContext().getResources().getString(R.string.formula_index_key))) {
                mIndexTerm = addTerm(getFormulaRoot(), l, -1, v, this, addDepth);
            } else if (v.getText().toString().equals(getContext().getResources().getString(R.string.formula_arg_term_key))) {
                mArgTerm = addTerm(getFormulaRoot(), l, v, this, false);
            }
        }
        return v;
    }

    @Override
    public void updateTextSize() {
        super.updateTextSize();
        final int padding = getFormulaList().getDimen().get(ScaledDimensions.Type.HOR_SYMBOL_PADDING);
        symbolLayout.setPadding(padding, 0, padding, 0);
        if (mLoopType == INTEGRAL && maxValueLayout != null && minValueLayout != null) {
            maxValueLayout.setPadding(4 * padding, 0, 0, 0);
            minValueLayout.setPadding(0, 0, 2 * padding, 0);
        }
    }

    @Override
    public TermField getArgumentTerm() {
        return mArgTerm;
    }


    @Override
    public void onDelete(CalcEditText owner) {
        final TermField t = findTerm(owner);
        TermField r = (t != null && t != getArgumentTerm()) ? getArgumentTerm() : null;
        parentField.onTermDelete(removeElements(), r);
    }


    @Override
    public ArrayList<String> getArguments() {
        final String indexName = getIndexName();
        if (indexName != null) {
            ArrayList<String> retValue = new ArrayList<>();
            retValue.add(indexName);
            return retValue;
        }
        return null;
    }

    @Override
    public int getArgumentIndex(String text) {
        final String indexName = getIndexName();
        if (text != null && indexName != null) {
            return indexName.equals(text) ? 0 : ViewUtils.INVALID_INDEX;
        }
        return ViewUtils.INVALID_INDEX;
    }

    @Override
    public CalculatedValue getArgumentValue(int idx) {
        if (idx != 0) {
            argValue.invalidate(CalculatedValue.ErrorType.TERM_NOT_READY);
        }
        return argValue;
    }

    /**
     * Procedure creates the formula layout
     */
    private void onCreate(String code, int idx, TermField.BracketsType bracketsType) throws Exception {
        if (idx < 0 || idx > layout.getChildCount()) {
            throw new Exception("cannot create FormulaTermLoop for invalid insertion index " + idx);
        }
        mLoopType = getLoopType(getContext(), code);
        if (mLoopType == null) {
            throw new Exception("cannot create FormulaTermLoop for unknown loop type");
        }
        switch (mLoopType) {
            case SUMMATION:
            case PRODUCT:
                useBrackets = bracketsType == TermField.BracketsType.ALWAYS;
                inflateElements(useBrackets ? R.layout.formula_loop_brackets : R.layout.formula_loop, true);
                break;
            case INTEGRAL:
                useBrackets = bracketsType == TermField.BracketsType.ALWAYS;
                inflateElements(useBrackets ? R.layout.formula_loop_integral_brackets : R.layout.formula_loop_integral,
                        true);
                break;
            case DERIVATIVE:
                useBrackets = bracketsType == TermField.BracketsType.ALWAYS;
                inflateElements(useBrackets ? R.layout.formula_loop_derivative_brackets : R.layout.formula_loop_derivative,
                        true);
                break;
        }
        initializeElements(idx);
        symbolLayout = getLayoutWithTag(SYMBOL_LAYOUT_TAG);
        if (mIndexTerm == null || mArgTerm == null || symbolLayout == null) {
            throw new Exception("cannot initialize loop terms");
        }
        if (mLoopType != LoopType.DERIVATIVE) {
            minValueLayout = getLayoutWithTag(MIN_VALUE_LAYOUT_TAG);
            maxValueLayout = getLayoutWithTag(MAX_VALUE_LAYOUT_TAG);
            if (mMinValueTerm == null || mMaxValueTerm == null) {
                throw new Exception("cannot initialize loop minimum/maximum value terms");
            }
            mMinValueTerm.bracketsType = TermField.BracketsType.NEVER;
            mMaxValueTerm.bracketsType = TermField.BracketsType.NEVER;
        }
        mArgTerm.bracketsType = TermField.BracketsType.IFNECESSARY;

        // restore the previous code
        final String opCode = getContext().getResources().getString(mLoopType.getSymbolId());
        final int opPosition = code.indexOf(opCode);
        if (opPosition >= 0) {
            try {
                // the code shall be before code
                getArgumentTerm().setText(code.subSequence(opPosition + opCode.length(), code.length()));
                isContentValid(ValidationPassType.VALIDATE_SINGLE_FORMULA);
            } catch (Exception ex) {
                // nothig to do
            }
        }
    }

    private LinearLayout getLayoutWithTag(final String tag) {
        LinearLayout retValue = layout.findViewWithTag(tag);
        if (retValue != null) {
            retValue.setTag(null);
        }
        return retValue;
    }

    /**
     * Returns loop type
     */
    public LoopType getLoopType() {
        return mLoopType;
    }

    /**
     * Returns the index name for this loop
     */
    public String getIndexName() {
        // Do not check here ContentType of indexTerm since this procedure itself is called
        // from checkContentType for indexTerm where ContentType is not yet set
        return mIndexTerm.getParser().getFunctionName();
    }

    public boolean isUseBrackets() {
        return useBrackets;
    }


}
