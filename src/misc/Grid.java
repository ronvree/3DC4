package misc;

import java.util.Arrays;
import java.util.Random;

/**
 * A grid keeping track of which pieces have been set at which location by which player
 */
public final class Grid {

    /**
     * Constants
     */

    /** Width of the grid (top view) */
    public static final int XRANGE = 4;
    /** Height of the grid (top view) */
    public static final int YRANGE = 4;
    /** Depth of the grid (top view) */
    public static final int ZRANGE = 4;
    /** Amount of slots */
    private static final int SLOTS = XRANGE * YRANGE * ZRANGE;

    /**
     * Instance Variables
     */

    /** Keeps track of piece locations */
    private final Color[][][] grid;
    /** Counts the amount of pieces in the grid */
    private int pieceCounter;

    /**
     * Create a new grid
     */
    public Grid() {
        grid = new Color[XRANGE][YRANGE][ZRANGE];
        pieceCounter = 0;

    }

    /**
     * Static Queries
     */

    /**
     * @return if the x coordinate is valid
     */
    public static boolean isValidX(int x) {
        return x >= 0 && x < XRANGE;
    }

    /**
     * @return if the y coordinate is valid
     */
    public static boolean isValidY(int y) {
        return y >= 0 && y < YRANGE;
    }

    /**
     * @return if the z coordinate is valid
     */
    public static boolean isValidZ(int z) {
        return z >= 0 && z < ZRANGE;
    }

    /**
     * @return if there's a column at this x,y coordinate
     */
    public static boolean isValidColumn(int x, int y) {
        return isValidX(x) && isValidY(y);
    }

    /**
     * @return if there's a slot at this x,y,z coordinate
     */
    public static boolean isValidSlot(int x, int y, int z)    {
        return isValidX(x) && isValidY(y) && isValidZ(z);
    }

    /**
     * Non-Static Queries
     */

    /**
     * Checks if the specified slot is occupied
     * @param x -- x coordinate
     * @param y -- y coordinate
     * @param z -- z coordinate
     * @return The color which occupies the specified slot. Return null if slot is unoccupied
     */
    public Color occupiedBy(int x, int y, int z) throws InvalidCoordinatesException {
        if (Grid.isValidSlot(x, y, z)) {
            return grid[x][y][z];
        } else {
            throw new InvalidCoordinatesException(x, y, z);
        }
    }

    /**
     * Checks if the specified column is full
     * @param x -- x coordinate of the column
     * @param y -- y coordinate of the column
     * @return whether the specified column is full
     */
    public boolean isFull(int x, int y) throws InvalidCoordinatesException {
        if (Grid.isValidColumn(x, y)) {
            return grid[x][y][ZRANGE - 1] == null;
        } else {
            throw new InvalidCoordinatesException(x, y);
        }
    }

    /**
     * Checks if the entire grid is filled
     * @return if every slot is occupied
     */
    public boolean isFull() {
        return pieceCounter == SLOTS;
    }

    /**
     * Get a new grid instance with the same piece positions
     */
    public Grid deepCopy() {
        Grid copy = new Grid();
        for (int x = 0; x < XRANGE; x++) {
            for (int y = 0; y < YRANGE; y++) {
                System.arraycopy(this.grid[x][y], 0, copy.grid[x][y], 0, ZRANGE);
            }
        }
        copy.pieceCounter = this.pieceCounter;
        return copy;
    }

    /**
     * Commands
     */

    /**
     * @param color -- Color of the player making the move
     * @param x -- x coordinate of the hole the piece is dropped in (top view)
     * @param y -- y coordinate of the hole the piece is dropped in (top view)
     * @return the z coordinate in which the piece landed
     */
    public int drop(Color color, int x, int y) throws FullColumnException, InvalidCoordinatesException {
        if (Grid.isValidColumn(x, y)) {
            for (int z = 0; z < ZRANGE; z++) {
                if (grid[x][y][z] == null) {
                    grid[x][y][z] = color;
                    pieceCounter++;
                    return z;
                }
            }
            throw new FullColumnException(x, y);
        } else {
            throw new InvalidCoordinatesException(x, y);
        }
    }

    /**
     * Undo the last move made in this column. Return a boolean indicating if a move has been undone
     * @param x -- Column x coordinate
     * @param y -- Column y coordinate
     */
    public void undo(int x, int y) throws EmptyColumnException, InvalidCoordinatesException {
        if (Grid.isValidColumn(x, y)) {
            for (int z = ZRANGE - 1; z >= 0; z--) {
                if (grid[x][y][z] != null) {
                    grid[x][y][z] = null;
                    pieceCounter--;
                    return;
                }
            }
            throw new EmptyColumnException(x, y);
        } else {
            throw new InvalidCoordinatesException(x, y);
        }
    }

    /**
     * Clears the entire grid
     */
    public void reset() {
        for (int x = 0; x < XRANGE; x++) {
            for (int y = 0; y < YRANGE; y++) {
                for (int z = 0; z < ZRANGE; z++) {
                    grid[x][y][z] = null;
                }
            }
        }
        pieceCounter = 0;
    }

    /**
     * Redefine equality of grids
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Grid that = (Grid) o;

        return Arrays.deepEquals(grid, that.grid);
    }

    /**
     * Zobrist hashing
     */

    private static final int REDINDEX = 0;
    private static final int YELINDEX = 1;

    private static final int[][] ZOBRIST_TABLE;
    static {
        Random random = new Random();
        ZOBRIST_TABLE = new int[SLOTS][2];
        for (int i = 0; i < SLOTS; i++) {
            for (int j = 0; j < 2; j++) {
                ZOBRIST_TABLE[i][j] = random.nextInt();
            }
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (int x = 0; x < XRANGE; x++) {
            for (int y = 0; y < YRANGE; y++) {
                for (int z = 0; z < ZRANGE; z++) {
                    Color c = grid[x][y][z];
                    if (c != null) {
                        h ^= ZOBRIST_TABLE[x + XRANGE * y + XRANGE * YRANGE * z][c == Color.RED? REDINDEX:YELINDEX];
                    }
                }
            }
        }
        return h;
    }


    /**
     * Exception classes
     */

    /**
     * Exception that indicates a column is empty
     */
    class EmptyColumnException extends Exception {

        final int x;
        final int y;

        public EmptyColumnException(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String getMessage() {
            return String.format("EmptyColumnException at column: %d, %d", x, y);
        }

    }

    /**
     * Exception that indicates a column is full
     */
    public class FullColumnException extends Exception {

        final int x;
        final int y;

        FullColumnException(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String getMessage() {
            return String.format("FullColumnException at column: %d, %d", x, y);
        }

    }

    /**
     * Exception that indicates invalid coordinates have been given
     */

    public class InvalidCoordinatesException extends Exception {

        private int[] coordinates;

        InvalidCoordinatesException(int... coordinates) {
            this.coordinates = coordinates;
        }

        @Override
        public String getMessage() {
            return String.format("InvalidCoordinatesException: %s", Arrays.asList(coordinates).toString());
        }

    }


}
