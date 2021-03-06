package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.Note;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Getter
public class NoteItem extends VBox {

    private final Note note;

    public NoteItem(Note note, Consumer<Note> onOpen) {
        Label title = new Label();
        title.textProperty().bind(note.getTitle());
        title.getStyleClass().add("zttl--note-title");

        String date = note.getUpdated().get().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Label content = new Label(date + ": " + note.getContent().get().split("\n")[0]);
        content.getStyleClass().addAll("zttl--note-content");

        note.getUpdated().addListener(observable -> {
            var d = note.getUpdated().get().format(DateTimeFormatter.ISO_LOCAL_DATE);
            var s = d + ": " + note.getContent().get().split("\n")[0];
            content.setText(s);
        });

        this.getChildren().addAll(title, content);
        this.getStyleClass().add("zttl--note-item");
        this.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(event.getClickCount() == 2){
                    onOpen.accept(note);
                }
            }
        });

        this.note = note;
    }

}
