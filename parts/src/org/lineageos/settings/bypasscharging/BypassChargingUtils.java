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

import android.util.Log;

import org.lineageos.settings.utils.FileUtils;

/**
 * Stateless utility class for Bypass Charging.
 *
 * The sysfs node is the single source of truth. The kernel resets
 * input_suspend to 0 on every power-cycle, which is the desired safe
 * default — we intentionally preserve that behaviour.
 */
public final class BypassChargingUtils {

    private static final String TAG = "BypassChargingUtils";

    /** Sysfs node: "1" = input suspended (bypass ON), "0" = normal charging. */
    public static final String BYPASS_CHARGING_PATH =
            "/sys/class/power_supply/battery/input_suspend";

    /** Preference key used in XML and as the MainSwitchPreference key. */
    public static final String KEY_BYPASS_CHARGING = "bypass_charging_enable";

    private BypassChargingUtils() {
        // Not instantiable
    }

    /**
     * Returns {@code true} if the sysfs node exists on this device.
     * Always call this before showing the UI.
     */
    public static boolean isSupported() {
        return FileUtils.fileExists(BYPASS_CHARGING_PATH);
    }

    /**
     * Reads the live state directly from the sysfs node.
     *
     * @return {@code true} if the node currently reads "1" (bypass charging ON)
     */
    public static boolean getCurrentHardwareState() {
        if (!isSupported()) return false;
        final String value = FileUtils.readOneLine(BYPASS_CHARGING_PATH);
        return value != null && "1".equals(value.trim());
    }

    /**
     * Writes the requested state directly to the sysfs node.
     * Nothing is persisted — the kernel owns the state entirely.
     *
     * @param enable {@code true} to suspend input (bypass ON),
     *               {@code false} to resume normal charging
     */
    public static void setEnabled(boolean enable) {
        if (!isSupported()) {
            Log.w(TAG, "setEnabled: sysfs node not found, ignoring");
            return;
        }
        final boolean ok = FileUtils.writeLine(BYPASS_CHARGING_PATH, enable ? "1" : "0");
        if (!ok) {
            Log.e(TAG, "setEnabled: failed to write to " + BYPASS_CHARGING_PATH);
        }
    }
}
