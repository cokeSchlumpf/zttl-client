package com.wellnr.zttl.core.views.app;

import com.wellnr.zttl.core.model.NoteStatus;
import com.wellnr.zttl.core.ports.NotesRepository;
import com.wellnr.zttl.core.views.app.model.Note;
import com.wellnr.zttl.core.views.app.model.App;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AppController {

    private final App model;

    private final AppView view;

    public AppController(NotesRepository notesRepository) {
        List<Note> inboxNotes = notesRepository
                .getNotes()
                .stream()
                .filter(note -> note.getStatus().equals(NoteStatus.INBOX))
                .map(Note::fromNote)
                .collect(Collectors.toList());

        List<Note> archiveNotes = notesRepository
                .getNotes()
                .stream()
                .filter(note -> note.getStatus().equals(NoteStatus.ARCHIVED))
                .map(Note::fromNote)
                .collect(Collectors.toList());

        List<Note> openNotes = new ArrayList<>();

        Set<String> knownTags = notesRepository.getTags();

        this.model = new App(openNotes, inboxNotes, archiveNotes, knownTags);
        this.view = new AppView(
                model,
                this::onAddTagToNote,
                this::onSelectedNoteChanged,
                this::onNoteClosed,
                this::onOpenNote,
                this::onRemoveTagFromNote);

        openNotes.forEach(note -> note.getContent().addListener((observable, oldValue, newValue) -> {
            AppController.this.onNoteChanged(note, newValue);
        }));
    }

    public AppView getView() {
        return view;
    }

    private void onAddTagToNote(Note note, String tag) {
        note.getTags().add(tag);
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

    private void onOpenNote(Note note) {
        if (!model.getOpenNotes().contains(note)) {
            model.getOpenNotes().add(note);
        } else {
            model.getCurrentNote().setValue(Optional.of(note));
        }
    }

    private void onRemoveTagFromNote(Note note, String tag) {
        note.getTags().remove(tag);
    }

    private void onSelectedNoteChanged(Note note) {
        this.model.getCurrentNote().setValue(Optional.of(note));
        this.model.getWordCount().setValue(Optional.of(note.getContent().get().split(" ").length));
    }

}
