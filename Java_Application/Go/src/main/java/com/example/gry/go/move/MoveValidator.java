package com.example.gry.go.move;

import com.example.gry.go.board.Board;
import com.example.gry.go.board.Coordinate;
import com.example.gry.go.board.StateOfIntersection;
import com.example.gry.go.player.PlayerColour;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa odpowiedzialna za walidację ruchów w grze Go.
 */
public class MoveValidator {
    private final List<Board> boardHistory = new ArrayList<>();
    private Board koBoard; // Plansza, której nie można odtworzyć (sytuacja Ko)

    public boolean isValidMove(Move move, Board currentBoard, PlayerColour playerColour) {
        // Podstawowe walidacje
        if (move == null || currentBoard == null) return false;
        if (!currentBoard.isInBounds(move.getCoordinate())) return false;
        if (!currentBoard.getIntersection(move.getCoordinate()).isEmpty()) return false;

        // Stwórz kopię planszy do symulacji
        Board simulatedBoard = new Board(currentBoard);
        Coordinate coord = move.getCoordinate();
        int row = coord.row();
        int col = coord.column();

        // Symuluj ruch
        simulatedBoard.setBoard(coord, playerColour.convertToStateOfIntersection());

        // Symuluj zbicie
        int capturedStones = simulatedBoard.captureGroupsIfNoLiberties(
                row, col, playerColour.convertToStateOfIntersection()
        );

        // Sprawdź oddechy po zbiciu
        boolean hasLiberties = simulatedBoard.hasLiberties(row, col);
        if (!hasLiberties && capturedStones == 0) {
            return false; // Samobójstwo bez zbicia
        }

        // Sprawdź regułę Ko
        if (isKoViolation(simulatedBoard)) {
            return false;
        }

        return true;
    }

    private boolean isKoViolation(Board newBoard) {
        // Sprawdź czy nowa plansza jest identyczna z koBoard
        if (koBoard != null && newBoard.equals(koBoard)) {
            return true;
        }

        // Sprawdź czy nowa plansza powtarza poprzednią pozycję
        if (!boardHistory.isEmpty()) {
            Board lastBoard = boardHistory.get(boardHistory.size() - 1);
            if (newBoard.equals(lastBoard)) {
                return true;
            }
        }

        return false;
    }

    public void recordBoardState(Board board, int capturedStones) {
        // Zapisz aktualny stan planszy
        boardHistory.add(new Board(board));

        // Aktualizuj koBoard jeśli było zbicie jednego kamienia
        if (capturedStones == 1) {
            if (boardHistory.size() >= 2) {
                // Ustaw koBoard na stan sprzed dwóch ruchów
                koBoard = new Board(boardHistory.get(boardHistory.size() - 2));
            } else {
                koBoard = null;
            }
        } else {
            koBoard = null;
        }
    }

    public void reset() {
        boardHistory.clear();
        koBoard = null;
    }
}
