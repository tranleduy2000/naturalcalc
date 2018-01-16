package com.duy.natural.calc.calculator.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duy.natural.calc.calculator.evaluator.result.CalculatedResult;
import com.nstudio.calc.casio.R;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.interfaces.IExpr;

import java.io.StringWriter;

import io.github.kexanie.library.MathView;

/**
 * Created by Duy on 1/14/2018.
 */

public class LaTeXFragment extends BottomSheetDialogFragment {
    private static final String EXTRA_EXPR = "EXTRA_EXPR";

    public static LaTeXFragment newInstance(CalculatedResult expr) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_EXPR, expr);
        LaTeXFragment fragment = new LaTeXFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_latex_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CalculatedResult result = (CalculatedResult) getArguments().getSerializable(EXTRA_EXPR);
        showToMathView((MathView) view.findViewById(R.id.input_view), result.getInput());
        showToMathView((MathView) view.findViewById(R.id.result_fraction_view), result.getFraction());
        showToMathView((MathView) view.findViewById(R.id.result_decimal_view), result.getFraction());
    }

    private void showToMathView(MathView mathView, IExpr expr) {
        ExprEvaluator exprEvaluator = new ExprEvaluator();
        EvalEngine evalEngine = exprEvaluator.getEvalEngine();
        TeXUtilities teXUtilities = new TeXUtilities(evalEngine, true);
        StringWriter writer = new StringWriter();
        teXUtilities.toTeX(expr, writer);
        mathView.setText("$$" + writer.toString() + "$$");
    }
}
