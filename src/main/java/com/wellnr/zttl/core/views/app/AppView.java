package com.wellnr.zttl.core.views.app;

import com.wellnr.zttl.core.components.NoteBrowser;
import com.wellnr.zttl.core.components.NoteTab;
import com.wellnr.zttl.core.components.StatusBar;
import com.wellnr.zttl.core.views.app.model.App;
import com.wellnr.zttl.core.views.app.model.Note;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AppView extends BorderPane {

   public AppView(
      final App model,
      final BiConsumer<Note, String> onAddTagToNote,
      final Consumer<Note> onNoteChanged,
      final Consumer<Note> onNoteClosed,
      final Consumer<Note> onOpenNote,
      final BiConsumer<Note, String> onRemoveTagFromNote) {

      super();

      SplitPane sp = new SplitPane();
      this.setCenter(sp);

      {
         MenuItem setWorkingDir = new MenuItem("Set Working Directory ...");
         MenuItem about = new MenuItem("About");

         Menu mainMenu = new Menu("zttl");
         mainMenu.getItems().addAll(setWorkingDir, about);

         MenuBar menu = new MenuBar(mainMenu);
         menu.getStyleClass().add("zttl--menu");

         String os = System.getProperty("os.name");
         if (os != null && os.startsWith("Mac")) menu.useSystemMenuBarProperty().set(true);

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
         TabPane tabPane = new TabPane();
         tabPane.getStyleClass().add("zttl--tabs");
         tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

         tabPane.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
               if (newValue instanceof NoteTab) {
                  onNoteChanged.accept(((NoteTab) newValue).getNote());
               }
            });

         model.getOpenNotes().addListener((ListChangeListener<Note>) c -> {
            c.next();

            c.getAddedSubList().forEach(note -> {
               NoteTab tab = new NoteTab(note, model.getKnownTags(), onNoteClosed, onAddTagToNote, onRemoveTagFromNote);
               tabPane.getTabs().add(tab);
               tabPane.getSelectionModel().select(tab);
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
         StatusBar s = new StatusBar(model.getWordCount());
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
}
