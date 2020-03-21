package com.wellnr.zttl.core.ports;

import com.wellnr.zttl.core.model.Note;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface NotesRepository {

    Optional<Note> getNoteById(String id);

    List<Note> getNotes();

    Set<String> getTags();

    void saveNote(Note note);

}
