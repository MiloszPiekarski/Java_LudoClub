package com.example.gry.go;

import com.example.gry.go.board.Board;
import com.example.gry.go.board.StateOfIntersection;
import com.example.gry.go.move.MoveValidator;
import com.example.gry.go.player.Player;
import com.example.gry.go.player.PlayerColour;

import java.util.List;

public class Go {
    private final Board board;
    private Player currentPlayer;
    private Player opponentPlayer;
    private final List<Player> players;
    private int consecutivePasses = 0;
    private boolean gameOver = false;
    private Player winner = null;

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

    public void makeMove(Player player, com.example.gry.go.move.Move move) {
        if (gameOver) {
            throw new IllegalStateException("Gra już się zakończyła");
        }

        consecutivePasses = 0;

        if (!new MoveValidator().isValidMove(move, board, move.getStoneColour())) {
            throw new IllegalArgumentException("Nieprawidłowy ruch");
        }

        board.getIntersection(move.getCoordinate().row(), move.getCoordinate().column())
                .setIntersectionState(move.getStoneColour().convertToStateOfIntersection());

        board.captureGroupsIfNoLiberties(
                move.getCoordinate().row(),
                move.getCoordinate().column(),
                move.getStoneColour().convertToStateOfIntersection()
        );

        switchPlayer();
    }

    public void pass() {
        if (gameOver) {
            throw new IllegalStateException("Gra już się zakończyła");
        }

        consecutivePasses++;
        System.out.println("Gracz " + currentPlayer.getPlayerColour() + " spasował");

        if (consecutivePasses >= 2) {
            endGame();
        } else {
            switchPlayer();
        }
    }

    public void endGame() {
        gameOver = true;
        System.out.println("Gra zakończona!");

        int blackScore = board.calculateScore(StateOfIntersection.BLACK);
        int whiteScore = board.calculateScore(StateOfIntersection.WHITE);

        whiteScore += 6.5;

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
        return board.calculateScore(StateOfIntersection.WHITE) + 6.5;
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
}