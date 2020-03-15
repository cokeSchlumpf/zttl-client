package com.wellnr.zttl.core.views.app.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class Note {

    private final StringProperty id;

    private final StringProperty title;

    private final StringProperty content;

    private final ObjectProperty<LocalDate> updated;

    public Note(String id, LocalDate updated, String title, String content) {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.updated = new SimpleObjectProperty<>(updated);
    }

}
