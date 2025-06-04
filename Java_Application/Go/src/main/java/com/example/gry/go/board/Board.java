package com.example.gry.go.board;

import com.example.gry.go.player.PlayerColour;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Board {
    /**
     * Rozmiar planszy (długość boku planszy).
     */
    private final int size;

    /**
     * Plansza do gry w Go. Tablica dwuwymiarowa, gdzie każdy element to przecięcie.
     */
    private final Intersection[][] intersections;

    private final int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    /**
     * Konstruktor klasy Board.
     *
     * @param size Rozmiar planszy (długość boku planszy).
     */
    public Board(int size) {
        if(size <= 0) {
            // Ewentualnie można przypisać rozmiar domyślny np. 9×9
            throw new IllegalArgumentException("Rozmiar planszy musi być większy od 0.");
        }
        this.size = size;
        this.intersections = new Intersection[size][size];
        try {
            this.initializeBoard();
        }
        catch (NegativeCoordinatesException e) {
            throw new IllegalArgumentException("Współrzędne nie mogą być ujemne.", e);
        }
        catch (InvalidChangeOfIntersectionStateException e) {
            throw new InvalidChangeOfIntersectionStateException("Nieprawidłowa zmiana stanu przecięcia.", e);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nieprawidłowy argument.", e);
        }
        catch (BoardInitializationException e) {
            throw new BoardInitializationException("Nieznany błąd podczas inicjalizacji planszy.", e);
        }
        catch (Exception e) {
            throw new RuntimeException("Nieznany błąd podczas inicjalizacji planszy.", e);
        }
    }

    public Board(Board other) {
        this.size = other.size;
        this.intersections = new Intersection[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                StateOfIntersection state = other.intersections[row][col].getIntersectionState();
                this.intersections[row][col] = new Intersection(new Coordinate(row, col), state);
            }
        }
    }

    /**
     * Inicjalizuje planszę do gry w Go. Wszystkie przecięcia są puste.
     */
    private void initializeBoard() {
        for(int row = 0; row < size; row++) {
            for(int column = 0; column < size; column++) {
                try {
                    Coordinate coordinate = new Coordinate(row, column);
                    intersections[row][column] = new Intersection(coordinate, StateOfIntersection.EMPTY);
                }
                catch (NegativeCoordinatesException e) {
                    throw new NegativeCoordinatesException("Współrzędne nie mogą być ujemne.", e);
                }
                catch (InvalidChangeOfIntersectionStateException e) {
                    throw new InvalidChangeOfIntersectionStateException("Nieprawidłowa zmiana stanu przecięcia.", e);
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Nieprawidłowy argument.", e);
                }
                catch (Exception e) {
                    throw new BoardInitializationException("Nieznany błąd podczas inicjalizacji planszy.", e);
                }
            }
        }
    }

    private void exploreTerritory(int row, int col, boolean[][] visited,
                                  Set<Intersection> region, Set<StateOfIntersection> borderingColors) {
        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(new Coordinate(row, col));
        visited[row][col] = true;

        // BFS – przeszukujemy wszystkie puste pola połączone z początkowym
        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int r = current.row();
            int c = current.column();

            Intersection intersection = intersections[r][c];
            region.add(intersection);

            // Sprawdzamy sąsiadów w czterech kierunkach
            for (int[] dir : directions()) {
                int newRow = r + dir[0];
                int newCol = c + dir[1];

                // Sprawdzenie granic planszy
                if (!isInBounds(newRow, newCol)) continue;

                // Jeśli pole już odwiedzone, pomijamy
                if (visited[newRow][newCol]) continue;

                Intersection neighbor = intersections[newRow][newCol];

                if (neighbor.isEmpty()) {
                    // Jeśli sąsiadujące pole jest puste — dodaj do kolejki do dalszego przeszukiwania
                    queue.add(new Coordinate(newRow, newCol));
                    visited[newRow][newCol] = true;
                } else {
                    // Jeśli pole nie jest puste — zapamiętaj jego kolor jako graniczny
                    borderingColors.add(neighbor.getIntersectionState());
                }
            }
        }
    }

    /**
     * Oblicza wynik gracza na podstawie koloru przecięcia.
     *
     * @param colour Kolor przecięcia, dla którego ma zostać obliczony wynik.
     * @return Wynik dla danego koloru przecięcia.
     */
    public int calculateScore(StateOfIntersection colour) {
        boolean[][] visited = new boolean[size][size]; // oznaczenie odwiedzonych pól
        int score = 0; // wynik końcowy

        // Iterujemy przez każde pole planszy
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Intersection intersection = intersections[row][col];

                // 1. Zliczamy własne kamienie jako punkty
                if (intersection.getIntersectionState() == colour) {
                    score++;
                }

                // 2. Analizujemy potencjalne terytorium
                else if (intersection.isEmpty() && !visited[row][col]) {
                    Set<Intersection> region = new HashSet<>(); // puste pola należące do jednego obszaru
                    Set<StateOfIntersection> borderingColours = new HashSet<>(); // kolory wokół obszaru

                    // Przeglądamy pusty obszar i zbieramy otaczające kolory
                    exploreTerritory(row, col, visited, region, borderingColours);

                    // Jeśli cały pusty obszar jest otoczony przez jeden kolor (czyli terytorium gracza),
                    // dodajemy jego rozmiar do wyniku gracza
                    if (borderingColours.size() == 1 && borderingColours.contains(colour)) {
                        score += region.size();
                    }
                }
            }
        }

        return score;
    }

    public int captureGroupsIfNoLiberties(int row, int col, StateOfIntersection currentColor) {
        StateOfIntersection opponent = (currentColor == StateOfIntersection.BLACK)
                ? StateOfIntersection.WHITE
                : StateOfIntersection.BLACK;

        int capturedCount = 0;

        for (int[] dir : directions()) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (!isInBounds(newRow, newCol)) continue;

            Intersection neighbor = getIntersection(newRow, newCol);
            if (neighbor.getIntersectionState() == opponent) {
                if (!hasLiberties(newRow, newCol)) {
                    Set<Intersection> group = collectGroup(newRow, newCol, opponent);
                    capturedCount += group.size();
                    clearGroup(group);
                }
            }
        }

        return capturedCount;
    }

    public Set<Intersection> collectGroup(int row, int col, StateOfIntersection color) {
        Set<Intersection> group = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(new Coordinate(row, col));
        group.add(intersections[row][col]);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int r = current.row();
            int c = current.column();

            for (int[] dir : directions) {
                int newRow = r + dir[0];
                int newCol = c + dir[1];

                if (!isInBounds(newRow, newCol)) continue;

                Intersection neighbor = intersections[newRow][newCol];
                if (neighbor.getIntersectionState() == color && !group.contains(neighbor)) {
                    group.add(neighbor);
                    queue.add(new Coordinate(newRow, newCol));
                }
            }
        }

        return group;
    }

    private boolean groupHasLiberties(Set<Intersection> group) {
        for (Intersection stone : group) {
            int row = stone.getCoordinate().row();
            int col = stone.getCoordinate().column();

            for (int[] dir : directions()) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (isInBounds(newRow, newCol) && getIntersection(newRow, newCol).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void clearGroup(Set<Intersection> group) {
        if (group == null) return;

        for (Intersection stone : group) {
            if (stone != null) {
                stone.setIntersectionState(StateOfIntersection.EMPTY);
            }
        }
    }

    private int[][] directions() {
        return new int[][] {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };
    }

    public boolean hasLiberties(int row, int column) {
        if (!isInBounds(row, column)) return false;

        StateOfIntersection color = intersections[row][column].getIntersectionState();
        if (color == StateOfIntersection.EMPTY) return false;

        boolean[][] visited = new boolean[size][size];
        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(new Coordinate(row, column));
        visited[row][column] = true;

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int r = current.row();
            int c = current.column();

            for (int[] dir : directions) {
                int nr = r + dir[0];
                int nc = c + dir[1];

                if (!isInBounds(nr, nc)) continue;
                if (visited[nr][nc]) continue;

                StateOfIntersection neighborState = intersections[nr][nc].getIntersectionState();

                if (neighborState == StateOfIntersection.EMPTY) {
                    return true; // Oddech znaleziony
                }

                if (neighborState == color) {
                    visited[nr][nc] = true;
                    queue.add(new Coordinate(nr, nc));
                }
            }
        }

        return false; // Brak oddechów
    }

    public void resetBoard() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                try {
                    intersections[row][col].setIntersectionState(StateOfIntersection.EMPTY);
                } catch (Exception e) {
                    System.err.println("Błąd resetowania przecięcia: " + e.getMessage());
                }
            }
        }
    }

    /**
     * @return Rozmiar planszy (długość boku planszy).
     */
    public int getSize() {
        return size;
    }

    /**
     * @param coordinate Współrzędne przecięcia na planszy.
     * @return Przecięcie na planszy w podanych współrzędnych.
     */
    public Intersection getIntersection(Coordinate coordinate) {
        if(isInBounds(coordinate)) {
            return intersections[coordinate.row()][coordinate.column()];
        }

        throw new CoordinatesOutsideTheBoardException("Współrzędne są poza zakresem planszy.");
    }

    /**
     * @param row Współrzędna wiersza
     * @param column Współrzędna kolumny
     * @return Przecięcie na planszy w podanych współrzędnych.
     */
    public Intersection getIntersection(int row, int column) {
        if(isInBounds(row, column)) {
            return intersections[row][column];
        }

        throw new CoordinatesOutsideTheBoardException("Współrzędne są poza zakresem planszy.");
    }

    public Intersection setBoard(Coordinate coordinate, StateOfIntersection state) {
        intersections[coordinate.row()][coordinate.column()].setIntersectionState(state);
        return intersections[coordinate.row()][coordinate.column()];
    }

    public void updateIntersection(Coordinate coordinate, StateOfIntersection state) {
        intersections[coordinate.row()][coordinate.column()].setIntersectionState(state);
    }

    /**
     * Metoda sprawdzająca, czy podane współrzędne są w obrębie planszy.
     *
     * @param coordinate Współrzędne do sprawdzenia.
     * @return true, jeśli współrzędne są w obrębie planszy, false w przeciwnym razie.
     */
    public boolean isInBounds(Coordinate coordinate) {
        return coordinate.row() >= 0 && coordinate.row() < size &&
               coordinate.column() >= 0 && coordinate.column() < size;
    }

    /**
     * Metoda sprawdzająca, czy podane współrzędne są w obrębie planszy.
     *
     * @param row Współrzędna wiersza
     * @param column Współrzędna kolumny
     * @return true, jeśli współrzędne są w obrębie planszy, false w przeciwnym razie.
     */
    public boolean isInBounds(int row, int column) {
        return row >= 0 && row < size && column >= 0 && column < size;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Board other)) return false;
        if (this.size != other.size) return false;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (this.intersections[row][col].getIntersectionState() !=
                        other.intersections[row][col].getIntersectionState()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = size;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                result = 31 * result + intersections[row][col].getIntersectionState().hashCode();
            }
        }
        return result;
    }
}
