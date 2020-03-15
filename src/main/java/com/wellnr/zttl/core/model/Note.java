package com.wellnr.zttl.core.model;

import lombok.*;

import java.time.LocalDate;

@With
@Value
public class Note {

    private String id;

    private NoteStatus status;

    private LocalDate created;

    private LocalDate updated;

    private String title;

    private String content;

}
