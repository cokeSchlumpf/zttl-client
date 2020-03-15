package com.wellnr.zttl.core.views.app;

import com.wellnr.zttl.core.model.NoteStatus;
import com.wellnr.zttl.core.ports.NotesRepository;
import com.wellnr.zttl.core.views.app.model.Note;
import com.wellnr.zttl.core.views.app.model.App;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AppController {

    private final App model;

    private final AppView view;

    private final NotesRepository notesRepository;

    public AppController(NotesRepository notesRepository) {
        List<Note> inboxNotes = notesRepository
                .getNotes()
                .stream()
                .filter(note -> note.getStatus().equals(NoteStatus.INBOX))
                .map(note -> new Note(note.getId(), note.getUpdated(), note.getTitle(), note.getContent()))
                .collect(Collectors.toList());

        List<Note> archiveNotes = notesRepository
                .getNotes()
                .stream()
                .filter(note -> note.getStatus().equals(NoteStatus.ARCHIVED))
                .map(note -> new Note(note.getId(), note.getUpdated(), note.getTitle(), note.getContent()))
                .collect(Collectors.toList());

        List<Note> openNotes = new ArrayList<>();

        this.notesRepository = notesRepository;
        this.model = new App(openNotes, inboxNotes, archiveNotes);
        this.view = new AppView(
                model,
                this::onSelectedNoteChanged,
                this::onNoteClosed);

        openNotes.forEach(note -> note.getContent().addListener((observable, oldValue, newValue) -> {
            AppController.this.onNoteChanged(note, newValue);
        }));
    }

    public AppView getView() {
        return view;
    }

    private void onNoteChanged(Note note, String newValue) {
        this.model.getCurrentNote().get().ifPresent(current -> {
            if (current.equals(note)) {
                this.model.getWordCount().setValue(Optional.of(newValue.split(" ").length));
            }
        });

    }

    private void onNoteClosed(Note note) {
        model.getOpenNotes().remove(note);

        this.model.getCurrentNote().get().ifPresent(current -> {
            if (current.equals(note)) {
                this.model.getCurrentNote().setValue(Optional.empty());
                this.model.getWordCount().setValue(Optional.empty());
            }
        });
    }

    private void onSelectedNoteChanged(Note note) {
        this.model.getCurrentNote().setValue(Optional.of(note));
        this.model.getWordCount().setValue(Optional.of(note.getContent().get().split(" ").length));
    }
}
