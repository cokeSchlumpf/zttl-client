package com.wellnr.zttl.core.views.app.model;

import com.wellnr.zttl.core.model.NoteStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter
public class Note {

    private final StringProperty id;

    private final ObjectProperty<NoteStatus> status;

    private final StringProperty title;

    private final StringProperty content;

    private final ObservableSet<String> tags;

    private final ObjectProperty<LocalDateTime> created;

    private final ObjectProperty<LocalDateTime> updated;

    private final ObjectProperty<Optional<LocalDateTime>> modified;

    public Note(String id, NoteStatus status, LocalDateTime created, LocalDateTime updated, String title, Set<String> tags, String content) {
        this.id = new SimpleStringProperty(id);
        this.status = new SimpleObjectProperty<>(status);
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.tags = FXCollections.observableSet(new HashSet<>(tags));
        this.created = new SimpleObjectProperty<>(created);
        this.updated = new SimpleObjectProperty<>(updated);
        this.modified = new SimpleObjectProperty<>(Optional.empty());
    }

    public static Note fromNote(com.wellnr.zttl.core.model.Note m) {
        return new Note(m.getId(), m.getStatus(), m.getCreated(), m.getUpdated(), m.getTitle(), m.getTags(), m.getContent());
    }

    public void updateFromNote(com.wellnr.zttl.core.model.Note note) {
        this.status.setValue(note.getStatus());
        this.title.setValue(note.getTitle());
        this.content.setValue(note.getContent());
        this.tags.clear();
        this.tags.addAll(note.getTags());
        this.created.setValue(note.getCreated());
        this.updated.setValue(note.getUpdated());
        this.modified.setValue(Optional.empty());
    }

    public com.wellnr.zttl.core.model.Note toNote() {
        return new com.wellnr.zttl.core.model.Note(
           id.get(),
           status.get(),
           created.get(),
           updated.get(),
           title.get(),
           tags,
           content.get());
    }

}
