package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.Note;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

        TextField fldTags = new TextField();
        fldTags.getStyleClass().add("zttl--note-editor--title-field");
        Label lblTags = new Label("Tags");
        lblTags.getStyleClass().add("zttl--note-editor--title-label");

        TagLabel lblTag01 = new TagLabel("Foo Bar");
        TagLabel lblTag02 = new TagLabel("Lorem Ipsum");

        HBox fldAllTags = new HBox(lblTag01, lblTag02, fldTags);
        fldAllTags.getStyleClass().add("zttl--note-editor--tags");

        VBox form = new VBox(fldTitle, lblTitle, new VSpace(), fldAllTags, lblTags);
        form.getStyleClass().add("zttl--note-editor--form");

        TextArea ta = new TextArea();
        ta.getStyleClass().addAll("zttl--text-area");
        ta.setWrapText(true);
        ta.setText(note.getContent().get());
        ta.textProperty().bindBidirectional(note.getContent());

        this.setTop(form);
        this.setCenter(ta);
    }

}
