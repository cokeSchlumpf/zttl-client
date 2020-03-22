package com.wellnr.zttl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellnr.zttl.adapters.InMemoryNotesRepository;
import com.wellnr.zttl.adapters.InMemorySettingsRepository;
import com.wellnr.zttl.core.ports.NotesRepository;
import com.wellnr.zttl.core.ports.SettingsRepository;
import com.wellnr.zttl.core.views.app.AppController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            NotesRepository notesRepository = new InMemoryNotesRepository();
            SettingsRepository settingsRepository = new InMemorySettingsRepository();
            Scene scene = new Scene(new AppController(notesRepository, settingsRepository, primaryStage).getView());

            scene.getStylesheets().addAll(
                    "https://fonts.googleapis.com/css?family=IBM+Plex+Sans:400,500,600,700&display=swap",
                    "https://fonts.googleapis.com/css?family=IBM+Plex+Mono:300,400,500,600,700&display=swap",
                    "views/styles.css");

            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.DECORATED);
            primaryStage.setTitle("Zttl Notes");
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String ...args) {
        launch();
    }

}
