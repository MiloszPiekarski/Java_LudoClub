package com.example.gry.go.player;

import com.example.gry.go.board.Board;
import com.example.gry.go.board.Coordinate;
import com.example.gry.go.move.Move;

public class HumanPlayer extends Player {
    /**
     * Następny ruch gracza.
     */
    private Coordinate nextMove;

    /**
     * Konstruktor klasy Player.
     *
     * @param nickname nazwa gracza. Musi być unikalna.
     */
    public HumanPlayer(String nickname, PlayerColour playerColour) {
        super(nickname, playerColour);
    }

    /**
     * Ustawia zmienną nextMove na podane współrzędne.
     *
     * @param coordinate Współrzędne następnego ruchu gracza.
     */
    public void setNextMove(Coordinate coordinate) {
        this.nextMove = coordinate;
    }

    /**
     * Wykonuje ruch gracza na planszy.
     *
     * @param board Plansza, na której gracz wykonuje ruch.
     * @return Zwraca ruch gracza na planszy.
     */
    @Override
    public Move provideMove(Board board) {
        if(nextMove == null) {
            throw new IllegalStateException("Ruch nie został ustawiony dla gracza: " + getNickname());
        }

        Move move = new Move(nextMove, playerColour);
        nextMove = null;

        return move;
    }
}
