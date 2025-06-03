package com.example.gry.go.gui.controllers;

import com.example.gry.go.Go;
import com.example.gry.go.board.Coordinate;
import com.example.gry.go.board.StateOfIntersection;
import com.example.gry.go.player.HumanPlayer;
import com.example.gry.go.player.Player;
import com.example.gry.go.player.PlayerColour;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML private Pane boardGrid;
    @FXML private VBox infoPanel;
    @FXML private Label currentPlayerLabel;
    @FXML private Label blackScoreLabel;
    @FXML private Label whiteScoreLabel;

    private Go goGame;
    private int boardSize = 9;
    private Canvas gridCanvas;
    private double stoneSize;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stoneSize = calculateStoneSize();
        startNewGame();

        boardGrid.widthProperty().addListener((obs, oldVal, newVal) -> resizeBoard());
        boardGrid.heightProperty().addListener((obs, oldVal, newVal) -> resizeBoard());
    }

    private double calculateStoneSize() {
        double minSize = 30.0;
        double availableWidth = boardGrid.getWidth() > 0 ? boardGrid.getWidth() : 600;
        double availableHeight = boardGrid.getHeight() > 0 ? boardGrid.getHeight() : 600;

        double widthBasedSize = availableWidth / (boardSize + 1);
        double heightBasedSize = availableHeight / (boardSize + 1);

        return Math.max(minSize, Math.min(widthBasedSize, heightBasedSize));
    }

    private void resizeBoard() {
        if (goGame != null && gridCanvas != null) {
            stoneSize = calculateStoneSize();
            redrawBoard();
        }
    }

    private void redrawBoard() {
        boardGrid.getChildren().clear();
        createBoard();
        updateGameInfo();
    }

    private void startNewGame() {
        HumanPlayer blackPlayer = new HumanPlayer("Gracz 1", PlayerColour.BLACK);
        HumanPlayer whitePlayer = new HumanPlayer("Gracz 2", PlayerColour.WHITE);

        goGame = new Go(boardSize, blackPlayer, whitePlayer);
        goGame.startGame();

        redrawBoard();
    }

    private void createBoard() {
        double boardWidth = stoneSize * boardSize;
        double boardHeight = stoneSize * boardSize;

        gridCanvas = new Canvas(boardWidth, boardHeight);
        drawGrid(gridCanvas);

        gridCanvas.setLayoutX((boardGrid.getWidth() - boardWidth) / 2);
        gridCanvas.setLayoutY((boardGrid.getHeight() - boardHeight) / 2);

        boardGrid.getChildren().add(gridCanvas);

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                createIntersection(row, col);
            }
        }
    }

    private void drawGrid(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#e6b800"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);

        for (int row = 0; row < boardSize; row++) {
            double y = row * stoneSize + stoneSize / 2;
            gc.strokeLine(
                    stoneSize / 2,
                    y,
                    canvas.getWidth() - stoneSize / 2,
                    y
            );
        }

        for (int col = 0; col < boardSize; col++) {
            double x = col * stoneSize + stoneSize / 2;
            gc.strokeLine(
                    x,
                    stoneSize / 2,
                    x,
                    canvas.getHeight() - stoneSize / 2
            );
        }

        if (boardSize == 9 || boardSize == 13 || boardSize == 19) {
            gc.setFill(Color.BLACK);
            int[] points = {2, boardSize / 2, boardSize - 3};
            if (boardSize == 9) points = new int[]{2, 4, 6};
            if (boardSize == 13) points = new int[]{3, 6, 9};

            for (int row : points) {
                for (int col : points) {
                    if (boardSize > 9 || (row != col)) {
                        double x = col * stoneSize + stoneSize / 2;
                        double y = row * stoneSize + stoneSize / 2;
                        gc.fillOval(x - 2, y - 2, 4, 4);
                    }
                }
            }
        }
    }

    private void createIntersection(int row, int col) {
        Pane stoneContainer = new Pane();
        stoneContainer.setPrefSize(stoneSize, stoneSize);
        stoneContainer.setMouseTransparent(true);

        stoneContainer.setLayoutX(gridCanvas.getLayoutX() + col * stoneSize);
        stoneContainer.setLayoutY(gridCanvas.getLayoutY() + row * stoneSize);

        stoneContainer.setId("stone_" + row + "_" + col);
        boardGrid.getChildren().add(stoneContainer);

        updateStoneView(row, col);
    }

    private void updateStoneView(int row, int col) {
        Pane container = null;

        for (Node node : boardGrid.getChildren()) {
            if (node.getId() != null && node.getId().equals("stone_" + row + "_" + col)) {
                container = (Pane) node;
                break;
            }
        }

        if (container == null) return;

        container.getChildren().clear();

        try {
            StateOfIntersection state = goGame.getBoard().getIntersection(row, col).getIntersectionState();

            if (state != StateOfIntersection.EMPTY) {
                Circle stone = new Circle(stoneSize / 2 - 1);
                stone.setCenterX(stoneSize / 2);
                stone.setCenterY(stoneSize / 2);
                stone.setStroke(Color.BLACK);

                if (state == StateOfIntersection.BLACK) {
                    stone.setFill(Color.BLACK);
                } else {
                    stone.setFill(Color.WHITE);
                }

                container.getChildren().add(stone);
            }
        } catch (Exception e) {
            System.err.println("Błąd aktualizacji kamienia " + row + "," + col + ": " + e.getMessage());
        }
    }

    private void updateAllStones() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                updateStoneView(row, col);
            }
        }
    }

    @FXML
    public void handleMove(MouseEvent event) {
        if (goGame == null || goGame.isGameOver()) {
            showGameOverAlert();
            return;
        }

        double clickX = event.getX() - gridCanvas.getLayoutX();
        double clickY = event.getY() - gridCanvas.getLayoutY();

        int col = (int) (clickX / stoneSize);
        int row = (int) (clickY / stoneSize);

        if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
            Player currentPlayer = goGame.getCurrentPlayer();

            if (currentPlayer instanceof HumanPlayer) {
                HumanPlayer humanPlayer = (HumanPlayer) currentPlayer;

                try {
                    humanPlayer.setNextMove(new Coordinate(row, col));
                    goGame.makeMove(humanPlayer, humanPlayer.provideMove(goGame.getBoard()));

                    updateAllStones();
                    updateGameInfo();

                    if (goGame.isGameOver()) {
                        showGameOverAlert();
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Nieprawidłowy ruch: " + e.getMessage());
                }
            }
        }
    }

    private void updateGameInfo() {
        if (goGame == null) return;

        if (goGame.isGameOver()) {
            currentPlayerLabel.setText("Gra zakończona!");
        } else {
            PlayerColour currentColour = goGame.getCurrentPlayer().getPlayerColour();
            currentPlayerLabel.setText("Aktualny gracz: " +
                    (currentColour == PlayerColour.BLACK ? "Czarne" : "Białe") +
                    " | Pasowania: " + goGame.getConsecutivePasses());
        }

        try {
            blackScoreLabel.setText("Czarne: " + goGame.getBlackScore());
            whiteScoreLabel.setText("Białe: " + String.format("%.1f", goGame.getWhiteScore()));
        } catch (Exception e) {
            System.err.println("Błąd obliczania punktów: " + e.getMessage());
        }
    }

    private void showGameOverAlert() {
        if (goGame == null || !goGame.isGameOver()) return;

        Player winner = goGame.getWinner();
        String winnerText = (winner != null)
                ? "Zwycięzca: " + winner.getNickname() + " (" + winner.getPlayerColour() + ")"
                : "Remis!";

        String scores = String.format(
                "Wyniki:\nCzarne: %d\nBiałe: %.1f (z komi)",
                goGame.getBlackScore(),
                goGame.getWhiteScore()
        );

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Koniec gry");
        alert.setHeaderText("Gra w Go zakończona!");
        alert.setContentText(winnerText + "\n\n" + scores);

        alert.showAndWait();
    }

    @FXML
    private void handlePass() {
        if (goGame == null) return;

        if (goGame.isGameOver()) {
            showGameOverAlert();
            return;
        }

        goGame.pass();
        updateGameInfo();

        if (goGame.isGameOver()) {
            showGameOverAlert();
        }
    }

    @FXML
    private void handleNewGame() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Nowa gra");
        alert.setHeaderText("Rozpocząć nową grę?");
        alert.setContentText("Bieżąca gra zostanie zakończona.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            startNewGame();
        }
    }

    @FXML
    private void handleEndGame() {
        if (goGame == null) return;

        if (!goGame.isGameOver()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Zakończenie gry");
            alert.setHeaderText("Czy na pewno zakończyć grę?");
            alert.setContentText("Obecny stan gry zostanie utracony.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                goGame.endGame();  // Wywołanie publicznej metody
                showGameOverAlert();
                updateGameInfo();
            }
        } else {
            showGameOverAlert();
        }
    }

    @FXML
    private void handleSurrender() {
        if (goGame == null) return;

        if (goGame.isGameOver()) {
            showGameOverAlert();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Poddanie się");
        alert.setHeaderText("Czy na pewno chcesz poddać się?");
        alert.setContentText("Przegrasz aktualną grę.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            goGame.surrender(goGame.getCurrentPlayer());
            showGameOverAlert();
            updateGameInfo();
        }
    }

    @FXML
    public void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Wyjdź z aplikacji");
        alert.setHeaderText("Czy na pewno chcesz wyjść?");
        alert.setContentText("Niezapisane dane gry zostaną utracone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }
}