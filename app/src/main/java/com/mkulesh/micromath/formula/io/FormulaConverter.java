package com.mkulesh.micromath.formula.io;

import com.mkulesh.micromath.formula.views.TermField;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.interfaces.IExpr;

import java.io.StringWriter;

/**
 * Created by Duy on 1/14/2018.
 */

public class FormulaConverter {


    public static TermField convertExpressionToView(IExpr expr) {
        TeXUtilities teXUtilities  = new TeXUtilities(EvalEngine.get(), true);
        StringWriter writer = new StringWriter();
        teXUtilities.toTeX(expr, writer);
        return null;
    }
}
