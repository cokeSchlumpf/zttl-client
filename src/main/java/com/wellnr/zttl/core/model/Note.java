package com.wellnr.zttl.core.model;

import lombok.Value;
import lombok.With;

import java.time.LocalDate;
import java.util.Set;

@With
@Value
public class Note {

    String id;

    NoteStatus status;

    LocalDate created;

    LocalDate updated;

    String title;

    Set<String> tags;

    String content;

}
