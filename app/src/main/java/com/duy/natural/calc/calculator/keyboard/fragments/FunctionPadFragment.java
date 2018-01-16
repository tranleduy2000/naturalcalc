package com.duy.natural.calc.calculator.keyboard.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duy.natural.calc.calculator.keyboard.OnCalcButtonClickListener;
import com.nstudio.calc.casio.R;

/**
 * Created by Duy on 1/14/2018.
 */

public class FunctionPadFragment extends BaseKeyboardFragment {

    public static FunctionPadFragment newInstance(OnCalcButtonClickListener listener) {
        FunctionPadFragment fragment = new FunctionPadFragment();
        fragment.setListener(listener);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pad_function, container, false);
    }


}
