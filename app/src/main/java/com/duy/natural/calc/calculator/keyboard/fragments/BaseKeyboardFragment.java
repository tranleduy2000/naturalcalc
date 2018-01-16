package com.duy.natural.calc.calculator.keyboard.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import com.duy.natural.calc.calculator.CalculatorContract;
import com.duy.natural.calc.calculator.calcbutton.CalcButtonManager;
import com.duy.natural.calc.calculator.calcbutton.Category;
import com.duy.natural.calc.calculator.keyboard.OnCalcButtonClickListener;

/**
 * Created by Duy on 1/15/2018.
 */

abstract class BaseKeyboardFragment extends Fragment implements CalculatorContract.IKeyboardView {
    @Nullable
    protected CalcButtonManager mCalcButtonManager;
    @Nullable
    protected OnCalcButtonClickListener mListener;

    public void setListener(OnCalcButtonClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCalcButtonManager = new CalcButtonManager(getContext(), (ViewGroup) view, mListener);
    }

    @Override
    public void setPresenter(CalculatorContract.IPresenter presenter) {

    }

    @Override
    public void onButtonPressed(String code) {

    }

    @Override
    public void setPaletteBlockEnabled(Category category, boolean enabled) {
        if (mCalcButtonManager != null) {
            mCalcButtonManager.setPaletteBlockEnabled(category, enabled);
        }
    }

    @Override
    public void enableHiddenInput(boolean hiddenInputEnabled) {
        if (mCalcButtonManager != null) {
            mCalcButtonManager.setEnabled(hiddenInputEnabled);
        }
    }

    @Override
    public void setEnabled(boolean enable) {
        if (mCalcButtonManager != null) {
            mCalcButtonManager.setEnabled(enable);
        }
    }
}
