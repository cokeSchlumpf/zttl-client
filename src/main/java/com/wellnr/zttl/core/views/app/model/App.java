package com.wellnr.zttl.core.views.app.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class App {

    private final ObservableList<Note> openNotes;

    private final ObservableList<Note> inboxNotes;

    private final ObservableList<Note> archivedNotes;

    private final ObjectProperty<Optional<Note>> currentNote;

    private final ObjectProperty<Optional<Integer>> wordCount;

    public App(List<Note> openNotes, List<Note> inboxNotes, List<Note> archivedNotes) {
        this.openNotes = FXCollections.observableList(openNotes);
        this.inboxNotes = FXCollections.observableList(inboxNotes);
        this.archivedNotes = FXCollections.observableList(archivedNotes);
        this.currentNote = new SimpleObjectProperty<>(Optional.empty());
        this.wordCount = new SimpleObjectProperty<>(Optional.empty());
    }

}
