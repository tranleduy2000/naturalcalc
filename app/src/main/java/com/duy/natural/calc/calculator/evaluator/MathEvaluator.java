/*
 * Copyright (c) 2017 by Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.natural.calc.calculator.evaluator;

import android.support.annotation.NonNull;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import java.util.regex.Pattern;

public class MathEvaluator {
    private static final String TAG = "MathEvaluator";
    private ExprEvaluator mExprEvaluator;

    private MathEvaluator() {
        mExprEvaluator = new ExprEvaluator();
        EvalEngine evalEngine = mExprEvaluator.getEvalEngine();
        evalEngine.setRecursionLimit(512);
        evalEngine.setIterationLimit(512);
        compileRules();
    }

    public static boolean isNumber(IExpr iExpr) {
        return (iExpr.isNumber() && !iExpr.isFraction() && !iExpr.isComplex());
    }


    public static String clean(Object string) {
        return string.toString().replaceAll(Pattern.quote("\\"), "")
                .replaceAll("\\s+", "")
                .replaceAll("[\r\n]", "");
    }

    @NonNull
    public static MathEvaluator newInstance() {
        return new MathEvaluator();
    }


    public ExprEvaluator getExprEvaluator() {
        return mExprEvaluator;
    }

    public void clearLocalVariables() {
        try {
            mExprEvaluator.clearVariables();
        } catch (Exception ignored) {
        }
    }

    private void compileRules() {

    }

    public IExpr evaluate(String expr) {
        return evaluate(mExprEvaluator.getEvalEngine().parse(expr));
    }

    public IExpr evaluate(IExpr expr) {
        IExpr result = mExprEvaluator.eval(expr);
        result = F.evalExpandAll(result);
        return result;
    }

    public void defineVariable(String x, IExpr xAst) {
        mExprEvaluator.defineVariable(x, xAst);
    }

    public IExpr evaluateNumeric(IExpr ast) {
        EvalEngine evalEngine = mExprEvaluator.getEvalEngine();
        evalEngine.setNumericMode(true);
        ast = evalEngine.evalWithoutNumericReset(ast);
        evalEngine.setNumericMode(false);
        return ast;
    }

    public IExpr evaluateNumeric(String expr) {
        return evaluateNumeric(mExprEvaluator.parse(expr));
    }
}

