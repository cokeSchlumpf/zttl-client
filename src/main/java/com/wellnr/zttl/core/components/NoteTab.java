package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.Note;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;

import java.util.function.Consumer;

public class NoteTab extends Tab {

    private final Note note;

    public NoteTab(Note note, Consumer<Note> onClose) {
        NoteEditor editor = new NoteEditor(note, onClose);

        this.setContent(editor);
        this.textProperty().bind(note.getTitle());
        this.note = note;

        this.setOnClosed(event -> onClose.accept(note));
    }

    public Note getNote() {
        return note;
    }
}
