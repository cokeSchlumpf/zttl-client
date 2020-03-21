package com.wellnr.zttl.core.views.app.model;

import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter
public class App {

   private final ObservableList<Note> openNotes;

   private final ObservableList<Note> inboxNotes;

   private final ObservableList<Note> archivedNotes;

   private final ObservableSet<String> knownTags;

   private final ObjectProperty<Optional<Note>> currentNote;

   private final ObjectProperty<Optional<Integer>> wordCount;

   private final IntegerProperty noteCount;

   public App(
      List<Note> openNotes,
      List<Note> inboxNotes,
      List<Note> archivedNotes,
      Set<String> knownTags) {

      this.openNotes = FXCollections.observableList(openNotes);
      this.inboxNotes = FXCollections.observableList(inboxNotes);
      this.archivedNotes = FXCollections.observableList(archivedNotes);
      this.currentNote = new SimpleObjectProperty<>(Optional.empty());

      this.wordCount = new SimpleObjectProperty<>(Optional.empty());
      this.knownTags = FXCollections.observableSet(knownTags);

      this.noteCount = new SimpleIntegerProperty(inboxNotes.size() + archivedNotes.size());

      this.archivedNotes.addListener(
         (InvalidationListener) observable -> this.noteCount.set(inboxNotes.size() + archivedNotes.size()));

      this.inboxNotes.addListener(
         (InvalidationListener) observable -> this.noteCount.set(inboxNotes.size() + archivedNotes.size()));
   }

}
