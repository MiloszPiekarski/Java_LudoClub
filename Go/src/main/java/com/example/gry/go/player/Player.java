package com.example.gry.go.player;

/**
 * Reprezentuje gracza w grze Go.
 */
public abstract class Player extends com.example.gry.games.Player{
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
        super(nickname);
        this.playerColour = playerColour;
    }

    /**
     * @return Zwraca kolor gracza.
     */
    public PlayerColour getPlayerColour() {
        return playerColour;
    }
}
