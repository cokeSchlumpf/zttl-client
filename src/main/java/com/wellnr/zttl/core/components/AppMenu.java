package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.App;
import javafx.beans.InvalidationListener;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

public class AppMenu extends MenuBar {

   public AppMenu(
      App model,
      Runnable onAbout,
      Runnable onClose,
      Runnable onCloseAll,
      Runnable onMoveToArchive,
      Runnable onMoveToInbox,
      Runnable onNew,
      Runnable onSave,
      Runnable onSaveAll,
      Runnable onSettings,
      Runnable onQuit) {

      {
         var noteDisabled = model.getCurrentNote().get().isEmpty();
         var notesDisabled = model.getOpenNotes().isEmpty();

         MenuItem newNote = new MenuItem("New note");
         newNote.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCodeCombination.META_DOWN));
         newNote.setOnAction(event -> onNew.run());

         MenuItem save = new MenuItem("Save note");
         save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.META_DOWN));
         save.setOnAction(event -> onSave.run());
         save.setDisable(noteDisabled);

         MenuItem close = new MenuItem("Close note");
         close.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCodeCombination.META_DOWN));
         close.setOnAction(event -> onClose.run());
         close.setDisable(noteDisabled);

         MenuItem moveToArchive = new MenuItem("Move to archive");
         moveToArchive.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCodeCombination.META_DOWN, KeyCodeCombination.SHIFT_DOWN));
         moveToArchive.setOnAction(event -> onMoveToArchive.run());
         moveToArchive.setDisable(noteDisabled);

         MenuItem moveToInbox = new MenuItem("Move to inbox");
         moveToInbox.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCodeCombination.META_DOWN, KeyCodeCombination.SHIFT_DOWN));
         moveToInbox.setOnAction(event -> onMoveToInbox.run());
         moveToInbox.setDisable(noteDisabled);

         model.getCurrentNote().addListener(observable -> {
            var disabled = model.getCurrentNote().get().isEmpty();
            save.setDisable(disabled);
            close.setDisable(disabled);
            moveToArchive.setDisable(disabled);
            moveToInbox.setDisable(disabled);
         });

         MenuItem saveAllNotes = new MenuItem("Save all notes");
         saveAllNotes.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.META_DOWN, KeyCodeCombination.SHIFT_DOWN));
         saveAllNotes.setOnAction(event -> onSaveAll.run());
         saveAllNotes.setDisable(notesDisabled);

         MenuItem closeAllNotes = new MenuItem("Close all notes");
         closeAllNotes.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCodeCombination.META_DOWN, KeyCodeCombination.SHIFT_DOWN));
         closeAllNotes.setOnAction(event -> onCloseAll.run());
         closeAllNotes.setDisable(notesDisabled);

         model.getOpenNotes().addListener((InvalidationListener) observable -> {
            var disabled = model.getOpenNotes().isEmpty();
            saveAllNotes.setDisable(disabled);
            closeAllNotes.setDisable(disabled);
         });

         Menu noteMenu = new Menu("Notes");
         noteMenu.getItems().addAll(
            newNote, new SeparatorMenuItem(),
            save, saveAllNotes, new SeparatorMenuItem(),
            close, closeAllNotes, new SeparatorMenuItem(),
            moveToArchive, moveToInbox);

         this.getMenus().add(noteMenu);
      }

      {
         MenuItem aboutMenuItem = new MenuItem("About Zettels");
         aboutMenuItem.setOnAction(event -> onAbout.run());

         MenuItem settingsMenuItem = new MenuItem("Settings ...");
         settingsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCodeCombination.META_DOWN));
         settingsMenuItem.setOnAction(event -> onSettings.run());

         MenuItem quitMenuItem = new MenuItem("Quit Zettels");
         quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCodeCombination.META_DOWN));
         quitMenuItem.setOnAction(event -> onQuit.run());

         Menu mainMenu = new Menu("Help");
         mainMenu.getItems().addAll(aboutMenuItem, settingsMenuItem, new SeparatorMenuItem(), quitMenuItem);

         var os = System.getProperty("os.name");
         var isMacOS = os != null && os.startsWith("Mac");

         if (isMacOS) {
            this.useSystemMenuBarProperty().set(true);
            this.getMenus().add(mainMenu);
         } else {
            this.getMenus().add(mainMenu);
         }
      }

      this.getStyleClass().add("zttl--menu");


   }

}
