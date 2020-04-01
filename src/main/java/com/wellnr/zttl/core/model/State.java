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

   private static final String LAYOUT_DIVIDER_POSITION = "layout-divider-position";
   private static final String LAYOUT_FULLSCREEN = "layout-fullscreen";
   private static final String LAYOUT_SIZE_WIDTH = "layout-size-width";
   private static final String LAYOUT_SIZE_HEIGHT = "layout-size-height";
   private static final String OPEN_NOTES = "open-notes";
   private static final String SELECTED_NOTE = "selected-notes";

   @JsonProperty(LAYOUT_DIVIDER_POSITION)
   Double layoutDividerPosition;

   @JsonProperty(LAYOUT_FULLSCREEN)
   Boolean layoutFullscreen;

   @JsonProperty(LAYOUT_SIZE_HEIGHT)
   Double layoutSizeHeight;

   @JsonProperty(LAYOUT_SIZE_WIDTH)
   Double layoutSizeWidth;

   @JsonProperty(OPEN_NOTES)
   List<String> openNotes;

   @JsonProperty(SELECTED_NOTE)
   String selectedNote;

   @JsonCreator
   public static State apply(
      @JsonProperty(LAYOUT_DIVIDER_POSITION) Double layoutDividerPosition,
      @JsonProperty(LAYOUT_FULLSCREEN) Boolean layoutFullscreen,
      @JsonProperty(LAYOUT_SIZE_HEIGHT) Double layoutSizeHeight,
      @JsonProperty(LAYOUT_SIZE_WIDTH) Double layoutSizeWidth,
      @JsonProperty(OPEN_NOTES) List<String> openNotes,
      @JsonProperty(SELECTED_NOTE) String selectedNote) {

      if (layoutDividerPosition == null) layoutDividerPosition = 0.3;
      if (layoutFullscreen == null) layoutFullscreen = false;
      if (layoutSizeHeight == null) layoutSizeHeight = 600.0;
      if (layoutSizeWidth == null) layoutSizeWidth = 800.0;

      return new State(
         layoutDividerPosition, layoutFullscreen, layoutSizeHeight,
         layoutSizeWidth, openNotes, selectedNote);
   }

   public static State apply() {
      return apply(null, null, null, null, List.of(), null);
   }

   public Optional<String> getSelectedNote() {
      return Optional.ofNullable(selectedNote);
   }

}

