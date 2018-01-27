package com.duy.natural.calc.calculator.settings;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nstudio.calc.casio.R;

/**
 * Created by Duy on 1/27/2018.
 */
public class BaseActivity extends AppCompatActivity {
    protected Toolbar mToolbar;

    protected void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
