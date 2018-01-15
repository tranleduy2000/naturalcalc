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

import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.FractionSym;
import org.matheclipse.core.expression.Symbol;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.rules.PowerRules;

/**
 * Created by Duy on 9/25/2017.
 */

public class ResultUtil {
    private static final String TAG = "ExprUtil";

    @NonNull
    public static Boolean isComplex(IExpr expr) {
        if (expr.isComplex() || expr.isComplexNumeric() || expr.isComplexInfinity()) {
            return true;
        }
        if (expr instanceof ASTRealMatrix) {
            return false;
        }
        if (expr instanceof IAST) {
            IAST expr1 = (IAST) expr;
            for (int i = 0; i < expr1.size(); i++) {
                IExpr iExpr = expr1.get(i);
                if (isComplex(iExpr)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isFractionForm(IExpr expr) {
        if (expr.isFraction()) {
            return true;
        }
        if (expr instanceof IAST) {
            IAST expr1 = (IAST) expr;
            for (int i = 0; i < expr1.size(); i++) {
                IExpr iExpr = expr1.get(i);
                if (isFractionForm(iExpr)) return true;
            }
        } else if (expr instanceof IComplex) {
            if (isFractionForm(((IComplex) expr).getRealPart())) return true;
            if (isFractionForm(((IComplex) expr).getImaginaryPart())) return true;
        }
        return false;
    }

    public static boolean containsSqrt(IExpr expr) {
        if (expr instanceof PowerRules) {
            return true;
        }
        if (expr instanceof IAST) {
            IAST expr1 = (IAST) expr;
            for (int i = 0; i < expr1.size(); i++) {
                IExpr iExpr = expr1.get(i);
                if (isFractionForm(iExpr)) return true;
            }
        } else if (expr instanceof IComplex) {
            if (isFractionForm(((IComplex) expr).getRealPart())) return true;
            if (isFractionForm(((IComplex) expr).getImaginaryPart())) return true;
        }
        return false;
    }

    /**
     * @return false if contains function, don't include sqrt function
     */
    @NonNull
    public static Boolean isResultFraction(IExpr expr) {
        if (expr instanceof Symbol) {
            return acceptFractionForm((Symbol) expr);
        } else if (expr instanceof ASTRealMatrix) {
            return false;
        } else if (expr instanceof IAST) {
            IAST expr1 = (IAST) expr;
            for (int i = 0; i < expr1.size(); i++) {
                IExpr iExpr = expr1.get(i);
                if (!isResultFraction(iExpr)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean acceptFractionForm(Symbol symbol) {
        //sqrt + - * / ^ Pi e
        return symbol.equals(F.Sqrt)
                || symbol.equals(F.Times)
                || symbol.equals(F.Plus)
                || symbol.equals(F.Negative)
                || symbol.equals(F.DivideBy)
                || symbol.equals(F.Power)
                || symbol.equals(F.Pi)
                || symbol.equals(F.E)
                || symbol.equals(F.I)
                || symbol.equals(F.DirectedInfinity)
                || symbol.equals(F.Infinity)
                || (symbol.getSymbolName().equalsIgnoreCase("List")); //support matrix
    }

    public static boolean isFraction(IExpr expr) {
        return expr instanceof FractionSym;
    }

}
