package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.Note;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class SaveNoteMessageBox extends Alert {

   public SaveNoteMessageBox(Stage owner, Note note) {
      super(Alert.AlertType.CONFIRMATION);

      this.setTitle("Unsaved changes");
      this.setContentText("`" + note.getTitle().get() + "` has unsaved changes. Do you want to change them?");
      this.initOwner(owner);

      this.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
   }

   public void run(Runnable onSave, Runnable onContinue) {
      run(onSave, onContinue, () -> {
      });
   }

   public void run(Runnable onSave, Runnable onContinue, Runnable onCancel) {
      this.showAndWait().ifPresentOrElse(
         bt -> {
            if (bt == ButtonType.YES) {
               onSave.run();
            } else if (bt == ButtonType.NO) {
               onContinue.run();
            } else {
               onCancel.run();
            }
         },
         onCancel);


   }

}
