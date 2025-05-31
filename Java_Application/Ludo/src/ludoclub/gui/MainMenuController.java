package ludoclub.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class MainMenuController {
    public void start(Stage stage) {
        VBox root = new VBox(24);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 60 0 0 0;");

        // LOGO GRAFICZNE
        Image logoImg = new Image(getClass().getResource("/ludoclub/gui/logo.png").toExternalForm());
        ImageView logoView = new ImageView(logoImg);
        logoView.setFitWidth(150);
        logoView.setPreserveRatio(true);

        // LOGO TEKSTOWE
        Label logo = new Label("Ludo Club");
        logo.getStyleClass().add("title");

        Button newGameBtn = new Button("Nowa Gra");
        Button loadGameBtn = new Button("Wczytaj Grę");
        Button leaderboardBtn = new Button("Tablica wyników");
        Button exitBtn = new Button("Wyjdź");

        newGameBtn.setPrefWidth(220);
        loadGameBtn.setPrefWidth(220);
        leaderboardBtn.setPrefWidth(220);
        exitBtn.setPrefWidth(220);

        newGameBtn.setOnAction(e -> new ludoclub.gui.NewGameController().start(stage));
        loadGameBtn.setOnAction(e -> new ludoclub.gui.LoadGameController().start(stage));
        exitBtn.setOnAction(e -> stage.close());

        // NOWOŚĆ: obsługa Tablicy Wyników
        leaderboardBtn.setOnAction(e -> {
            List<String> wyniki = ludoclub.model.DBManager.getLastResults(20);
            String msg = wyniki.isEmpty() ? "Brak wyników." : String.join("\n", wyniki);
            Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
            a.setTitle("Tablica wyników");
            a.setHeaderText("Ostatnie 20 gier");
            a.showAndWait();
        });

        VBox menuBox = new VBox(18, newGameBtn, loadGameBtn, leaderboardBtn, exitBtn);
        menuBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(logoView, logo, menuBox);

        Scene scene = new Scene(root, 600, 600);
        scene.getStylesheets().add(getClass().getResource("/ludoclub/gui/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Ludo Club - Menu Główne");
        stage.show();
    }
}