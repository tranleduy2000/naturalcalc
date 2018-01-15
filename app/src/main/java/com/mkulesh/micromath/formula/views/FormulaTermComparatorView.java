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

import com.duy.natural.calc.calculator.calcbutton.Category;
import com.duy.natural.calc.calculator.calcbutton.ICalcButton;
import com.duy.natural.calc.calculator.evaluator.CalculateTask;
import com.duy.natural.calc.calculator.evaluator.CalculateTask.CancelException;
import com.mkulesh.micromath.formula.type.ComparatorType;
import com.mkulesh.micromath.formula.type.TermType;
import com.mkulesh.micromath.math.CalculatedValue;
import com.mkulesh.micromath.widgets.CalcEditText;
import com.mkulesh.micromath.widgets.CalcTextView;
import com.nstudio.calc.casio.R;

public class FormulaTermComparatorView extends FormulaTermView {
    // Attention: this is not thread-safety declaration!
    private final CalculatedValue leftTermValue = new CalculatedValue(), rightTermValue = new CalculatedValue();
    /**
     * Private attributes
     */
    private ComparatorType comparatorType = null;
    private TermField mLeftTerm = null, mRightTerm = null;
    private CalcTextView operatorKey = null;
    private boolean useBrackets = false;


    public FormulaTermComparatorView(TermField owner, LinearLayout layout, String text, int index) throws Exception {
        super(owner.getFormulaRoot(), layout, owner.mTermDepth);
        setParentField(owner);
        onCreate(text, index, owner.bracketsType);
    }


    public FormulaTermComparatorView(Context context) {
        super();
    }

    public FormulaTermComparatorView(Context context, AttributeSet attrs) {
        super();
    }

    public static ComparatorType getComparatorType(Context context, String text) {
        ComparatorType retValue = null;
        for (ComparatorType f : ComparatorType.values()) {
            if (text.equals(f.getLowerCaseName())
                    || text.contains(context.getResources().getString(f.getSymbolId()))) {
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
        for (int i = 0; i < ComparatorType.values().length; i++) {
            final ComparatorType type = ComparatorType.values()[i];
            View view = parent.findViewById(type.getViewId());
            if (view instanceof ICalcButton) {
                ICalcButton p = (ICalcButton) view;
                p.initWithParameter(type.getSymbolId(), type.getDescriptionId(), type.getLowerCaseName());
                p.setCategories(categories);
            }
        }
    }


    @Override
    public CalculatedValue.ValueType getValue(CalculateTask thread, CalculatedValue outValue) throws CancelException {
        if (comparatorType != null) {
            mLeftTerm.getValue(thread, leftTermValue);
            mRightTerm.getValue(thread, rightTermValue);
            // Do not check invalid value since a comparator can handle it!
            switch (comparatorType) {
                case EQUAL:
                    return outValue.setValue((leftTermValue.getReal() == rightTermValue.getReal()) ? 1 : -1);
                case NOT_EQUAL:
                    return outValue.setValue((leftTermValue.getReal() != rightTermValue.getReal()) ? 1 : -1);
                case LESS:
                    return outValue.setValue((leftTermValue.getReal() < rightTermValue.getReal()) ? 1 : -1);
                case LESS_EQUAL:
                    return outValue.setValue((leftTermValue.getReal() <= rightTermValue.getReal()) ? 1 : -1);
                case GREATER:
                    return outValue.setValue((leftTermValue.getReal() > rightTermValue.getReal()) ? 1 : -1);
                case GREATER_EQUAL:
                    return outValue.setValue((leftTermValue.getReal() >= rightTermValue.getReal()) ? 1 : -1);
                case COMPARATOR_AND:
                    return outValue.setValue((leftTermValue.getReal() > 0 && rightTermValue.getReal() > 0) ? 1 : -1);
                case COMPARATOR_OR:
                    return outValue.setValue((leftTermValue.getReal() > 0 || rightTermValue.getReal() > 0) ? 1 : -1);
            }
        }
        return outValue.invalidate(CalculatedValue.ErrorType.TERM_NOT_READY);
    }

    @NonNull
    @Override
    public String toExpressionString() {
        String left = mLeftTerm.toExpressionString();
        String right = mRightTerm.toExpressionString();
        String opStr = operatorKey.toString();
        return "((" + left + ")" + opStr + "(" + right + "))";
    }


    @Override
    public TermType getTermType() {
        return TermType.COMPARATOR;
    }

    @Override
    public String getTermCode() {
        return getComparatorType().getLowerCaseName();
    }

    @Override
    protected CalcTextView initializeSymbol(CalcTextView v) {
        if (v.getText() != null) {
            String t = v.getText().toString();
            if (t.equals(getContext().getResources().getString(R.string.formula_operator_key))) {
                operatorKey = v;
                v.prepare(CalcTextView.SymbolType.TEXT, getFormulaRoot().getFormulaList().getActivity(), this);
                updateOperatorKey();
            } else if (t.equals(getContext().getResources().getString(R.string.formula_left_bracket_key))) {
                v.prepare(CalcTextView.SymbolType.LEFT_BRACKET, getFormulaRoot().getFormulaList().getActivity(), this);
                v.setText("."); // this text defines view width/height
            } else if (t.equals(getContext().getResources().getString(R.string.formula_right_bracket_key))) {
                v.prepare(CalcTextView.SymbolType.RIGHT_BRACKET, getFormulaRoot().getFormulaList().getActivity(),
                        this);
                v.setText("."); // this text defines view width/height
            }
        }
        return v;
    }

    @Override
    protected CalcEditText initializeTerm(CalcEditText v, LinearLayout l) {
        if (v.getText() != null) {
            if (v.getText().toString().equals(getContext().getResources().getString(R.string.formula_left_term_key))) {
                mLeftTerm = addTerm(getFormulaRoot(), l, v, this, false);
            }
            if (v.getText().toString().equals(getContext().getResources().getString(R.string.formula_right_term_key))) {
                mRightTerm = addTerm(getFormulaRoot(), l, v, this, false);
            }
        }
        return v;
    }


    @Override
    public void onDelete(CalcEditText owner) {
        if (parentField != null) {
            TermField t = findTerm(owner);
            TermField r = null;
            if (t != null) {
                r = (t == mLeftTerm) ? mRightTerm : mLeftTerm;
            }
            parentField.onTermDelete(removeElements(), r);
        }
        getFormulaRoot().getFormulaList().onManualInput();
    }

    /*********************************************************
     * FormulaTermComparator-specific methods
     *********************************************************/

    /**
     * Procedure creates the formula layout
     */
    private void onCreate(String text, int index, TermField.BracketsType bracketsType) throws Exception {
        if (index < 0 || index > layout.getChildCount()) {
            throw new Exception("cannot create FormulaTermComparator for invalid insertion index " + index);
        }
        comparatorType = getComparatorType(getContext(), text);
        if (comparatorType == null) {
            throw new Exception("cannot create FormulaTermComparator for unknown comparator");
        }
        useBrackets = bracketsType != TermField.BracketsType.NEVER;
        inflateElements(useBrackets ? R.layout.formula_operator_hor_brackets : R.layout.formula_operator_hor, true);
        initializeElements(index);
        if (mLeftTerm == null || mRightTerm == null) {
            throw new Exception("cannot initialize comparators terms");
        }
        // set texts for left and right parts
        TermField.divideString(text, getContext().getResources().getString(comparatorType.getSymbolId()), mLeftTerm,
                mRightTerm);
        // disable brackets of child terms in some cases
        switch (comparatorType) {
            case EQUAL:
            case NOT_EQUAL:
            case GREATER:
            case GREATER_EQUAL:
            case LESS:
            case LESS_EQUAL:
                mLeftTerm.bracketsType = TermField.BracketsType.NEVER;
                mRightTerm.bracketsType = TermField.BracketsType.NEVER;
                break;
            case COMPARATOR_AND:
            case COMPARATOR_OR:
                mLeftTerm.bracketsType = TermField.BracketsType.IFNECESSARY;
                mLeftTerm.getEditText().setComparatorEnabled(true);
                mRightTerm.bracketsType = TermField.BracketsType.IFNECESSARY;
                mRightTerm.getEditText().setComparatorEnabled(true);
                break;
        }
    }

    /**
     * Returns comparator type
     */
    public ComparatorType getComparatorType() {
        return comparatorType;
    }

    /**
     * If possible, changes the comparator type
     */
    public boolean changeComparatorType(ComparatorType newType) {
        if (operatorKey == null) {
            return false;
        }
        if (newType == ComparatorType.COMPARATOR_AND || newType == ComparatorType.COMPARATOR_OR
                || comparatorType == ComparatorType.COMPARATOR_AND || comparatorType == ComparatorType.COMPARATOR_OR) {
            return false;
        }
        comparatorType = newType;
        updateOperatorKey();
        return true;
    }

    /**
     * Procedure sets the operator text depends on the current comparator type
     */
    private void updateOperatorKey() {
        switch (getComparatorType()) {
            case EQUAL:
                operatorKey.setText("=");
                break;
            case NOT_EQUAL:
                operatorKey.setText("\u2260");
                break;
            case LESS:
                operatorKey.setText("<");
                break;
            case LESS_EQUAL:
                operatorKey.setText("\u2264");
                break;
            case GREATER:
                operatorKey.setText(">");
                break;
            case GREATER_EQUAL:
                operatorKey.setText("\u2265");
                break;
            case COMPARATOR_AND:
                operatorKey.setText(R.string.math_comparator_and_text);
                break;
            case COMPARATOR_OR:
                operatorKey.setText(R.string.math_comparator_or_text);
                break;
        }
    }

    /**
     * Returns whether the brackets are used
     */
    public boolean isUseBrackets() {
        return useBrackets;
    }


}
