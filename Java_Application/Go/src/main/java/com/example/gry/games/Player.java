package com.example.gry.games;

import com.example.gry.go.board.Board;
import com.example.gry.go.move.Move;

/**
 * Abstrakcyjna klasa reprezentująca gracza w grze.
 */
public abstract class Player {
    /**
     * Nazwa gracza. Traktowana jako ID. Każdy gracz ma unikalną nazwę.
     */
    protected String nickname;

    /**
     * Czy gracz jest online. 1 - jest online, 0 - jest offline.
     */
    protected boolean isOnline;

    /**
     * Konstruktor klasy Player.
     * @param nickname nazwa gracza. Musi być unikalna.
     */
    public Player(String nickname) {
        this.nickname = nickname; // TODO: Sprawdzenie czy nazwa gracza jest unikalna
        this.isOnline = true; // Domyślnie gracz jest online
    }

    /**
     * @return Zwraca nazwę gracza.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return Zwraca informację czy gracz jest online.
     */
    public boolean isOnline() {
        return isOnline;
    }

    /**
     * Metoda do wykonywania ruchu przez gracza.
     */
    public abstract Move provideMove(Board board);
}
