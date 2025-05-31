package ludoclub.model;

import java.io.Serializable;
import java.util.Random;
import ludoclub.model.Player;
import ludoclub.model.ColorType;
import ludoclub.model.Pawn;
/**
 * Klasa Bot reprezentuje gracza-robota.
 * Dziedziczy po Player i losowo wybiera pionki do ruchu.
 */
public class Bot extends Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Random rng = new Random();

    /**
     * Konstruktor.
     * @param username nick bota
     * @param colorType kolor bota z ColorType
     */
    public Bot(String username, ColorType colorType) {
        super(username, colorType);
    }

    @Override
    public String getUserType() {
        return "Bot";
    }

    /**
     * Bot wybiera losowo jeden ze swoich pionków, który jest w grze.
     * @return wybrany pawn lub null, jeżeli lista jest pusta
     */
    public Pawn choosePawnToMove() {
        var inGame = getPawns();  // wszystkie pionki należące do bota
        if (inGame.isEmpty()) return null;
        return inGame.get(rng.nextInt(inGame.size()));
    }
}
