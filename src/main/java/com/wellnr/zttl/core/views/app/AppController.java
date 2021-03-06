package com.wellnr.zttl.core.views.app;

import com.wellnr.zttl.core.components.DeleteNoteMessageBox;
import com.wellnr.zttl.core.components.SaveAllNotesMessageBox;
import com.wellnr.zttl.core.components.SaveNoteMessageBox;
import com.wellnr.zttl.core.model.NoteStatus;
import com.wellnr.zttl.core.model.Settings;
import com.wellnr.zttl.core.model.State;
import com.wellnr.zttl.core.ports.NotesRepository;
import com.wellnr.zttl.core.ports.SettingsRepository;
import com.wellnr.zttl.core.views.app.model.App;
import com.wellnr.zttl.core.views.app.model.Note;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.Event;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
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

   private final State state;

   public AppController(
      NotesRepository notesRepository,
      SettingsRepository settingsRepository,
      Stage primaryStage) {

      this.notesRepository = notesRepository;
      this.primaryStage = primaryStage;
      this.settingsRepository = settingsRepository;
      this.state = settingsRepository.getState();

      List<Note> allNotes = notesRepository
         .getNotes()
         .stream()
         .map(Note::fromNote)
         .collect(Collectors.toList());

      allNotes.forEach(this::initNote);

      List<Note> inboxNotes = allNotes
         .stream()
         .filter(note -> note.getStatus().get().equals(NoteStatus.INBOX))
         .collect(Collectors.toList());

      List<Note> archiveNotes = allNotes
         .stream()
         .filter(note -> note.getStatus().get().equals(NoteStatus.ARCHIVED))
         .collect(Collectors.toList());

      List<Note> openNotes = new ArrayList<>();
      Set<String> knownTags = notesRepository.getTags();

      this.model = new App(openNotes, inboxNotes, archiveNotes, knownTags, settingsRepository.getSettings());
      this.view = new AppView(
         model,
         notesRepository,
         primaryStage,
         this::onAddTagToNote,
         note -> onSelectedNoteChanged(note.orElse(null)),
         this::onNoteCloseRequest,
         this::onNoteClosed,
         this::onOpenNote,
         this::onOpenNoteById,
         this::onRemoveTagFromNote,
         this::onAbout,
         this::onClose,
         this::onCloseAll,
         this::onDelete,
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

      this.state
         .getOpenNotes()
         .stream()
         .map(id -> allNotes.stream().filter(n -> n.getId().get().equals(id)).findFirst())
         .filter(Optional::isPresent)
         .map(Optional::get)
         .forEach(this::onOpenNote);

      onSelectedNoteChanged(state
         .getSelectedNote()
         .flatMap(id -> allNotes.stream().filter(n -> n.getId().get().equals(id)).findFirst())
         .orElse(null));
   }

   public void init() {
      this.primaryStage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.F11));

      if (!this.state.getLayoutFullscreen()) {
         this.primaryStage.setWidth(this.state.getLayoutSizeWidth());
         this.primaryStage.setHeight(this.state.getLayoutSizeHeight());
      } else {
         this.primaryStage.setFullScreen(this.state.getLayoutFullscreen());
      }

      this.view.layoutDividerPosition.setValue(this.state.getLayoutDividerPosition());
      this.view.layoutNotesBrowserDividerPositions.setValue(this.state.getLayoutNotesBrowserDividerPositions());
      this.primaryStage.setTitle("Zettels");
      this.primaryStage.show();

      // After full screen this is required again ...
      this.view.layoutDividerPosition.setValue(this.state.getLayoutDividerPosition());
      this.view.layoutNotesBrowserDividerPositions.setValue(this.state.getLayoutNotesBrowserDividerPositions());
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

   private void onDelete() {
      model.getCurrentNote().get().ifPresent(note -> {
         var alert = new DeleteNoteMessageBox(primaryStage, note);

         alert.run(
            () -> {
               notesRepository.deleteNote(note.toNote());
               model.getOpenNotes().remove(note);
               model.getArchivedNotes().remove(note);
               model.getInboxNotes().remove(note);
            });
      });
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

   private void onOpenNoteById(String id) {
      List<Note> allNotes = new ArrayList<>();
      allNotes.addAll(model.getInboxNotes());
      allNotes.addAll(model.getArchivedNotes());

      allNotes
         .stream()
         .filter(note -> note.getId().get().equals(id))
         .findFirst()
         .ifPresent(this::onOpenNote);
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

   private void onSelectedNoteChanged(Note note) {
      this.model.getCurrentNote().setValue(Optional.ofNullable(note));
      this.model.getWordCount().setValue(Optional.ofNullable(note).map(n -> n.getContent().get().split(" ").length));
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
         saveState();
         Platform.exit();
         System.exit(0);
      } else {
         var alert = new SaveAllNotesMessageBox(primaryStage, unsaved);

         alert.run(
            () -> {
               unsaved.forEach(this::onSave);
               saveState();
               Platform.exit();
               System.exit(0);
            },
            () -> {
               unsaved.forEach(this::onRevertNote);
               saveState();
               Platform.exit();
               System.exit(0);
            });
      }
   }

   private void saveState() {
      State newState = this.state
         .withLayoutDividerPosition(view.layoutDividerPosition.doubleValue())
         .withLayoutNotesBrowserDividerPositions(view.layoutNotesBrowserDividerPositions.get())
         .withLayoutFullscreen(primaryStage.isFullScreen())
         .withOpenNotes(model.getOpenNotes().stream().map(n -> n.getId().get()).collect(Collectors.toList()))
         .withSelectedNote(model.getCurrentNote().get().map(n -> n.getId().get()).orElse(null));

      if (newState.getLayoutFullscreen()) {
         newState = newState
            .withLayoutSizeWidth(primaryStage.getWidth())
            .withLayoutSizeHeight(primaryStage.getHeight());
      }

      settingsRepository.saveState(newState);
   }

}
