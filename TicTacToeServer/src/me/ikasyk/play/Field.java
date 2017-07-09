package me.ikasyk.play;

import me.ikasyk.play.ex.CellAssignException;

/**
 * Playing process of each game.
 */
public class Field {
    // Field size.
    public static final int SIZE = 20;

    // Count of "X" or "O" in line or diagonal for win.
    public static final int WIN_LINE = 5;

    public Win winStatus = null;
    private Cell[][] data = new Cell[SIZE][SIZE];

    /**
     * Creates empty field.
     */
    public Field() {
        clear();
    }

    /**
     * Resets all cells of field.
     */
    public void clear() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                data[i][j] = Cell.NOTHING;
            }
        }
    }

    /**
     * Returns the current cell of the field.
     *
     * @param i - the Y coord of cell.
     * @param j - the X coord of cell.
     * @return the current cell if its found.
     */
    public Cell get(int i, int j) {
        if (i > SIZE - 1 || j > SIZE - 1 || i < 0 || j < 0) {
            throw new IllegalArgumentException("Coords of field out of bounds.");
        }
        return data[i][j];
    }

    /**
     * Updates the value of the current cell and checks the win status.
     *
     * @param i     - the Y coord of cell.
     * @param j     - the X coord of cell.
     * @param value - the value of cell that will be set.
     */
    public void set(int i, int j, Cell value) {
        if (i > SIZE - 1 || j > SIZE - 1 || i < 0 || j < 0) {
            throw new ArrayIndexOutOfBoundsException("Coords of field out of bounds.");
        }
        if (value == Cell.NOTHING) {
            throw new IllegalArgumentException("NOTHING cell cannot assign.");
        }
        data[i][j] = value;
        checkWin(i, j);
    }

    /**
     * Updates the value of the current cell only if it hasn't updated yet.
     *
     * @param i     - the Y coord of cell.
     * @param j     - the X coord of cell.
     * @param value - the value of cell that will be set.
     * @throws CellAssignException when the cell has a value.
     */
    public void strictSet(int i, int j, Cell value) throws CellAssignException {
        if (i > SIZE - 1 || j > SIZE - 1 || i < 0 || j < 0) {
            throw new ArrayIndexOutOfBoundsException("Coords of field out of bounds.");
        }
        if (data[i][j] != Cell.NOTHING) {
            throw new CellAssignException("Cell {" + i + "; " + j + "} has already assigned.");
        }

        set(i, j, value);
    }

    /**
     * Values of each cell.
     */
    public enum Cell {
        X, O, NOTHING;

        public static String cell2str(Cell cell) {
            if (cell == X) return "X";
            else if (cell == O) return "O";
            return " ";
        }
    }

    /**
     * Game winner data.
     */
    static class Win {
        public final int i, j;
        public final Dir direction;
        public Win(Dir dir, int i, int j) {
            direction = dir;
            this.i = i;
            this.j = j;
        }

        enum Dir {
            H, V, D, SD
        }
    }

    /**
     * Checks if user win.
     *
     * @param i - the Y coord of updated cell.
     * @param j - the X coord of updated cell.
     */
    protected void checkWin(int i, int j) {
        Cell cell = get(i,j);

        int i_to = i, i_from = i, j_from = j, j_to = j;
        boolean win = false;

        // Horizontal
        while (j_from >= 0 && data[i][j_from] == cell) j_from--;
        while (j_to < SIZE && data[i][j_to] == cell) j_to++;

        if (j_to - j_from - 1 >= WIN_LINE) {
            winStatus = new Win(Win.Dir.H, i_from, i_to);
            return;
        }

        i_to = i; i_from = i; j_from = j; j_to = j;
        // Vertical
        while (i_from >= 0 && data[i_from][j] == cell) i_from--;
        while (i_to < SIZE && data[i_to][j] == cell) i_to++;

        if (i_to - i_from - 1 >= WIN_LINE) {
            winStatus = new Win(Win.Dir.V, i_from, i_to);
            return;
        }

        i_to = i; i_from = i; j_from = j; j_to = j;
        // Main diagonal
        while (i_from >= 0 && j_from >= 0 && data[i_from][j_from] == cell) {i_from--; j_from--;}
        while (i_to < SIZE && j_to < SIZE && data[i_to][j_to] == cell) {i_to++; j_to++;}

        if (i_to - i_from - 1 >= WIN_LINE) {
            winStatus = new Win(Win.Dir.D, i_from, i_to);
        }

        i_to = i; i_from = i; j_from = j; j_to = j;
        // Second diagonal
        while (i_from >= 0 && j_from < SIZE && data[i_from][j_from] == cell) { i_from--; j_from++; }
        while (i_to < SIZE && j_to >= 0 && data[i_to][j_to] == cell) {i_to++; j_to--;}

        if (i_to - i_from - 1 >= WIN_LINE) {
            winStatus = new Win(Win.Dir.SD, i_from, i_to);
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("|");
        for (int i = 0; i < SIZE; i++) {
            sb.append(String.format(" %-2d", i + 1));
        }
        sb.append("|\r\n");
        for (int i = 0; i < SIZE; i++) {
            sb.append("|");
            for (int j = 0; j < SIZE; j++) {
                sb.append(String.format(" %s ", Cell.cell2str(get(i, j))));
            }
            sb.append("| ");
            sb.append(i + 1);
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
