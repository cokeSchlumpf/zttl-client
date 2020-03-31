package com.wellnr.zttl.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;

import java.util.List;
import java.util.Optional;

@With
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class State {

   private static final String OPEN_NOTES = "open-notes";
   private static final String SELECTED_NOTE = "selected-notes";

   @JsonProperty(OPEN_NOTES)
   List<String> openNotes;

   @JsonProperty(SELECTED_NOTE)
   String selectedNote;

   @JsonCreator
   public static State apply(
      @JsonProperty(OPEN_NOTES) List<String> openNotes,
      @JsonProperty(SELECTED_NOTE) String selectedNote) {

      return new State(openNotes, selectedNote);
   }

   public static State apply() {
      return apply(List.of(), null);
   }

   public Optional<String> getSelectedNote() {
      return Optional.ofNullable(selectedNote);
   }

}

