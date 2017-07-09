package me.ikasyk.play.ex;

import java.io.IOException;

/**
 * Throws when the current cell has a value.
 */
public class CellAssignException extends IOException {
    public CellAssignException() {
    }

    public CellAssignException(String message) {
        super(message);
    }
}
