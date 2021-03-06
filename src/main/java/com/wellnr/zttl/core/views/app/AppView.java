package com.wellnr.zttl.core.views.app;

import com.wellnr.zttl.core.components.*;
import com.wellnr.zttl.core.model.Settings;
import com.wellnr.zttl.core.ports.NotesRepository;
import com.wellnr.zttl.core.views.app.model.App;
import com.wellnr.zttl.core.views.app.model.Note;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AppView extends BorderPane {

   private final TabPane tabPane;

   private final App model;

   private final Stage primaryStage;

   private final Consumer<Settings> onSaveSettings;

   public final DoubleProperty layoutDividerPosition;

   public final ObjectProperty<List<Double>> layoutNotesBrowserDividerPositions;

   public AppView(
      final App model,
      final NotesRepository notesRepository,
      final Stage primaryStage,
      final BiConsumer<Note, String> onAddTagToNote,
      final Consumer<Optional<Note>> onNoteChanged,
      final BiConsumer<Note, Event> onNoteCloseRequest,
      final Consumer<Note> onNoteClosed,
      final Consumer<Note> onOpenNote,
      final Consumer<String> onOpenNoteById,
      final BiConsumer<Note, String> onRemoveTagFromNote,
      final Runnable onAbout,
      final Runnable onClose,
      final Runnable onCloseAll,
      final Runnable onDelete,
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
      this.layoutDividerPosition = new SimpleDoubleProperty();
      this.layoutNotesBrowserDividerPositions = new SimpleObjectProperty<>(List.of());

      SplitPane sp = new SplitPane();
      this.setCenter(sp);

      {
         AppMenu menu = new AppMenu(
            model, onAbout, onClose, onCloseAll, onDelete,
            onMoveToArchive, onMoveToInbox, onNew,
            onSave, onSaveAll,
            onSettings, onQuit);

         this.setTop(menu);
      }

      {
         VBox container = new VBox();
         container.getStyleClass().addAll("zttl--sidepanel");
         container.setPrefWidth(200);

         NoteBrowser noteBrowser = new NoteBrowser(model, onOpenNote, layoutNotesBrowserDividerPositions);

         TabPane tabPane = new TabPane();
         tabPane.getStyleClass().add("zttl--sidepanel");
         tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
         tabPane.getTabs().add(new Tab("BROWSE", noteBrowser));
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
                  note, notesRepository, model.getKnownTags(), onNoteClosed, onNoteCloseRequest,
                  onAddTagToNote, onOpenNoteById, onRemoveTagFromNote);

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
         sp.getStyleClass().add("zttl--split-pane");
         this.layoutDividerPosition.bindBidirectional(sp.getDividers().get(0).positionProperty());
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
