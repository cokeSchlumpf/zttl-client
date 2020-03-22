package com.wellnr.zttl.core.views.app;

import com.wellnr.zttl.core.components.SaveAllNotesMessageBox;
import com.wellnr.zttl.core.components.SaveNoteMessageBox;
import com.wellnr.zttl.core.model.NoteStatus;
import com.wellnr.zttl.core.model.Settings;
import com.wellnr.zttl.core.ports.NotesRepository;
import com.wellnr.zttl.core.ports.SettingsRepository;
import com.wellnr.zttl.core.views.app.model.App;
import com.wellnr.zttl.core.views.app.model.Note;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.Event;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AppController {

   private final NotesRepository notesRepository;

   private final SettingsRepository settingsRepository;

   private final App model;

   private final AppView view;

   private final Stage primaryStage;

   public AppController(
      NotesRepository notesRepository,
      SettingsRepository settingsRepository,
      Stage primaryStage) {

      this.notesRepository = notesRepository;
      this.primaryStage = primaryStage;
      this.settingsRepository = settingsRepository;

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

      {
         List<Note> allNotes = new ArrayList<>();
         allNotes.addAll(inboxNotes);
         allNotes.addAll(archiveNotes);
         allNotes.forEach(this::initNote);
      }

      this.model = new App(openNotes, inboxNotes, archiveNotes, knownTags, settingsRepository.getSettings());
      this.view = new AppView(
         model,
         primaryStage,
         this::onAddTagToNote,
         this::onSelectedNoteChanged,
         this::onNoteCloseRequest,
         this::onNoteClosed,
         this::onOpenNote,
         this::onRemoveTagFromNote,
         this::onAbout,
         this::onClose,
         this::onCloseAll,
         this::onMoveToArchive,
         this::onMoveToInbox,
         this::onNew,
         this::onSave,
         this::onSaveAll,
         this::onSettings,
         this::onSaveSettings,
         this::onQuit);

      primaryStage.setOnCloseRequest(event -> {
         event.consume();
         this.onQuit();
      });
   }

   public AppView getView() {
      return view;
   }

   private void initNote(Note note) {
      note.getStatus().addListener((observable, oldValue, newValue) ->
         this.onNoteStatusChanged(note, oldValue, newValue));

      note.getTitle().addListener(observable -> this.onNoteChanged(note));
      note.getTags().addListener((InvalidationListener) observable -> this.onNoteChanged(note));
      note.getContent().addListener(observable -> this.onNoteChanged(note));
   }

   private void onAbout() {

   }

   private void onAddTagToNote(Note note, String tag) {
      note.getTags().add(tag);
   }

   private void onClose() {
      model.getCurrentNote().get().ifPresent(note -> {
         if (note.getModified().get().isPresent()) {
            var alert = new SaveNoteMessageBox(primaryStage, note);
            alert.run(
               () -> {
                  onSave(note);
                  model.getOpenNotes().remove(note);
               },
               () -> {
                  onRevertNote(note);
                  model.getOpenNotes().remove(note);
               });
         } else {
            model.getOpenNotes().remove(note);
         }
      });
   }

   private void onCloseAll() {
      List<Note> unsaved = model
         .getOpenNotes()
         .stream()
         .filter(n -> n.getModified().get().isPresent())
         .collect(Collectors.toList());

      if (unsaved.isEmpty()) {
         model.getOpenNotes().clear();
      } else {
         var alert = new SaveAllNotesMessageBox(primaryStage, unsaved);
         alert.run(
            () -> {
               unsaved.forEach(this::onSave);
               model.getOpenNotes().clear();
            },
            () -> {
               unsaved.forEach(this::onRevertNote);
               model.getOpenNotes().clear();
            });
      }
   }

   private void onMoveToArchive() {
      this.model.getCurrentNote().get().ifPresent(note -> note.getStatus().setValue(NoteStatus.ARCHIVED));
   }

   private void onMoveToInbox() {
      this.model.getCurrentNote().get().ifPresent(note -> note.getStatus().setValue(NoteStatus.INBOX));
   }

   private void onNew() {
      Note note = Note.fromNote(notesRepository.createNewNote());
      initNote(note);

      switch (note.getStatus().get()) {
         case INBOX:
            this.model.getInboxNotes().add(note);
            break;
         case ARCHIVED:
            this.model.getArchivedNotes().add(note);
            break;
      }

      onOpenNote(note);
   }

   private void onNoteChanged(Note note) {
      model.getCurrentNote().get().ifPresent(current -> {
         if (current == note) {
            var count = note.getContent().get().split(" ").length;
            model.getWordCount().setValue(Optional.of(count));
         }
      });

      note.getModified().setValue(Optional.of(LocalDateTime.now()));
   }

   private void onNoteCloseRequest(Note note, Event event) {
      if (note.getModified().get().isPresent()) {
         SaveNoteMessageBox msgbox = new SaveNoteMessageBox(primaryStage, note);
         msgbox.run(
            () -> onSave(note),
            () -> {
               onRevertNote(note);
               model.getOpenNotes().remove(note);
            },
            event::consume);
      }
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

   private void onNoteStatusChanged(Note note, NoteStatus oldValue, NoteStatus newValue) {
      switch (oldValue) {
         case INBOX:
            this.model.getInboxNotes().remove(note);
            break;
         case ARCHIVED:
            this.model.getArchivedNotes().remove(note);
            break;
      }

      switch (newValue) {
         case INBOX:
            this.model.getInboxNotes().add(note);
            break;
         case ARCHIVED:
            this.model.getArchivedNotes().add(note);
            break;
      }

      note.getModified().setValue(Optional.of(LocalDateTime.now()));
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

   private void onRevertNote(Note note) {
      notesRepository
         .getNoteById(note.getId().get())
         .ifPresent(note::updateFromNote);
   }

   private void onSave() {
      model.getCurrentNote().get().ifPresent(this::onSave);
   }

   private void onSave(Note note) {
      if (note.getStatus().get().equals(NoteStatus.NEW)) {
         note.getStatus().setValue(NoteStatus.INBOX);
      }

      note.getModified().setValue(Optional.empty());
      note.getUpdated().setValue(LocalDateTime.now());
      note.updateFromNote(notesRepository.saveNote(note.toNote()));
   }

   private void onSaveAll() {
      this.model.getOpenNotes().forEach(this::onSave);
   }

   private void onSelectedNoteChanged(Optional<Note> note) {
      this.model.getCurrentNote().setValue(note);
      this.model.getWordCount().setValue(note.map(n -> n.getContent().get().split(" ").length));
   }

   private void onSaveSettings(Settings settings) {
      this.settingsRepository.saveSettings(settings);
      this.model.getSettings().setValue(settings);
   }

   private void onSettings() {
      this.view.showSettings();
   }

   private void onQuit() {
      List<Note> unsaved = model
         .getOpenNotes()
         .stream()
         .filter(n -> n.getModified().get().isPresent())
         .collect(Collectors.toList());

      if (unsaved.isEmpty()) {
         Platform.exit();
         System.exit(0);
      } else {
         var alert = new SaveAllNotesMessageBox(primaryStage, unsaved);

         alert.run(
            () -> {
               unsaved.forEach(this::onSave);
               Platform.exit();
               System.exit(0);
            },
            () -> {
               unsaved.forEach(this::onRevertNote);
               Platform.exit();
               System.exit(0);
            });
      }
   }

}
