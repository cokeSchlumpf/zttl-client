package com.wellnr.zttl.core.model;

import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;
import java.util.Set;

@With
@Value
public class Note {

    String id;

    NoteStatus status;

    LocalDateTime created;

    LocalDateTime updated;

    String title;

    Set<String> tags;

    String content;

}
