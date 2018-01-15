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

import com.duy.natural.calc.calculator.CalculatorContract;
import com.duy.natural.calc.calculator.calcbutton.CalcButtonManager;
import com.duy.natural.calc.calculator.calcbutton.Category;
import com.duy.natural.calc.calculator.keyboard.adapters.PagerSectionAdapter;
import com.nstudio.calc.casio.R;

/**
 * Created by Duy on 1/13/2018.
 */

public class KeyboardFragment extends Fragment implements CalculatorContract.IKeyboardView, OnCalcButtonClickListener {
    private static final String TAG = "KeyboardFragment";
    @Nullable
    private CalcButtonManager mCalcButtonManager;
    private CalculatorContract.IPresenter mPresenter;
    private PagerSectionAdapter mAdapter;

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
        if (code != null) {
            mPresenter.getDisplayView().onButtonPressed(code);
        }
    }

    @Override
    public void setPaletteBlockEnabled(Category category, boolean enabled) {
       /* if (mCalcButtonManager != null) {
            mCalcButtonManager.setPaletteBlockEnabled(category, enabled);
            for (int i = 0; i < mAdapter.getCount(); i++) {
                Fragment fragment = mAdapter.getRegisteredFragment(i);
                if (fragment instanceof CalculatorContract.IKeyboardView) {
                    ((CalculatorContract.IKeyboardView) fragment).setPaletteBlockEnabled(category, enabled);
                }
            }
        }*/
    }

    @Override
    public void enableHiddenInput(boolean hiddenInputEnabled) {
        if (mCalcButtonManager != null) {
            mCalcButtonManager.enableHiddenInput(hiddenInputEnabled);
        }
    }

    @Override
    public void setEnabled(boolean enable) {
      /*  if (mCalcButtonManager != null) {
            mCalcButtonManager.setEnabled(enable);
            for (int i = 0; i < mAdapter.getCount(); i++) {
                Fragment fragment = mAdapter.getRegisteredFragment(i);
                if (fragment instanceof CalculatorContract.IKeyboardView) {
                    ((CalculatorContract.IKeyboardView) fragment).setEnabled(enable);
                }
            }
        }*/
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
        mAdapter = new PagerSectionAdapter(getChildFragmentManager(), this);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(mAdapter.getCount());
        ((TabLayout) view.findViewById(R.id.tab_layout)).setupWithViewPager(viewPager);
        mCalcButtonManager = new CalcButtonManager(getContext(), (ViewGroup) view, this);
    }

    @Override
    public void onButtonPressed(@Nullable View view, String code) {
        onButtonPressed(code);
    }
}
