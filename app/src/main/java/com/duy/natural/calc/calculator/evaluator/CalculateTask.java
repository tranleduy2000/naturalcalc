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
package com.duy.natural.calc.calculator.evaluator;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.duy.common.utils.DLog;
import com.duy.natural.calc.calculator.CalculatorContract;
import com.mkulesh.micromath.formula.FormulaList;
import com.mkulesh.micromath.formula.views.CalculationResultView;
import com.duy.natural.calc.calculator.evaluator.result.CalculatedResult;
import com.mkulesh.micromath.properties.DocumentProperties;
import com.nstudio.calc.casio.R;

import org.matheclipse.core.interfaces.IExpr;

import java.util.ArrayList;

public class CalculateTask extends AsyncTask<Void, CalculationResultView, Void> implements OnClickListener {
    private static final String TAG = "CalculateTask";
    private final FormulaList mFormulaList;
    private final ArrayList<CalculationResultView> mFormulas;
    private CalculatorContract.IDisplayView mDisplayView;
    private DocumentProperties mDocumentProperties;

    public CalculateTask(FormulaList list, CalculatorContract.IDisplayView displayView, ArrayList<CalculationResultView> formulas) {
        mFormulaList = list;
        mDocumentProperties = list.getDocumentSettings();
        mDisplayView = displayView;
        mFormulas = formulas;
    }

    @Override
    protected void onPreExecute() {
        mDisplayView.showProgressBar();
        mFormulaList.setInOperation(this, true, this);
    }

    @Override
    protected Void doInBackground(Void... params) {
        MathEvaluator mEvaluator = MathEvaluator.newInstance();
        for (CalculationResultView f : mFormulas) {
            if (!f.isEmpty()) {
                try {
                    final String expr = f.toExpressionString();
                    if (DLog.DEBUG) DLog.d(TAG, "expr = " + expr);
                    f.calculate(this);

                    IExpr numeric;
                    IExpr fraction = mEvaluator.evaluate(expr);
                    if (!ResultUtil.isResultFraction(fraction)) {
                        checkCancellation();
                        fraction = mEvaluator.evaluateNumeric(expr);
                        numeric = fraction;
                    } else {
                        numeric = mEvaluator.evaluateNumeric(expr);
                    }

                    if (DLog.DEBUG) DLog.d(TAG, "result = " + fraction);
                    f.onCalculateResult(new CalculatedResult(expr, fraction, numeric));
                } catch (Exception e) {
                    e.printStackTrace();
                    f.onCalculateError(e);
                }
                publishProgress(f);
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(CalculationResultView... formula) {
        CalculationResultView f = formula[0];
        if (f != null) {
            f.showResult();
        }
    }

    @Override
    protected void onCancelled() {
        mFormulaList.setInOperation(this, false, this);
        String error = mFormulaList.getActivity().getResources().getString(R.string.error_calculation_aborted);
        Toast.makeText(mFormulaList.getActivity(), error, Toast.LENGTH_LONG).show();
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Void result) {
        mDisplayView.hideProgressBar();
        mFormulaList.setInOperation(this, false, this);
    }

    @Override
    public void onClick(View v) {
        cancel(false);
    }

    public void checkCancellation() throws CancelException {
        if (isCancelled()) {
            throw new CancelException();
        }
    }

    public static final class CancelException extends Exception {
        private static final long serialVersionUID = 4916095827341L;

        public CancelException() {
            // empty
        }
    }
}
