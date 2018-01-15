package com.duy.natural.calc.calculator.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nstudio.calc.casio.R;

/**
 * Created by Duy on 1/15/2018.
 */

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle(R.string.title_activity_settings);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, SettingsFragment.newInstance());
        transaction.commit();
    }
}
