package com.example.gry.go.player;

import com.example.gry.go.board.StateOfIntersection;

/**
 * Możliwe kolory graczy.
 */
public enum PlayerColour {
    /**
     * Biały kolor kamieni.
     */
    WHITE,
    /**
     * Czarny kolor kamieni.
     */
    BLACK;

    /**
     * Konwertuje kolor gracza na stan przecięcia.
     *
     * @return Stan przecięcia odpowiadający kolorowi gracza.
     */
    public StateOfIntersection convertToStateOfIntersection() {
        switch (this) {
            case WHITE:
                return StateOfIntersection.WHITE;
            case BLACK:
                return StateOfIntersection.BLACK;
            default:
                throw new IllegalArgumentException("Nieznany kolor gracza: " + this);
        }
    }
}
