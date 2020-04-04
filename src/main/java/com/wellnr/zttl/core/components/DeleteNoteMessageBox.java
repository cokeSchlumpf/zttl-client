package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.Note;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class DeleteNoteMessageBox extends Alert {

   public DeleteNoteMessageBox(Stage owner, Note note) {
      super(AlertType.CONFIRMATION);

      this.setTitle("Delete Note");
      this.setContentText(" Do you really want to delete the note `" + note.getTitle().get() + "`?");
      this.initOwner(owner);

      this.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
   }

   public void run(Runnable onDelete) {
      this.showAndWait().ifPresent(
         bt -> {
            if (bt == ButtonType.YES) {
               onDelete.run();
            }
         });


   }

}
