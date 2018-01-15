package com.duy.natural.calc.calculator.keyboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duy.common.utils.DLog;
import com.duy.natural.calc.calculator.CalculatorContract;
import com.duy.natural.calc.calculator.calcbutton.CalcButtonManager;
import com.duy.natural.calc.calculator.calcbutton.Category;
import com.nstudio.calc.casio.R;

/**
 * Created by Duy on 1/13/2018.
 */

public class KeyboardFragment extends Fragment implements CalculatorContract.IKeyboardView, OnCalcButtonClickListener {
    private static final String TAG = "KeyboardFragment";
    //    private CalcButtonManager mCalcButtonManager;
    private CalculatorContract.IPresenter mPresenter;

    public static KeyboardFragment newInstance() {
        Bundle args = new Bundle();
        KeyboardFragment fragment = new KeyboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setPresenter(CalculatorContract.IPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onButtonPressed(String code) {
        if (DLog.DEBUG) DLog.d(TAG, "onButtonPressed() called with: code = [" + code + "]");
        if (code != null) {
            mPresenter.getDisplayView().onButtonPressed(code);
        }
    }

    @Override
    public void setPaletteBlockEnabled(Category category, boolean enabled) {
        if (DLog.DEBUG)
            DLog.d(TAG, "setPaletteBlockEnabled() called with: category = [" + category + "], enabled = [" + enabled + "]");
//        mCalcButtonManager.setPaletteBlockEnabled(category, enabled);
    }

    @Override
    public void enableHiddenInput(boolean hiddenInputEnabled) {
        if (DLog.DEBUG)
            DLog.d(TAG, "enableHiddenInput() called with: hiddenInputEnabled = [" + hiddenInputEnabled + "]");
//        mCalcButtonManager.enableHiddenInput(hiddenInputEnabled);
    }

    @Override
    public void setEnabled(boolean enable) {
        if (DLog.DEBUG) DLog.d(TAG, "setEnabled() called with: enable = [" + enable + "]");
//        mCalcButtonManager.setEnabled(enable);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_keyboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerSectionAdapter(getChildFragmentManager(), this));
        viewPager.setCurrentItem(1);
        ((TabLayout) view.findViewById(R.id.tab_layout)).setupWithViewPager(viewPager);

        new CalcButtonManager(getContext(), (ViewGroup) view, this);
    }

    @Override
    public void onButtonPressed(@Nullable View view, String code) {
        onButtonPressed(code);
    }
}
