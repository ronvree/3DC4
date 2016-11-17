package misc;

import java.util.Arrays;

/**
 * A grid keeping track of which pieces have been set at which location by which player
 */
public interface Grid {

    /**
     * Constants
     */

    /** Width of the grid (top view) */
    int XRANGE = 4;
    /** Height of the grid (top view) */
    int YRANGE = 4;
    /** Depth of the grid (top view) */
    int ZRANGE = 4;

    /**
     * Static Queries
     */
    /**
     * @return if the x coordinate is valid
     */
    static boolean isValidX(int x) {
        return x >= 0 && x < XRANGE;
    }

    /**
     * @return if the y coordinate is valid
     */
    static boolean isValidY(int y) {
        return y >= 0 && y < YRANGE;
    }

    /**
     * @return if the z coordinate is valid
     */
    static boolean isValidZ(int z) {
        return z >= 0 && z < ZRANGE;
    }

    /**
     * @return if there's a column at this x,y coordinate
     */
    static boolean isValidColumn(int x, int y) {
        return isValidX(x) && isValidY(y);
    }

    /**
     * @return if there's a slot at this x,y,z coordinate
     */
    static boolean isValidSlot(int x, int y, int z)    {
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
    Color occupiedBy(int x, int y, int z) throws InvalidCoordinatesException;

    /**
     * Checks if the specified column is full
     * @param x -- x coordinate of the column
     * @param y -- y coordinate of the column
     * @return whether the specified column is full
     */
    boolean isFull(int x, int y) throws InvalidCoordinatesException;

    /**
     * Checks if the entire grid is filled
     * @return if every slot is occupied
     */
    boolean isFull();

    /**
     * Get a new grid instance with the same piece positions
     */
    Grid deepCopy();

    /**
     * Commands
     */

    /**
     * @param color -- Color of the player making the move
     * @param x -- x coordinate of the hole the piece is dropped in (top view)
     * @param y -- y coordinate of the hole the piece is dropped in (top view)
     * @return the z coordinate in which the piece landed
     */
    int drop(Color color, int x, int y) throws FullColumnException, InvalidCoordinatesException;

    /**
     * Execute this move. Return the z coordinate in which the piece landed
     */
    int drop(Move move) throws FullColumnException, InvalidCoordinatesException;


    /**
     * Undo the last move made in this column. Return a boolean indicating if a move has been undone
     * @param x -- Column x coordinate
     * @param y -- Column y coordinate
     */
    void undo(int x, int y) throws EmptyColumnException, InvalidCoordinatesException;

    /**
     * Clears the entire grid
     */
    void reset();

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
    class FullColumnException extends Exception {

        final int x;
        final int y;

        public FullColumnException(int x, int y) {
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

    class InvalidCoordinatesException extends Exception {

        private int[] coordinates;

        public InvalidCoordinatesException(int... coordinates) {
            this.coordinates = coordinates;
        }

        @Override
        public String getMessage() {
            return String.format("InvalidCoordinatesException: %s", Arrays.asList(coordinates).toString());
        }

    }


}
