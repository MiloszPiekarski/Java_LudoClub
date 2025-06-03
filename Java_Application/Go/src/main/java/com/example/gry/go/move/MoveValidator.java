package com.example.gry.go.move;

import com.example.gry.go.board.Board;
import com.example.gry.go.board.StateOfIntersection;
import com.example.gry.go.player.PlayerColour;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa odpowiedzialna za walidację ruchów w grze Go.
 */
public class MoveValidator {
    private final List<Board> history = new ArrayList<>();

    /**
     * Sprawdza, czy ruch jest poprawny.
     *
     * @param move ruch do sprawdzenia
     * @param board plansza, na której wykonano ruch
     * @return true, jeśli ruch jest poprawny, false w przeciwnym razie
     */
    public boolean isValidMove(Move move, Board board, PlayerColour playerColour) {
        if (move == null || board == null) {
            return false;
        }

        if(!board.isInBounds(move.getCoordinate())) {
            return false;
        }

        if(board.getIntersection(move.getCoordinate()).getIntersectionState() != StateOfIntersection.EMPTY) {
            return false;
        }

        // Symuluj ruch na kopii planszy
        Board boardCopy = new Board(board);
        boardCopy.getIntersection(move.getCoordinate().row(), move.getCoordinate().column()).setIntersectionState(move.getStoneColour().convertToStateOfIntersection());

        // (opcjonalnie) usuń kamienie przeciwnika, jeśli nie mają oddechów

        // Samobójstwo: czy ten ruch nie powoduje, że własny kamień nie ma oddechów?
        if (!boardCopy.hasLiberties(move.getCoordinate().row(), move.getCoordinate().column())) {
            return false;
        }

        // Reguła Ko: czy nowy stan planszy już występował?
        for (Board past : history) {
            if (boardCopy.equals(past)) {
                return false;
            }
        }

        return true;
    }

    public void saveBoardState(Board board) {
        // Zapisz kopię planszy po zatwierdzonym ruchu
        history.add(new Board(board));
    }

    public void reset() {
        history.clear();
    }
}
