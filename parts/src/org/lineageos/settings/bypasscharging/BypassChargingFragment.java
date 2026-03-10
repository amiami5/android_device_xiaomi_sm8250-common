/*
 * Copyright (C) 2026 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.bypasscharging;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.android.settingslib.widget.MainSwitchPreference;

import org.lineageos.settings.R;

public class BypassChargingFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "BypassChargingFragment";

    private MainSwitchPreference mSwitchBar;

    // -------------------------------------------------------------------------
    // PreferenceFragmentCompat
    // -------------------------------------------------------------------------

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.bypass_charging);

        mSwitchBar = findPreference(BypassChargingUtils.KEY_BYPASS_CHARGING);

        if (mSwitchBar != null) {
            // Prevent the AndroidX preference library from ever reading or
            // writing this preference to SharedPreferences.  The sysfs node
            // is our sole store of state.
            mSwitchBar.setPersistent(false);

            mSwitchBar.setOnPreferenceChangeListener(this);
        }
    }

    /**
     * Sync the toggle and its summary to the live hardware state every time
     * the fragment becomes visible.  This covers:
     *   - First open after boot (kernel resets node to 0 → toggle shows OFF)
     *   - Returning via back-navigation from another screen
     *   - Screen-off / screen-on while the fragment is in the back stack
     */
    @Override
    public void onResume() {
        super.onResume();
        syncUiWithHardware();
    }

    // -------------------------------------------------------------------------
    // Preference.OnPreferenceChangeListener
    // -------------------------------------------------------------------------

    /**
     * Called by the AndroidX preference framework when the user flips the
     * switch.  {@code newValue} is the Boolean the user wants to set.
     *
     * Return {@code true} to accept the change and let the framework update
     * the visual state of the preference immediately; we also write to the
     * sysfs node here so the hardware tracks the UI without any delay.
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSwitchBar && newValue instanceof Boolean) {
            final boolean enable = (Boolean) newValue;
            BypassChargingUtils.setEnabled(enable);
            updateSummary(enable);
            return true;
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Reads the current hardware state from sysfs and updates both the
     * checked state and the summary of the MainSwitchPreference.
     */
    private void syncUiWithHardware() {
        if (mSwitchBar == null) return;
        final boolean active = BypassChargingUtils.getCurrentHardwareState();
        mSwitchBar.setOnPreferenceChangeListener(null);
        mSwitchBar.setChecked(active);
        mSwitchBar.setOnPreferenceChangeListener(this);
        updateSummary(active);
    }

    /**
     * Updates the summary line beneath the toggle
     */
    private void updateSummary(boolean active) {
        if (mSwitchBar == null) return;
        mSwitchBar.setSummary(active
                ? getString(R.string.bypass_charging_status_on)
                : getString(R.string.bypass_charging_status_off));
    }
}
