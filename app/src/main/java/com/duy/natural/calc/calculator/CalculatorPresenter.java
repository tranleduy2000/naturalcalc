package com.duy.natural.calc.calculator;

/**
 * Created by Duy on 1/13/2018.
 */

public class CalculatorPresenter implements CalculatorContract.IPresenter {
    private final CalculatorContract.IDisplayView mDisplayView;
    private final CalculatorContract.IKeyboardView mKeyboardView;

    public CalculatorPresenter(CalculatorContract.IDisplayView displayView, CalculatorContract.IKeyboardView keyboardView) {
        mDisplayView = displayView;
        mKeyboardView = keyboardView;
        displayView.setPresenter(this);
        keyboardView.setPresenter(this);
    }

    @Override
    public CalculatorContract.IKeyboardView getKeyboardView() {
        return mKeyboardView;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public CalculatorContract.IDisplayView getDisplayView() {
        return mDisplayView;
    }
}
