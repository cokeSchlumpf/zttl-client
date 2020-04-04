package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.Note;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.fxmisc.easybind.EasyBind;
import org.ocpsoft.prettytime.PrettyTime;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

public class NoteStatus extends BorderPane {

   private final Note note;
   private final Label modifiedLabel;

   public NoteStatus(Note note) {
      this.note = note;

      this.modifiedLabel = new Label();
      this.modifiedLabel.getStyleClass().add("zttl--label");

      Label idLabel = new Label();
      idLabel.textProperty().bind(note.getId());
      idLabel.getStyleClass().add("zttl--label");

      Label statusLabel = new Label();
      statusLabel.getStyleClass().add("zttl--label");
      statusLabel.textProperty().bind(EasyBind.map(note.getStatus(), status -> {
         switch (status) {
            case NEW:
               return "New Note";
            case ARCHIVED:
               return "Archived";
            case INBOX:
               return "Inbox";
            default:
               return "";
         }
      }));

      this.setPadding(new Insets(5, 10, 5, 10));
      this.setPrefHeight(20);
      this.setMaxHeight(20);
      this.getStyleClass().add("zttl--note-status");

      this.setLeft(modifiedLabel);
      this.setCenter(statusLabel);
      this.setRight(idLabel);

      this.updateModified();
      note.getModified().addListener(observable -> this.updateModified());
      note.getUpdated().addListener(observable -> this.updateModified());
   }

   private void updateModified() {
      note
         .getModified()
         .get()
         .ifPresentOrElse(
            modified -> {
               PrettyTime p = new PrettyTime();
               Date date = Date.from(note
                  .getUpdated()
                  .get()
                  .atZone(ZoneId.systemDefault())
                  .toInstant());

               this.modifiedLabel.setText("Modified (Last saved " + p.format(date) +")");
            },
            () -> {
               DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
               this.modifiedLabel.setText(note.getUpdated().get().format(formatter));
            }
         );
   }

}
