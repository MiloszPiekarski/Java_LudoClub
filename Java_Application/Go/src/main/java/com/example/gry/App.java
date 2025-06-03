package com.example.gry;

import com.example.gry.go.gui.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gry/go/gui/views/main.fxml"));
        Scene scene = new Scene(loader.load(), 900, 700);

        stage.setTitle("Gra w Go");
        stage.setScene(scene);

        // Obsługa zamknięcia okna
        stage.setOnCloseRequest(event -> {
            event.consume(); // Zablokuj domyślne zamknięcie
            MainController controller = loader.getController();
            controller.handleExit(); // Wywołaj naszą metodę
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}