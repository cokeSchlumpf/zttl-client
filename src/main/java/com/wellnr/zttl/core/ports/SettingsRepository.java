package com.wellnr.zttl.core.ports;

import com.wellnr.zttl.core.model.Settings;
import com.wellnr.zttl.core.model.State;

public interface SettingsRepository {

   Settings getSettings();

   State getState();

   void saveSettings(Settings settings);

   void saveState(State state);

}
