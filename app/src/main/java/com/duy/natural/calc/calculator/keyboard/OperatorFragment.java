package com.duy.natural.calc.calculator.keyboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duy.natural.calc.calculator.calcbutton.CalcButtonManager;
import com.nstudio.calc.casio.R;

/**
 * Created by Duy on 1/14/2018.
 */

public class OperatorFragment extends Fragment {
    private OnCalcButtonClickListener listener;

    public static OperatorFragment newInstance(OnCalcButtonClickListener listener) {

        OperatorFragment fragment = new OperatorFragment();

        fragment.setListener(listener);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pad_boolean_op, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new CalcButtonManager(getContext(), (ViewGroup) view, listener);
    }


    public void setListener(OnCalcButtonClickListener listener) {
        this.listener = listener;
    }
}
