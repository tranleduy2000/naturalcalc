package com.duy.natural.calc.calculator.evaluator.result;

import org.matheclipse.core.interfaces.IExpr;

import java.io.Serializable;

/**
 * Created by Duy on 1/15/2018.
 */

public final class CalculatedResult implements Serializable, Cloneable {
    private final IExpr input;
    private final IExpr fraction, numeric;

    public CalculatedResult(IExpr input, IExpr fraction, IExpr numeric) {
        this.input = input;
        this.fraction = fraction;
        this.numeric = numeric;
    }

    public IExpr getFraction() {
        return fraction;
    }

    public IExpr getInput() {
        return input;
    }

    public IExpr getNumeric() {
        return numeric;
    }

    public String numericToString() {
        return numeric.toString().replaceAll("[\r\n\\\\]", "");
    }

    public String fractionToString() {
        return fraction.toString().replaceAll("[\r\n\\\\]", "");
    }
}
