package com.wellnr.zttl;

import com.wellnr.zttl.adapters.FilesystemNotesRepository;
import com.wellnr.zttl.adapters.UserHomeDirSettingsRepository;
import com.wellnr.zttl.core.ports.NotesRepository;
import com.wellnr.zttl.core.ports.SettingsRepository;
import com.wellnr.zttl.core.views.app.AppController;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Application extends javafx.application.Application {

    private static Application INSTANCE;

    public static Application getInstance() {
        return INSTANCE;
    }

    @Override
    public void start(Stage primaryStage) {
        INSTANCE = this;

        try {
            SettingsRepository settingsRepository = UserHomeDirSettingsRepository.create();
            NotesRepository notesRepository = FilesystemNotesRepository.apply(settingsRepository.getSettings().getWorkDirectory());
            AppController controller = new AppController(notesRepository, settingsRepository, primaryStage);

            Scene scene = new Scene(controller.getView());

            scene.getStylesheets().addAll(
                    "https://fonts.googleapis.com/css?family=IBM+Plex+Sans:400,500,600,700&display=swap",
                    "https://fonts.googleapis.com/css?family=IBM+Plex+Mono:300,400,500,600,700&display=swap",
                    "views/styles.css");

            primaryStage.setScene(scene);
            controller.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String ...args) {
        launch();
    }

}
