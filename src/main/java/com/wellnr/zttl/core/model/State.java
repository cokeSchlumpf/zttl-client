package com.wellnr.zttl.core.model;

import lombok.*;

import java.util.List;

@With
@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class State {

   List<String> openNotes;

   String selectedNote;

}

