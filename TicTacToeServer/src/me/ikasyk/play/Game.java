package me.ikasyk.play;

import me.ikasyk.play.ex.CellAssignException;
import me.ikasyk.play.ex.GameNotActiveException;
import me.ikasyk.play.ex.TurnaroundException;
import me.ikasyk.util.SocketMessage;

/**
 * Creates when two players ask for a game.
 */
public class Game extends Thread {
    private static int globalId = 0;
    private int id;

    private PlayerSession creator;
    private PlayerSession visitor;
    private Field field;

    private int moveId = 0;
    private boolean active = false;

    /**
     * Creates a new game.
     *
     * @param _creator - session of user who invites another user.
     * @param _visitor - session of user who accepts invitation.
     */
    public Game(PlayerSession _creator, PlayerSession _visitor) {
        field = new Field();
        creator = _creator;
        visitor = _visitor;
    }

    @Override
    public void run() {
        active = true;
        sendField();
    }

    /**
     * Common move for both players.
     *
     * @param i - the Y coordinate of cell where user wants to move.
     * @param j - the X coordinate of cell where user wants to move.
     * @throws CellAssignException    when the cell was assigned before.
     * @throws GameNotActiveException when at least one player go offline.
     */
    protected void move(int i, int j) throws CellAssignException, GameNotActiveException {
        if (active) {
            if (moveId % 2 == 0) {
                field.strictSet(i, j, Field.Cell.X);
            } else {
                field.strictSet(i, j, Field.Cell.O);
            }

            if (field.winStatus != null) {
                active = false;
                sendField();
                win();
            } else {
                moveId++;
                sendField();
            }
        } else {
            throw new GameNotActiveException("Game has already over.");
        }
    }

    /**
     * Move for creator.
     *
     * @param i - the Y coordinate of cell where user wants to move.
     * @param j - the X coordinate of cell where user wants to move.
     * @throws TurnaroundException    if now move visitor.
     * @throws CellAssignException    when the cell was assigned before.
     * @throws GameNotActiveException when at least one player go offline.
     */
    public void creatorMove(int i, int j) throws TurnaroundException, CellAssignException, GameNotActiveException {
        if (moveId % 2 == 0) {
            move(i, j);
        } else {
            throw new TurnaroundException("Now visitor's move.");
        }
    }

    /**
     * Move for visitor.
     *
     * @param i - the Y coordinate of cell where user wants to move.
     * @param j - the X coordinate of cell where user wants to move.
     * @throws TurnaroundException    if now move creator.
     * @throws CellAssignException    when the cell was assigned before.
     * @throws GameNotActiveException when at least one player go offline.
     */
    public void visitorMove(int i, int j) throws TurnaroundException, CellAssignException, GameNotActiveException {
        if (moveId % 2 == 1) {
            move(i, j);
        } else {
            throw new TurnaroundException("Now creator's move.");
        }
    }

    /**
     * Returns if user of session is game creator.
     *
     * @param p - player session.
     * @return true if p is creator's session.
     */
    public boolean isCreator(PlayerSession p) {
        return p == creator;
    }

    /**
     * Returns if user of session is game visitor.
     *
     * @param p - player session.
     * @return true if p is visitor's session.
     */
    public boolean isVisitor(PlayerSession p) {
        return p == visitor;
    }

    /**
     * Prints the field for both players.
     */
    public void sendField() {
        creator.drawField(field);
        visitor.drawField(field);

        if (isVisitorMove()) {
            visitor.out.println(new SocketMessage("Your move. Use /move [X] [Y].").get());
            creator.out.println(new SocketMessage("Wait for a move...").get());
        } else if (isCreatorMove()) {
            creator.out.println(new SocketMessage("Your move. Use /move [X] [Y].").get());
            visitor.out.println(new SocketMessage("Wait for a move...").get());
        }
    }

    /**
     * Prints the winner message for both players.
     */
    private void win() {
        if (moveId % 2 == 0) {
            creator.out.println(new SocketMessage("You are win!").get());
            visitor.out.println(new SocketMessage("\"X\" was win.").get());
        } else {
            creator.out.println(new SocketMessage("\"O\" was win.").get());
            visitor.out.println(new SocketMessage("You are win!").get());
        }
        visitor.gameOver();
        creator.gameOver();
    }

    /**
     * Returns if now move creator.
     *
     * @return true if now moving creator.
     */
    public boolean isCreatorMove() {
        return active && moveId % 2 == 0;
    }

    /**
     * Returns if now move visitor.
     *
     * @return true if now moving visitor.
     */
    public boolean isVisitorMove() {
        return active && moveId % 2 == 1;
    }
}
