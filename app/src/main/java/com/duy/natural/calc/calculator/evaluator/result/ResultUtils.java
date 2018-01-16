package com.duy.natural.calc.calculator.evaluator.result;

import android.support.annotation.Nullable;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.interfaces.IExpr;

import java.io.StringWriter;

import io.github.kexanie.library.MathView;

/**
 * Created by Duy on 1/16/2018.
 */

public class ResultUtils {
    public static void showLaTeX(MathView mathView, @Nullable CalculatedResult result) {
        if (result == null) {
            mathView.setText("");
        } else {
            String text = toTeX(result.getFraction());
            mathView.setText("$$" + text + "$$");
        }

    }

    public static String toTeX(IExpr expr) {
        ExprEvaluator exprEvaluator = new ExprEvaluator();
        EvalEngine evalEngine = exprEvaluator.getEvalEngine();
        TeXUtilities teXUtilities = new TeXUtilities(evalEngine, true);
        StringWriter writer = new StringWriter();
        teXUtilities.toTeX(expr, writer);
        return writer.toString();
    }
}
