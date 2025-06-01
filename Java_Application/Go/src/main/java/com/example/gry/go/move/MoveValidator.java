package com.example.gry.go.move;

import com.example.gry.go.board.Board;
import com.example.gry.go.board.StateOfIntersection;
import com.example.gry.go.player.PlayerColour;

/**
 * Klasa odpowiedzialna za walidację ruchów w grze Go.
 */
public class MoveValidator {
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

        return true;
    }
}
