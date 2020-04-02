package com.wellnr.zttl.core.components;

import impl.org.controlsfx.skin.AutoCompletePopup;
import impl.org.controlsfx.skin.AutoCompletePopupSkin;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import org.fxmisc.richtext.CodeArea;

import javax.swing.*;
import java.util.Collection;
import java.util.function.BiFunction;

public class AutoCompletionCodeAreaBind extends Popup {

   CodeArea codeArea;
   BiFunction<String, Integer, Collection<AutoCompletionSuggestion>> suggestionProvider;
   BiFunction<String, Integer, IndexRange> replaceTextProvider;
   AutoCompletePopup<AutoCompletionSuggestion> popup;

   public AutoCompletionCodeAreaBind(
      CodeArea codeArea,
      BiFunction<String, Integer, Collection<AutoCompletionSuggestion>> suggestionProvider,
      BiFunction<String, Integer, IndexRange> replaceTextProvider) {

      this.codeArea = codeArea;
      this.suggestionProvider = suggestionProvider;
      this.replaceTextProvider = replaceTextProvider;
      this.popup = new AutoCompletePopup<>();
      this.popup.getStyleClass().add("zttl--auto-complete-popup");

      codeArea.textProperty().addListener((obs, oldText, newText) -> {
         if (codeArea.isFocused()) {
            showPopup();
         }
      });

      codeArea.focusedProperty().addListener((obs, oldFocused, newFocused) -> {
         if (!newFocused) {
            hidePopup();
         }
      });

      codeArea.setOnMouseClicked(event -> hidePopup());

      popup.setOnSuggestion(sce -> {
         completeUserInput(sce.getSuggestion().getReplaceText());
         hidePopup();
      });
   }

   public void showPopup() {
      Collection<AutoCompletionSuggestion> suggestions = suggestionProvider.apply(codeArea.getText(), codeArea.getCaretPosition());

      if (suggestions.isEmpty()) {
         hidePopup();
      } else {
         popup.getSuggestions().setAll(suggestions);
         selectFirstSuggestion(popup);

         this.codeArea.getCaretBounds().ifPresent(bounds -> {
            if (!popup.isShowing()) {
               popup.show(this.codeArea, bounds.getMinX(), bounds.getMaxY());
            }
         });
      }
   }

   public void hidePopup() {
      popup.hide();
   }

   private void completeUserInput(String suggestion) {
      IndexRange range = replaceTextProvider.apply(codeArea.getText(), codeArea.getCaretPosition());
      codeArea.deleteText(range);
      codeArea.insertText(range.getStart(), suggestion);
      codeArea.moveTo(range.getStart() + suggestion.length());
   }

   private static void selectFirstSuggestion(AutoCompletePopup<?> autoCompletionPopup) {
      Skin<?> skin = autoCompletionPopup.getSkin();
      if (skin instanceof AutoCompletePopupSkin) {
         AutoCompletePopupSkin<?> au = (AutoCompletePopupSkin<?>) skin;
         ListView<?> li = (ListView<?>) au.getNode();
         if (li.getItems() != null && !li.getItems().isEmpty()) {
            li.getSelectionModel().select(0);
         }
      }
   }

}
