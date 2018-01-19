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
import com.mkulesh.micromath.formula.type.FormulaTermType;
import com.mkulesh.micromath.formula.type.IntervalType;
import com.mkulesh.micromath.math.CalculatedValue;
import com.mkulesh.micromath.widgets.FormulaEditText;
import com.mkulesh.micromath.widgets.FormulaTextView;
import com.nstudio.calc.casio.R;

import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;

public class FormulaTermIntervalView extends FormulaTermView {
    // Attention: this is not thread-safety declaration!
    private final CalculatedValue minValue = new CalculatedValue();
    private final CalculatedValue nextValue = new CalculatedValue();
    private final CalculatedValue maxValue = new CalculatedValue();
    /**
     * Private attributes
     */
    private IntervalType mIntervalType = null;
    private TermField mMinValueTerm;
    private TermField mNextValueTerm;
    private TermField mMaxValueTerm = null;


    public FormulaTermIntervalView(TermField owner, LinearLayout layout, String s, int idx) throws Exception {
        super(owner.getFormulaRoot(), layout, owner.mTermDepth);
        setParentField(owner);
        onCreate(s, idx);
    }


    public FormulaTermIntervalView(Context context) {
        super();
    }

    public FormulaTermIntervalView(Context context, AttributeSet attrs) {
        super();
    }

    public static IntervalType getIntervalType(Context context, String s) {
        IntervalType retValue = null;
        for (IntervalType f : IntervalType.values()) {
            if (s.equals(f.getCode())
                    || s.contains(context.getResources().getString(f.getSymbolId()))) {
                retValue = f;
                break;
            }
        }
        return retValue;
    }



    @Override
    public CalculatedValue.ValueType getValue(CalculateTask thread, CalculatedValue outValue) throws CancelException {
        if (getFormulaRoot() instanceof EquationView) {
            minValue.processRealTerm(thread, mMinValueTerm);
            nextValue.processRealTerm(thread, mNextValueTerm);
            maxValue.processRealTerm(thread, mMaxValueTerm);
            if (minValue.isNaN() || nextValue.isNaN() || maxValue.isNaN()) {
                return outValue.invalidate(CalculatedValue.ErrorType.NOT_A_REAL);
            }
            final CalculatedValue calcDelta = getDelta(minValue.getReal(), nextValue.getReal(), maxValue.getReal());
            final CalculatedValue ravArg = ((EquationView) getFormulaRoot()).getArgumentValue(0);
            if (calcDelta.isNaN() || ravArg.isNaN()) {
                return outValue.invalidate(CalculatedValue.ErrorType.NOT_A_REAL);
            }
            final long idx = ravArg.getInteger();
            final int N = getNumberOfPoints(minValue.getReal(), maxValue.getReal(), calcDelta.getReal());
            if (idx == 0) {
                return outValue.setValue(minValue.getReal());
            } else if (idx == N) {
                return outValue.setValue(maxValue.getReal());
            } else if (idx > 0 && idx < N) {
                return outValue.setValue(minValue.getReal() + calcDelta.getReal() * (double) idx);
            }
        }
        return outValue.invalidate(CalculatedValue.ErrorType.TERM_NOT_READY);
    }

    @NonNull
    @Override
    public String toExpressionString() {
        return null;
    }

    @Override
    public FormulaTermType.TermType getTermType() {
        return FormulaTermType.TermType.INTERVAL;
    }

    @Override
    public String getTermCode() {
        return getIntervalType().getCode();
    }

    @Override
    protected FormulaTextView initializeSymbol(FormulaTextView v) {
        if (v.getText() != null) {
            String t = v.getText().toString();
            if (t.equals(getContext().getResources().getString(R.string.formula_left_bracket_key))) {
                v.prepare(FormulaTextView.SymbolType.LEFT_SQR_BRACKET, getFormulaRoot().getFormulaList().getActivity(),
                        this);
                v.setText("."); // this text defines view width/height
            } else if (t.equals(getContext().getResources().getString(R.string.formula_right_bracket_key))) {
                v.prepare(FormulaTextView.SymbolType.RIGHT_SQR_BRACKET, getFormulaRoot().getFormulaList().getActivity(),
                        this);
                v.setText("."); // this text defines view width/height
            } else if (t.equals(getContext().getResources().getString(R.string.formula_first_separator_key))) {
                v.prepare(FormulaTextView.SymbolType.TEXT, getFormulaRoot().getFormulaList().getActivity(), this);
                v.setText(getContext().getResources().getString(R.string.formula_interval_first_separator));
            } else if (t.equals(getContext().getResources().getString(R.string.formula_second_separator_key))) {
                v.prepare(FormulaTextView.SymbolType.TEXT, getFormulaRoot().getFormulaList().getActivity(), this);
                v.setText(getContext().getResources().getString(R.string.formula_interval_second_separator));
            }
        }
        return v;
    }

    @Override
    protected FormulaEditText initializeTerm(FormulaEditText child, LinearLayout parent) {
        if (child.getText() != null) {
            if (child.getText().toString().equals(getContext().getResources().getString(R.string.formula_min_value_key))) {
                mMinValueTerm = addTerm(getFormulaRoot(), parent, child, this, false);
                mMinValueTerm.bracketsType = TermField.BracketsType.NEVER;
            } else if (child.getText().toString()
                    .equals(getContext().getResources().getString(R.string.formula_next_value_key))) {
                mNextValueTerm = addTerm(getFormulaRoot(), parent, child, this, false);
                mNextValueTerm.bracketsType = TermField.BracketsType.NEVER;
            } else if (child.getText().toString()
                    .equals(getContext().getResources().getString(R.string.formula_max_value_key))) {
                mMaxValueTerm = addTerm(getFormulaRoot(), parent, child, this, false);
                mMaxValueTerm.bracketsType = TermField.BracketsType.NEVER;
            }
        }
        return child;
    }


    /**
     * Procedure creates the formula layout
     */
    private void onCreate(String s, int idx) throws Exception {
        if (idx < 0 || idx > layout.getChildCount()) {
            throw new Exception("cannot create FormulaFunction for invalid insertion index " + idx);
        }
        mIntervalType = getIntervalType(getContext(), s);
        if (mIntervalType == null) {
            throw new Exception("cannot create FormulaFunction for unknown function");
        }
        inflateElements(R.layout.formula_interval, true);
        initializeElements(idx);
        if (mMinValueTerm == null || mNextValueTerm == null || mMaxValueTerm == null) {
            throw new Exception("cannot initialize function terms");
        }
    }

    /**
     * Returns function type
     */
    public IntervalType getIntervalType() {
        return mIntervalType;
    }

    /**
     * Procedure returns declared interval if this root formula represents an interval
     */
    public ArrayList<Double> getInterval(CalculateTask thread) throws CancelException {
        minValue.processRealTerm(thread, mMinValueTerm);
        nextValue.processRealTerm(thread, mNextValueTerm);
        maxValue.processRealTerm(thread, mMaxValueTerm);
        if (minValue.isNaN() || nextValue.isNaN() || maxValue.isNaN()) {
            return null;
        }
        final CalculatedValue calcDelta = getDelta(minValue.getReal(), nextValue.getReal(), maxValue.getReal());
        if (calcDelta.isNaN()) {
            return null;
        }
        final int N = getNumberOfPoints(minValue.getReal(), maxValue.getReal(), calcDelta.getReal());
        ArrayList<Double> retValue = new ArrayList<>(N);
        for (int idx = 0; idx <= N; idx++) {
            if (thread != null) {
                thread.checkCancellation();
            }
            if (idx == 0) {
                retValue.add(minValue.getReal());
            } else if (idx == N) {
                retValue.add(maxValue.getReal());
            } else {
                retValue.add(minValue.getReal() + calcDelta.getReal() * (double) idx);
            }
        }
        return retValue;
    }

    /**
     * Procedure checks and returns delta value
     */
    private CalculatedValue getDelta(final double min, final double next, final double max) {
        final CalculatedValue calcVal = new CalculatedValue();
        if (next <= min || max < next) {
            // error: invalid boundaries
            calcVal.invalidate(CalculatedValue.ErrorType.NOT_A_NUMBER);
        } else {
            calcVal.setValue(next - min);
        }
        return calcVal;
    }

    private int getNumberOfPoints(double min, double max, double delta) {
        int N = (int) FastMath.ceil(((max - min) / delta));
        if (N > 0 && min + delta * (double) N > max + delta / 2) {
            N--;
        }
        return N;
    }

}
