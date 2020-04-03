package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.ports.NotesRepository;
import com.wellnr.zttl.core.views.app.model.Note;
import javafx.collections.ObservableSet;
import javafx.event.Event;
import javafx.scene.control.Tab;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NoteTab extends Tab {

    private final Note note;

    public NoteTab(
            Note note,
            NotesRepository notesRepository,
            ObservableSet<String> knownTags,
            Consumer<Note> onClose,
            BiConsumer<Note, Event> onCloseRequest,
            BiConsumer<Note, String> onAddTagToNote,
            BiConsumer<Note, String> onRemoveTagFromNote) {

        NoteEditor editor = new NoteEditor(note, notesRepository, knownTags, onAddTagToNote, onRemoveTagFromNote);

        this.setContent(editor);
        this.textProperty().bind(note.getTitle());
        this.note = note;

        this.setOnCloseRequest(event -> onCloseRequest.accept(note, event));
        this.setOnClosed(event -> onClose.accept(note));
    }

    public Note getNote() {
        return note;
    }
}
