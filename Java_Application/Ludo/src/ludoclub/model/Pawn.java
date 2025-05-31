package ludoclub.model;

import java.awt.Point;
import java.io.Serializable;
import java.util.List;
import ludoclub.model.Player;

/**
 * Reprezentuje pojedynczy pionek w grze Ludo.
 * Obsługuje: wyjście z bazy po wyrzuceniu 6, ruch po ścieżce, wejście na metę.
 */
public class Pawn implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Player owner;
    private final int homeRow, homeCol;
    private final List<Point> path;
    private int position;       // 0 = w bazie, 1..N = kolejne pola na ścieżce
    private boolean atHome;     // true = w bazie
    private boolean atGoal;     // true = pionek na mecie (środek)

    public Pawn(Player owner, int homeRow, int homeCol, List<Point> path) {
        this.owner   = owner;
        this.homeRow = homeRow;
        this.homeCol = homeCol;
        this.path    = path;
        this.position = 0;
        this.atHome   = true;
        this.atGoal   = false;
    }

    /** Czy pionek wciąż w bazie (domku)? */
    public boolean isAtHome() { return atHome; }

    /** Czy pionek dotarł do mety (czarne pole w środku)? */
    public boolean isAtGoal() { return atGoal; }

    public Player getOwner() { return owner; }

    public int getPosition() { return position; }

    public List<Point> getPath() { return path; }

    /**
     * Zwraca bieżące współrzędne pionka na planszy:
     *  - w bazie: (homeRow, homeCol)
     *  - na planszy: ścieżka.get(position-1)
     *  - na mecie: (6,6)
     */
    public Point getCoordinates() {
        if (atGoal) {
            return new Point(6,6); // środek planszy
        } else if (atHome || path.isEmpty() || position <= 0) {
            return new Point(homeRow, homeCol);
        } else {
            return path.get(position - 1);
        }
    }

    /**
     * Przewiduje do jakiego pola trafi pionek po ruchu (bez ruszania pionka!).
     */
    public Point predictCoordinates(int steps) {
        if (atGoal) {
            return new Point(6, 6);
        }
        if (atHome) {
            if (steps == 6 && !path.isEmpty()) {
                return path.get(0);
            } else {
                return new Point(homeRow, homeCol);
            }
        } else {
            int dest = position + steps;
            if (dest > path.size()) {
                // nielegalny ruch, pionek nie ruszy się
                return getCoordinates();
            }
            if (dest == path.size()) {
                return new Point(6, 6); // meta
            } else {
                return path.get(dest - 1);
            }
        }
    }

    /**
     * Czy pionek może wykonać ruch o podaną liczbę kroków (czy nie przeskoczy mety)?
     */
    public boolean canMove(int steps) {
        if (atGoal) return false; // już w mecie
        if (atHome) {
            // może wyjść z bazy tylko przy 6 i jeśli są wolne pola
            return steps == 6 && !path.isEmpty();
        }
        int dest = position + steps;
        // pozycja == path.size() => wejście do mety (środek)
        return dest <= path.size();
    }

    /**
     * Wykonuje ruch pionka o daną liczbę kroków.
     * Obsługuje wyjście z bazy, ruch po planszy, wejście do mety.
     * Zwraca true jeśli ruch zakończony sukcesem, false jeśli nielegalny.
     */
    public boolean move(int steps) {
        if (!canMove(steps)) return false;

        if (atHome) {
            if (steps == 6) {
                atHome = false;
                position = 1;
            }
            return true;
        } else {
            position += steps;
            if (position == path.size()) {
                atGoal = true;
            }
            return true;
        }
    }

    /**
     * Czy pionek może się ruszyć o daną liczbę pól (czy nie wyjdzie poza metę, itd.)
     * Ta metoda jest do wykorzystania przez GUI, by podświetlić możliwe pionki.
     */
    public boolean canMovePawn(int steps) {
        return canMove(steps);
    }

    /**
     * Resetuje pionek do bazy (po skuciu przez innego gracza).
     */
    public void resetToBase() {
        atHome = true;
        atGoal = false;
        position = 0;
    }
}