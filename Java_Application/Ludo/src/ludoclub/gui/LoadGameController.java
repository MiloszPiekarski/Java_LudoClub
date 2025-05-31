package ludoclub.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ludoclub.model.Game;

public class LoadGameController {
    public void start(Stage stage) {
        VBox root = new VBox(26);
        root.setStyle("-fx-padding:60; -fx-alignment:center;");

        Button loadBtn = new Button("Wczytaj ostatnią grę");
        loadBtn.setPrefWidth(220);
        loadBtn.setOnAction(e -> {
            Game g = loadGame();
            if (g != null) {
                new ludoclub.gui.GameViewController(g).start(stage);
            }
        });

        Button backBtn = new Button("Wróć do menu");
        backBtn.setPrefWidth(220);
        backBtn.setOnAction(e -> new ludoclub.gui.MainMenuController().start(stage));

        root.getChildren().addAll(loadBtn, backBtn);
        Scene scene = new Scene(root, 400, 200);
        scene.getStylesheets().add(getClass().getResource("/ludoclub/gui/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Ludo Club - Menu Główne");
        stage.show();
    }

    private Game loadGame() {
        try (var in = new java.io.ObjectInputStream(
                new java.io.FileInputStream("savegame.ser"))) {
            return (Game) in.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
