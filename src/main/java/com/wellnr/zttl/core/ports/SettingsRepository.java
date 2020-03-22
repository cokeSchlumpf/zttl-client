package com.wellnr.zttl.core.ports;

import com.wellnr.zttl.core.model.Settings;

public interface SettingsRepository {

   Settings getSettings();

   void saveSettings(Settings settings);

}
