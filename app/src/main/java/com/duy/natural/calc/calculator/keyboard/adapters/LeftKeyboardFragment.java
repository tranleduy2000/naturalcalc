package com.duy.natural.calc.calculator.keyboard.adapters;

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

public class LeftKeyboardFragment extends BaseKeyboardFragment {
    public static LeftKeyboardFragment newInstance(OnCalcButtonClickListener listener) {
        LeftKeyboardFragment fragment = new LeftKeyboardFragment();
        fragment.setListener(listener);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_keyboard_left, container, false);
    }

}
