package com.wellnr.zttl.adapters;

import com.wellnr.zttl.core.model.Settings;
import com.wellnr.zttl.core.ports.SettingsRepository;

import java.nio.file.Path;

public class InMemorySettingsRepository implements SettingsRepository {

   private Settings settings = Settings.apply(Path.of("/Users/michael/Workspaces/zettelkastens"));

   @Override
   public Settings getSettings() {
      return settings;
   }

   @Override
   public void saveSettings(Settings settings) {
      this.settings = settings;
   }

}
