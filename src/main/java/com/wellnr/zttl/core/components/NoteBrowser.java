package com.wellnr.zttl.core.components;

import com.wellnr.zttl.core.views.app.model.App;
import com.wellnr.zttl.core.views.app.model.Note;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class NoteBrowser extends AnchorPane {

    public NoteBrowser(App model) {
        SplitPane sp = new SplitPane();
        sp.getStyleClass().add("zttl--note-browser--split-pane");
        sp.setOrientation(Orientation.VERTICAL);
        AnchorPane.setTopAnchor(sp, 0.0);
        AnchorPane.setBottomAnchor(sp, 0.0);
        AnchorPane.setRightAnchor(sp, 0.0);
        AnchorPane.setLeftAnchor(sp, 0.0);

        sp.getItems().add(renderSection("OPEN", model.getOpenNotes()));
        sp.getItems().add(renderSection("INBOX", model.getInboxNotes()));
        sp.getItems().add(renderSection("ARCHIVE", model.getArchivedNotes()));
        this.getStyleClass().add("zttl--note-browser");
        this.getChildren().add(sp);
    }

    private Node renderSection(String title, ObservableList<Note> notes) {
        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("zttl--note-browser--section--title");

        HBox titleContainer = new HBox();
        titleContainer.getStyleClass().add("zttl--note-browser--section--title");
        titleContainer.getChildren().add(lblTitle);

        VBox items = new VBox();
        items.getStyleClass().add("zttl--note-browser--section--items");
        notes.forEach(note -> {
            NoteItem item = new NoteItem(note);
            items.getChildren().add(item);
        });

        ScrollPane sp = new ScrollPane(items);
        sp.getStyleClass().add("zttl--note-browser--section--scroll-pane");
        sp.setFitToWidth(true);
        sp.setMinHeight(100);

        VBox vBox = new VBox(titleContainer, sp);
        vBox.getStyleClass().add("zttl--note-browser--section");
        return vBox;
    }

}
