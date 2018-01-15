package com.duy.natural.calc.calculator.keyboard.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.duy.natural.calc.calculator.keyboard.BasicPadFragment;
import com.duy.natural.calc.calculator.keyboard.FunctionPadFragment;
import com.duy.natural.calc.calculator.keyboard.OnCalcButtonClickListener;
import com.duy.natural.calc.calculator.keyboard.OperatorFragment;

/**
 * Created by Duy on 1/14/2018.
 */

public class PagerSectionAdapter extends FragmentPagerAdapter {
    private static final int COUNT = 3;
    private final OnCalcButtonClickListener listener;


    public PagerSectionAdapter(FragmentManager childFragmentManager, OnCalcButtonClickListener listener) {
        super(childFragmentManager);
        this.listener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return OperatorFragment.newInstance(listener);
            case 1:
                return BasicPadFragment.newInstance(listener);
            case 2:
                return FunctionPadFragment.newInstance(listener);
        }
        return null;
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
