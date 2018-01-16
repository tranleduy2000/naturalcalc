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
    IDENTITY_LAYOUT(1, R.string.math_function_identity, null, R.id.btn_parentheses),
    POWER_LAYOUT(2, R.string.math_function_power, null, R.id.btn_power),
    SQRT_LAYOUT(1, R.string.math_function_sqrt, null, R.id.btn_sqrt),
    SURD_LAYOUT(2, R.string.math_function_nthrt, null, R.id.btn_surd),
    FACTORIAL_LAYOUT(1, R.string.math_function_factorial, null, R.id.btn_factorial),
    ABS_LAYOUT(1, R.string.math_function_abs, null, R.id.btn_abs),
    CONJUGATE_LAYOUT(1, R.string.math_function_conjugate, null, R.id.btn_conjugate),
    FUNCTION_LINK(-1, NO_BUTTON, "content:com.mkulesh.micromath.link"),
    FUNCTION_INDEX(-1, R.string.math_function_index, "content:com.mkulesh.micromath.index"),


    /*Basic math*/
    Abs(1, "functions/Abs.md"),
    AbsArg(1, "functions/AbsArg.md"),
    ArcCos(1, "functions/ArcCos.md"),
    ArcCosh(1, "functions/ArcCosh.md"),
    ArcCot(1, "functions/ArcCot.md"),
    ArcCoth(1, "functions/ArcCoth.md"),
    ArcCsc(1, "functions/ArcCsc.md"),
    ArcCsch(1, "functions/ArcCsch.md"),
    ArcSec(1, "functions/ArcSec.md"),
    ArcSech(1, "functions/ArcSech.md"),
    ArcSin(1, "functions/ArcSin.md"),
    ArcSinh(1, "functions/ArcSinh.md"),
    ArcTan(1, "functions/ArcTan.md"),
    ArcTanh(1, "functions/ArcTanh.md"),
    Arg(1, "functions/Arg.md"),
    Ceiling(1, "functions/Ceiling.md"),
    Conjugate(1, "functions/Conjugate.md"),
    Cos(1, "functions/Cos.md"),
    Cosh(1, "functions/Cosh.md"),
    Cot(1, "functions/Cot.md"),
    Coth(1, "functions/Coth.md"),
    Csc(1, "functions/Csc.md"),
    Csch(1, "functions/Csch.md"),
    Exp(1, "functions/Exp.md"),
    Im(1, "functions/Im.md"),
    Ln(1, "functions/Ln.md"),
    Log(1, "functions/Log.md"),
    Log2(1, "functions/Log2.md"),
    Log10(1, "functions/Log10.md"),
    Pi(1, "functions/Pi.md"),
    Re(1, "functions/Re.md"),
    Round(1, "functions/Round.md"),
    Sec(1, "functions/Sec.md"),
    Sech(1, "functions/Sech.md"),
    Sin(1, "functions/Sin.md"),
    Sinh(1, "functions/Sinh.md"),
    Sign(1, "functions/Sign.md"),
    Solve(1, "functions/Solve.md"),
    Sqrt(1, "functions/Sqrt.md"),
    Sum(1, "functions/Sum.md"),
    Surd(1, "functions/Surd.md"),
    Tan(1, "functions/Tan.md"),
    Tanh(1, "functions/Tanh.md"),


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
    Xor(1, "functions/Xor.md"),

    /*Combinatorial*/
    BernoulliB(1, "functions/BernoulliB.md"),
    Binomial(1, "functions/Binomial.md"),
    CartesianProduct(1, "functions/CartesianProduct.md"),
    CatalanNumber(1, "functions/CatalanNumber.md"),
    DiceDissimilarity(1, "functions/DiceDissimilarity.md"),
    Factorial(1, "functions/Factorial.md"),
    Factorial2(1, "functions/Factorial2.md"),
    Fibonacci(1, "functions/Fibonacci.md"),
    IntegerPartitions(1, "functions/IntegerPartitions.md"),
    Intersection(1, "functions/Intersection.md"),
    JaccardDissimilarity(1, "functions/JaccardDissimilarity.md"),
    MatchingDissimilarity(1, "functions/MatchingDissimilarity.md"),
    Multinomial(1, "functions/Multinomial.md"),
    Partition(1, "functions/Partition.md"),
    Permutations(1, "functions/Permutations.md"),
    RogersTanimotoDissimilarity(1, "functions/RogersTanimotoDissimilarity.md"),
    StirlingS1(1, "functions/StirlingS1.md"),
    StirlingS2(1, "functions/StirlingS2.md"),
    Subsets(1, "functions/Subsets.md"),
    RussellRaoDissimilarity(1, "functions/RussellRaoDissimilarity.md"),
    SokalSneathDissimilarity(1, "functions/SokalSneathDissimilarity.md"),
    Tuples(1, "functions/Tuples.md"),
    Union(1, "functions/Union.md"),
    YuleDissimilarity(1, "functions/YuleDissimilarity.md"),

    /*Linear algebra*/
    ArrayDepth(1, "functions/ArrayDepth.md"),
    ArrayQ(1, "functions/ArrayQ.md"),
    BrayCurtisDistance(1, "functions/BrayCurtisDistance.md"),
    CanberraDistance(1, "functions/CanberraDistance.md"),
    CharacteristicPolynomial(1, "functions/CharacteristicPolynomial.md"),
    ChessboardDistance(1, "functions/ChessboardDistance.md"),
    ConjugateTranspose(1, "functions/ConjugateTranspose.md"),
    CosineDistance(1, "functions/CosineDistance.md"),
    Cross(1, "functions/Cross.md"),
    DesignMatrix(1, "functions/DesignMatrix.md"),
    Det(1, "functions/Det.md"),
    DiagonalMatrix(1, "functions/DiagonalMatrix.md"),
    Dimensions(1, "functions/Dimensions.md"),
    Dot(1, "functions/Dot.md"),
    Eigenvalues(1, "functions/Eigenvalues.md"),
    Eigenvectors(1, "functions/Eigenvectors.md"),
    EuclideanDistance(1, "functions/EuclideanDistance.md"),
    FrobeniusSolve(1, "functions/FrobeniusSolve.md"),
    HilbertMatrix(1, "functions/HilbertMatrix.md"),
    IdentityMatrix(1, "functions/IdentityMatrix.md"),
    Inner(1, "functions/Inner.md"),
    Inverse(1, "functions/Inverse.md"),
    JacobiMatrix(1, "functions/JacobiMatrix.md"),
    LinearProgramming(1, "functions/LinearProgramming.md"),
    LinearSolve(1, "functions/LinearSolve.md"),
    LUDecomposition(1, "functions/LUDecomposition.md"),
    MatrixPower(1, "functions/MatrixPower.md"),
    MatrixQ(1, "functions/MatrixQ.md"),
    MatrixRank(1, "functions/MatrixRank.md"),
    Norm(1, "functions/Norm.md"),
    Normalize(1, "functions/Normalize.md"),
    NullSpace(1, "functions/NullSpace.md"),
    Outer(1, "functions/Outer.md"),
    PseudoInverse(1, "functions/PseudoInverse.md"),
    QRDecomposition(1, "functions/QRDecomposition.md"),
    RowReduce(1, "functions/RowReduce.md"),
    SingularValueDecomposition(1, "functions/SingularValueDecomposition.md"),
    SquaredEuclideanDistance(1, "functions/SquaredEuclideanDistance.md"),
    Transpose(1, "functions/Transpose.md"),
    VandermondeMatrix(1, "functions/VandermondeMatrix.md"),
    VectorAngle(1, "functions/VectorAngle.md"),
    VectorQ(1, "functions/VectorQ.md"),;


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

    public String getFunctionName() {
        return name();
    }

    @NonNull
    public String getCode() {
        return lowerCaseName;
    }
}

