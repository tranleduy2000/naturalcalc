package com.duy.natural.calc.calculator.dialogs;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.duy.natural.calc.calculator.evaluator.result.CalculatedResult;
import com.nstudio.calc.casio.R;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.TeXUtilities;

import java.io.StringWriter;

import io.github.kexanie.library.MathView;

/**
 * Created by Duy on 1/14/2018.
 */

public class LaTeXDialogFragment2 extends AppCompatDialog {
    public LaTeXDialogFragment2(Context context, CalculatedResult expr) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_latex_view, null);
        MathView mathView = view.findViewById(R.id.math_view);
        showToMathView(mathView, expr);
        setContentView(view);
    }

    private void showToMathView(MathView mathView, CalculatedResult expr) {
        ExprEvaluator exprEvaluator = new ExprEvaluator();
        EvalEngine evalEngine = exprEvaluator.getEvalEngine();
        TeXUtilities teXUtilities = new TeXUtilities(evalEngine, true);
        StringWriter writer = new StringWriter();
        teXUtilities.toTeX(expr.getFraction(), writer);
        mathView.setText("$$" + writer.toString() + "$$");
    }
}
