package me.ikasyk.play.ex;

/**
 * Throws when user accepts incorrect user.
 */
public class UserNotInvitedException extends Exception {
    public UserNotInvitedException() {
    }

    public UserNotInvitedException(String message) {
        super(message);
    }
}
