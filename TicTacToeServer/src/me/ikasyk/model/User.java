package me.ikasyk.model;

/**
 * Data of each user.
 */
public class User {
    private static int globalId = 0;

    public final int id;
    private String name;

    /**
     * Creates a new user for each session.
     */
    public User() {
        id = ++globalId;
        name = "Player" + id;
    }

    /**
     * Returns username of current user.
     * @return username.
     */
    public String getName() {
        return name;
    }

    /**
     * Update the name of current user.
     * @param name - the new username.
     */
    public void setName(String name) {
        this.name = name;
    }
}
