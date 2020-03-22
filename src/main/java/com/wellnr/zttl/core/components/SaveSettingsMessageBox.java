package com.wellnr.zttl.core.components;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class SaveSettingsMessageBox extends Alert {

   public SaveSettingsMessageBox(Stage owner) {
      super(AlertType.CONFIRMATION);

      this.setTitle("Unsaved changes");
      this.setContentText("Settings have been modified. Do you want to save them?");
      this.initOwner(owner);

      this.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
   }

   public void run(Runnable onSave, Runnable onContinue) {
      this.showAndWait().ifPresentOrElse(
         bt -> {
            if (bt == ButtonType.YES) {
               onSave.run();
            } else if (bt == ButtonType.NO) {
               onContinue.run();
            } else {
               onContinue.run();
            }
         },
         onContinue::run);
   }

}
