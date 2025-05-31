package ludoclub.gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ludoclub.model.Player;
import ludoclub.model.ColorType;
import ludoclub.model.Pawn;
import ludoclub.model.Game;
import ludoclub.model.Bot;

/**
 * Kontroler ekranu konfiguracji nowej gry.
 * Tworzy 4 graczy (Player/Bot) z podanymi kolorami i nickami.
 * Dodaje każdego do Game, które samo utworzy dla niego pionki.
 */
public class NewGameController {

    private final Game game = new Game();
    private final java.util.List<TextField> playerNameFields = new java.util.ArrayList<>();
    private final java.util.List<ComboBox<String>> playerTypeBoxes = new java.util.ArrayList<>();

    // Tabele ColorType, indeksy odpowiadają graczom 1-4
    private static final ColorType[] COLORS = {
            ColorType.RED, ColorType.BLUE, ColorType.YELLOW, ColorType.GREEN
    };

    public void start(Stage stage) {
        VBox root = new VBox(16);
        root.setStyle("-fx-padding: 38; -fx-alignment: center;");

        Label title = new Label("Konfiguracja nowej gry LUDO CLUB");
        title.getStyleClass().add("subtitle");
        root.getChildren().add(title);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(12);

        for (int i = 0; i < 4; i++) {
            ComboBox<String> typeBox = new ComboBox<>();
            typeBox.getItems().addAll("Player", "Bot");
            typeBox.getSelectionModel().select(0);

            TextField nameField = new TextField();
            nameField.setPromptText("Nick gracza #" + (i + 1));
            nameField.getStyleClass().add("text-field");

            HBox colorIcon = new HBox();
            colorIcon.setAlignment(Pos.CENTER);
            Region colorBox = new Region();
            colorBox.setStyle("-fx-background-color: " + COLORS[i].getFxColor().toString().replace("0x", "#") +
                    "; -fx-background-radius: 50%; -fx-min-width:22; -fx-min-height:22;");
            colorIcon.getChildren().add(colorBox);

            grid.add(new Label("Gracz " + (i + 1) + ":"), 0, i);
            grid.add(colorIcon, 1, i);
            grid.add(typeBox, 2, i);
            grid.add(nameField, 3, i);

            playerTypeBoxes.add(typeBox);
            playerNameFields.add(nameField);
        }

        root.getChildren().add(grid);

        Button startBtn = new Button("Start Gry!");
        startBtn.setPrefWidth(200);
        startBtn.setOnAction(e -> {
            game.getPlayers().clear();

            for (int i = 0; i < 4; i++) {
                String type = playerTypeBoxes.get(i).getValue();
                String name = playerNameFields.get(i).getText();
                if (name == null || name.isBlank()) name = "Anonim" + (i + 1);

                Player player = "Bot".equals(type) ? new Bot(name, COLORS[i]) : new Player(name, COLORS[i]);
                game.addPlayer(player);
            }
            new ludoclub.gui.GameViewController(game).start(stage);
        });

        HBox bottom = new HBox(startBtn);
        bottom.setAlignment(Pos.CENTER);
        root.getChildren().add(bottom);

        Scene scene = new Scene(root, 660, 380);
        scene.getStylesheets().add(getClass().getResource("/ludoclub/gui/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Ludo Club - Nowa Gra");
        stage.show();
    }
}
