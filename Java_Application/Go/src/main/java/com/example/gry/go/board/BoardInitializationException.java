package com.example.gry.go.board;

/**
 * Wyjątek zgłaszany podczas błędnej inicjalizacji planszy.
 */
public class BoardInitializationException extends RuntimeException {
    /**
     * Konstruktor klasy BoardInitializationException.
     *
     * @param message komunikat o błędzie
     */
    public BoardInitializationException(String message) {
        super(message);
    }

    /**
     * Konstruktor klasy BoardInitializationException.
     *
     * @param message komunikat o błędzie
     * @param cause przyczyna błędu
     */
    public BoardInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
