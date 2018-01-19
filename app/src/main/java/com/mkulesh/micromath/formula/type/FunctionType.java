package com.mkulesh.micromath.formula.type;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nstudio.calc.casio.R;

import java.util.Locale;

import static com.duy.natural.calc.calculator.calcbutton.CalcButtonManager.NO_BUTTON;

/**
 * Supported functions, the name of enum also synchronize with name of function of symja library
 * https://github.com/axkr/symja_android_library/tree/master/symja_android_library/doc/functions
 */
public enum FunctionType implements FormulaTermType {
    //Layout
    IDENTITY_LAYOUT(1, R.string.math_function_identity, null, R.id.btn_parentheses),
    POWER_LAYOUT(2, R.string.math_function_power, null, R.id.btn_power),
    SQRT_LAYOUT(1, R.string.math_function_sqrt, null, R.id.btn_sqrt),
    SURD_LAYOUT(2, R.string.math_function_nthrt, null, R.id.btn_surd),
    FACTORIAL_LAYOUT(1, R.string.math_function_factorial, null, R.id.btn_factorial),
    ABS_LAYOUT(1, R.string.math_function_abs, null, R.id.btn_abs),
    CONJUGATE_LAYOUT(1, R.string.math_function_conjugate, null, R.id.btn_conjugate),
    FUNCTION_LINK(-1, NO_BUTTON, "content:com.mkulesh.micromath.link"),
    FUNCTION_INDEX(-1, R.string.math_function_index, "content:com.mkulesh.micromath.index"),


    //Other
    AngleVector(1, "functions/AngleVector.md"),
    AntihermitianMatrixQ(1, "functions/AntihermitianMatrixQ.md"),
    AntisymmetricMatrixQ(1, "functions/AntisymmetricMatrixQ.md"),
    BellB(1, "functions/BellB.md"),
    Beta(1, "functions/Beta.md"),
    Cancel(1, "functions/Cancel.md"),
    CarmichaelLambda(1, "functions/CarmichaelLambda.md"),
    Catalan(1, "functions/Catalan.md"),
    CentralMoment(1, "functions/CentralMoment.md"),
    ChebyshevT(1, "functions/ChebyshevT.md"),
    ChebyshevU(1, "functions/ChebyshevU.md"),
    ChineseRemainder(1, "functions/ChineseRemainder.md"),
    CholeskyDecomposition(1, "functions/CholeskyDecomposition.md"),
    Chop(1, "functions/Chop.md"),
    Coefficient(1, "functions/Coefficient.md"),
    CoefficientList(1, "functions/CoefficientList.md"),
    ComplexExpand(1, "functions/ComplexExpand.md"),
    CompoundExpression(1, "functions/CompoundExpression.md"),
    ContinuedFraction(1, "functions/ContinuedFraction.md"),
    Correlation(1, "functions/Correlation.md"),
    Covariance(1, "functions/Covariance.md"),
    CubeRoot(1, "functions/CubeRoot.md"),
    Curl(1, "functions/Curl.md"),
    D(1, "functions/D.md"),
    Denominator(1, "functions/Denominator.md"),
    Diagonal(1, "functions/Diagonal.md"),
    Diff(1, "functions/Diff.md"),
    DiracDelta(1, "functions/DiracDelta.md"),
    DirectedInfinity(1, "functions/DirectedInfinity.md"),
    DiscreteDelta(1, "functions/DiscreteDelta.md"),
    Discriminant(1, "functions/Discriminant.md"),
    Distribute(1, "functions/Distribute.md"),
    Divergence(1, "functions/Divergence.md"),
    Divisible(1, "functions/Divisible.md"),
    DivisorSigma(1, "functions/DivisorSigma.md"),
    DSolve(1, "functions/DSolve.md"),
    ElementData(1, "functions/ElementData.md"),
    Eliminate(1, "functions/Eliminate.md"),
    Equal(1, "functions/Equal.md"),
    Erf(1, "functions/Erf.md"),
    Erfc(1, "functions/Erfc.md"),
    EulerE(1, "functions/EulerE.md"),
    EulerPhi(1, "functions/EulerPhi.md"),
    Expand(1, "functions/Expand.md"),
    ExpandAll(1, "functions/ExpandAll.md"),
    Exponent(1, "functions/Exponent.md"),
    ExtendedGCD(1, "functions/ExtendedGCD.md"),
    Extract(1, "functions/Extract.md"),
    Factor(1, "functions/Factor.md"),
    FactorSquareFree(1, "functions/FactorSquareFree.md"),
    FactorSquareFreeList(1, "functions/FactorSquareFreeList.md"),
    FactorTerms(1, "functions/FactorTerms.md"),
    FindInstance(1, "functions/FindInstance.md"),
    FindRoot(1, "functions/FindRoot.md"),
    Fit(1, "functions/Fit.md"),
    FixedPoint(1, "functions/FixedPoint.md"),
    FixedPointList(1, "functions/FixedPointList.md"),
    Floor(1, "functions/Floor.md"),
    FourierMatrix(1, "functions/FourierMatrix.md"),
    FractionalPart(1, "functions/FractionalPart.md"),
    FromContinuedFraction(1, "functions/FromContinuedFraction.md"),
    FromPolarCoordinates(1, "functions/FromPolarCoordinates.md"),
    Gamma(1, "functions/Gamma.md"),
    Glaisher(1, "functions/Glaisher.md"),
    GoldenRatio(1, "functions/GoldenRatio.md"),
    Greater(1, "functions/Greater.md"),
    GreaterEqual(1, "functions/GreaterEqual.md"),
    GroebnerBasis(1, "functions/GroebnerBasis.md"),
    HarmonicNumber(1, "functions/HarmonicNumber.md"),
    Haversine(1, "functions/Haversine.md"),
    HermiteH(1, "functions/HermiteH.md"),
    HermitianMatrixQ(1, "functions/HermitianMatrixQ.md"),
    HornerForm(1, "functions/HornerForm.md"),
    Indeterminate(1, "functions/Indeterminate.md"),
    IntegerPart(1, "functions/IntegerPart.md"),
    Integrate(1, "functions/Integrate.md"),
    InterpolatingPolynomial(1, "functions/InterpolatingPolynomial.md"),
    InverseErf(1, "functions/InverseErf.md"),
    InverseErfc(1, "functions/InverseErfc.md"),
    InverseFunction(1, "functions/InverseFunction.md"),
    InverseHaversine(1, "functions/InverseHaversine.md"),
    InverseLaplaceTransform(1, "functions/InverseLaplaceTransform.md"),
    Khinchin(1, "functions/Khinchin.md"),
    Kurtosis(1, "functions/Kurtosis.md"),
    LaguerreL(1, "functions/LaguerreL.md"),
    LaplaceTransform(1, "functions/LaplaceTransform.md"),
    LeastSquares(1, "functions/LeastSquares.md"),
    LegendreP(1, "functions/LegendreP.md"),
    LegendreQ(1, "functions/LegendreQ.md"),
    Less(1, "functions/Less.md"),
    LessEqual(1, "functions/LessEqual.md"),
    Limit(1, "functions/Limit.md"),
    LogisticSigmoid(1, "functions/LogisticSigmoid.md"),
    LowerTriangularize(1, "functions/LowerTriangularize.md"),
    ManhattanDistance(1, "functions/ManhattanDistance.md"),
    MathMLForm(1, "functions/MathMLForm.md"),
    MatrixMinimalPolynomial(1, "functions/MatrixMinimalPolynomial.md"),
    Max(1, "functions/Max.md"),
    Mean(1, "functions/Mean.md"),
    Median(1, "functions/Median.md"),
    Min(1, "functions/Min.md"),
    Module(1, "functions/Module.md"),
    MoebiusMu(1, "functions/MoebiusMu.md"),
    MonomialList(1, "functions/MonomialList.md"),
    Negative(1, "functions/Negative.md"),
    NIntegrate(1, "functions/NIntegrate.md"),
    NMaximize(1, "functions/NMaximize.md"),
    NMinimize(1, "functions/NMinimize.md"),
    NRoots(1, "functions/NRoots.md"),
    Piecewise(1, "functions/Piecewise.md"),
    Pochhammer(1, "functions/Pochhammer.md"),
    PolynomialExtendedGCD(1, "functions/PolynomialExtendedGCD.md"),
    PolynomialGCD(1, "functions/PolynomialGCD.md"),
    PolynomialLCM(1, "functions/PolynomialLCM.md"),
    PolynomialQuotient(1, "functions/PolynomialQuotient.md"),
    PolynomialQuotientRemainder(1, "functions/PolynomialQuotientRemainder.md"),
    PolynomialRemainder(1, "functions/PolynomialRemainder.md"),
    PowerExpand(1, "functions/PowerExpand.md"),
    PrimeOmega(1, "functions/PrimeOmega.md"),
    PrimitiveRootList(1, "functions/PrimitiveRootList.md"),
    Product(1, "functions/Product.md"),
    ProductLog(1, "functions/ProductLog.md"),
    Projection(1, "functions/Projection.md"),
    Quantile(1, "functions/Quantile.md"),
    RandomInteger(1, "functions/RandomInteger.md"),
    RandomReal(1, "functions/RandomReal.md"),
    Rationalize(1, "functions/Rationalize.md"),
    Refine(1, "functions/Refine.md"),
    Resultant(1, "functions/Resultant.md"),
    Reverse(1, "functions/Reverse.md"),
    Roots(1, "functions/Roots.md"),
    Simplify(1, "functions/Simplify.md"),
    Skewness(1, "functions/Skewness.md"),
    Sow(1, "functions/Sow.md"),
    StandardDeviation(1, "functions/StandardDeviation.md"),
    StruveH(1, "functions/StruveH.md"),
    StruveL(1, "functions/StruveL.md"),
    Subfactorial(1, "functions/Subfactorial.md"),
    Table(1, "functions/Table.md"),
    TeXForm(1, "functions/TeXForm.md"),
    ToeplitzMatrix(1, "functions/ToeplitzMatrix.md"),
    Together(1, "functions/Together.md"),
    ToPolarCoordinates(1, "functions/ToPolarCoordinates.md"),
    Tr(1, "functions/Tr.md"),
    TrigExpand(1, "functions/TrigExpand.md"),
    TrigReduce(1, "functions/TrigReduce.md"),
    TrigToExp(1, "functions/TrigToExp.md"),
    Unequal(1, "functions/Unequal.md"),
    UnitStep(1, "functions/UnitStep.md"),
    UnitVector(1, "functions/UnitVector.md"),
    UpperTriangularize(1, "functions/UpperTriangularize.md"),
    Variance(1, "functions/Variance.md"),
    Zeta(1, "functions/Zeta.md"),


    //Basic math
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
    Ceiling(1, "functions/Ceiling.md") {
        @Override
        public boolean isInfinityArg() {
            return true;
        }
    },
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
    Log(2, "functions/Log.md"),
    Log2(1, "functions/Log2.md"),
    Log10(1, "functions/Log10.md"),
    Re(1, "functions/Re.md"),
    Round(1, "functions/Round.md"),
    Sec(1, "functions/Sec.md"),
    Sech(1, "functions/Sech.md"),
    Sin(1, "functions/Sin.md"),
    Sinh(1, "functions/Sinh.md"),
    Sign(1, "functions/Sign.md"),
    Solve(3, "functions/Solve.md"),
    Sqrt(1, "functions/Sqrt.md"),
    Sum(1, "functions/Sum.md"),

    Surd(1, "functions/Surd.md"),
    Tan(1, "functions/Tan.md"),
    Tanh(1, "functions/Tanh.md"),


    //Number theoretic functions
    CoprimeQ(2, "functions/CoprimeQ.md"),
    Divisors(1, "functions/Divisors.md"),
    EvenQ(1, "functions/EvenQ.md"),
    FactorInteger(1, "functions/FactorInteger.md"),
    GCD(2, "functions/GCD.md") {
        @Override
        public boolean isInfinityArg() {
            return true;
        }
    },
    IntegerExponent(2, "functions/IntegerExponent.md"),
    JacobiSymbol(2, "functions/JacobiSymbol.md"),
    LCM(2, "functions/LCM.md") {
        @Override
        public boolean isInfinityArg() {
            return true;
        }
    },
    MersennePrimeExponent(1, "functions/MersennePrimeExponent.md"),
    MersennePrimeExponentQ(1, "functions/MersennePrimeExponentQ.md"),
    Mod(2, "functions/Mod.md"),
    NextPrime(1, "functions/NextPrime.md"),
    OddQ(1, "functions/OddQ.md"),
    PartitionsP(1, "functions/PartitionsP.md"),
    PartitionsQ(1, "functions/PartitionsQ.md"),
    PerfectNumber(1, "functions/PerfectNumber.md"),
    PerfectNumberQ(1, "functions/PerfectNumberQ.md"),
    PowerMod(3, "functions/PowerMod.md"),
    Prime(1, "functions/Prime.md"),
    PrimePi(1, "functions/PrimePi.md"),
    PrimeQ(1, "functions/PrimeQ.md"),
    Quotient(2, "functions/Quotient.md"),

    //Logic
    AllTrue(1, "functions/AllTrue.md"),
    AnyTrue(1, "functions/AnyTrue.md"),
    And(2, "functions/And.md") {
        @Override
        public boolean isInfinityArg() {
            return true;
        }
    },
    Boole(1, "functions/Boole.md"),
    BooleanMinimize(1, "functions/BooleanMinimize.md"),
    BooleanQ(1, "functions/BooleanQ.md"),
    Equivalent(2, "functions/Equivalent.md") {
        @Override
        public boolean isInfinityArg() {
            return true;
        }
    },
    False(1, "functions/False.md"),
    Implies(2, "functions/Implies.md"),
    NoneTrue(1, "functions/NoneTrue.md"),
    Not(1, "functions/Not.md"),
    Or(2, "functions/Or.md") {
        @Override
        public boolean isInfinityArg() {
            return true;
        }
    },
    True(1, "functions/True.md"),
    Xor(2, "functions/Xor.md") {
        @Override
        public boolean isInfinityArg() {
            return true;
        }
    },

    //Combinatorial
    BernoulliB(1, "functions/BernoulliB.md"),
    Binomial(2, "functions/Binomial.md"),
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
    Multinomial(2, "functions/Multinomial.md") {
        @Override
        public boolean isInfinityArg() {
            return true;
        }
    },
    Partition(1, "functions/Partition.md"),
    Permutations(1, "functions/Permutations.md"),
    RogersTanimotoDissimilarity(1, "functions/RogersTanimotoDissimilarity.md"),
    StirlingS1(2, "functions/StirlingS1.md"),
    StirlingS2(2, "functions/StirlingS2.md"),
    Subsets(1, "functions/Subsets.md"),
    RussellRaoDissimilarity(1, "functions/RussellRaoDissimilarity.md"),
    SokalSneathDissimilarity(1, "functions/SokalSneathDissimilarity.md"),
    Tuples(1, "functions/Tuples.md"),
    Union(1, "functions/Union.md"),
    YuleDissimilarity(1, "functions/YuleDissimilarity.md"),

    // Linear algebra
    BrayCurtisDistance(2, "functions/BrayCurtisDistance.md"),
    CanberraDistance(2, "functions/CanberraDistance.md"),
    CharacteristicPolynomial(2, "functions/CharacteristicPolynomial.md"),
    ChessboardDistance(2, "functions/ChessboardDistance.md"),
    ConjugateTranspose(1, "functions/ConjugateTranspose.md"),
    CosineDistance(2, "functions/CosineDistance.md"),
    Cross(2, "functions/Cross.md"),
    DesignMatrix(3, "functions/DesignMatrix.md"),
    Det(1, "functions/Det.md"),
    DiagonalMatrix(1, "functions/DiagonalMatrix.md"),
    Dimensions(1, "functions/Dimensions.md"),
    Dot(2, "functions/Dot.md"),
    Eigenvalues(1, "functions/Eigenvalues.md"),
    Eigenvectors(1, "functions/Eigenvectors.md"),
    EuclideanDistance(2, "functions/EuclideanDistance.md"),
    FrobeniusSolve(1, "functions/FrobeniusSolve.md"),
    HilbertMatrix(1, "functions/HilbertMatrix.md"),
    IdentityMatrix(1, "functions/IdentityMatrix.md"),
    Inner(1, "functions/Inner.md"),
    Inverse(1, "functions/Inverse.md"),
    JacobiMatrix(1, "functions/JacobiMatrix.md"),
    LinearProgramming(1, "functions/LinearProgramming.md"),
    LinearSolve(2, "functions/LinearSolve.md"),
    LUDecomposition(1, "functions/LUDecomposition.md"),
    MatrixPower(2, "functions/MatrixPower.md"),
    MatrixRank(1, "functions/MatrixRank.md"),
    Norm(1, "functions/Norm.md") {
        @Override
        public boolean isInfinityArg() {
            return true;
        }
    },
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
    VectorAngle(2, "functions/VectorAngle.md");


    private final int argNumber;
    private final int descriptionId;
    private final String linkObject;
    private final String lowerCaseName;
    private int viewId;
    private String docPath;

    FunctionType(int argNumber, String docPath) {
        this(argNumber, NO_BUTTON, null, NO_BUTTON);
        this.docPath = docPath;
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

    @Nullable
    public String getDocumentPath() {
        return docPath;
    }

    @NonNull
    public String getCode() {
        return lowerCaseName;
    }

    public boolean isInfinityArg() {
        return false;
    }

    @NonNull
    @Override
    public String getLowerCaseName() {
        return lowerCaseName;
    }

    @Nullable
    @Override
    public TermType getType() {
        return TermType.FUNCTION;
    }
}

