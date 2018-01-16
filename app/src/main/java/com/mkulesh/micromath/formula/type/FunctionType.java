package com.mkulesh.micromath.formula.type;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

import com.nstudio.calc.casio.R;

import java.util.Locale;

import static com.duy.natural.calc.calculator.calcbutton.CalcButtonManager.NO_BUTTON;

/**
 * Supported functions, the name of enum also synchronize with name of function of symja library
 * https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions
 */
public enum FunctionType implements ButtonDescriptor {
    /*Layout*/
    IDENTITY(1, R.string.math_function_identity, null, R.id.btn_parentheses),
    FUNCTION_INDEX(-1, R.string.math_function_index, "content:com.mkulesh.micromath.index"),
    POWER(2, R.string.math_function_power, null, R.id.btn_power),
    SQRT_LAYOUT(1, R.string.math_function_sqrt, null, R.id.btn_sqrt),
    SURD_LAYOUT(2, R.string.math_function_nthrt, null, R.id.btn_surd),
    FACTORIAL(1, R.string.math_function_factorial, null, R.id.btn_factorial),
    ABS_LAYOUT(1, R.string.math_function_abs, null, R.id.btn_abs),
    CONJUGATE_LAYOUT(1, R.string.math_function_conjugate, null, R.id.btn_conjugate),

    SIN(1, R.string.math_function_sin, null, R.id.btn_sin),
    ASIN(1, R.string.math_function_asin, null, R.id.btn_asin),
    SINH(1, R.string.math_function_sinh, null, R.id.btn_sinh),
    ASINH(1, R.string.math_function_asinh, null, R.id.btn_asinh),
    COS(1, R.string.math_function_cos, null, R.id.btn_cos),
    ACOS(1, R.string.math_function_acos, null, R.id.btn_acos),
    COSH(1, R.string.math_function_cosh, null, R.id.btn_cosh),
    TAN(1, R.string.math_function_tan, null, R.id.btn_tan),
    ATAN(1, R.string.math_function_atan, null, R.id.btn_atan),
    ATAN2(2, R.string.math_function_atan2, null, R.id.btn_atan2),
    TANH(1, R.string.math_function_tanh, null, R.id.btn_tanh),
    ATANH(1, R.string.math_function_atanh, null, R.id.btn_atanh),
    COT(1, R.string.math_function_cot, null, R.id.btn_cot),
    ACOT(1, R.string.math_function_acot, null, R.id.btn_acot),
    COTH(1, R.string.math_function_coth, null, R.id.btn_coth),
    ACOTH(1, R.string.math_function_acoth, null, R.id.btn_acoth),
    EXP(1, R.string.math_function_exp, null, R.id.btn_exp),
    LOG(1, R.string.math_function_log, null, R.id.btn_log),
    LOG10(1, R.string.math_function_log10, null, R.id.btn_log10),
    CEILING(1, R.string.math_function_ceil, null, R.id.btn_celi),
    FLOOR(1, R.string.math_function_floor, null, R.id.btn_floor),
    RND(1, R.string.math_function_rnd, null, R.id.btn_rnd),
    MAX(2, R.string.math_function_max, null, R.id.btn_max),
    MIN(2, R.string.math_function_min, null, R.id.btn_min),
    HYPOT(2, R.string.math_function_hypot, null),
    IF(3, R.string.math_function_if, null),
    SQRT(1, R.string.math_function_sqrt, null),
    ABS(1, R.string.math_function_abs, null),
    SIGN(1, R.string.math_function_sign, null, R.id.btn_sign),
    RE(1, R.string.math_function_re, null, R.id.btn_re),
    IM(1, R.string.math_function_im, null, R.id.btn_im),
    /*todo Supported all function symja library*/
    /*Function link*/
    FUNCTION_LINK(-1, NO_BUTTON, "content:com.mkulesh.micromath.link"),
    ARG(1, NO_BUTTON, null, NO_BUTTON),

    /*Number theoretic functions*/
    CoprimeQ(2, "functions/CoprimeQ.md"),
    Divisors(1, "functions/Divisors.md"),
    EvenQ(1, "functions/EvenQ.md"),
    FactorInteger(1, "functions/FactorInteger.md"),
    GCD(1, "functions/GCD.md"),
    IntegerExponent(1, "functions/IntegerExponent.md"),
    JacobiSymbol(1, "functions/JacobiSymbol.md"),
    LCM(1, "functions/LCM.md"),
    MersennePrimeExponent(1, "functions/MersennePrimeExponent.md"),
    MersennePrimeExponentQ(1, "functions/MersennePrimeExponentQ.md"),
    Mod(1, "functions/Mod.md"),
    NextPrime(1, "functions/NextPrime.md"),
    OddQ(1, "functions/OddQ.md"),
    PartitionsP(1, "functions/PartitionsP.md"),
    PartitionsQ(1, "functions/PartitionsQ.md"),
    PerfectNumber(1, "functions/PerfectNumber.md"),
    PerfectNumberQ(1, "functions/PerfectNumberQ.md"),
    PowerMod(1, "functions/PowerMod.md"),
    Prime(1, "functions/Prime.md"),
    PrimePi(1, "functions/PrimePi.md"),
    PrimePowerQ(1, "functions/PrimePowerQ.md"),
    PrimeQ(1, "functions/PrimeQ.md"),
    Quotient(1, "functions/Quotient.md"),

    /*Logic*/
    AllTrue(1, "functions/AllTrue.md"),
    AnyTrue(1, "functions/AnyTrue.md"),
    And(1, "functions/And.md"),
    Boole(1, "functions/Boole.md"),
    BooleanMinimize(1, "functions/BooleanMinimize.md"),
    BooleanQ(1, "functions/BooleanQ.md"),
    Booleans(1, "functions/Booleans.md"),
    Equivalent(1, "functions/Equivalent.md"),
    False(1, "functions/False.md"),
    Implies(1, "functions/Implies.md"),
    NoneTrue(1, "functions/NoneTrue.md"),
    Not(1, "functions/Not.md"),
    Or(1, "functions/Or.md"),
    SatisfiableQ(1, "functions/SatisfiableQ.md"),
    TautologyQ(1, "functions/TautologyQ.md"),
    True(1, "functions/True.md"),
    TrueQ(1, "functions/TrueQ.md"),
    Xor(1, "functions/Xor.md"),;

    private final int argNumber;
    private final int descriptionId;
    private final String linkObject;
    private final String lowerCaseName;
    private int viewId;

    FunctionType(int argNumber, String docPath) {
        this(argNumber, NO_BUTTON, null, NO_BUTTON);
    }

    FunctionType(int argNumber, int descriptionId, String linkObject) {
        this.argNumber = argNumber;
        this.descriptionId = descriptionId;
        this.linkObject = linkObject;
        this.lowerCaseName = name().toLowerCase(Locale.ENGLISH);
    }

    FunctionType(int argNumber, int descriptionId, String linkObject, @IdRes int viewId) {
        this(argNumber, descriptionId, linkObject);
        this.viewId = viewId;
    }

    public int getViewId() {
        return viewId;
    }

    public int getArgNumber() {
        return argNumber;
    }

    public int getDescriptionId() {
        return descriptionId;
    }

    public String getLinkObject() {
        return linkObject;
    }

    public boolean isLink() {
        return linkObject != null;
    }

    @NonNull
    public String getLowerCaseName() {
        return lowerCaseName;
    }
}

