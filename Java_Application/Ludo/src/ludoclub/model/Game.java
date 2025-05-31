package ludoclub.model;

import java.awt.Point;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import ludoclub.model.Player;
import ludoclub.model.ColorType;
import ludoclub.model.Pawn;

public class Game implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Player> players = new ArrayList<>();
    private int currentPlayerIdx = 0;

    // Pola startowe
    public static final List<Point> START_FIELDS = List.of(
            new Point(5, 1),    // RED
            new Point(1, 7),    // BLUE
            new Point(11, 5),   // GREEN
            new Point(7, 11)    // YELLOW
    );

    private static final Point[] RED_BASE = {
            new Point(1,1), new Point(1,3),
            new Point(3,1), new Point(3,3)
    };
    private static final Point[] BLUE_BASE = {
            new Point(1,11), new Point(1,9),
            new Point(3,11), new Point(3,9)
    };
    private static final Point[] GREEN_BASE = {
            new Point(11,1), new Point(11,3),
            new Point(9,1),  new Point(9,3)
    };
    private static final Point[] YELLOW_BASE = {
            new Point(11,11), new Point(11,9),
            new Point(9,11),  new Point(9,9)
    };

    // --- Walk tables ---
    private final List<Point> redPath    = Arrays.asList(
            new Point(5, 1), new Point(5, 2), new Point(5, 3), new Point(5, 4),
            new Point(5, 5), new Point(4, 5), new Point(3, 5), new Point(2, 5),
            new Point(1, 5), new Point(0, 5), new Point(0, 6), new Point(0, 7),
            new Point(1, 7), new Point(2, 7), new Point(3, 7), new Point(4, 7),
            new Point(5, 7), new Point(5, 8), new Point(5, 9), new Point(5, 10),
            new Point(5, 11), new Point(5, 12), new Point(6, 12), new Point(7, 12),
            new Point(7, 11), new Point(7, 10), new Point(7, 9), new Point(7, 8),
            new Point(7, 7), new Point(8, 7), new Point(9, 7), new Point(10, 7),
            new Point(11, 7), new Point(12, 7), new Point(12, 6), new Point(12, 5),
            new Point(11, 5), new Point(10, 5), new Point(9, 5), new Point(8, 5),
            new Point(7, 5), new Point(7, 4), new Point(7, 3), new Point(7, 2),
            new Point(7, 1), new Point(7, 0), new Point(6, 0), new Point(6, 1),
            new Point(6, 2), new Point(6, 3), new Point(6, 4), new Point(6, 5),
            new Point(6, 6)
    );

    private final List<Point> bluePath   = Arrays.asList(
            new Point(1, 7), new Point(2, 7), new Point(3, 7), new Point(4, 7),
            new Point(5, 7), new Point(5, 8), new Point(5, 9), new Point(5, 10),
            new Point(5, 11), new Point(5, 12), new Point(6, 12), new Point(7, 12),
            new Point(7, 11), new Point(7, 10), new Point(7, 9), new Point(7, 8),
            new Point(7, 7), new Point(8, 7), new Point(9, 7), new Point(10, 7),
            new Point(11, 7), new Point(12, 7), new Point(12, 6), new Point(12, 5),
            new Point(11, 5), new Point(10, 5), new Point(9, 5), new Point(8, 5),
            new Point(7, 5), new Point(7, 4), new Point(7, 3), new Point(7, 2),
            new Point(7, 1), new Point(7, 0), new Point(6, 0), new Point(5, 0),
            new Point(5, 1), new Point(5, 2), new Point(5, 3), new Point(5, 4),
            new Point(5, 5), new Point(4, 5), new Point(3, 5), new Point(2, 5),
            new Point(1, 5), new Point(0, 5), new Point(0, 6), new Point(1, 6),
            new Point(2, 6), new Point(3, 6), new Point(4, 6), new Point(5, 6),
            new Point(6, 6)
    );

    private final List<Point> greenPath  = Arrays.asList(
            new Point(11, 5), new Point(10, 5), new Point(9, 5), new Point(8, 5),
            new Point(7, 5), new Point(7, 4), new Point(7, 3), new Point(7, 2),
            new Point(7, 1), new Point(7, 0), new Point(6, 0), new Point(5, 0),
            new Point(5, 1), new Point(5, 2), new Point(5, 3), new Point(5, 4),
            new Point(5, 5), new Point(4, 5), new Point(3, 5), new Point(2, 5),
            new Point(1, 5), new Point(0, 5), new Point(0, 6), new Point(0, 7),
            new Point(1, 7), new Point(2, 7), new Point(3, 7), new Point(4, 7),
            new Point(5, 7), new Point(5, 8), new Point(5, 9), new Point(5, 10),
            new Point(5, 11), new Point(5, 12), new Point(6, 12), new Point(7, 12),
            new Point(7, 11), new Point(7, 10), new Point(7, 9), new Point(7, 8),
            new Point(7, 7), new Point(8, 7), new Point(9, 7), new Point(10, 7),
            new Point(11, 7), new Point(12, 7), new Point(12, 6), new Point(11, 6),
            new Point(10, 6), new Point(9, 6), new Point(8, 6), new Point(7, 6),
            new Point(6, 6)
    );

    private final List<Point> yellowPath = Arrays.asList(
            new Point(7, 11), new Point(7, 10), new Point(7, 9), new Point(7, 8),
            new Point(7, 7), new Point(8, 7), new Point(9, 7), new Point(10, 7),
            new Point(11, 7), new Point(12, 7), new Point(12, 6), new Point(12, 5),
            new Point(11, 5), new Point(10, 5), new Point(9, 5), new Point(8, 5),
            new Point(7, 5), new Point(7, 4), new Point(7, 3), new Point(7, 2),
            new Point(7, 1), new Point(7, 0), new Point(6, 0), new Point(5, 0),
            new Point(5, 1), new Point(5, 2), new Point(5, 3), new Point(5, 4),
            new Point(5, 5), new Point(4, 5), new Point(3, 5), new Point(2, 5),
            new Point(1, 5), new Point(0, 5), new Point(0, 6), new Point(0, 7),
            new Point(1, 7), new Point(2, 7), new Point(3, 7), new Point(4, 7),
            new Point(5, 7), new Point(5, 8), new Point(5, 9), new Point(5, 10),
            new Point(5, 11), new Point(5, 12), new Point(6, 12), new Point(6, 11),
            new Point(6, 10), new Point(6, 9), new Point(6, 8), new Point(6, 7),
            new Point(6, 6)
    );

    public Game() {}

    public void addPlayer(Player player) {
        ColorType color = player.getColorType();
        Point[] base;
        List<Point> path;
        switch (color) {
            case RED    -> { base = RED_BASE;    path = redPath; }
            case BLUE   -> { base = BLUE_BASE;   path = bluePath; }
            case GREEN  -> { base = GREEN_BASE;  path = greenPath; }
            case YELLOW -> { base = YELLOW_BASE; path = yellowPath; }
            default     -> throw new IllegalStateException("Nieobsługiwany kolor: " + color);
        }
        for (Point b : base) {
            Pawn p = new Pawn(player, b.x, b.y, path);
            player.addPawn(p);
        }
        players.add(player);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIdx);
    }

    public void nextTurn() {
        if (!players.isEmpty())
            currentPlayerIdx = (currentPlayerIdx + 1) % players.size();
    }

    public List<Player> getPlayers() { return players; }

    // Czy pole jest polem startowym?
    public boolean isStartField(Point p) {
        for (Point s : START_FIELDS) {
            if (s.equals(p)) return true;
        }
        return false;
    }

    // Liczba pionków na danym polu (w grze, nie w bazie, nie na mecie)
    public int countPawnsOnField(Point pt) {
        int count = 0;
        for (Player pl : players) {
            for (Pawn pw : pl.getPawns()) {
                if (!pw.isAtHome() && !pw.isAtGoal() && pw.getCoordinates().equals(pt)) count++;
            }
        }
        return count;
    }

    // Czy obecny gracz ukończył grę (wszystkie pionki na mecie)
    public boolean isCurrentPlayerFinished() {
        Player p = getCurrentPlayer();
        return p.getPawns().stream().allMatch(Pawn::isAtGoal);
    }

    // Główna metoda ruchu, z kuciem
    public boolean tryMovePawn(Pawn pawn, int dice) {
        if (!pawn.canMove(dice)) return false;
        Point target = pawn.predictCoordinates(dice);

        // KUCIE (tylko na szarych, tylko 1 pionek przeciwnika, nie na polu startowym)
        if (!isStartField(target)) {
            for (Player other : players) {
                if (other == pawn.getOwner()) continue;
                List<Pawn> others = other.getPawns().stream()
                        .filter(p -> !p.isAtHome() && !p.isAtGoal() && p.getCoordinates().equals(target))
                        .collect(Collectors.toList());
                if (others.size() == 1 && countPawnsOnField(target) == 1) {
                    others.get(0).resetToBase();
                }
            }
        }

        return pawn.move(dice);
    }

    public boolean canMovePawn(Pawn pawn, int dice) {
        return pawn.canMovePawn(dice);
    }

    public int countGoalPawns(Player player) {
        return (int) player.getPawns().stream().filter(Pawn::isAtGoal).count();
    }
}