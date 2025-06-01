package com.example.gry.go.board;

/**
 * @param row wiersz na planszy (współrzędne y)
 * @param column kolumna na planszy (współrzędne x)
 */
public record Coordinate(int row, int column) {
    /**
     * Konstruktor klasy Coordinate.
     * Jest nadpisywany, ponieważ sprawdzamy w nim poprawność współrzędnych.
     *
     * @param row wiersz na planszy (współrzędne y)
     * @param column kolumna na planszy (współrzędne x)
     */
    public Coordinate{
        if(row < 0 || column < 0) {
            throw new NegativeCoordinatesException("Współrzędne nie mogą być ujemne. Wiersz: " + row + ", Kolumna: " + column);
        }
    }

    /**
     * @return Współrzędne punktu na planszy w postaci: (wiersz, kolumna).
     */
    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }
}
