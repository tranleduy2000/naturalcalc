package com.duy.natural.calc.calculator.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.duy.common.preferences.PreferencesCompat;
import com.nstudio.calc.casio.R;

/**
 * Created by Duy on 1/15/2018.
 */

public class SettingsFragment extends PreferenceFragment {
    public static SettingsFragment newInstance() {

        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_setting, false);
        addPreferencesFromResource(R.xml.pref_setting);

        PreferencesCompat.bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_vibrate_strength)));
    }
}
