package me.ikasyk.play.ex;

/**
 * Throws when user was found but is offline.
 */
public class UserNotOnlineException extends Exception {
    public UserNotOnlineException() {
    }

    public UserNotOnlineException(String message) {
        super(message);
    }
}
