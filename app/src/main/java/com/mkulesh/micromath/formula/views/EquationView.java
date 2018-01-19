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
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.mkulesh.micromath.dialogs.DialogResultDetails;
import com.mkulesh.micromath.formula.FormulaList;
import com.mkulesh.micromath.formula.IArgumentHolder;
import com.duy.natural.calc.calculator.evaluator.CalculateTask;
import com.duy.natural.calc.calculator.evaluator.CalculateTask.CancelException;
import com.mkulesh.micromath.formula.type.BaseType;
import com.mkulesh.micromath.formula.views.TermField.ErrorNotification;
import com.duy.natural.calc.calculator.evaluator.result.CalculatedResult;
import com.mkulesh.micromath.math.CalculatedValue;
import com.mkulesh.micromath.math.EquationArrayResult;
import com.mkulesh.micromath.utils.ViewUtils;
import com.mkulesh.micromath.widgets.FormulaEditText;
import com.mkulesh.micromath.widgets.FormulaTextView;
import com.nstudio.calc.casio.R;

import java.util.ArrayList;

public class EquationView extends CalculationResultView implements IArgumentHolder, ICalculable {
    private TermField mLeftTerm = null;
    private TermField mRightTerm = null;

    private CalculatedValue[] argumentValues = null;
    private EquationConstantResult constantResult = null;
    private EquationArrayResult arrayResult = null;
    @Nullable
    private Exception mCalculateError = null;
    @Nullable
    private CalculatedResult mResult = null;

    public EquationView(FormulaList formulaList, int id) {
        super(formulaList, null, 0);
        setId(id);
        onCreate();
    }


    public EquationView(Context context) {
        super(null, null, 0);
    }

    public EquationView(Context context, AttributeSet attrs) {
        super(null, null, 0);
    }

    @Override
    public String toString() {
        String n = getName();
        if (n == null) {
            n = "<EMPTY>";
        }
        if (getArguments() != null) {
            n += getArguments().toString();
        }
        if (isInterval()) {
            n += ": interval";
        }
        return "Formula " + getBaseType().toString() + "(Id: " + getId() + ", Name: " + n + ")";
    }

    @Override
    public void onCalculateResult(CalculatedResult result) {
        mResult = result;
        mCalculateError = null;
    }

    @Override
    public void onCalculateError(Exception e) {
        mCalculateError = e;
        mResult = null;
    }


    @Override
    public BaseType getBaseType() {
        return BaseType.EQUATION;
    }

    @Override
    public boolean isContentValid(ValidationPassType type) {
        boolean isValid = super.isContentValid(type);
        constantResult = null;
        arrayResult = null;

        switch (type) {
            case VALIDATE_SINGLE_FORMULA:
                break;
            case VALIDATE_LINKS:
                // additional checks for recursive links
                if (isValid && !mLeftTerm.isEmpty()) {
                    String errorMsg = null;
                    final ArrayList<EquationView> allFunctions = getAllFunctions();
                    if (allFunctions.contains(this)) {
                        isValid = false;
                        errorMsg = getContext().getResources().getString(R.string.error_recursive_call);
                    }
                    mLeftTerm.setError(errorMsg, ErrorNotification.LAYOUT_BORDER, null);
                }
                if (!isValid || isInterval()) {
                    break;
                }
                // check that the equation result can be cached
                if (isConstantResult()) {
                    constantResult = new EquationConstantResult();
                    break;
                }
                // check that the equation can be calculated as an array
                if (isArray()) {
                    final String errorMsg = checkArrayResult();
                    if (errorMsg == null) {
                        arrayResult = new EquationArrayResult(this, mRightTerm);
                    } else {
                        mLeftTerm.setError(errorMsg, ErrorNotification.LAYOUT_BORDER, null);
                    }
                }
                break;
        }
        return isValid;
    }

    private boolean isConstantResult() {
        final ArrayList<EquationView> linkedIntervals = getAllIntervals();
        final ArrayList<String> arguments = getArguments();
        return linkedIntervals.isEmpty() && (arguments == null || arguments.isEmpty());
    }

    private String checkArrayResult() {
        final Resources res = getContext().getResources();

        final ArrayList<String> arguments = getArguments();
        if (arguments.size() > EquationArrayResult.MAX_DIMENSION) {
            // error: invalid array dimension
            return String.format(res.getString(R.string.error_invalid_array_dimension), Integer.toString(200));
        }

        // Linked intervals are not allowed since all indexed variables in the right part
        // will be defined by term parser as arguments but not as a linked variables
        final ArrayList<EquationView> linkedIntervals = getAllIntervals();
        for (EquationView e : linkedIntervals) {
            if (!arguments.contains(e.getName())) {
                // error: interval is not defined as index
                return String.format(res.getString(R.string.error_invalid_array_interval), e.getName());
            }
        }

        // check that all arguments are valid intervals
        for (String s : arguments) {
            FormulaView f = getFormulaList().getFormula(s, 0, getId(), true);
            if (f == null || !(f instanceof EquationView) || !((EquationView) f).isInterval()) {
                // error: index not an interval
                return String.format(res.getString(R.string.error_invalid_array_index), s);
            }
        }
        return null;
    }


    @Override
    public ArrayList<String> getArguments() {
        return mLeftTerm.getParser().getFunctionArgs();
    }

    @Override
    public int getArgumentIndex(String text) {
        if (text != null && getArguments() != null) {
            return getArguments().indexOf(text);
        }
        return ViewUtils.INVALID_INDEX;
    }

    @Override
    public CalculatedValue getArgumentValue(int idx) {
        if (argumentValues != null && idx < argumentValues.length && argumentValues[idx] != null) {
            return argumentValues[idx];
        }
        return CalculatedValue.NaN;
    }


    @Override
    public CalculatedValue.ValueType getValue(CalculateTask thread, CalculatedValue outValue) throws CancelException {
        if (constantResult != null && argumentValues == null) {
            return outValue.assign(constantResult.getValue(thread));
        } else if (arrayResult != null && argumentValues != null) {
            return outValue.assign(arrayResult.getValue(argumentValues));
        }
        return mRightTerm.getValue(thread, outValue);
    }

    @NonNull
    @Override
    public String toExpressionString() {
        String left = mLeftTerm.toExpressionString();
        String right = mRightTerm.toExpressionString();
        return left + ":=" + right;
    }


    @Override
    public void invalidateResult() {
        arrayResult = null;
    }

    @Override
    public void calculate(CalculateTask thread) throws CancelException {
        if (arrayResult == null) {
            return;
        }
        arrayResult.calculate(thread, getArguments());
    }

    @Override
    public void showResult() {
        // empty
    }

    @Override
    public boolean enableDetails() {
        return arrayResult != null && arrayResult.getDimNumber() == 1 && arrayResult.getRawValues() != null;
    }

    @Override
    public void onDetails(View owner) {
        if (enableDetails()) {
            DialogResultDetails d = new DialogResultDetails(getFormulaList().getActivity(),
                    arrayResult,
                    getFormulaList().getDocumentSettings());
            d.show();
        }
    }

    /**
     * Procedure creates the formula layout
     */
    private void onCreate() {
        inflateRootLayout(R.layout.formula_equation, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        // create name term
        {
            FormulaEditText v = layout.findViewById(R.id.formula_equation_name);
            mLeftTerm = addTerm(this, layout, v, this, false);
        }
        // create assign character
        {
            FormulaTextView v = layout.findViewById(R.id.formula_equation_assign);
            v.prepare(FormulaTextView.SymbolType.TEXT, getFormulaList().getActivity(), this);
        }
        // create value term
        {
            FormulaEditText v = layout.findViewById(R.id.formula_equation_value);
            mRightTerm = addTerm(this, layout, v, this, false);
            mRightTerm.bracketsType = TermField.BracketsType.NEVER;
        }
    }


    /**
     * Procedure returns the parsed name of this formula
     */
    public String getName() {
        return mLeftTerm.getParser().getFunctionName();
    }

    /**
     * Procedure sets the list of argument values
     */
    public boolean setArgumentValues(CalculatedValue[] argumentValues) {
        this.argumentValues = argumentValues;
        return this.argumentValues != null;
    }

    /**
     * Procedure checks whether this root formula represents an interval
     */
    public boolean isInterval() {
        FormulaTermView t = mRightTerm.getTerm();
        return (t != null && t instanceof FormulaTermIntervalView);
    }

    /**
     * Procedure checks whether this root formula represents an array
     */
    public boolean isArray() {
        return mLeftTerm.getParser().isArray();
    }

    /**
     * Procedure returns declared interval if this root formula represents an interval
     */
    public ArrayList<Double> getInterval(CalculateTask thread) throws CancelException {
        FormulaTermView t = mRightTerm.getTerm();
        if (t != null && t instanceof FormulaTermIntervalView) {
            return ((FormulaTermIntervalView) t).getInterval(thread);
        }
        return null;
    }

    /**
     * Procedure fills the given value array and array with minimum and maximum values from this interval
     */
    public double[] fillBoundedInterval(CalculateTask thread, double[] targetValues, double[] minMaxValues)
            throws CancelException {
        if (!isInterval() || minMaxValues == null || minMaxValues.length != 2) {
            return null;
        }
        final ArrayList<Double> arr = getInterval(thread);
        if (arr == null || arr.isEmpty()) {
            return null;
        }
        ArrayList<Double> newArr = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            final double v = arr.get(i);
            if (minMaxValues[1] != Double.POSITIVE_INFINITY && v > minMaxValues[1]) {
                break;
            }
            if ((minMaxValues[0] != Double.NEGATIVE_INFINITY && v >= minMaxValues[0])
                    || minMaxValues[0] == Double.NEGATIVE_INFINITY) {
                newArr.add(v);
            }
        }
        double[] retValues = (targetValues != null && targetValues.length == newArr.size()) ? targetValues
                : new double[newArr.size()];
        minMaxValues[0] = minMaxValues[1] = Double.NaN;
        for (int i = 0; i < retValues.length; i++) {
            final double v = newArr.get(i);
            retValues[i] = v;
            if (i == 0) {
                minMaxValues[0] = minMaxValues[1] = v;
            } else {
                minMaxValues[0] = Math.min(minMaxValues[0], v);
                minMaxValues[1] = Math.max(minMaxValues[1], v);
            }
        }
        return retValues;
    }

    /**
     * Checks that the given equation has given properties
     */
    public boolean isEqual(String name, int argNumber, int rootId, boolean excludeRoot) {
        if (getName() == null) {
            return false;
        }
        if ((excludeRoot && getId() == rootId) || !getName().equals(name)) {
            return false;
        }
        // argument number does not matter
        if (argNumber == ViewUtils.INVALID_INDEX) {
            return true;
        }
        // check argument number
        if (getArguments() != null && getArguments().size() == argNumber) {
            // normal function with arguments
            return true;
        } else if (getArguments() == null && argNumber == 0) {
            // a constant
            return true;
        } else if (isInterval() && argNumber == 1) {
            // an interval
            return true;
        }
        return false;
    }


    private class EquationConstantResult {
        private CalculatedValue value = null;

        public CalculatedValue getValue(CalculateTask thread) throws CancelException {
            if (value == null) {
                value = new CalculatedValue();
                mRightTerm.getValue(thread, value);
            }
            return value;
        }
    }
}
