package com.duy.natural.calc.calculator.keyboard.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.duy.natural.calc.calculator.keyboard.OnCalcButtonClickListener;
import com.duy.natural.calc.calculator.keyboard.fragments.BasicPadFragment;
import com.duy.natural.calc.calculator.keyboard.fragments.FunctionPadFragment;
import com.duy.natural.calc.calculator.keyboard.fragments.LeftKeyboardFragment;

/**
 * Created by Duy on 1/14/2018.
 */

public class PagerSectionAdapter extends FragmentPagerAdapter {
    private static final int COUNT = 3;
    private final OnCalcButtonClickListener listener;
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public PagerSectionAdapter(FragmentManager childFragmentManager, OnCalcButtonClickListener listener) {
        super(childFragmentManager);
        this.listener = listener;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LeftKeyboardFragment.newInstance(listener);
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
