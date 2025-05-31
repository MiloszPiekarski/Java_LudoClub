package ludoclub;

import javafx.application.Application;
import javafx.stage.Stage;
import ludoclub.model.DBManager;
import ludoclub.gui.MainMenuController;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        DBManager.createTables();       // jeśli używasz bazy
        new MainMenuController().start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
