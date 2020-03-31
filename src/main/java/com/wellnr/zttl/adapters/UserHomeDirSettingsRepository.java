package com.wellnr.zttl.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellnr.zttl.common.databind.ObjectMapperFactory;
import com.wellnr.zttl.core.model.Settings;
import com.wellnr.zttl.core.model.State;
import com.wellnr.zttl.core.ports.SettingsRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class UserHomeDirSettingsRepository implements SettingsRepository {

   private final Path settingsFile;

   private final Path stateFile;

   private final ObjectMapper om;

   private UserHomeDirSettingsRepository(Path settingsFile, Path stateFile, ObjectMapper om) {
      this.settingsFile = settingsFile;
      this.stateFile = stateFile;
      this.om = om;
   }

   public static UserHomeDirSettingsRepository create() {
      try {
         ObjectMapper om = ObjectMapperFactory.create(true);
         Path settingsDir = Path.of(System.getProperty("user.home")).resolve(".zettels");
         Path settingsFile = settingsDir.resolve("settings.json");
         Path stateFile = settingsDir.resolve("state.json");

         Files.createDirectories(settingsDir);

         if (!Files.exists(settingsFile)) {
            try (OutputStream os = Files.newOutputStream(settingsFile)) {
               om.writeValue(os, Settings.apply(settingsDir.resolve("notes")));
            }
         }

         if (!Files.exists(stateFile)) {
            try (OutputStream os = Files.newOutputStream(stateFile)) {
               om.writeValue(os, State.apply());
            }
         }

         return new UserHomeDirSettingsRepository(settingsFile, stateFile, om);
      } catch (IOException e) {
         e.printStackTrace(); // TODO mw: Improve Error Handling
         System.exit(42);
         throw new RuntimeException(e);
      }
   }

   @Override
   public Settings getSettings() {
      try (InputStream is = Files.newInputStream(settingsFile)) {
         return om.readValue(is, Settings.class);
      } catch (IOException e) {
         throw new RuntimeException(e); // TODO mw: Improve Error Handling
      }
   }

   @Override
   public State getState() {
      try (InputStream is = Files.newInputStream(stateFile)) {
         return om.readValue(is, State.class);
      } catch (IOException e) {
         throw new RuntimeException(e); // TODO mw: Improve Error Handling
      }
   }

   @Override
   public void saveSettings(Settings settings) {
      try (OutputStream os = Files.newOutputStream(settingsFile)) {
         om.writeValue(os, settings);
      } catch (IOException e) {
         throw new RuntimeException(e); // TODO mw: Improve Error Handling
      }
   }

   @Override
   public void saveState(State state) {
      try (OutputStream os = Files.newOutputStream(stateFile)) {
         om.writeValue(os, state);
      } catch (IOException e) {
         throw new RuntimeException(e); // TODO mw: Improve Error Handling
      }
   }

}
