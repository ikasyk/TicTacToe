package me.ikasyk.play;

import me.ikasyk.model.User;
import me.ikasyk.play.ex.*;
import me.ikasyk.play.service.GameService;
import me.ikasyk.play.service.PlayerService;
import me.ikasyk.util.SocketMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Each client, creates when connection between client and server app is established.
 */
public class PlayerSession extends Thread {
    BufferedReader in;
    PrintWriter out;
    private User player;
    private Socket socket;
    private Game activeGame = null;
    private volatile boolean active = true;
    private List<PlayerSession> invites = new ArrayList<>();

    /**
     * Creates a player session by socket.
     *
     * @param socket - the socket of player.
     */
    public PlayerSession(Socket socket) {
        this.socket = socket;
        this.player = new User();

        System.out.println("New user created: " + player.getName());
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);


            while (true) {
                String input = in.readLine();
                if (input == null || input.equals(".")) {
                    break;
                }
                input = input.trim();

                System.out.println("[" + player.getName() + "] " + input);

                if (input.startsWith("/login")) {
                    Matcher m = Pattern.compile("/login ([a-zA-Z_][a-zA-Z_0-9]{2,31}+)").matcher(input);

                    if (m.matches()) {
                        player.setName(m.group(1));
                    } else {
                        out.println(new SocketMessage("Incorrect login. It must consist of letters A-Z, digits 0-9 and underscore _ and not start from digit.").get());
                    }
                } else if (input.startsWith("/invite")) {
                    Matcher m = Pattern.compile("/invite ([0-9]+)").matcher(input);

                    if (m.matches()) {

                        if (Integer.parseInt(m.group(1)) == this.player.id) {
                            out.println(new SocketMessage("You are an egoist.").get());
                        } else {
                            try {
                                PlayerSession visitorSession = PlayerService.Instance.findById(Integer.parseInt(m.group(1)));

                                if (visitorSession != null) {
                                    if (visitorSession.played()) {
                                        out.println(new SocketMessage(visitorSession.getPlayer().getName() + " now is played.").get());
                                    } else {
                                        System.out.println("[" + player.getName() + "] invites [" + visitorSession.getPlayer().getName() + "].");

                                        visitorSession.invite(this);

                                        out.println(new SocketMessage(visitorSession.getPlayer().getName() + " successfully invited.").get());
                                    }
                                } else {
                                    out.println(new SocketMessage("User #" + m.group(1) + " not found.").get());
                                }
                            } catch (UserNotOnlineException e) {
                                out.println(new SocketMessage("User #" + m.group(1) + " exists, but is offline.").get());
                            }

                        }
                    } else {
                        out.println(new SocketMessage("Incorrect command syntax. Use this: /invite [ID]").get());
                    }

                } else if (input.startsWith("/myinvites")) {

                    StringBuilder sb = new StringBuilder();
                    for (PlayerSession playerSession : invites) {
                        if (playerSession.active) {
                            sb.append("#");
                            sb.append(playerSession.getPlayer().id);
                            sb.append(" ");
                            sb.append(playerSession.getPlayer().getName());
                            sb.append("\r\n");
                        }
                    }
                    out.println(new SocketMessage(sb.toString()).get());

                } else if (input.startsWith("/hello") || input.startsWith("/info")) {

                    out.println(new SocketMessage("Hi, " + player.getName() + "! Your ID is " + player.id + ".\r\n" +
                        "To invite a player to game, type /invite [ID].").get());


                } else if (input.startsWith("/accept")) {

                    Matcher m = Pattern.compile("/accept ([0-9]+)").matcher(input);

                    if (m.matches()) {
                        if (Integer.parseInt(m.group(1)) == this.player.id) {
                            out.println(new SocketMessage("You are an egoist.").get());
                        } else {

                            try {
                                PlayerSession creatorSession = PlayerService.Instance.findById(Integer.parseInt(m.group(1)));

                                if (creatorSession != null) {
                                    if (creatorSession.played()) {
                                        out.println(new SocketMessage(creatorSession.getPlayer().getName() + " now is played.").get());
                                    } else {
                                        System.out.println("[" + player.getName() + "] accepts [" + creatorSession.getPlayer().getName() + "].");

                                        accept(creatorSession);

                                        out.println(new SocketMessage(creatorSession.getPlayer().getName() + " successfully accepted.").get());
                                    }
                                } else {
                                    out.println(new SocketMessage("User #" + m.group(1) + " not found.").get());
                                }

                            } catch (UserNotInvitedException ex) {
                                out.println(new SocketMessage("Error: " + ex.getMessage()).get());
                            } catch (UserNotOnlineException ex) {
                                out.println(new SocketMessage("User #" + m.group(1) + " exists, but is offline.").get());
                            }
                        }
                    } else {
                        out.println(new SocketMessage("Incorrect command syntax. Use this: /accept [ID]").get());
                    }

                } else if (input.startsWith("/list")) {
                    StringBuilder sb = new StringBuilder();

                    for (PlayerSession playerSession : PlayerService.Instance.getSessions()) {
                        if (playerSession.active) {
                            sb.append("#");
                            sb.append(playerSession.getPlayer().id);
                            sb.append(" ");
                            sb.append(playerSession.getPlayer().getName());
                            sb.append("\r\n");
                        }
                    }

                    out.println(new SocketMessage(sb.toString()).get());
                } else if (input.startsWith("/move")) {

                    if (this.activeGame == null) {
                        out.println(new SocketMessage("You are not in game.").get());
                    } else {
                        Matcher m = Pattern.compile("/move ([0-9]+) ([0-9]+)").matcher(input);

                        if (m.matches()) {
                            int j = Integer.parseInt(m.group(1)), i = Integer.parseInt(m.group(2));

                            if (i < 1 || i > Field.SIZE || j < 1 || j > Field.SIZE) {
                                out.println(new SocketMessage("Coords are not correct.").get());
                            } else {
                                try {
                                    i--;
                                    j--;
                                    if (activeGame.isCreator(this)) {
                                        activeGame.creatorMove(i, j);
                                    } else if (activeGame.isVisitor(this)) {
                                        activeGame.visitorMove(i, j);
                                    } else {
                                        out.println(new SocketMessage("[Error] You are not in game..").get());
                                    }
                                } catch (GameNotActiveException ex) {
                                    out.println(new SocketMessage("The game is not active..").get());
                                } catch (TurnaroundException ex) {
                                    out.println(new SocketMessage("Not your move.").get());
                                } catch (CellAssignException ex) {
                                    out.println(new SocketMessage("This cell has been assigned.").get());
                                }
                            }

                        } else {
                            out.println(new SocketMessage("Incorrect command syntax. Use this: /move [X] [Y].").get());
                        }
                    }

                } else {
                    out.println(new SocketMessage("Command is not found.").get());
                }
            }
        } catch (IOException ex) {
            System.out.println("Exception in " + player.getName() + ": " + ex);
        } finally {
            try {
                socket.close();
                active = false;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println(player.getName() + " is logged out.");
        }
    }

    /**
     * Returns if user online.
     *
     * @return user status.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns player object.
     *
     * @return data of current session owner.
     */
    public User getPlayer() {
        return player;
    }

    /**
     * Send invite to current user from another user.
     *
     * @param creator - user that sent invite.
     */
    public synchronized void invite(PlayerSession creator) {
        invites.add(creator);
        out.println(new SocketMessage(creator.getPlayer().getName() + " (#" + creator.getPlayer().id + ") invite you to play.").get());
    }

    /**
     * Checks if player of another session invited the current player.
     *
     * @param ps - session of another player.
     * @return if the list of invites contains ps.
     */
    public boolean isInvited(PlayerSession ps) {
        return invites.contains(ps);
    }

    /**
     * Returns if user is busy in game.
     *
     * @return if user has active game.
     */
    public boolean played() {
        return activeGame != null;
    }

    /**
     * Removes the invite from another player session.
     *
     * @param inv - session of another player.
     * @return if remove was success.
     */
    public boolean removeInvite(PlayerSession inv) {
        return invites.remove(inv);
    }

    /**
     * Accepts invite from another player.
     *
     * @param creator - session of another player.
     * @throws UserNotInvitedException when method was called, but the current user wasn't invited by creator.
     */
    public void accept(PlayerSession creator) throws UserNotInvitedException {
        if (removeInvite(creator)) {
            Game game = new Game(creator, this);
            GameService.Instance.addGame(game);
            creator.activeGame = game;
            this.activeGame = game;

            creator.out.println(new SocketMessage("Start game with " + this.getPlayer().getName()).get());
            out.println(new SocketMessage("Start game with " + creator.getPlayer().getName()).get());
        } else {
            throw new UserNotInvitedException("Invite was not sent.");
        }
    }

    /**
     * Print game field.
     *
     * @param field - the field of current game.
     */
    public void drawField(Field field) {
        out.println(new SocketMessage(field.toString()).get());
    }

    /**
     * Reset active game.
     */
    public void gameOver() {
        this.activeGame = null;
    }
}
