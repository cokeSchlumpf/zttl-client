package com.wellnr.zttl.core.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellnr.zttl.common.databind.ObjectMapperFactory;
import com.wellnr.zttl.core.model.Settings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.val;

import java.util.function.Consumer;

public class SettingsTab extends Tab {

   private final KeyCodeCombination save = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.META_DOWN);
   private final KeyCodeCombination close = new KeyCodeCombination(KeyCode.W, KeyCodeCombination.META_DOWN);

   private final TextArea ta;

   private final ObjectProperty<Settings> settings;

   private final ObjectMapper om;

   private final Consumer<Settings> onChange;

   private final Runnable onRequestClose;

   private final Stage primaryStage;

   private final BooleanProperty modifed;

   public SettingsTab(
      ObjectProperty<Settings> settings,
      Stage primaryStage,
      Consumer<Settings> onChange,
      Runnable onRequestClose) {

      this.settings = settings;
      this.onChange = onChange;
      this.primaryStage = primaryStage;
      this.onRequestClose = onRequestClose;
      this.om = ObjectMapperFactory.create(true);
      this.modifed = new SimpleBooleanProperty(false);

      ta = new TextArea();
      ta.getStyleClass().addAll("zttl--text-area");
      ta.setWrapText(true);

      ta.textProperty().addListener((observable, oldValue, newValue) -> this.onTextChanged(newValue));
      ta.setOnKeyPressed(this::onKeysPressed);

      this.settings.addListener(observable -> this.setSettings());
      this.modifed.addListener((obs, oldValue, newValue) -> this.onModifiedChanged(newValue));
      this.setText("Settings");
      this.setOnCloseRequest(this::onCloseRequest);
      this.setContent(ta);
      this.setSettings();
   }

   private void setSettings() {
      try {
         ta.setText(om.writeValueAsString(settings.get()));
         modifed.setValue(false);
      } catch (JsonProcessingException e) {
         throw new RuntimeException(e); // TODO mw: Better exception handling
      }
   }

   private void onCloseRequest(Event event) {
      if (this.modifed.get()) {
         var alert = new SaveSettingsMessageBox(primaryStage);

         alert.run(
            () -> {
               try {
                  onChange.accept(om.readValue(ta.getText(), Settings.class));
                  modifed.setValue(false);
               } catch (Exception e) {
                  event.consume();
                  val error = new SaveSettingsErrorMsgBox(primaryStage);
                  error.showAndWait();
               }
            },
            () -> {
               // Nothing to do, just continue.
            });
      }
   }

   private void onKeysPressed(KeyEvent event) {
      if (save.match(event)) {
         try {
            event.consume();
            onChange.accept(om.readValue(ta.getText(), Settings.class));
            modifed.setValue(false);
         } catch (Exception e) {
            val alert = new SaveSettingsErrorMsgBox(primaryStage);
            alert.showAndWait();
         }
      } else if (close.match(event)) {
         event.consume();

         if (this.modifed.get()) {
            var alert = new SaveSettingsMessageBox(primaryStage);

            alert.run(
               () -> {
                  try {
                     onChange.accept(om.readValue(ta.getText(), Settings.class));
                     modifed.setValue(false);
                     onRequestClose.run();
                  } catch (Exception e) {
                     val error = new SaveSettingsErrorMsgBox(primaryStage);
                     error.showAndWait();
                  }
               },
               onRequestClose);
         } else {
            onRequestClose.run();
         }
      }
   }

   private void onModifiedChanged(boolean newValue) {
      if (newValue) {
         this.setText("Settings *");
      } else {
         this.setText("Settings");
      }
   }

   private void onTextChanged(String newValue) {
      var errorClass = "zttl--text-area--error";
      modifed.setValue(true);

      try {
         om.readValue(newValue, Settings.class);
         ta.getStyleClass().remove(errorClass);
      } catch (Exception e) {
         ta.getStyleClass().add(errorClass);
      }
   }

}
