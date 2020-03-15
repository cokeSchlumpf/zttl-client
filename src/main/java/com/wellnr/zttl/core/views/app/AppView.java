package com.wellnr.zttl.core.views.app;

import com.wellnr.zttl.core.components.NoteItem;
import com.wellnr.zttl.core.components.NoteTab;
import com.wellnr.zttl.core.components.NoteBrowser;
import com.wellnr.zttl.core.components.StatusBar;
import com.wellnr.zttl.core.views.app.model.Note;
import com.wellnr.zttl.core.views.app.model.App;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class AppView extends BorderPane {

    public AppView(
            final App model,
            final Consumer<Note> onNoteChanged,
            final Consumer<Note> onNoteClosed) {

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

            NoteBrowser noteBrowser = new NoteBrowser(model);

            TabPane tabPane = new TabPane();
            tabPane.getStyleClass().add("zttl--sidepanel");
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            tabPane.getTabs().add(new Tab("BROWSE ZTTLS", noteBrowser));
            sp.getItems().add(tabPane);
        }

        {
            TabPane tabPane = new TabPane();
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

            tabPane.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        if (newValue instanceof NoteTab) {
                            onNoteChanged.accept(((NoteTab) newValue).getNote());
                        }
                    });

            model.getOpenNotes().forEach(note -> {
                tabPane.getTabs().add(new NoteTab(note, onNoteClosed));
            });

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
