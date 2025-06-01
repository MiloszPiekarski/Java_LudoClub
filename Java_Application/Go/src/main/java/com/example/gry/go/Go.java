package com.example.gry.go;

import com.example.gry.games.Game;
import com.example.gry.games.Player;
import com.example.gry.go.board.Board;
import com.example.gry.go.board.StateOfIntersection;
import com.example.gry.go.move.MoveValidator;
import com.example.gry.go.player.PlayerColour;

import java.util.List;

/**
 * Klasa reprezentująca grę Go.
 */
public class Go extends Game {
    /**
     * Plansza do gry Go.
     */
    private final Board board;

    /**
     * Aktualny gracz wykonujący ruch.
     */
    private Player currentPlayer;

    /**
     * Przeciwnik aktualnego gracza.
     */
    private Player opponentPlayer;

    /**
     * @param boardSize Rozmiar planszy do gry Go.
     */
    public Go(int boardSize, Player blackPlayer, Player whitePlayer) {
        this.board = new Board(boardSize);
        this.currentPlayer = blackPlayer;
        this.opponentPlayer = whitePlayer;
    }

    /**
     * @return Plansza do gry.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * @return
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @return
     */
    public Player getOpponentPlayer() {
        return opponentPlayer;
    }

    /**
     * Konstruktor klasy Go.
     * @param players Lista graczy uczestniczących w grze.
     */
    @Override
    public void startGame(List<Player> players) {
    }

    /**
     * Destruktor klasy Go.
     */
    @Override
    public void endGame() {

    }

    /**
     * Metoda zapisująca aktualny stan gry.
     */
    @Override
    public void saveGame() {

    }

    /**
     * @param player Gracz wykonujący ruch.
     * @param move   Ruch wykonany przez gracza.
     */
    @Override
    public void makeMove(Player player, com.example.gry.go.move.Move move) {


        if (player == null) {
            throw new IllegalArgumentException("Gracz nie może być null.");
        }
        if (move == null) {
            throw new IllegalArgumentException("Ruch nie może być null.");
        }
        if (!players.contains(player)) {
            throw new IllegalArgumentException("Gracz nie bierze udziału w tej grze.");
        }

        if(!new MoveValidator().isValidMove(move, board, move.getStoneColour())) {
            throw new IllegalArgumentException("Nielegalny ruch.");
        }

        // Stawia kamień na planszy
        board.getIntersection(move.getCoordinate()).setIntersectionState(convertPlayerColourToIntersectionStateColour(move.getStoneColour()));
    }

    /**
     * Zamienia kolor gracza na kolor przecięcia na planszy.
     *
     * @param playerColour Kolor gracza, który chce się zamienić na kolor przecięcia.
     * @return Kolor przecięcia na planszy.
     */
    public StateOfIntersection convertPlayerColourToIntersectionStateColour(PlayerColour playerColour) {
        if(playerColour == PlayerColour.WHITE) {
            return StateOfIntersection.WHITE;
        }
        return StateOfIntersection.BLACK;
    }

    /**
     * Metoda zapisująca przebieg rozgrywki.
     */
    @Override
    protected void logAction() {

    }
}
