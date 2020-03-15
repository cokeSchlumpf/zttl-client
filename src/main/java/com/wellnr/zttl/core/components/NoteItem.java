package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.Note;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

public class NoteItem extends VBox {

    public NoteItem(Note note) {
        Label title = new Label(note.getTitle().get());
        title.getStyleClass().add("zttl--note-title");

        String date = note.getUpdated().get().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Label content = new Label(date + ": " + note.getContent().get().split("\n")[0]);
        content.getStyleClass().addAll("zttl--note-content");

        this.getChildren().addAll(title, content);
        this.getStyleClass().add("zttl--note-item");
        this.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2){
                    System.out.println("Double clicked");
                }
            }
        });
    }

}
