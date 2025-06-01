package com.example.gry.go.board;

/**
 * Wyjątek zgłaszany, gdy współrzędne są ujemne.
 */
public class NegativeCoordinatesException extends IllegalArgumentException {
    /**
     * Konstruktor klasy NegativeCoordinatesException.
     *
     * @param message komunikat o błędzie
     */
    public NegativeCoordinatesException(String message) {
        super(message);
    }

    /**
     * Konstruktor klasy NegativeCoordinatesException.
     *
     * @param message komunikat o błędzie
     * @param cause przyczyna błędu
     */
    public NegativeCoordinatesException(String message, Throwable cause) {
        super(message, cause);
    }
}
