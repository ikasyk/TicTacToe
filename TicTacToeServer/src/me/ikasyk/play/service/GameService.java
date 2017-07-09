package me.ikasyk.play.service;

import me.ikasyk.play.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to provide connection between games.
 */
public class GameService {
    public static GameService Instance = new GameService();

    private List<Game> games = new ArrayList<>();

    private GameService() {
    }

    /**
     * Adds new game to database.
     * @param game - created game.
     * @return game object.
     */
    public synchronized Game addGame(Game game) {
        games.add(game);
        game.start();
        return game;
    }
}
