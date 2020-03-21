package com.wellnr.zttl.core.components;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Tags extends FlowPane {

   private final AutoCompleteTextField fldTags;

   private final ObservableSet<String> knownTags;

   private final ObservableSet<String> tags;

   private final Consumer<String> onAddTag;

   private final Consumer<String> onRemoveTag;

   public Tags(
      ObservableSet<String> tags,
      ObservableSet<String> knownTags,
      Consumer<String> onRemoveTag,
      Consumer<String> onAddTag) {

      this.knownTags = knownTags;
      this.tags = tags;
      this.onAddTag = onAddTag;
      this.onRemoveTag = onRemoveTag;

      this.knownTags.addListener((InvalidationListener) o -> updateAvailableTags());

      this.tags.addListener(this::onTagsChanged);
      List<TagLabel> tagLabel = this.tags
         .stream()
         .map(label -> new TagLabel(label, onRemoveTag))
         .collect(Collectors.toList());

      this.fldTags = new AutoCompleteTextField("Add new Tag ...");
      this.fldTags.getStyleClass().add("zttl--tags--textfield");
      this.fldTags.setOnKeyPressed(this::onKeyPressed);
      this.fldTags.getFocusedProperty().addListener(this::onFocusChanged);

      this.getChildren().addAll(tagLabel);
      this.getChildren().add(fldTags);
      this.getStyleClass().add("zttl--tags");

      updateAvailableTags();
   }

   private void updateAvailableTags() {
      fldTags.getEntries().clear();
      fldTags.getEntries().addAll(knownTags);
      tags.forEach(fldTags.getEntries()::remove);
   }

   private void onFocusChanged(ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) {
      if (!newValue) {
         fldTags.setText("");
      }
   }

   private void onKeyPressed(KeyEvent event) {
      if (event.getCode() == KeyCode.ENTER) {
         event.consume();

         if (!fldTags.getText().isBlank() && !tags.contains(fldTags.getText())) {
            onAddTag.accept(fldTags.getText());
            fldTags.setText("");
         }
      }
   }

   private void onTagsChanged(SetChangeListener.Change<? extends String> c) {
      if (c.getElementAdded() != null) {
         this
            .getChildren()
            .add(this.getChildren().size() - 1, new TagLabel(c.getElementAdded(), onRemoveTag));
      }

      if (c.getElementRemoved() != null) {
          this
             .getChildren()
             .stream()
             .filter(node -> node instanceof TagLabel && ((TagLabel) node).getLabel().equals(c.getElementRemoved()))
             .findFirst()
             .ifPresent(this.getChildren()::remove);
      }

      updateAvailableTags();
   }

}
