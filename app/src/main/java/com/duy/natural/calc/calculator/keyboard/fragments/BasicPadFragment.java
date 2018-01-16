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

public class BasicPadFragment extends BaseKeyboardFragment {
    public static BasicPadFragment newInstance(OnCalcButtonClickListener listener) {
        BasicPadFragment fragment = new BasicPadFragment();
        fragment.setListener(listener);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pad_basic, container, false);
    }
}
