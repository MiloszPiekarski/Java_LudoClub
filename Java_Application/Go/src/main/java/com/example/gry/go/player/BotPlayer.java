package com.example.gry.go.player;

import com.example.gry.go.board.Coordinate;
import com.example.gry.go.board.Board;
import com.example.gry.go.move.Move;

public class BotPlayer extends Player{
    /**
     * Konstruktor klasy Player.
     *
     * @param nickname nazwa gracza. Musi być unikalna.
     */
    public BotPlayer(String nickname, PlayerColour playerColour) {
        super(nickname, playerColour);
    }

    @Override
    public Move provideMove(Board board) {
        int x = (int) (Math.random() * board.getSize());
        int y = (int) (Math.random() * board.getSize());

        return new Move(new Coordinate(x, y), playerColour);
    }
}
