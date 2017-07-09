package me.ikasyk.play.ex;

import java.io.IOException;

/**
 * Throws when player moves not in his move.
 */
public class TurnaroundException extends IOException {
    public TurnaroundException() {
    }

    public TurnaroundException(String message) {
        super(message);
    }
}
