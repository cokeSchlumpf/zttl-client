package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.App;
import com.wellnr.zttl.core.views.app.model.Note;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class NoteBrowser extends AnchorPane {

   private final StringProperty filter;

   public NoteBrowser(App model, Consumer<Note> onOpen) {
      this.filter = new SimpleStringProperty();

      SplitPane sp = new SplitPane();
      sp.getStyleClass().add("zttl--note-browser--split-pane");
      sp.setOrientation(Orientation.VERTICAL);
      AnchorPane.setTopAnchor(sp, 0.0);
      AnchorPane.setBottomAnchor(sp, 0.0);
      AnchorPane.setRightAnchor(sp, 0.0);
      AnchorPane.setLeftAnchor(sp, 0.0);

      TextField fldFilter = new TextField();
      fldFilter.textProperty().bindBidirectional(filter);
      fldFilter.setPromptText("Filter ...");
      fldFilter.getStyleClass().add("zttl--note-browser--filter");

      sp.getItems().add(renderSection("OPEN", model.getOpenNotes(), onOpen));
      sp.getItems().add(renderSection("INBOX", model.getInboxNotes(), onOpen));
      sp.getItems().add(renderSection("ARCHIVE", model.getArchivedNotes(), onOpen));
      sp.getItems().add(fldFilter);
      this.getStyleClass().add("zttl--note-browser");
      this.getChildren().add(sp);
   }

   private Node renderSection(String title, ObservableList<Note> notes, Consumer<Note> onOpen) {
      Label lblTitle = new Label(title);
      lblTitle.getStyleClass().add("zttl--note-browser--section--title");

      HBox titleContainer = new HBox();
      titleContainer.getStyleClass().add("zttl--note-browser--section--title");
      titleContainer.getChildren().add(lblTitle);

      VBox items = new VBox();
      items.getStyleClass().add("zttl--note-browser--section--items");
      notes.forEach(note -> {
         NoteItem item = new NoteItem(note, onOpen);
         items.getChildren().add(item);
      });

      notes.addListener((ListChangeListener<Note>) c -> {
         c.next();
         c.getAddedSubList().forEach(note -> {
            NoteItem item = new NoteItem(note, onOpen);
            items.getChildren().add(item);
         });

         c.getRemoved().forEach(note -> {
            items
               .getChildren()
               .stream()
               .filter(node -> node instanceof NoteItem && ((NoteItem) node).getNote().equals(note))
               .findFirst()
               .ifPresent(node -> items.getChildren().remove(node));
         });

         updateVisibilities(items);
      });

      filter.addListener(observable -> updateVisibilities(items));

      ScrollPane sp = new ScrollPane(items);
      sp.getStyleClass().add("zttl--note-browser--section--scroll-pane");
      sp.setFitToWidth(true);
      sp.setMinHeight(100);

      VBox vBox = new VBox(titleContainer, sp);
      vBox.getStyleClass().add("zttl--note-browser--section");
      return vBox;
   }

   private void updateVisibilities(VBox items) {
       items
          .getChildren()
          .stream()
          .filter(node -> node instanceof NoteItem)
          .forEach(node -> {
              var item = (NoteItem) node;
              var match = matchFilter(item);
              item.setVisible(match);
              item.setManaged(match);
          });
   }

   private boolean matchFilter(NoteItem note) {
       var search = filter.get().toLowerCase();

       if (search.isBlank()) {
           return true;
       } else {
           return note.getNote().getTitle().get().toLowerCase().contains(search) ||
              note.getNote().getContent().get().toLowerCase().contains(search) ||
              String.join(",", note.getNote().getTags()).toLowerCase().contains(search);
       }
   }

}
