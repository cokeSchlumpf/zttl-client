package com.wellnr.zttl.core.views.app.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import lombok.Getter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Note {

    private final StringProperty id;

    private final StringProperty title;

    private final StringProperty content;

    private final ObservableSet<String> tags;

    private final ObjectProperty<LocalDate> updated;

    public Note(String id, LocalDate updated, String title, Set<String> tags, String content) {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.tags = FXCollections.observableSet(new HashSet<>(tags));
        this.updated = new SimpleObjectProperty<>(updated);
    }

    public static Note fromNote(com.wellnr.zttl.core.model.Note m) {
        return new Note(m.getId(), m.getUpdated(), m.getTitle(), m.getTags(), m.getContent());
    }

}
