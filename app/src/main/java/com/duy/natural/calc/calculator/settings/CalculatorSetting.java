package com.duy.natural.calc.calculator.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nstudio.calc.casio.R;

/**
 * Created by Duy on 1/15/2018.
 */

public class CalculatorSetting {
    private Context mContext;
    private SharedPreferences mPreferences;

    public CalculatorSetting(Context context) {
        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isUseVibrate() {
        return getBoolean(mContext.getString(R.string.pref_key_enable_vibrate), true);
    }


    public long getVibrateStrength() {
        return getInt(mContext.getString(R.string.pref_key_vibrate_strength), 50);
    }

    private boolean getBoolean(String key, boolean def) {
        try {
            return mPreferences.getBoolean(key, def);
        } catch (Exception e) {
            String string = mPreferences.getString(key, "");
            return string.isEmpty() ? def : Boolean.parseBoolean(string);
        }
    }

    private int getInt(String key, int def) {
        try {
            return mPreferences.getInt(key, def);
        } catch (Exception e) {
            String string = mPreferences.getString(key, "");
            return string.isEmpty() ? def : Integer.parseInt(string);
        }
    }

    public boolean useLightTheme() {
        return false;
    }
}
