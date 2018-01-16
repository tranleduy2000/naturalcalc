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
import com.duy.natural.calc.calculator.evaluator.result.CalculatedResult;
import com.mkulesh.micromath.editstate.FormulaState;
import com.mkulesh.micromath.formula.FormulaList;

public abstract class CalculationResultView extends LinkHolderView {

    public CalculationResultView(FormulaList formulaList, LinearLayout layout, int termDepth) {
        super(formulaList, layout, termDepth);
    }


    public CalculationResultView(Context context) {
        super(null, null, 0);
    }

    public CalculationResultView(Context context, AttributeSet attrs) {
        super(null, null, 0);
    }


    /**
     * Procedure performs invalidation for this object
     */
    public abstract void invalidateResult();

    /**
     * Procedure performs calculation for this object.
     * <p>
     * This method is called in a separate thread and shall not update any UI elements
     */
    public abstract void calculate(CalculateTask thread) throws CancelException;

    /**
     * @see ICalculable#toExpressionString()
     */
    @NonNull
    public abstract String toExpressionString();

    /**
     * Procedure shows calculation result for this object.
     * <p>
     * This method is called from UI thread
     */
    public abstract void showResult();

    /**
     * Procedure returns true if the calculation and content checking shall be skipped for this formula
     */
    public boolean disableCalculation() {
        return false;
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + getBaseType().toString() + "(Id: " + getId() + ")";
    }


    @Override
    public void undo(FormulaState state) {
        super.undo(state);
        invalidateResult();
    }

    public abstract void onCalculateResult(CalculatedResult result);

    public abstract void onCalculateError(Exception e);
}
