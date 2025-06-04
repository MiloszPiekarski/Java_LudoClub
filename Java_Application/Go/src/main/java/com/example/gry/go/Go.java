package com.example.gry.go;

import com.example.gry.go.board.Board;
import com.example.gry.go.board.Coordinate;
import com.example.gry.go.board.Intersection;
import com.example.gry.go.board.StateOfIntersection;
import com.example.gry.go.move.Move;
import com.example.gry.go.move.MoveValidator;
import com.example.gry.go.player.Player;
import com.example.gry.go.player.PlayerColour;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Go {
    private final Board board;
    private Player currentPlayer;
    private Player opponentPlayer;
    private final List<Player> players;
    private int consecutivePasses = 0;
    private boolean gameOver = false;
    private Player winner = null;
    private Set<Coordinate> markedDeadStones = new HashSet<>();
    private boolean inDeadStoneMarkingMode = false;
    private int blackCaptures = 0;
    private int whiteCaptures = 0;
    private final MoveValidator moveValidator = new MoveValidator();
    private int lastCapturedStones = 0;

    public Go(int boardSize, Player blackPlayer, Player whitePlayer) {
        this.board = new Board(boardSize);
        this.currentPlayer = blackPlayer;
        this.opponentPlayer = whitePlayer;
        this.players = List.of(blackPlayer, whitePlayer);
    }

    public void startGame() {
        consecutivePasses = 0;
        gameOver = false;
        winner = null;
        board.resetBoard();
        System.out.println("Rozpoczęto nową grę w Go!");
    }

    public void switchPlayer() {
        Player temp = currentPlayer;
        currentPlayer = opponentPlayer;
        opponentPlayer = temp;
    }

    public void makeMove(Player player, Move move) {
        if (gameOver) {
            throw new IllegalStateException("Gra już się zakończyła");
        }

        consecutivePasses = 0;

        if (!moveValidator.isValidMove(move, board, move.getStoneColour())) {
            throw new IllegalArgumentException("Nieprawidłowy ruch");
        }

        // Zapisz stan przed ruchem
        Board boardBeforeMove = new Board(board);

        // Wykonaj ruch
        Coordinate coord = move.getCoordinate();
        board.updateIntersection(move.getCoordinate(),
                move.getStoneColour().convertToStateOfIntersection());

        // Wykonaj zbicie
        lastCapturedStones = board.captureGroupsIfNoLiberties(
                coord.row(), coord.column(), move.getStoneColour().convertToStateOfIntersection()
        );

        // Zapisz stan po ruchu
        moveValidator.recordBoardState(boardBeforeMove, lastCapturedStones);

        // Aktualizuj statystyki zbitych kamieni
        if (move.getStoneColour() == PlayerColour.BLACK) {
            blackCaptures += lastCapturedStones;
        } else {
            whiteCaptures += lastCapturedStones;
        }

        switchPlayer();
    }

    public void pass() {
        if (gameOver) {
            throw new IllegalStateException("Gra już się zakończyła");
        }

        consecutivePasses++;
        System.out.println("Gracz " + currentPlayer.getPlayerColour() + " spasował");

        if (consecutivePasses >= 2) {
            endGame(); // TYLKO ustawia tryb oznaczania, nie kończy całkowicie
        } else {
            switchPlayer();
        }
    }

    public void endGame() {
        gameOver = true;
        inDeadStoneMarkingMode = true; // Ustaw tryb oznaczania martwych kamieni
        System.out.println("Gra zakończona! Oznacz martwe grupy kamieni.");
    }

    public void toggleDeadStone(int row, int col) {
        if (!inDeadStoneMarkingMode) return;

        Coordinate coord = new Coordinate(row, col);
        Intersection intersection = board.getIntersection(coord);
        if (intersection.isEmpty()) return;

        if (markedDeadStones.contains(coord)) {
            Set<Intersection> group = board.collectGroup(row, col, intersection.getIntersectionState());
            for (Intersection stone : group) {
                markedDeadStones.remove(stone.getCoordinate());
            }
        } else {
            Set<Intersection> group = board.collectGroup(row, col, intersection.getIntersectionState());
            for (Intersection stone : group) {
                markedDeadStones.add(stone.getCoordinate());
            }
        }
    }

    public void removeMarkedDeadStones() {
        for (Coordinate coord : markedDeadStones) {
            board.getIntersection(coord).setIntersectionState(StateOfIntersection.EMPTY);
        }
        markedDeadStones.clear();
        inDeadStoneMarkingMode = false;
        calculateScores(); // Wywołanie publicznej metody
    }

    public void calculateScores() {
        int blackTerritory = board.calculateScore(StateOfIntersection.BLACK);
        int whiteTerritory = board.calculateScore(StateOfIntersection.WHITE);

        // Punkty = terytorium + przechwycone kamienie
        int blackScore = blackTerritory + blackCaptures;
        double whiteScore = whiteTerritory + whiteCaptures + 6.5; // + komi

        if (blackScore > whiteScore) {
            winner = players.stream()
                    .filter(p -> p.getPlayerColour() == PlayerColour.BLACK)
                    .findFirst()
                    .orElse(null);
        } else if (whiteScore > blackScore) {
            winner = players.stream()
                    .filter(p -> p.getPlayerColour() == PlayerColour.WHITE)
                    .findFirst()
                    .orElse(null);
        }
    }

    public boolean isInDeadStoneMarkingMode() {
        return inDeadStoneMarkingMode;
    }

    public Set<Coordinate> getMarkedDeadStones() {
        return markedDeadStones;
    }

    public void setInDeadStoneMarkingMode(boolean mode) {
        this.inDeadStoneMarkingMode = mode;
        if (!mode) {
            this.gameOver = true; // Po zakończeniu oznaczania, gra jest całkowicie zakończona
        }
    }

    private void removeDeadGroups() {
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                Intersection intersection = board.getIntersection(row, col);
                if (!intersection.isEmpty() && !board.hasLiberties(row, col)) {
                    Set<Intersection> group = board.collectGroup(row, col, intersection.getIntersectionState());
                    board.clearGroup(group);  // Wywołanie publicznej metody
                }
            }
        }
    }

    public void surrender(Player surrenderingPlayer) {
        if (gameOver) {
            throw new IllegalStateException("Gra już się zakończyła");
        }

        gameOver = true;

        // Ustalenie zwycięzcy - przeciwnik poddającego się gracza
        winner = players.stream()
                .filter(p -> p != surrenderingPlayer)
                .findFirst()
                .orElse(null);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        return winner;
    }

    public int getBlackScore() {
        return board.calculateScore(StateOfIntersection.BLACK);
    }

    public double getWhiteScore() {
        return board.calculateScore(StateOfIntersection.WHITE) + 6.5; // komi 6.5 punktu
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOpponentPlayer() {
        return opponentPlayer;
    }

    public int getConsecutivePasses() {
        return consecutivePasses;
    }

    public int getBlackCaptures() {
        return blackCaptures;
    }

    public int getWhiteCaptures() {
        return whiteCaptures;
    }
}