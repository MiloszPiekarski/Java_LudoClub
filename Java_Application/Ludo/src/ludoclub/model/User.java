package ludoclub.model;

import java.io.Serializable;

/**
 * Bazowa klasa użytkownika (Player/Bot) przechowująca nick i ColorType.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String username;
    protected ludoclub.model.ColorType colorType;

    public User(String username, ludoclub.model.ColorType colorType) {
        this.username  = username;
        this.colorType = colorType;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return enum z kolorem gracza */
    public ludoclub.model.ColorType getColorType() {
        return colorType;
    }
    public void setColorType(ludoclub.model.ColorType colorType) {
        this.colorType = colorType;
    }

    /** Zwraca typ: "Player" lub "Bot". */
    public abstract String getUserType();
}
