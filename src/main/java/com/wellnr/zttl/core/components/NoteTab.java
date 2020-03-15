package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.Note;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;

import java.util.function.Consumer;

public class NoteTab extends Tab {

    private final Note note;

    public NoteTab(Note note, Consumer<Note> onClose) {
        TextArea ta = new TextArea();
        ta.getStyleClass().addAll("zttl--text-area");
        ta.setWrapText(true);
        ta.setText(note.getContent().get());
        ta.textProperty().bindBidirectional(note.getContent());

        this.setContent(ta);
        this.setText(note.getTitle().get());
        this.note = note;

        this.setOnClosed(event -> onClose.accept(note));
    }

    public Note getNote() {
        return note;
    }
}
