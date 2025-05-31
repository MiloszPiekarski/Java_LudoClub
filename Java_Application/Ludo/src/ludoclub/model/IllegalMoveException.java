// Wyjątek rzucany przy nieprawidłowym ruchu pionka
package ludoclub.model;

public class IllegalMoveException extends Exception {
    public IllegalMoveException(String message) {
        super(message);
    }
}
