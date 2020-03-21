package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.Note;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.List;

public class SaveAllNotesMessageBox extends Alert {

   public SaveAllNotesMessageBox(Stage owner, List<Note> note) {
      super(AlertType.CONFIRMATION);

      this.setTitle("Unsaved changes");
      this.setContentText("There are " + note.size() + " unsaved notes. Do you want to save them?");
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
