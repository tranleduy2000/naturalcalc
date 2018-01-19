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
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.duy.natural.calc.calculator.evaluator.CalculateTask;
import com.duy.natural.calc.calculator.evaluator.CalculateTask.CancelException;
import com.mkulesh.micromath.formula.type.ComparatorType;
import com.mkulesh.micromath.formula.type.FormulaTermType;
import com.mkulesh.micromath.math.CalculatedValue;
import com.mkulesh.micromath.widgets.FormulaEditText;
import com.mkulesh.micromath.widgets.FormulaTextView;
import com.nstudio.calc.casio.R;

public class FormulaComparatorView extends FormulaTermView {
    // Attention: this is not thread-safety declaration!
    private final CalculatedValue leftTermValue = new CalculatedValue(), rightTermValue = new CalculatedValue();
    /**
     * Private attributes
     */
    private ComparatorType mComparatorType = null;
    private TermField mLeftTerm = null, mRightTerm = null;
    private FormulaTextView operatorKey = null;
    private boolean useBrackets = false;


    public FormulaComparatorView(TermField owner, LinearLayout layout, String text, int index) throws Exception {
        super(owner.getFormulaRoot(), layout, owner.mTermDepth);
        setParentField(owner);
        onCreate(text, index, owner.bracketsType);
    }


    public FormulaComparatorView(Context context) {
        super();
    }

    public FormulaComparatorView(Context context, AttributeSet attrs) {
        super();
    }

    @Nullable
    public static ComparatorType getComparatorType(Context context, String text) {
        for (ComparatorType type : ComparatorType.values()) {
            String symbol = context.getResources().getString(type.getSymbolId());
            if (text.equalsIgnoreCase(type.getCode()) || text.contains(symbol)) {
                return type;
            }
        }
        return null;
    }


    @Override
    public CalculatedValue.ValueType getValue(CalculateTask thread, CalculatedValue outValue) throws CancelException {
        return outValue.invalidate(CalculatedValue.ErrorType.TERM_NOT_READY);
    }

    @NonNull
    @Override
    public String toExpressionString() {
        String left = mLeftTerm.toExpressionString();
        String right = mRightTerm.toExpressionString();
        String opStr = mComparatorType.toString();
        return "((" + left + ")" + opStr + "(" + right + "))";
    }


    @Override
    public FormulaTermType.TermType getTermType() {
        return FormulaTermType.TermType.COMPARATOR;
    }

    @Override
    public String getTermCode() {
        return getComparatorType().getCode();
    }

    @Override
    protected FormulaTextView initializeSymbol(FormulaTextView v) {
        if (v.getText() != null) {
            String t = v.getText().toString();
            if (t.equals(getContext().getResources().getString(R.string.formula_operator_key))) {
                operatorKey = v;
                v.prepare(FormulaTextView.SymbolType.TEXT, getFormulaRoot().getFormulaList().getActivity(), this);
                updateOperatorKey();
            } else if (t.equals(getContext().getResources().getString(R.string.formula_left_bracket_key))) {
                v.prepare(FormulaTextView.SymbolType.LEFT_BRACKET, getFormulaRoot().getFormulaList().getActivity(), this);
                v.setText("."); // this text defines view width/height
            } else if (t.equals(getContext().getResources().getString(R.string.formula_right_bracket_key))) {
                v.prepare(FormulaTextView.SymbolType.RIGHT_BRACKET, getFormulaRoot().getFormulaList().getActivity(),
                        this);
                v.setText("."); // this text defines view width/height
            }
        }
        return v;
    }

    @Override
    protected FormulaEditText initializeTerm(FormulaEditText child, LinearLayout parent) {
        if (child.getText() != null) {
            if (child.getText().toString().equals(getContext().getResources().getString(R.string.formula_left_term_key))) {
                mLeftTerm = addTerm(getFormulaRoot(), parent, child, this, false);
            }
            if (child.getText().toString().equals(getContext().getResources().getString(R.string.formula_right_term_key))) {
                mRightTerm = addTerm(getFormulaRoot(), parent, child, this, false);
            }
        }
        return child;
    }


    @Override
    public void onDelete(FormulaEditText owner) {
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
        mComparatorType = getComparatorType(getContext(), text);
        if (mComparatorType == null) {
            throw new Exception("cannot create FormulaTermComparator for unknown comparator");
        }
        useBrackets = bracketsType != TermField.BracketsType.NEVER;
        inflateElements(useBrackets ? R.layout.formula_operator_hor_brackets : R.layout.formula_operator_hor, true);
        initializeElements(index);
        if (mLeftTerm == null || mRightTerm == null) {
            throw new Exception("cannot initialize comparators terms");
        }
        // set texts for left and right parts
        TermField.divideString(text, getContext().getResources().getString(mComparatorType.getSymbolId()), mLeftTerm,
                mRightTerm);
        // disable brackets of child terms in some cases
        switch (mComparatorType) {
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
        return mComparatorType;
    }

    /**
     * If possible, changes the comparator type
     */
    public boolean changeComparatorType(ComparatorType newType) {
        if (operatorKey == null) {
            return false;
        }
        if (newType == ComparatorType.COMPARATOR_AND || newType == ComparatorType.COMPARATOR_OR
                || mComparatorType == ComparatorType.COMPARATOR_AND || mComparatorType == ComparatorType.COMPARATOR_OR) {
            return false;
        }
        mComparatorType = newType;
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
