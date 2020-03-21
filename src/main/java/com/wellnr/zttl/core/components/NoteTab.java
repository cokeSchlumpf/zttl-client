package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.Note;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NoteTab extends Tab {

    private final Note note;

    public NoteTab(
            Note note,
            ObservableSet<String> knownTags,
            Consumer<Note> onClose,
            BiConsumer<Note, String> onAddTagToNote,
            BiConsumer<Note, String> onRemoveTagFromNote) {

        NoteEditor editor = new NoteEditor(note, knownTags, onClose, onAddTagToNote, onRemoveTagFromNote);

        this.setContent(editor);
        this.textProperty().bind(note.getTitle());
        this.note = note;

        this.setOnClosed(event -> onClose.accept(note));
    }

    public Note getNote() {
        return note;
    }
}
