package com.wellnr.zttl.core.components;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class SaveSettingsErrorMsgBox extends Alert {

   public SaveSettingsErrorMsgBox(Stage primaryStage) {
      super(Alert.AlertType.ERROR);
      this.setTitle("Save settings");
      this.setHeaderText("Settings can't be saved");
      this.setContentText("The settings contain errors. Please correct them before you save the settings.");
      this.getButtonTypes().add(ButtonType.OK);
      this.initOwner(primaryStage);
   }

}
