/*
 * Copyright (c) 2017 by Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.common.preferences;

import android.support.v7.preference.Preference;

import com.duy.common.utils.DLog;

public class PreferencesCompat {

    private static final String TAG = "PreferencesCompat";

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see PreferencesSupportV7
     */
    public static void bindPreferenceSummaryToValue(Preference preference) {
        PreferencesSupportV7.bindPreferenceSummaryToValue(preference);
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see PreferencesNative
     */
    public static void bindPreferenceSummaryToValue(Preference preference, Object value) {
if (DLog.DEBUG) DLog.d(TAG, "bindPreferenceSummaryToValue() called with: preference = [" + preference + "]");
        PreferencesSupportV7.bindPreferenceSummaryToValue(preference, value);
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see PreferencesSupportV7
     */
    public static void bindPreferenceSummaryToValue(android.preference.Preference preference) {
        PreferencesNative.bindPreferenceSummaryToValue(preference);
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see PreferencesNative
     */
    public static void bindPreferenceSummaryToValue(android.preference.Preference preference, Object value) {
if (DLog.DEBUG) DLog.d(TAG, "bindPreferenceSummaryToValue() called with: preference = [" + preference + "]");
        PreferencesNative.bindPreferenceSummaryToValue(preference, value);
    }

}