package com.wellnr.zttl.core.components;

import com.wellnr.zttl.common.ScheduledTask;
import com.wellnr.zttl.common.events.ActionHandlerProperty;
import com.wellnr.zttl.common.events.SubscribableAction;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.fxmisc.easybind.EasyBind;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


public class SearchReplaceBox extends HBox {

   public final BooleanProperty matchCaseProperty;

   public final IntegerProperty matchedCasesProperty;

   public final IntegerProperty currentMatchProperty;

   public final BooleanProperty replaceVisibleProperty;

   public final StringProperty searchTextProperty;

   public final StringProperty replaceTextProperty;

   private final ActionHandlerProperty onClearSearchProperty;

   private final ActionHandlerProperty onCloseProperty;

   private final ActionHandlerProperty onGotoNextProperty;

   private final ActionHandlerProperty onGotoPrevProperty;

   private final ActionHandlerProperty onReplaceProperty;

   private final ActionHandlerProperty onReplaceAllProperty;

   private final ActionHandlerProperty onSearchProperty;

   private final TextField fldSearch;

   private final TextField fldReplace;

   private ScheduledTask emitSearch;

   public SearchReplaceBox() {

      this.currentMatchProperty = new SimpleIntegerProperty(0);
      this.replaceVisibleProperty = new SimpleBooleanProperty(false);
      this.matchCaseProperty = new SimpleBooleanProperty(false);
      this.matchedCasesProperty = new SimpleIntegerProperty(0);
      this.searchTextProperty = new SimpleStringProperty();
      this.replaceTextProperty = new SimpleStringProperty();

      this.onClearSearchProperty = new ActionHandlerProperty();
      this.onCloseProperty = new ActionHandlerProperty();
      this.onGotoNextProperty = new ActionHandlerProperty();
      this.onGotoPrevProperty = new ActionHandlerProperty();
      this.onReplaceProperty = new ActionHandlerProperty();
      this.onReplaceAllProperty = new ActionHandlerProperty();
      this.onSearchProperty = new ActionHandlerProperty();
      this.emitSearch = null;

      this.searchTextProperty.addListener((observable, oldValue, newValue) -> {
         if (emitSearch != null) {
            emitSearch.cancel();
         }

         if (!newValue.isBlank()) {
            emitSearch = ScheduledTask.schedule(
               () -> Platform.runLater(onSearchProperty::emit),
               Duration.of(300, ChronoUnit.MILLIS));
         } else {
            emitSearch = ScheduledTask.schedule(
               () -> Platform.runLater(onClearSearchProperty::emit),
               Duration.of(300, ChronoUnit.MILLIS));
         }
      });

      {
         /*
          * Build View
          */
         this.fldSearch = new TextField();
         this.fldReplace = new TextField();

         this.fldSearch.getStyleClass().add("zttl--search-replace--textfield");
         this.fldSearch.setPromptText("Search");
         this.fldSearch.textProperty().bindBidirectional(searchTextProperty);
         this.fldSearch.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
               this.onCloseProperty.emit();
               event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
               this.onGotoNextProperty.emit();
               event.consume();
            } else if (new KeyCodeCombination(KeyCode.R, KeyCodeCombination.META_DOWN).match(event)) {
               this.replaceVisibleProperty.setValue(!this.replaceVisibleProperty.get());

               if (this.replaceVisibleProperty.get()) {
                  Platform.runLater(fldReplace::requestFocus);
               }

               event.consume();
            }
         });

         Label result = new Label("3 matches");
         result.getStyleClass().add("zttl--search-replace--label");
         result.textProperty().bind(
            EasyBind.combine(matchedCasesProperty, currentMatchProperty, (count, current) -> {
               if (count.intValue() > 0) {
                  return (current.intValue() + 1) + "/" + count.intValue();
               } else {
                  return "No results";
               }
            }));



         Button bttNext = new Button(">");
         bttNext.getStyleClass().addAll("zttl--search-replace--button");
         bttNext.disableProperty().bind(EasyBind.map(matchedCasesProperty, i -> i.intValue() == 0));
         bttNext.setOnAction(event -> this.onGotoNextProperty.emit());

         Button bttPrev = new Button("<");
         bttPrev.getStyleClass().addAll("zttl--search-replace--button");
         bttPrev.disableProperty().bind(EasyBind.map(matchedCasesProperty, i -> i.intValue() == 0));
         bttPrev.setOnAction(event -> this.onGotoPrevProperty.emit());

         Button bttMatchCase = new Button("Match Case");
         bttMatchCase.getStyleClass().addAll("zttl--search-replace--button");

         bttMatchCase.setOnAction(event -> {
            this.matchCaseProperty.set(!this.matchCaseProperty.getValue());
            this.onSearchProperty.emit();
         });

         matchCaseProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
               bttMatchCase.getStyleClass().add("active");
            } else {
               bttMatchCase.getStyleClass().remove("active");
            }
         });

         HBox bttnsSearch = new HBox(5);
         bttnsSearch.getStyleClass().add("zttl--search-replace--buttons");
         bttnsSearch.getChildren().addAll(bttPrev, bttNext, bttMatchCase, result);

         BorderPane boxSearch = new BorderPane();
         boxSearch.setCenter(fldSearch);
         boxSearch.setRight(bttnsSearch);

         this.fldReplace.getStyleClass().add("zttl--search-replace--textfield");
         this.fldReplace.setPromptText("Replace");
         this.fldReplace.textProperty().bindBidirectional(this.replaceTextProperty);
         this.fldReplace.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
               this.onCloseProperty.emit();
               event.consume();
            } else if (event.getCode() == KeyCode.ENTER) {
               this.onReplaceProperty.emit();
               this.onGotoNextProperty.emit();

               ScheduledTask.schedule(
                  () -> Platform.runLater(this.fldReplace::requestFocus),
                  Duration.of(300, ChronoUnit.MILLIS));

               event.consume();
            }
         });

         Button bttReplace = new Button("Replace");
         bttReplace.getStyleClass().addAll("zttl--search-replace--button");
         bttReplace.setOnAction(event -> this.onReplaceProperty.emit());

         Button bttReplaceAll = new Button("Replace All");
         bttReplaceAll.getStyleClass().addAll("zttl--search-replace--button");
         bttReplaceAll.setOnAction(event -> this.onReplaceAllProperty.emit());

         HBox bttnsReplace = new HBox(5);
         bttnsReplace.getStyleClass().add("zttl--search-replace--buttons");
         bttnsReplace.getChildren().addAll(bttReplace, bttReplaceAll);

         BorderPane boxReplace = new BorderPane();
         boxReplace.setCenter(this.fldReplace);
         boxReplace.setRight(bttnsReplace);

         HBox.setHgrow(boxSearch, Priority.ALWAYS);
         HBox.setHgrow(boxReplace, Priority.ALWAYS);

         this.getChildren().addAll(boxSearch);
         this.setSpacing(5);
         this.getStyleClass().add("zttl--search-replace");

         this.replaceVisibleProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
               this.getChildren().add(boxReplace);
            } else {
               this.getChildren().remove(boxReplace);
            }
         });

         this.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
               this.onCloseProperty.emit();
               event.consume();
            }
         });
      }
   }

   public void clear() {
      this.searchTextProperty.set("");
      this.replaceTextProperty.set("");
      this.matchCaseProperty.setValue(false);
      this.replaceVisibleProperty.setValue(false);
      this.matchedCasesProperty.set(0);
      this.currentMatchProperty.set(0);
   }

   public SubscribableAction onClearSearch() {
      return this.onClearSearchProperty;
   }

   public SubscribableAction onClose() {
      return this.onCloseProperty;
   }

   public SubscribableAction onGotoNext() {
      return this.onGotoNextProperty;
   }

   public SubscribableAction onGotoPrev() {
      return this.onGotoPrevProperty;
   }

   public SubscribableAction onReplace() {
      return this.onReplaceProperty;
   }

   public SubscribableAction onReplaceAll() {
      return this.onReplaceAllProperty;
   }

   public SubscribableAction onSearch() {
      return this.onSearchProperty;
   }

   public void requestFocus() {
      this.fldSearch.requestFocus();
   }

   public void requestFocus(boolean replace) {
      if (replace) {
         this.fldReplace.requestFocus();
      } else {
         requestFocus();
      }
   }

}
