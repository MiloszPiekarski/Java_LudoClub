package com.example.gry.go.move;

import com.example.gry.go.board.Coordinate;
import com.example.gry.go.board.StateOfIntersection;
import com.example.gry.go.player.PlayerColour;

/**
 * Reprezentuje ruch gracza na planszy.
 */
public class Move {
    /**
     * Współrzędne ruchu.
     */
    private final Coordinate coordinate;

    /**
     * Kolor kamienia gracza wykonującego ruch.
     */
    private final PlayerColour stoneColour;

    /**
     * Konstruktor klasy Move.
     *
     * @param coordinate Współrzędne ruchu.
     * @param stoneColour Kolor kamienia gracza wykonującego ruch.
     */
    public Move(Coordinate coordinate, PlayerColour stoneColour) {
        if (coordinate == null) {
            throw new IllegalArgumentException("Współrzędne nie mogą być null.");
        }
        if (stoneColour == null) {
            throw new IllegalArgumentException("Kolor nie może być null.");
        }
        this.coordinate = coordinate;
        this.stoneColour = stoneColour;
    }

    /**
     * @return Współrzędne ruchu.
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * @return Kolor kamienia gracza wykonującego ruch.
     */
    public PlayerColour getStoneColour() {
        return stoneColour;
    }

    /**
     * @return Zwraca reprezentację ruchu w postaci łańcucha znaków.
     */
    @Override
    public String toString() {
        return "Move{" +
                "coordinate = " + coordinate +
                ", stoneColour = " + stoneColour +
                '}';
    }
}
