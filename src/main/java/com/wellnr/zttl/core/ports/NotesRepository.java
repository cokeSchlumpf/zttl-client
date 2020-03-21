package com.wellnr.zttl.core.ports;

import com.wellnr.zttl.core.model.Note;

import java.util.List;
import java.util.Set;

public interface NotesRepository {

    List<Note> getNotes();

    Set<String> getTags();

    void saveNote(Note note);

}
