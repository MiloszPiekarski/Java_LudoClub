package com.example.gry.go.player;

import com.example.gry.go.board.Board;
import com.example.gry.go.move.Move;

/**
 * Reprezentuje gracza w grze Go.
 */
public abstract class Player {
    protected String nickname;
    protected boolean isOnline;
    /**
     * Kolor gracza.
     */
    protected final PlayerColour playerColour;

    /**
     * Konstruktor klasy Player.
     *
     * @param nickname nazwa gracza. Musi być unikalna.
     * @param playerColour kolor gracza.
     */
    public Player(String nickname, PlayerColour playerColour) {
        this.nickname = nickname;
        this.isOnline = true;
        this.playerColour = playerColour;
    }

    /**
     * @return Zwraca kolor gracza.
     */
    public PlayerColour getPlayerColour() {
        return playerColour;
    }

    /**
     * @return Zwraca nazwę gracza.
     */
    public String getNickname() {
        return nickname;
    }

    public abstract Move provideMove(Board board);
}
