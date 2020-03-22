package com.wellnr.zttl.core.views.app;

import com.wellnr.zttl.core.components.*;
import com.wellnr.zttl.core.model.Settings;
import com.wellnr.zttl.core.views.app.model.App;
import com.wellnr.zttl.core.views.app.model.Note;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AppView extends BorderPane {

   private final TabPane tabPane;

   private final App model;

   private final Stage primaryStage;

   private final Consumer<Settings> onSaveSettings;

   public AppView(
      final App model,
      final Stage primaryStage,
      final BiConsumer<Note, String> onAddTagToNote,
      final Consumer<Optional<Note>> onNoteChanged,
      final BiConsumer<Note, Event> onNoteCloseRequest,
      final Consumer<Note> onNoteClosed,
      final Consumer<Note> onOpenNote,
      final BiConsumer<Note, String> onRemoveTagFromNote,
      final Runnable onAbout,
      final Runnable onClose,
      final Runnable onCloseAll,
      final Runnable onMoveToArchive,
      final Runnable onMoveToInbox,
      final Runnable onNew,
      final Runnable onSave,
      final Runnable onSaveAll,
      final Runnable onSettings,
      final Consumer<Settings> onSaveSettings,
      final Runnable onQuit) {

      super();
      this.model = model;
      this.primaryStage = primaryStage;
      this.onSaveSettings = onSaveSettings;

      SplitPane sp = new SplitPane();
      this.setCenter(sp);

      {
         AppMenu menu = new AppMenu(
            model, onAbout, onClose, onCloseAll,
            onMoveToArchive, onMoveToInbox, onNew,
            onSave, onSaveAll,
            onSettings, onQuit);

         this.setTop(menu);
      }

      {
         VBox container = new VBox();
         container.getStyleClass().addAll("zttl--sidepanel");
         container.setPrefWidth(200);

         NoteBrowser noteBrowser = new NoteBrowser(model, onOpenNote);

         TabPane tabPane = new TabPane();
         tabPane.getStyleClass().add("zttl--sidepanel");
         tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
         tabPane.getTabs().add(new Tab("BROWSE ZTTLS", noteBrowser));
         sp.getItems().add(tabPane);
      }

      {
         this.tabPane = new TabPane();
         this.tabPane.getStyleClass().add("zttl--tabs");
         this.tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

         this.tabPane.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
               if (newValue instanceof NoteTab) {
                  onNoteChanged.accept(Optional.of(((NoteTab) newValue).getNote()));
               } else {
                  onNoteChanged.accept(Optional.empty());
               }
            });

         model.getOpenNotes().addListener((ListChangeListener<Note>) c -> {
            c.next();

            c.getAddedSubList().forEach(note -> {
               NoteTab tab = new NoteTab(
                  note, model.getKnownTags(), onNoteClosed, onNoteCloseRequest,
                  onAddTagToNote, onRemoveTagFromNote);

               this.tabPane.getTabs().add(tab);
               this.tabPane.getSelectionModel().select(tab);
            });

            c.getRemoved().forEach(note -> tabPane
               .getTabs()
               .stream()
               .filter(tab -> tab instanceof NoteTab && ((NoteTab) tab).getNote().equals(note))
               .findFirst()
               .ifPresent(tab -> tabPane.getTabs().remove(tab)));
         });

         model.getCurrentNote().addListener((observable, oldValue, newValue) -> newValue
            .flatMap(
               note -> tabPane
                  .getTabs()
                  .stream()
                  .filter(tab -> tab instanceof NoteTab && ((NoteTab) tab).getNote().equals(note))
                  .findFirst())
            .ifPresent(tab -> tabPane.getSelectionModel().select(tab)));

         sp.getItems().add(tabPane);
      }

      {
         StatusBar s = new StatusBar(model.getNoteCount(), model.getWordCount());
         this.setBottom(s);
      }

      {
         sp.setDividerPositions(0.3);
         sp.getStyleClass().add("zttl--split-pane");
         sp.getDividers().forEach(divider -> divider.positionProperty().addListener(System.out::println));
      }

      this.getStyleClass().addAll("zttl--container");
      this.setPrefWidth(800);
      this.setPrefHeight(600);
   }

   public void showSettings() {
      tabPane
         .getTabs()
         .stream()
         .filter(tab -> tab instanceof SettingsTab)
         .findFirst()
         .ifPresentOrElse(
            tabPane.getSelectionModel()::select,
            () -> {
               var tab = new SettingsTab(model.getSettings(), primaryStage, onSaveSettings, this::hideSettings);
               tabPane.getTabs().add(tab);
               tabPane.getSelectionModel().select(tab);
            });
   }

   public void hideSettings() {
      tabPane
         .getTabs()
         .stream()
         .filter(tab -> tab instanceof SettingsTab)
         .forEach(tabPane.getTabs()::remove);
   }

}
