package com.example.gry.go.board;

/**
 * Wyjątek zgłaszany, gdy zmiana stanu przecięcia jest nieprawidłowa.
 */
public class InvalidChangeOfIntersectionStateException extends RuntimeException {
    /**
     * Konstruktor klasy InvalidChangeOfIntersectionState.
     *
     * @param message komunikat o błędzie
     */
    public InvalidChangeOfIntersectionStateException(String message) {
        super(message);
    }

    /**
     * Konstruktor klasy InvalidChangeOfIntersectionState.
     *
     * @param message komunikat o błędzie
     * @param cause przyczyna błędu
     */
    public InvalidChangeOfIntersectionStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
