package com.example.gry.go.board;

/**
 * Reprezentuje przecięcie na planszy gry Go.
 */
public class Intersection {
    /**
     * Współrzędne przecięcia na planszy.
     */
    private final Coordinate coordinate;

    /**
     * Stan przecięcia. Może być puste, zajęte przez czarny kamień lub biały kamień.
     */
    private StateOfIntersection intersectionState;

    /**
     * Konstruktor klasy Intersection.
     * @param coordinate Współrzędne przecięcia na planszy.
     * @param intersectionState Stan przecięcia. Może być puste, zajęte przez czarny kamień lub biały kamień.
     */
    public Intersection(Coordinate coordinate, StateOfIntersection intersectionState) {
        try {
            this.coordinate = coordinate;
        }
        catch (NegativeCoordinatesException e) {
            throw new NegativeCoordinatesException("Współrzędne nie mogą być ujemne.", e);
        } catch (Exception e) {
            throw new RuntimeException("Nieznany błąd podczas inicjalizacji współrzędnych.", e);
        }
        this.intersectionState = intersectionState;
    }

    /**
     * @return Współrzędne przecięcia na planszy.
     */
    public Coordinate getCoordinate() {
        return this.coordinate;
    }

    /**
     * @return Stan przecięcia. Może być puste, zajęte przez czarny kamień lub biały kamień.
     */
    public StateOfIntersection getIntersectionState() {
        return this.intersectionState;
    }

    /**
     * Ustawia nowy stan przecięcia.
     *
     * @param newIntersectionState Nowy stan przecięcia. Może być puste, zajęte przez czarny kamień lub biały kamień.
     */
    public void setIntersectionState(StateOfIntersection newIntersectionState) {
        if(newIntersectionState == null) {
            throw new IllegalArgumentException("Stan przecięcia nie może być null.");
        }

        if(this.intersectionState == StateOfIntersection.BLACK && newIntersectionState == StateOfIntersection.WHITE) {
            throw new InvalidChangeOfIntersectionStateException("Nie można zmienić stanu przecięcia z czarnego na białe.");
        }

        if(this.intersectionState == StateOfIntersection.WHITE && newIntersectionState == StateOfIntersection.BLACK) {
            throw new InvalidChangeOfIntersectionStateException("Nie można zmienić stanu przecięcia z białego na czarne");
        }

        this.intersectionState = newIntersectionState;
    }

    @Override
    public String toString() {
        return coordinate.toString() + ": " + intersectionState;
    }
}
