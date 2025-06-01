package com.example.gry.go.board;

import java.util.HashSet;
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

    /**
     * Oblicza wynik gracza na podstawie koloru przecięcia.
     *
     * @param colour Kolor przecięcia, dla którego ma zostać obliczony wynik.
     * @return Wynik dla danego koloru przecięcia.
     */
    public int calculateScore(StateOfIntersection colour) {
        Set<Coordinate> visited = new HashSet<>();

        // Dodajemy wszystkie przecięcia na planszy do zbioru visited, żeby potem z niego usuwać
        for(int row = 0; row < size; row++) {
            for(int column = 0; column < size; column++) {
                visited.add(intersections[row][column].getCoordinate());
            }
        }

        // Jeśli przecięcie jest zajęte przez dany kolor, usuwamy je z visited
        for(int row = 0; row < size; row++) {
            for(int column = 0; column < size; column++) {
                if(intersections[row][column].getIntersectionState() == colour) {
                    visited.remove(intersections[row][column].getCoordinate());
                }
            }
        }

        return 0;
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


}
