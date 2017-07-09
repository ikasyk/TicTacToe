package me.ikasyk.play.ex;

import java.io.IOException;

/**
 * Throws when the game is not active (user is offline, game is over etc).
 */
public class GameNotActiveException extends IOException {
    public GameNotActiveException() {
    }

    public GameNotActiveException(String message) {
        super(message);
    }

}
