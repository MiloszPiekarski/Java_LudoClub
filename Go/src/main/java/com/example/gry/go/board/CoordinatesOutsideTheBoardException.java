package com.example.gry.go.board;

/**
 * Wyjątek zgłaszany, gdy współrzędne są poza planszą.
 */
public class CoordinatesOutsideTheBoardException extends RuntimeException {
    /**
     * Konstruktor klasy CoordinatesOutsideTheBoardException.
     *
     * @param message komunikat o błędzie
     */
    public CoordinatesOutsideTheBoardException(String message) {
        super(message);
    }

    /**
     * Konstruktor klasy CoordinatesOutsideTheBoardException.
     *
     * @param message komunikat o błędzie
     * @param cause przyczyna błędu
     */
    public CoordinatesOutsideTheBoardException(String message, Throwable cause) {
        super(message, cause);
    }
}
