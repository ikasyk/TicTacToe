package me.ikasyk.play.service;

import me.ikasyk.play.PlayerSession;
import me.ikasyk.play.ex.UserNotOnlineException;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to provide connection between players.
 */
public final class PlayerService {
    public static PlayerService Instance = new PlayerService();

    private List<PlayerSession> sessions = new ArrayList<>();

    private PlayerService() {
    }

    /**
     * Adds new user session to database.
     * @param ss - the current session.
     * @return session object.
     */
    public synchronized PlayerSession addSession(PlayerSession ss) {
        sessions.add(ss);
        return ss;
    }

    /**
     * Finds user session by player id.
     * @param id - number of the player.
     * @return the user session.
     * @throws UserNotOnlineException when session was found, but user is offline.
     */
    public PlayerSession findById(int id) throws UserNotOnlineException {
        for (PlayerSession it : sessions) {
            if (it.getPlayer().id == id) {
                if (!it.isActive()) {
                    throw new UserNotOnlineException();
                } else {
                    return it;
                }
            }
        }
        return null;
    }

    /**
     * Returns reference to player session list.
     * @return players sessions list.
     */
    public List<PlayerSession> getSessions() {
        return sessions;
    }
}
