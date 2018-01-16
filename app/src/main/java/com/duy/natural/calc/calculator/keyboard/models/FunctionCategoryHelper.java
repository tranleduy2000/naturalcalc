package com.duy.natural.calc.calculator.keyboard.models;

import com.mkulesh.micromath.formula.type.FunctionType;
import com.nstudio.calc.casio.R;

import java.util.ArrayList;

/**
 * Created by Duy on 1/16/2018.
 */

public class FunctionCategoryHelper {
    public static ArrayList<FunctionCategory> getAllCategories() {
        ArrayList<FunctionCategory> functionCategories = new ArrayList<>();
        addBasicMath(functionCategories);
        addNumberTheoretic(functionCategories);
        addLinearAlgebra(functionCategories);
        addLogic(functionCategories);
        addCombinatorial(functionCategories);
        return functionCategories;
    }

    private static void addBasicMath(ArrayList<FunctionCategory> functionCategories) {
        FunctionCategory category = new FunctionCategory(R.string.fun_category_title_basic);
        category.add(FunctionType.Abs);
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
        category.add(FunctionType.Conjugate);
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
        category.add(FunctionType.Log2);
        category.add(FunctionType.Log10);
        category.add(FunctionType.Mod);
        category.add(FunctionType.Pi);
        category.add(FunctionType.Quotient);
        category.add(FunctionType.Re);
        category.add(FunctionType.Round);
        category.add(FunctionType.Sec);
        category.add(FunctionType.Sech);
        category.add(FunctionType.Sin);
        category.add(FunctionType.Sinh);
        category.add(FunctionType.Sign);
        category.add(FunctionType.Solve);
        category.add(FunctionType.Sqrt);
        category.add(FunctionType.Sum);
        category.add(FunctionType.Surd);
        category.add(FunctionType.Tan);
        category.add(FunctionType.Tanh);

        functionCategories.add(category);
    }

    private static void addLinearAlgebra(ArrayList<FunctionCategory> functionCategories) {
        FunctionCategory category = new FunctionCategory(R.string.fun_category_title_linear_algebra);
        category.add(FunctionType.ArrayDepth);
        category.add(FunctionType.ArrayQ);
        category.add(FunctionType.BrayCurtisDistance);
        category.add(FunctionType.CanberraDistance);
        category.add(FunctionType.CharacteristicPolynomial);
        category.add(FunctionType.ChessboardDistance);
        category.add(FunctionType.ConjugateTranspose);
        category.add(FunctionType.CosineDistance);
        category.add(FunctionType.Cross);
        category.add(FunctionType.DesignMatrix);
        category.add(FunctionType.Det);
        category.add(FunctionType.DiagonalMatrix);
        category.add(FunctionType.Dimensions);
        category.add(FunctionType.Dot);
        category.add(FunctionType.Eigenvalues);
        category.add(FunctionType.Eigenvectors);
        category.add(FunctionType.EuclideanDistance);
        category.add(FunctionType.FrobeniusSolve);
        category.add(FunctionType.HilbertMatrix);
        category.add(FunctionType.IdentityMatrix);
        category.add(FunctionType.Inner);
        category.add(FunctionType.Inverse);
        category.add(FunctionType.JacobiMatrix);
        category.add(FunctionType.LinearProgramming);
        category.add(FunctionType.LinearSolve);
        category.add(FunctionType.LUDecomposition);
        category.add(FunctionType.MatrixPower);
        category.add(FunctionType.MatrixQ);
        category.add(FunctionType.MatrixRank);
        category.add(FunctionType.Norm);
        category.add(FunctionType.Normalize);
        category.add(FunctionType.NullSpace);
        category.add(FunctionType.Outer);
        category.add(FunctionType.PseudoInverse);
        category.add(FunctionType.QRDecomposition);
        category.add(FunctionType.RowReduce);
        category.add(FunctionType.SingularValueDecomposition);
        category.add(FunctionType.SquaredEuclideanDistance);
        category.add(FunctionType.Transpose);
        category.add(FunctionType.VandermondeMatrix);
        category.add(FunctionType.VectorAngle);
        category.add(FunctionType.VectorQ);
        functionCategories.add(category);
    }

    private static void addCombinatorial(ArrayList<FunctionCategory> functionCategories) {
        FunctionCategory category = new FunctionCategory(R.string.fun_category_title_combinatorial);
        category.add(FunctionType.BernoulliB);
        category.add(FunctionType.Binomial);
        category.add(FunctionType.CartesianProduct);
        category.add(FunctionType.CatalanNumber);
        category.add(FunctionType.DiceDissimilarity);
        category.add(FunctionType.Factorial);
        category.add(FunctionType.Factorial2);
        category.add(FunctionType.Fibonacci);
        category.add(FunctionType.IntegerPartitions);
        category.add(FunctionType.Intersection);
        category.add(FunctionType.JaccardDissimilarity);
        category.add(FunctionType.MatchingDissimilarity);
        category.add(FunctionType.Multinomial);
        category.add(FunctionType.Partition);
        category.add(FunctionType.Permutations);
        category.add(FunctionType.RogersTanimotoDissimilarity);
        category.add(FunctionType.StirlingS1);
        category.add(FunctionType.StirlingS2);
        category.add(FunctionType.Subsets);
        category.add(FunctionType.RussellRaoDissimilarity);
        category.add(FunctionType.SokalSneathDissimilarity);
        category.add(FunctionType.Tuples);
        category.add(FunctionType.Union);
        category.add(FunctionType.YuleDissimilarity);
        functionCategories.add(category);
    }

    private static void addLogic(ArrayList<FunctionCategory> functionCategories) {
        FunctionCategory functions = new FunctionCategory(R.string.fun_category_title_logic);
        functions.add(FunctionType.AllTrue);
        functions.add(FunctionType.AnyTrue);
        functions.add(FunctionType.And);
        functions.add(FunctionType.Boole);
        functions.add(FunctionType.BooleanMinimize);
        functions.add(FunctionType.BooleanQ);
        functions.add(FunctionType.Booleans);
        functions.add(FunctionType.Equivalent);
        functions.add(FunctionType.False);
        functions.add(FunctionType.Implies);
        functions.add(FunctionType.NoneTrue);
        functions.add(FunctionType.Not);
        functions.add(FunctionType.Or);
        functions.add(FunctionType.SatisfiableQ);
        functions.add(FunctionType.TautologyQ);
        functions.add(FunctionType.True);
        functions.add(FunctionType.TrueQ);
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
        functions.add(FunctionType.PrimePowerQ);
        functions.add(FunctionType.PrimeQ);
        functions.add(FunctionType.Quotient);

        functionCategories.add(functions);
    }
}
