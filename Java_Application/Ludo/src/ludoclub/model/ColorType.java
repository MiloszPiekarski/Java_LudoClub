package ludoclub.model;

import javafx.scene.paint.Color;
import java.io.Serializable;

/**
 * Enum reprezentujący kolory graczy i ich warianty
 */
public enum ColorType implements Serializable {
    // Oryginalne
    RED(Color.RED),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),

    // Jaśniejsze/ciemniejsze wersje
    LIGHT_RED(Color.web("#FF6060")),
    LIGHT_BLUE(Color.web("#68AFFF")),
    LIGHT_GREEN(Color.web("#60E075")),
    DARK_YELLOW(Color.web("#FFD300"));

    private final Color fxColor;
    ColorType(Color fxColor) { this.fxColor = fxColor; }
    public Color getFxColor() { return fxColor; }
}