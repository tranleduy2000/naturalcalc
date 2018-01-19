package com.duy.natural.calc.calculator.keyboard.models;

import com.mkulesh.micromath.formula.type.FunctionType;
import com.nstudio.calc.casio.R;

import java.util.ArrayList;

/**
 * Created by Duy on 1/16/2018.
 */

public class FunctionCategoryHelper {
    public static ArrayList<FunctionCategory> getAllCategories() {
        ArrayList<FunctionCategory> categories = new ArrayList<>();

        addUseFullFunction(categories);
        addBasicMath(categories);
        addNumberTheoretic(categories);
        addLinearAlgebra(categories);
        addLogic(categories);
        addCombinatorial(categories);
        addOther(categories);
        return categories;
    }

    private static void addUseFullFunction(ArrayList<FunctionCategory> categories) {
        FunctionCategory category = new FunctionCategory(R.string.fun_category_title_common);
        category.add(FunctionType.ExpandAll);
        category.add(FunctionType.Factor);
        category.add(FunctionType.Limit);
        category.add(FunctionType.PowerExpand);
        category.add(FunctionType.Simplify);
        category.add(FunctionType.Solve);
        categories.add(category);
    }

    private static void addOther(ArrayList<FunctionCategory> functionCategories) {
        FunctionCategory category = new FunctionCategory(R.string.fun_category_title_other);
        category.add(FunctionType.AngleVector);
        category.add(FunctionType.AntihermitianMatrixQ);
        category.add(FunctionType.AntisymmetricMatrixQ);
        category.add(FunctionType.BellB);
        category.add(FunctionType.Beta);
        category.add(FunctionType.Cancel);
        category.add(FunctionType.CarmichaelLambda);
        category.add(FunctionType.Catalan);
        category.add(FunctionType.CentralMoment);
        category.add(FunctionType.ChebyshevT);
        category.add(FunctionType.ChebyshevU);
        category.add(FunctionType.ChineseRemainder);
        category.add(FunctionType.CholeskyDecomposition);
        category.add(FunctionType.Chop);
        category.add(FunctionType.Coefficient);
        category.add(FunctionType.CoefficientList);
        category.add(FunctionType.ComplexExpand);
        category.add(FunctionType.CompoundExpression);
        category.add(FunctionType.ContinuedFraction);
        category.add(FunctionType.Correlation);
        category.add(FunctionType.Covariance);
//        category.add(FunctionType.CubeRoot);
        category.add(FunctionType.Curl);
//        category.add(FunctionType.D);
        category.add(FunctionType.Denominator);
        category.add(FunctionType.Diagonal);
        category.add(FunctionType.Diff);
        category.add(FunctionType.DiracDelta);
        category.add(FunctionType.DirectedInfinity);
        category.add(FunctionType.DiscreteDelta);
        category.add(FunctionType.Discriminant);
        category.add(FunctionType.Distribute);
        category.add(FunctionType.Divergence);
        category.add(FunctionType.Divisible);
        category.add(FunctionType.DivisorSigma);
        category.add(FunctionType.DSolve);
        category.add(FunctionType.ElementData);
        category.add(FunctionType.Eliminate);
        category.add(FunctionType.Equal);
        category.add(FunctionType.Erf);
        category.add(FunctionType.Erfc);
        category.add(FunctionType.EulerE);
        category.add(FunctionType.EulerPhi);
        category.add(FunctionType.Expand);
        category.add(FunctionType.Exponent);
        category.add(FunctionType.ExtendedGCD);
        category.add(FunctionType.Extract);
        category.add(FunctionType.FactorSquareFree);
        category.add(FunctionType.FactorSquareFreeList);
        category.add(FunctionType.FactorTerms);
        category.add(FunctionType.FindInstance);
        category.add(FunctionType.FindRoot);
        category.add(FunctionType.Fit);
        category.add(FunctionType.FixedPoint);
        category.add(FunctionType.FixedPointList);
        category.add(FunctionType.Floor);
        category.add(FunctionType.FourierMatrix);
        category.add(FunctionType.FractionalPart);
        category.add(FunctionType.FromContinuedFraction);
        category.add(FunctionType.FromPolarCoordinates);
        category.add(FunctionType.Gamma);
        category.add(FunctionType.Glaisher);
        category.add(FunctionType.GoldenRatio);
        category.add(FunctionType.Greater);
        category.add(FunctionType.GreaterEqual);
        category.add(FunctionType.GroebnerBasis);
        category.add(FunctionType.HarmonicNumber);
        category.add(FunctionType.Haversine);
        category.add(FunctionType.HermiteH);
        category.add(FunctionType.HermitianMatrixQ);
        category.add(FunctionType.HornerForm);
        category.add(FunctionType.Indeterminate);
        category.add(FunctionType.IntegerPart);
//        category.add(FunctionType.Integrate);
        category.add(FunctionType.InterpolatingPolynomial);
        category.add(FunctionType.InverseErf);
        category.add(FunctionType.InverseErfc);
        category.add(FunctionType.InverseFunction);
        category.add(FunctionType.InverseHaversine);
        category.add(FunctionType.InverseLaplaceTransform);
        category.add(FunctionType.Khinchin);
        category.add(FunctionType.Kurtosis);
        category.add(FunctionType.LaguerreL);
        category.add(FunctionType.LaplaceTransform);
        category.add(FunctionType.LeastSquares);
        category.add(FunctionType.LegendreP);
        category.add(FunctionType.LegendreQ);
        category.add(FunctionType.Less);
        category.add(FunctionType.LessEqual);
        category.add(FunctionType.LogisticSigmoid);
        category.add(FunctionType.LowerTriangularize);
        category.add(FunctionType.ManhattanDistance);
        category.add(FunctionType.MathMLForm);
        category.add(FunctionType.MatrixMinimalPolynomial);
        category.add(FunctionType.Max);
        category.add(FunctionType.Mean);
        category.add(FunctionType.Median);
        category.add(FunctionType.Min);
        category.add(FunctionType.Module);
        category.add(FunctionType.MonomialList);
        category.add(FunctionType.Negative);
        category.add(FunctionType.NIntegrate);
        category.add(FunctionType.NMaximize);
        category.add(FunctionType.NMinimize);
        category.add(FunctionType.NRoots);
        category.add(FunctionType.Piecewise);
        category.add(FunctionType.Pochhammer);
        category.add(FunctionType.PolynomialExtendedGCD);
        category.add(FunctionType.PolynomialGCD);
        category.add(FunctionType.PolynomialLCM);
        category.add(FunctionType.PolynomialQuotient);
        category.add(FunctionType.PolynomialQuotientRemainder);
        category.add(FunctionType.PolynomialRemainder);

        category.add(FunctionType.PrimeOmega);
        category.add(FunctionType.PrimitiveRootList);
//        category.add(FunctionType.Product);
        category.add(FunctionType.ProductLog);
        category.add(FunctionType.Projection);
        category.add(FunctionType.Quantile);
        category.add(FunctionType.RandomInteger);
        category.add(FunctionType.RandomReal);
        category.add(FunctionType.Rationalize);
        category.add(FunctionType.Refine);
        category.add(FunctionType.Resultant);
        category.add(FunctionType.Reverse);
        category.add(FunctionType.Roots);
        category.add(FunctionType.Skewness);
        category.add(FunctionType.Sow);

        category.add(FunctionType.StandardDeviation);
        category.add(FunctionType.StruveH);
        category.add(FunctionType.StruveL);
        category.add(FunctionType.Subfactorial);
        category.add(FunctionType.Table);
        category.add(FunctionType.TeXForm);
        category.add(FunctionType.ToeplitzMatrix);
        category.add(FunctionType.Together);
        category.add(FunctionType.ToPolarCoordinates);
        category.add(FunctionType.Tr);
        category.add(FunctionType.TrigExpand);
        category.add(FunctionType.TrigReduce);
        category.add(FunctionType.TrigToExp);
        category.add(FunctionType.Unequal);
        category.add(FunctionType.UnitStep);
        category.add(FunctionType.UnitVector);
        category.add(FunctionType.Variance);
        category.add(FunctionType.Zeta);

        functionCategories.add(category);
    }

    private static void addBasicMath(ArrayList<FunctionCategory> functionCategories) {
        FunctionCategory category = new FunctionCategory(R.string.fun_category_title_basic);
        category.add(FunctionType.AbsArg);
        category.add(FunctionType.ArcCos);
        category.add(FunctionType.ArcCosh);
        category.add(FunctionType.ArcCot);
        category.add(FunctionType.ArcCoth);
        category.add(FunctionType.ArcCsc);
        category.add(FunctionType.ArcCsch);
        category.add(FunctionType.ArcSec);
        category.add(FunctionType.ArcSech);
        category.add(FunctionType.ArcSin);
        category.add(FunctionType.ArcSinh);
        category.add(FunctionType.ArcTan);
        category.add(FunctionType.ArcTanh);
        category.add(FunctionType.Arg);
        category.add(FunctionType.Ceiling);
//        category.add(FunctionType.Conjugate);
        category.add(FunctionType.Cos);
        category.add(FunctionType.Cosh);
        category.add(FunctionType.Cot);
        category.add(FunctionType.Coth);
        category.add(FunctionType.Csc);
        category.add(FunctionType.Csch);
        category.add(FunctionType.Exp);
        category.add(FunctionType.GCD);
        category.add(FunctionType.Im);
        category.add(FunctionType.LCM);
        category.add(FunctionType.Ln);
        category.add(FunctionType.Log);
//        category.add(FunctionType.Log2);
        category.add(FunctionType.Log10);
        category.add(FunctionType.Mod);
        category.add(FunctionType.Quotient);
        category.add(FunctionType.Re);
        category.add(FunctionType.Round);
        category.add(FunctionType.Sec);
        category.add(FunctionType.Sech);
        category.add(FunctionType.Sin);
        category.add(FunctionType.Sinh);
        category.add(FunctionType.Sign);
        category.add(FunctionType.Tan);
        category.add(FunctionType.Tanh);
        functionCategories.add(category);
    }

    private static void addLinearAlgebra(ArrayList<FunctionCategory> functionCategories) {
        FunctionCategory category = new FunctionCategory(R.string.fun_category_title_linear_algebra);
        category.add(FunctionType.BrayCurtisDistance);
        category.add(FunctionType.CanberraDistance);
        category.add(FunctionType.CharacteristicPolynomial);
        category.add(FunctionType.ChessboardDistance);
        category.add(FunctionType.ConjugateTranspose);
        category.add(FunctionType.CosineDistance);
        category.add(FunctionType.Cross);
        category.add(FunctionType.DesignMatrix);
        category.add(FunctionType.Det);
        category.add(FunctionType.Dot);
        category.add(FunctionType.Eigenvalues);
        category.add(FunctionType.Eigenvectors);
        category.add(FunctionType.EuclideanDistance);
        category.add(FunctionType.Inverse);
        category.add(FunctionType.LinearSolve);
        category.add(FunctionType.LUDecomposition);
        category.add(FunctionType.MatrixPower);
        category.add(FunctionType.MatrixRank);
        category.add(FunctionType.Norm);
        category.add(FunctionType.Normalize);
        category.add(FunctionType.NullSpace);
//        category.add(FunctionType.Outer);
        category.add(FunctionType.PseudoInverse);
        category.add(FunctionType.QRDecomposition);
        category.add(FunctionType.RowReduce);
        category.add(FunctionType.SingularValueDecomposition);
        category.add(FunctionType.SquaredEuclideanDistance);
        category.add(FunctionType.Transpose);
        category.add(FunctionType.VectorAngle);
        functionCategories.add(category);
    }

    private static void addCombinatorial(ArrayList<FunctionCategory> functionCategories) {
        FunctionCategory category = new FunctionCategory(R.string.fun_category_title_combinatorial);
        category.add(FunctionType.BernoulliB);
        category.add(FunctionType.Binomial);
        category.add(FunctionType.CatalanNumber);
        category.add(FunctionType.Factorial2);
        category.add(FunctionType.Fibonacci);
        category.add(FunctionType.IntegerPartitions);
        category.add(FunctionType.Multinomial);
        category.add(FunctionType.StirlingS1);
        category.add(FunctionType.StirlingS2);
        functionCategories.add(category);
    }

    private static void addLogic(ArrayList<FunctionCategory> functionCategories) {
        FunctionCategory functions = new FunctionCategory(R.string.fun_category_title_logic);
        functions.add(FunctionType.And);
        functions.add(FunctionType.Boole);
        functions.add(FunctionType.BooleanMinimize);
//        functions.add(FunctionType.Equivalent);
//        functions.add(FunctionType.Implies);
        functions.add(FunctionType.Not);
        functions.add(FunctionType.Or);
        functions.add(FunctionType.Xor);

        functionCategories.add(functions);
    }

    private static void addNumberTheoretic(ArrayList<FunctionCategory> functionCategories) {
        FunctionCategory functions = new FunctionCategory(R.string.fun_category_title_number_theoretic);
        functions.add(FunctionType.CoprimeQ);
        functions.add(FunctionType.Divisors);
        functions.add(FunctionType.EvenQ);
        functions.add(FunctionType.FactorInteger);
        functions.add(FunctionType.GCD);
        functions.add(FunctionType.IntegerExponent);
        functions.add(FunctionType.JacobiSymbol);
        functions.add(FunctionType.LCM);
        functions.add(FunctionType.MersennePrimeExponent);
        functions.add(FunctionType.MersennePrimeExponentQ);
        functions.add(FunctionType.Mod);
        functions.add(FunctionType.NextPrime);
        functions.add(FunctionType.OddQ);
        functions.add(FunctionType.PartitionsP);
        functions.add(FunctionType.PartitionsQ);
        functions.add(FunctionType.PerfectNumber);
        functions.add(FunctionType.PerfectNumberQ);
        functions.add(FunctionType.PowerMod);
        functions.add(FunctionType.Prime);
        functions.add(FunctionType.PrimePi);
        functions.add(FunctionType.PrimeQ);
        functions.add(FunctionType.Quotient);

        functionCategories.add(functions);
    }
}
