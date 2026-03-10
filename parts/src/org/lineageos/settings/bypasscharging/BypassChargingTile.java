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

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

/**
 * Quick Settings tile for Bypass Charging.
 */
public class BypassChargingTile extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile(BypassChargingUtils.getCurrentHardwareState());
    }

    @Override
    public void onClick() {
        final Tile tile = getQsTile();
        if (tile == null) return;

        final boolean newState = (tile.getState() != Tile.STATE_ACTIVE);

        // Update icon on the tap frame.
        updateTile(newState);

        BypassChargingUtils.setEnabled(newState);
    }

    private void updateTile(boolean active) {
        final Tile tile = getQsTile();
        if (tile == null) return;
        tile.setState(active ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }
}
