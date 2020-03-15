package com.wellnr.zttl.core.ports;

import com.wellnr.zttl.core.model.Note;

import java.util.List;

public interface NotesRepository {

    List<Note> getNotes();

    void saveNote(Note note);

}
