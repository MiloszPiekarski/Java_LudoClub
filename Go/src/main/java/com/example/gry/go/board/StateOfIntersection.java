package com.example.gry.go.board;

/**
 * Możliwe stany przecięcia na planszy.
 */
public enum StateOfIntersection {
    /**
     * Stan przecięcia, gdy jest puste.
     */
    EMPTY,

    /**
     * Stan przecięcia, gdy jest zajęte przez czarny kamień.
     */
    BLACK,

    /**
     * Stan przecięcia, gdy jest zajęte przez biały kamień.
     */
    WHITE
}
