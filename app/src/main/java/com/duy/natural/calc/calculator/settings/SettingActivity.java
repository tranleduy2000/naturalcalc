package com.duy.natural.calc.calculator.settings;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nstudio.calc.casio.R;

/**
 * Created by Duy on 1/15/2018.
 */

public class SettingActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setupToolbar();
        setTitle(R.string.title_activity_settings);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content, SettingsFragment.newInstance());
        transaction.commit();
    }

}
