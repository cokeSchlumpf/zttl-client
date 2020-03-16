package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.Note;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class NoteEditor extends BorderPane {

    private final Note note;

    public NoteEditor(Note note, Consumer<Note> onClose) {
        this.note = note;

        BorderPane titleRow = new BorderPane();
        titleRow.setLeft(new Label("Title"));
        titleRow.setRight(new TextField());

        TextField fldTitle = new TextField();
        fldTitle.getStyleClass().add("zttl--note-editor--title-field");
        fldTitle.textProperty().bindBidirectional(note.getTitle());

        Label lblTitle = new Label("Title");
        lblTitle.getStyleClass().add("zttl--note-editor--title-label");

        VBox form = new VBox(fldTitle, lblTitle);

        TextArea ta = new TextArea();
        ta.getStyleClass().addAll("zttl--text-area");
        ta.setWrapText(true);
        ta.setText(note.getContent().get());
        ta.textProperty().bindBidirectional(note.getContent());

        this.setTop(form);
        this.setCenter(ta);
    }

}
