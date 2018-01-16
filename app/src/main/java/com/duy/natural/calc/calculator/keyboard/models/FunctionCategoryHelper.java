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

        addCategoryNumberTheoretic(functionCategories);
        addCategoryLogic(functionCategories);
        return functionCategories;
    }

    private static void addCategoryLogic(ArrayList<FunctionCategory> functionCategories) {
        FunctionCategory function = new FunctionCategory(R.string.fun_category_title_number_theoretic);
        function.add(FunctionType.CoprimeQ);
        function.add(FunctionType.Divisors);
        function.add(FunctionType.EvenQ);
        function.add(FunctionType.FactorInteger);
        function.add(FunctionType.GCD);
        function.add(FunctionType.IntegerExponent);
        function.add(FunctionType.JacobiSymbol);
        function.add(FunctionType.LCM);
        function.add(FunctionType.MersennePrimeExponent);
        function.add(FunctionType.MersennePrimeExponentQ);
        function.add(FunctionType.Mod);
        function.add(FunctionType.NextPrime);
        function.add(FunctionType.OddQ);
        function.add(FunctionType.PartitionsP);
        function.add(FunctionType.PartitionsQ);
        function.add(FunctionType.PerfectNumber);
        function.add(FunctionType.PerfectNumberQ);
        function.add(FunctionType.PowerMod);
        function.add(FunctionType.Prime);
        function.add(FunctionType.PrimePi);
        function.add(FunctionType.PrimePowerQ);
        function.add(FunctionType.PrimeQ);
        function.add(FunctionType.Quotient);

        functionCategories.add(function);
    }

    private static void addCategoryNumberTheoretic(ArrayList<FunctionCategory> functionCategories) {
        FunctionCategory numberTheoretic = new FunctionCategory(R.string.fun_category_title_number_theoretic);
        numberTheoretic.add(FunctionType.CoprimeQ);
        numberTheoretic.add(FunctionType.Divisors);
        numberTheoretic.add(FunctionType.EvenQ);
        numberTheoretic.add(FunctionType.FactorInteger);
        numberTheoretic.add(FunctionType.GCD);
        numberTheoretic.add(FunctionType.IntegerExponent);
        numberTheoretic.add(FunctionType.JacobiSymbol);
        numberTheoretic.add(FunctionType.LCM);
        numberTheoretic.add(FunctionType.MersennePrimeExponent);
        numberTheoretic.add(FunctionType.MersennePrimeExponentQ);
        numberTheoretic.add(FunctionType.Mod);
        numberTheoretic.add(FunctionType.NextPrime);
        numberTheoretic.add(FunctionType.OddQ);
        numberTheoretic.add(FunctionType.PartitionsP);
        numberTheoretic.add(FunctionType.PartitionsQ);
        numberTheoretic.add(FunctionType.PerfectNumber);
        numberTheoretic.add(FunctionType.PerfectNumberQ);
        numberTheoretic.add(FunctionType.PowerMod);
        numberTheoretic.add(FunctionType.Prime);
        numberTheoretic.add(FunctionType.PrimePi);
        numberTheoretic.add(FunctionType.PrimePowerQ);
        numberTheoretic.add(FunctionType.PrimeQ);
        numberTheoretic.add(FunctionType.Quotient);

        functionCategories.add(numberTheoretic);
    }
}
