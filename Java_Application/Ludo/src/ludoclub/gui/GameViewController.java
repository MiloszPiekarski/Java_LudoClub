package ludoclub.gui;

import java.awt.Point;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import ludoclub.model.Player;
import ludoclub.model.Pawn;
import ludoclub.model.ColorType;
import ludoclub.model.Game;
import ludoclub.model.Bot;

public class GameViewController {
    private final Game game;
    private HBox root;
    private Label statusLabel, diceLabel;
    private boolean mustRoll = true;
    private int diceRoll = 0;
    private List<Pawn> selectablePawns = new ArrayList<>();
    private final List<Player> finishOrder = new ArrayList<>();
    private boolean gameEnded = false;
    private Stage mainStage;

    public static double BOT_MOVE_DELAY = 0.1;
    private boolean onlyBotsGame;

    // OOP FEATURE: Do obsługi wielowątkowego timera gry
    private volatile boolean running = false;
    private long startTime = 0;

    private static final List<Point> START_FIELDS = List.of(
            new Point(5, 1),   // RED
            new Point(1, 7),   // BLUE
            new Point(11, 5),  // GREEN
            new Point(7, 11)   // YELLOW
    );

    public GameViewController(Game game) {
        this.game = game;
        this.onlyBotsGame = game.getPlayers().stream().allMatch(p -> p instanceof Bot);
    }

    public void start(Stage stage) {
        mainStage = stage;
        root = new HBox(createPlayersPanel(), createBoardGrid());
        Scene scene = new Scene(root, 1000, 800);
        URL css = getClass().getResource("/ludoclub/gui/style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Ludo Club – Gra");
        stage.show();
        updateUI();

        // OOP FEATURE: startujemy timer w osobnym wątku
        startGameTimer();

        if (game.getCurrentPlayer() instanceof Bot && !gameEnded) {
            rollDice();
        }
    }

    private VBox createPlayersPanel() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(240);
        panel.setStyle("-fx-background-color:#00BFFF;-fx-border-color:#dde2e8;-fx-border-width:0 1 0 0;");

        statusLabel = new Label("Tura gracza: " + game.getCurrentPlayer().getUsername());
        statusLabel.setStyle("-fx-font-size:18px;-fx-font-weight:bold;");
        diceLabel = new Label(mustRoll ? "Rzuć kostką!" : "Wyrzucono: " + diceRoll);
        diceLabel.setStyle("-fx-font-size:16px;");

        Button rollBtn = new Button("🎲 Rzuć kostką");
        rollBtn.setPrefWidth(180);
        rollBtn.setDisable(!mustRoll || gameEnded || (game.getCurrentPlayer() instanceof Bot));
        rollBtn.setOnAction(e -> rollDice());

        VBox playersBox = new VBox(10);
        Player current = game.getCurrentPlayer();
        for (Player p : game.getPlayers()) {
            VBox card = new VBox(5);
            boolean isCurrent = p == game.getCurrentPlayer();
            String border = isCurrent ? "-fx-border-color:#bb42e4; -fx-border-width:2;" : "";
            card.setStyle(
                    "-fx-background-color: white" +
                            (isCurrent ? "," + pastelBgForColor(p.getColorType()) : "") +
                            "; -fx-background-radius:12;" + border +
                            "-fx-effect:dropshadow(two-pass-box,rgba(0,0,0,0.1),4,0,0,2);"
            );
            HBox line = new HBox(8);
            line.getChildren().addAll(new Circle(12, p.getColorType().getFxColor()), styledLabel(p.getUsername(), 14, true));
            long inPlay  = p.getPawns().stream().filter(t -> !t.isAtHome() && !t.isAtGoal()).count();
            long inGoal  = p.getPawns().stream().filter(Pawn::isAtGoal).count();

            card.getChildren().addAll(
                    line,
                    styledLabel("Pionki w grze: " + inPlay, 13, false),
                    styledLabel("Na mecie: " + inGoal, 13, false),
                    styledLabel("Typ: " + p.getUserType(), 12, false)
            );
            playersBox.getChildren().add(card);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button saveBtn = new Button("💾 Zapisz grę");
        saveBtn.setPrefWidth(180);
        saveBtn.setDisable(gameEnded);
        saveBtn.setOnAction(e -> saveGame());

        Button backBtn = new Button("⬅ Wróć do menu");
        backBtn.setPrefWidth(180);
        backBtn.setOnAction(e ->
                new ludoclub.gui.MainMenuController()
                        .start((Stage) backBtn.getScene().getWindow()));

        // OOP FEATURE: przycisk uruchamiający anonimowy wątek (widoczne na siłę!)
        Button threadBtn = new Button("DEMO: Wątek anonimowy");
        threadBtn.setPrefWidth(180);
        threadBtn.setOnAction(e -> {
            new Thread() {
                @Override
                public void run() {
                    // Wypisz do konsoli oraz wrzuć do alertu
                    String msg = "Wątek anonimowy DZIAŁA! Czas: " + new java.util.Date();
                    System.out.println(msg);
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
                        alert.setTitle("Wątek anonimowy");
                        alert.setHeaderText("Demo klasy anonimowej + Thread!");
                        alert.showAndWait();
                    });
                }
            }.start();
        });

        panel.getChildren().setAll(
                statusLabel, diceLabel, rollBtn,
                playersBox, spacer, saveBtn, threadBtn, backBtn
        );
        return panel;
    }

    // Bardzo jasne tło dla aktywnego gracza
    private String pastelBgForColor(ColorType type) {
        if (type == ColorType.RED)    return "rgba(255,90,90,0.12)";
        if (type == ColorType.BLUE)   return "rgba(90,160,255,0.10)";
        if (type == ColorType.GREEN)  return "rgba(110,255,170,0.11)";
        if (type == ColorType.YELLOW) return "rgba(255,240,110,0.12)";
        return "white";
    }

    private Label styledLabel(String txt, int size, boolean bold) {
        Label l = new Label(txt);
        l.setStyle("-fx-font-size:" + size + "px;" +
                (bold ? "-fx-font-weight:bold;" : ""));
        return l;
    }

    private GridPane createBoardGrid() {
        GridPane g = new GridPane();
        g.setStyle("-fx-background-color: white;");
        g.setAlignment(Pos.CENTER);
        g.setHgap(2);
        g.setVgap(2);
        g.setPadding(new Insets(20));

        for (int r = 0; r < 13; r++) {
            for (int c = 0; c < 13; c++) {
                Rectangle cell = new Rectangle(40,40);
                cell.setArcWidth(14); cell.setArcHeight(14);
                cell.setFill(cellColor(r,c));
                g.add(cell, c, r);
            }
        }

        // Rysuj pionki, aktywnego gracza NA WIERZCHU
        List<Player> sortedPlayers = new ArrayList<>(game.getPlayers());
        Player current = game.getCurrentPlayer();
        sortedPlayers.remove(current);
        sortedPlayers.add(current); // aktywny ostatni

        for (Player pl : sortedPlayers) {
            for (Pawn pn : pl.getPawns()) {
                if (pn.isAtGoal()) continue;
                Point pt = pn.getCoordinates();
                Circle pawnView = new Circle(14, pl.getColorType().getFxColor());

                if (!mustRoll && selectablePawns.contains(pn)) {
                    pawnView.setStroke(Color.PURPLE);
                    pawnView.setStrokeWidth(4);
                    pawnView.setOnMouseClicked(e -> handlePawnMove(pn));
                }
                g.add(pawnView, pt.y, pt.x);
            }
        }
        return g;
    }

    private Color cellColor(int r, int c) {
        if (r<5 && c<5)
            return (r==0||r==4||c==0||c==4)
                    ? ColorType.RED.getFxColor()
                    : innerBase(r,c,1,1,3,3);
        if (r<5 && c>=8)
            return (r==0||r==4||c==8||c==12)
                    ? ColorType.BLUE.getFxColor()
                    : innerBase(r,c,1,9,3,11);
        if (r>=8 && c<5)
            return (r==8||r==12||c==0||c==4)
                    ? ColorType.GREEN.getFxColor()
                    : innerBase(r,c,9,1,11,3);
        if (r>=8 && c>=8)
            return (r==8||r==12||c==8||c==12)
                    ? ColorType.YELLOW.getFxColor()
                    : innerBase(r,c,9,9,11,11);

        if (r==6 && c==6) return Color.BLACK;

        if ((r==6 && c>=1 && c<=5) || (c==1 && r==5))
            return ColorType.LIGHT_RED.getFxColor();
        if ((r==1 && c>=7 && c<=11) || (c==6 && r>=1 && r<=5))
            return ColorType.LIGHT_BLUE.getFxColor();
        if ((r==6 && c>=7 && c<=11) || (c==11 && r>=7 && r<=11))
            return ColorType.DARK_YELLOW.getFxColor();
        if ((r==11 && c>=1 && c<=5) || (c==6 && r>=7 && r<=11))
            return ColorType.LIGHT_GREEN.getFxColor();

        return Color.LIGHTGRAY;
    }

    private Color innerBase(int r,int c,int r1,int c1,int r2,int c2) {
        return ((r==r1||r==r2) && (c==c1||c==c2))
                ? Color.web("#eaeaea")
                : Color.WHITE;
    }

    private void rollDice() {
        if (gameEnded) return;
        diceRoll = (int)(Math.random()*6)+1;
        diceLabel.setText("Wyrzucono: " + diceRoll);
        mustRoll = false;

        skipFinishedPlayers();

        selectablePawns = game.getCurrentPlayer().getPawns().stream()
                .filter(p -> p.canMovePawn(diceRoll))
                .collect(Collectors.toList());

        if (game.getCurrentPlayer() instanceof Bot && !gameEnded) {
            handleBotTurn();
            return;
        }

        if (selectablePawns.isEmpty()) {
            statusLabel.setText("Tura gracza: " + game.getCurrentPlayer().getUsername() + ". Brak możliwego ruchu.");
            PauseTransition pt = new PauseTransition(Duration.seconds(1));
            pt.setOnFinished(e -> {
                mustRoll = true;
                nextPlayerTurn();
            });
            pt.play();
            return;
        }

        if (selectablePawns.size()==1) {
            statusLabel.setText("Tura gracza: " + game.getCurrentPlayer().getUsername());
            PauseTransition pt = new PauseTransition(Duration.seconds(1));
            pt.setOnFinished(e -> {
                Platform.runLater(() -> {
                    handlePawnMove(selectablePawns.get(0));
                    selectablePawns.clear();
                });
            });
            pt.play();
            return;
        }

        statusLabel.setText("Tura gracza: " + game.getCurrentPlayer().getUsername() + ". Wybierz pionek.");
        updateUI();
    }

    private void handleBotTurn() {
        if (selectablePawns.isEmpty()) {
            statusLabel.setText("Tura bota: " + game.getCurrentPlayer().getUsername() + ". Brak możliwego ruchu.");
            PauseTransition pt = new PauseTransition(Duration.seconds(BOT_MOVE_DELAY));
            pt.setOnFinished(e -> {
                mustRoll = true;
                nextPlayerTurn();
            });
            pt.play();
            return;
        }

        Pawn toMove;
        if (game.getCurrentPlayer() instanceof Bot bot) {
            List<Pawn> canMove = selectablePawns;
            toMove = canMove.get(new Random().nextInt(canMove.size()));
        } else {
            toMove = selectablePawns.get(0);
        }

        statusLabel.setText("Ruch bota: " + game.getCurrentPlayer().getUsername());
        PauseTransition pt = new PauseTransition(Duration.seconds(BOT_MOVE_DELAY));
        pt.setOnFinished(e -> {
            handlePawnMove(toMove);
            selectablePawns.clear();
        });
        pt.play();
    }

    private void handlePawnMove(Pawn pawn) {
        if (gameEnded) return;
        Player current = game.getCurrentPlayer();
        Point dest = pawn.predictCoordinates(diceRoll);

        Player beaten = findOpponentOn(dest, current);
        boolean wasBeaten = false;
        Pawn beatenPawn = null;

        int countCurrent = countPlayerPawnsOn(dest, current);
        int countAny    = countAnyPawnsOn(dest);
        boolean isStartField = START_FIELDS.contains(dest);

        if (!isStartField && countAny == 1 && beaten != null) {
            for (Pawn other : beaten.getPawns()) {
                if (!other.isAtHome() && !other.isAtGoal() && other.getCoordinates().equals(dest)) {
                    other.resetToBase();
                    wasBeaten = true;
                    beatenPawn = other;
                    break;
                }
            }
            game.tryMovePawn(pawn, diceRoll);
        } else {
            game.tryMovePawn(pawn, diceRoll);
        }

        if (wasBeaten && beatenPawn != null && !onlyBotsGame) {
            showAlert(current.getUsername() + " skuł pionek gracza " + beaten.getUsername(), "Skucie");
        }

        long nowOnGoal = current.getPawns().stream().filter(Pawn::isAtGoal).count();
        if (nowOnGoal == 4 && !finishOrder.contains(current)) {
            finishOrder.add(current);
            int place = finishOrder.size();
            if (place < 3) {
                Platform.runLater(() ->
                        showAlert(current.getUsername() + " zajął " + ordinal(place) + " miejsce!", "Zakończył grę")
                );
            }
        }

        if (finishOrder.size() == 3 && !gameEnded) {
            finishOrder.addAll(game.getPlayers().stream()
                    .filter(p -> !finishOrder.contains(p))
                    .toList());
            showFinalRanking();
            gameEnded = true;
            updateUI();
            return;
        }

        if (diceRoll==6 && !finishOrder.contains(current) && !gameEnded) {
            mustRoll = true;
            diceLabel.setText("Wyrzucono: 6! Rzuć jeszcze raz");
            updateUI();
            if (current instanceof Bot) {
                PauseTransition pt = new PauseTransition(Duration.seconds(BOT_MOVE_DELAY));
                pt.setOnFinished(e -> rollDice());
                pt.play();
            }
        } else {
            mustRoll = true;
            nextPlayerTurn();
        }
        selectablePawns.clear();
        updateUI();
    }

    private int countPlayerPawnsOn(Point pt, Player owner) {
        int c = 0;
        for (Pawn p : owner.getPawns()) {
            if (!p.isAtHome() && !p.isAtGoal() && p.getCoordinates().equals(pt)) c++;
        }
        return c;
    }
    private int countAnyPawnsOn(Point pt) {
        int c = 0;
        for (Player pl : game.getPlayers()) {
            for (Pawn p : pl.getPawns()) {
                if (!p.isAtHome() && !p.isAtGoal() && p.getCoordinates().equals(pt)) c++;
            }
        }
        return c;
    }

    private void showAlert(String msg, String title) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
            a.setTitle(title);
            a.setHeaderText(null);
            a.showAndWait();
        });
    }

    // OOP FEATURE: Klasa wewnętrzna loggera (własny wątek)
    private class LoggerThread extends Thread {
        private final String data;
        public LoggerThread(String data) { this.data = data; }
        @Override
        public void run() {
            try (java.io.FileWriter fw = new java.io.FileWriter("log_thread_ludo.txt", true)) {
                fw.write(data + "\n");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveReportToFile(String datetime, String ranking) {
        String fileName = "raport_ludo.txt";
        StringBuilder sb = new StringBuilder();
        sb.append("Data zakończenia gry: ").append(datetime).append("\n");
        sb.append("Ranking końcowy:\n");
        sb.append(ranking);
        sb.append("---------------\n");
        try (java.io.FileWriter fw = new java.io.FileWriter(fileName, true)) {
            fw.write(sb.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // OOP FEATURE: log w osobnym wątku (LoggerThread)
        new LoggerThread("[LoggerThread] RAPORT ZAPISANY: " + datetime).start();

        // NOWOŚĆ: Wysyłka raportu do serwera (sieciowo przez socket TCP)
        sendReportToServer(sb.toString());
    }

    // KOMUNIKACJA SIECIOWA: klient TCP do serwera raportów (np. LudoReportServer)
    private void sendReportToServer(String reportText) {
        new Thread(() -> { // osobny wątek żeby nie blokować GUI!
            try (Socket socket = new Socket("localhost", 5050);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(reportText);
                System.out.println("[Network] Wysłano raport do serwera!");
            } catch (Exception ex) {
                System.err.println("[Network] Błąd połączenia z serwerem raportów: " + ex.getMessage());
            }
        }).start();
    }

    // OOP FEATURE: prosty licznik czasu gry w osobnym wątku
    private void startGameTimer() {
        running = true;
        startTime = System.currentTimeMillis();
        Thread timerThread = new Thread(() -> {
            while (running) {
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                long elapsed = (System.currentTimeMillis() - startTime) / 1000;
                System.out.println("[TimerThread] Upłynęło sekund: " + elapsed);
            }
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }
    private void stopGameTimer() {
        running = false;
    }

    private void showFinalRanking() {
        Platform.runLater(() -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < finishOrder.size(); i++) {
                sb.append((i+1)).append(". ").append(finishOrder.get(i).getUsername()).append("\n");
            }
            String now = java.time.LocalDateTime.now().toString();
            for (int i = 0; i < finishOrder.size(); i++) {
                int points = 4 - i;
                ludoclub.model.DBManager.insertResult(finishOrder.get(i).getUsername(), points, now);
            }
            saveReportToFile(now, sb.toString());

            // OOP FEATURE: zakończ timer
            stopGameTimer();

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Koniec gry");
            a.setHeaderText("Ranking końcowy:");
            a.setContentText(sb.toString());
            ButtonType backToMenuBtn = new ButtonType("Wróć do menu", ButtonBar.ButtonData.OK_DONE);
            a.getButtonTypes().setAll(backToMenuBtn);
            a.showAndWait();
            Platform.runLater(() -> {
                new ludoclub.gui.MainMenuController().start(mainStage);
            });

        });
    }

    private void skipFinishedPlayers() {
        while (finishOrder.contains(game.getCurrentPlayer()) && finishOrder.size() < 3) {
            game.nextTurn();
        }
    }

    private Player findOpponentOn(Point pt, Player owner) {
        for (Player pl : game.getPlayers()) {
            if (pl == owner) continue;
            for (Pawn pn : pl.getPawns()) {
                if (!pn.isAtHome() && !pn.isAtGoal() && pn.getCoordinates().equals(pt)) {
                    return pl;
                }
            }
        }
        return null;
    }

    private void nextPlayerTurn() {
        if (gameEnded) return;
        do {
            game.nextTurn();
        } while (finishOrder.contains(game.getCurrentPlayer()) && finishOrder.size() < 3);
        statusLabel.setText("Tura gracza: " + game.getCurrentPlayer().getUsername());
        diceLabel.setText("Rzuć kostką!");
        updateUI();

        if (game.getCurrentPlayer() instanceof Bot && !gameEnded) {
            PauseTransition pt = new PauseTransition(Duration.seconds(BOT_MOVE_DELAY));
            pt.setOnFinished(e -> rollDice());
            pt.play();
        }
    }

    private void updateUI() {
        root.getChildren().setAll(createPlayersPanel(), createBoardGrid());
    }

    private void saveGame() {
        try (var o = new java.io.ObjectOutputStream(new java.io.FileOutputStream("savegame.ser"))) {
            o.writeObject(game);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private String ordinal(int i) {
        switch (i) {
            case 1: return "pierwsze";
            case 2: return "drugie";
            case 3: return "trzecie";
            case 4: return "czwarte";
            default: return i + " miejsce";
        }
    }
}