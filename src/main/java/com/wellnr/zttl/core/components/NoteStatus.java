package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.Note;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.fxmisc.easybind.EasyBind;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

public class NoteStatus extends BorderPane {

   private final Note note;
   private final Label modified;

   public NoteStatus(Note note) {
      this.note = note;

      this.modified = new Label();
      this.modified.getStyleClass().add("zttl--label");

      Label idLabel = new Label();
      idLabel.textProperty().bind(note.getId());
      idLabel.getStyleClass().add("zttl--label");


      this.setPadding(new Insets(5, 10, 5, 10));
      this.setPrefHeight(20);
      this.setMaxHeight(20);
      this.getStyleClass().add("zttl--note-status");
      this.setRight(idLabel);
      this.setLeft(modified);

      this.updateModified();
      note.getModified().addListener(observable -> this.updateModified());
   }

   private void updateModified() {
      note
         .getModified()
         .get()
         .ifPresentOrElse(
            modified -> {
               PrettyTime p = new PrettyTime();
               Date date = Date.from(modified
                  .atStartOfDay()
                  .atZone(ZoneId.systemDefault())
                  .toInstant());

               this.modified.setText("Modified " + p.format(date));
            },
            () -> {
               DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
               this.modified.setText(note.getUpdated().get().format(formatter));
            }
         );
   }

}
