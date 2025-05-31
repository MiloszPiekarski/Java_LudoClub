package ludoclub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import ludoclub.model.User;
import ludoclub.model.Pawn;
import ludoclub.model.ColorType;

/**
 * Reprezentuje gracza w grze Ludo.
 */
public class Player extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Pawn> pawns = new ArrayList<>();
    private boolean finished = false;

    /** @param username nick gracza
     *  @param colorType wybrany ColorType */
    public Player(String username, ColorType colorType) {
        super(username, colorType);
    }

    /** Dodaje pionek do listy */
    public void addPawn(Pawn pawn) {
        pawns.add(pawn);
    }

    /** @return lista pionków gracza */
    public List<Pawn> getPawns() {
        return List.copyOf(pawns);
    }

    public boolean isFinished() {
        return finished;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String getUserType() {
        return "Player";
    }
}
