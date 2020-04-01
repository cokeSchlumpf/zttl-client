package com.wellnr.zttl.adapters;

import com.wellnr.zttl.core.model.Settings;
import com.wellnr.zttl.core.model.State;
import com.wellnr.zttl.core.ports.SettingsRepository;

import java.nio.file.Path;

public class InMemorySettingsRepository implements SettingsRepository {

   private State state = State.apply();

   private Settings settings = Settings.apply(Path.of("/Users/michael/Workspaces/zettelkastens-data"));

   @Override
   public Settings getSettings() {
      return settings;
   }

   @Override
   public State getState() {
      return state;
   }

   @Override
   public void saveSettings(Settings settings) {
      this.settings = settings;
   }

   @Override
   public void saveState(State state) {
      this.state = state;
   }

}
