package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.Note;
import javafx.collections.ObservableSet;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NoteEditor extends BorderPane {

    private final Note note;

    public NoteEditor(
            Note note,
            ObservableSet<String> knownTags,
            Consumer<Note> onClose,
            BiConsumer<Note, String> onAddTag,
            BiConsumer<Note, String> onRemoveTag) {
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
        Tags tags = new Tags(
                note.getTags(),
                knownTags,
                s -> onRemoveTag.accept(note, s),
                s -> onAddTag.accept(note, s));

        VBox form = new VBox(fldTitle, lblTitle, new VSpace(), tags, lblTags);
        form.getStyleClass().add("zttl--note-editor--form");

        MarkdownEditor editor = new MarkdownEditor();
        editor.setText(note.getContent().get());
        editor.textProperty.bindBidirectional(note.getContent());

        NoteStatus status = new NoteStatus(note);

        this.getStyleClass().add("zttl--note-editor");
        this.setTop(form);
        this.setCenter(editor);
        this.setBottom(status);
    }

}
