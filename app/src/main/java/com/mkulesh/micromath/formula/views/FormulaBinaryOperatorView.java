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
import android.widget.LinearLayout;

import com.duy.natural.calc.calculator.evaluator.CalculateTask;
import com.duy.natural.calc.calculator.evaluator.CalculateTask.CancelException;
import com.mkulesh.micromath.formula.type.OperatorType;
import com.mkulesh.micromath.math.CalculatedValue;
import com.mkulesh.micromath.widgets.CalcEditText;
import com.mkulesh.micromath.widgets.CalcTextView;
import com.nstudio.calc.casio.R;

public class FormulaBinaryOperatorView extends FormulaTermView {
    // Attention: this is not thread-safety declaration!
    private final CalculatedValue fVal = new CalculatedValue();
    private final CalculatedValue gVal = new CalculatedValue();

    private OperatorType mOperatorType = null;
    private TermField mLeftTerm = null;
    private TermField mRightTerm = null;
    private boolean mUseBrackets = false;

    public FormulaBinaryOperatorView(TermField owner, LinearLayout layout, String text, int index) throws Exception {
        super(owner.getFormulaRoot(), layout, owner.mTermDepth);
        setParentField(owner);
        onCreate(text, index, owner.bracketsType);
    }

    public FormulaBinaryOperatorView(Context context) {
        super();
    }

    public FormulaBinaryOperatorView(Context context, AttributeSet attrs) {
        super();
    }

    public static OperatorType getOperatorType(Context context, String text) {
        OperatorType result = null;
        for (OperatorType operatorType : OperatorType.values()) {
            if (text.equalsIgnoreCase(operatorType.getCode())
                    || text.contains(context.getResources().getString(operatorType.getSymbolId()))) {
                result = operatorType;
                break;
            }
        }
        return result;
    }

    @Override
    public CalculatedValue.ValueType getValue(CalculateTask thread, CalculatedValue outValue) throws CancelException {
        return outValue.invalidate(CalculatedValue.ErrorType.TERM_NOT_READY);
    }

    @NonNull
    @Override
    public String toExpressionString() {
        String leftStr = mLeftTerm.toExpressionString();
        String rightStr = mRightTerm.toExpressionString();
        String opStr = mOperatorType.toString();
        return "((" + leftStr + ")" + opStr + "(" + rightStr + "))";
    }


    @Override
    public TermField.TermType getTermType() {
        return TermField.TermType.OPERATOR;
    }

    @Override
    public String getTermCode() {
        return getOperatorType().getCode();
    }

    @Override
    protected CalcTextView initializeSymbol(CalcTextView view) {
        if (view.getText() != null) {
            String t = view.getText().toString();
            if (t.equals(getContext().getResources().getString(R.string.formula_operator_key))) {
                switch (mOperatorType) {
                    case PLUS:
                        view.prepare(CalcTextView.SymbolType.PLUS, getFormulaRoot().getFormulaList().getActivity(), this);
                        view.setText("..");
                        break;
                    case MINUS:
                        view.prepare(CalcTextView.SymbolType.MINUS, getFormulaRoot().getFormulaList().getActivity(), this);
                        view.setText("..");
                        break;
                    case MULTIPLY:
                        view.prepare(CalcTextView.SymbolType.MULT, getFormulaRoot().getFormulaList().getActivity(), this);
                        view.setText(".");
                        break;
                    case FRACTION:
                        view.prepare(CalcTextView.SymbolType.HOR_LINE, getFormulaRoot().getFormulaList().getActivity(), this);
                        view.setText("_");
                        break;
                    case DIVIDE_SLASH:
                        view.prepare(CalcTextView.SymbolType.SLASH, getFormulaRoot().getFormulaList().getActivity(), this);
                        view.setText("_");
                        break;
                }
            } else if (t.equals(getContext().getResources().getString(R.string.formula_left_bracket_key))) {
                view.prepare(CalcTextView.SymbolType.LEFT_BRACKET, getFormulaRoot().getFormulaList().getActivity(), this);
                view.setText("."); // this text defines view width/height
            } else if (t.equals(getContext().getResources().getString(R.string.formula_right_bracket_key))) {
                view.prepare(CalcTextView.SymbolType.RIGHT_BRACKET, getFormulaRoot().getFormulaList().getActivity(),
                        this);
                view.setText("."); // this text defines view width/height
            }
        }
        return view;
    }

    @Override
    protected CalcEditText initializeTerm(CalcEditText editText, LinearLayout l) {
        if (editText.getText() != null) {
            if (editText.getText().toString().equals(getContext().getResources().getString(R.string.formula_left_term_key))) {
                final boolean addDepth = mOperatorType == OperatorType.FRACTION;
                mLeftTerm = addTerm(getFormulaRoot(), l, editText, this, addDepth);
            }
            if (editText.getText().toString().equals(getContext().getResources().getString(R.string.formula_right_term_key))) {
                final int addDepth = (mOperatorType == OperatorType.FRACTION) ? 1 : 0;
                mRightTerm = addTerm(getFormulaRoot(), l, -1, editText, this, addDepth);
            }
        }
        return editText;
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


    /**
     * Procedure creates the formula layout
     */
    private void onCreate(String s, int index, TermField.BracketsType bracketsType) throws Exception {
        if (index < 0 || index > layout.getChildCount()) {
            throw new Exception("cannot create FormulaTermOperator for invalid insertion index " + index);
        }
        mOperatorType = getOperatorType(getContext(), s);
        if (mOperatorType == null) {
            throw new Exception("cannot create FormulaTermOperator for unknown operator");
        }
        switch (mOperatorType) {
            case PLUS:
            case MINUS:
                mUseBrackets = bracketsType != TermField.BracketsType.NEVER;
                inflateElements(mUseBrackets ? R.layout.formula_operator_hor_brackets : R.layout.formula_operator_hor, true);
                break;
            case MULTIPLY:
            case DIVIDE_SLASH:
                mUseBrackets = bracketsType == TermField.BracketsType.ALWAYS;
                inflateElements(mUseBrackets ? R.layout.formula_operator_hor_brackets : R.layout.formula_operator_hor, true);
                break;
            case FRACTION:
                mUseBrackets = bracketsType == TermField.BracketsType.ALWAYS;
                inflateElements(mUseBrackets ? R.layout.formula_operator_vert_brackets : R.layout.formula_operator_vert, true);
                break;
        }
        initializeElements(index);
        if (mLeftTerm == null || mRightTerm == null) {
            throw new Exception("cannot initialize operator terms");
        }
        // set texts for left and right parts
        TermField.divideString(s, getContext().getResources().getString(mOperatorType.getSymbolId()), mLeftTerm, mRightTerm);

        // disable brackets of child terms in some cases
        switch (mOperatorType) {
            case FRACTION:
            case PLUS:
                mLeftTerm.bracketsType = TermField.BracketsType.NEVER;
                mRightTerm.bracketsType = TermField.BracketsType.NEVER;
                break;
            case DIVIDE_SLASH:
            case MULTIPLY:
                mLeftTerm.bracketsType = TermField.BracketsType.IFNECESSARY;
                mRightTerm.bracketsType = TermField.BracketsType.IFNECESSARY;
                break;
            case MINUS:
                mLeftTerm.bracketsType = TermField.BracketsType.NEVER;
                mRightTerm.bracketsType = TermField.BracketsType.IFNECESSARY;
                break;
        }
    }

    /**
     * Returns operator type
     */
    public OperatorType getOperatorType() {
        return mOperatorType;
    }

    /**
     * Returns whether the brackets are used
     */
    public boolean isUseBrackets() {
        return mUseBrackets;
    }


}
