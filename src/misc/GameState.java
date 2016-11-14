package misc;

import java.util.Stack;

/**
 * Keeps track of game progression
 */
public class GameState {

    /**
     * Direction vectors for connect-check
     *
     * Opposing vectors form axes
     */

    /**
     * 1D
     */

    private static final int[] UP = new int[]{0, 0, 1};
    private static final int[] DOWN = new int[]{0, 0, -1};
    private static final int[] LEFT = new int[]{-1, 0, 0};
    private static final int[] RIGHT = new int[]{1, 0, 0};
    private static final int[] FORWARD = new int[]{0, -1, 0};
    private static final int[] BACKWARD = new int[]{0, 1, 0};

    private static final int[][] axis1 = new int[][]{UP, DOWN};
    private static final int[][] axis2 = new int[][]{LEFT, RIGHT};
    private static final int[][] axis3 = new int[][]{FORWARD, BACKWARD};

    /**
     * 2D
     */

    private static final int[] D1 = new int[]{1, 1, 0};
    private static final int[] D2 = new int[]{-1, -1, 0};
    private static final int[] D3 = new int[]{1, -1, 0};
    private static final int[] D4 = new int[]{-1, 1, 0};
    private static final int[] D5 = new int[]{1, 0, 1};
    private static final int[] D6 = new int[]{-1, 0, -1};
    private static final int[] D7 = new int[]{1, 0, -1};
    private static final int[] D8 = new int[]{-1, 0, 1};
    private static final int[] D9 = new int[]{0, 1, 1};
    private static final int[] D10 = new int[]{0, -1, -1};
    private static final int[] D11 = new int[]{0, 1, -1};
    private static final int[] D12 = new int[]{0, -1, 1};

    private static final int[][] axis4 = new int[][]{D1, D2};
    private static final int[][] axis5 = new int[][]{D3, D4};
    private static final int[][] axis6 = new int[][]{D5, D6};
    private static final int[][] axis7 = new int[][]{D7, D8};
    private static final int[][] axis8 = new int[][]{D9, D10};
    private static final int[][] axis9 = new int[][]{D11, D12};

    /**
     * 3D
     */

    private static final int[] DD1 = new int[]{1,1,1};
    private static final int[] DD2 = new int[]{-1,-1,-1};
    private static final int[] DD3 = new int[]{1,1,-1};
    private static final int[] DD4 = new int[]{-1,-1,1};
    private static final int[] DD5 = new int[]{1,-1,1};
    private static final int[] DD6 = new int[]{-1,1,-1};
    private static final int[] DD7 = new int[]{-1,1,1};
    private static final int[] DD8 = new int[]{1,-1,-1};

    private static final int[][] axis10 = new int[][]{DD1, DD2};
    private static final int[][] axis11 = new int[][]{DD3, DD4};
    private static final int[][] axis12 = new int[][]{DD5, DD6};
    private static final int[][] axis13 = new int[][]{DD7, DD8};

    /**
     * All axes that need to be checked
     */
    private static final int[][][] axes = new int[][][]{axis1, axis2, axis3, axis4, axis5, axis6, axis7, axis8, axis9, axis10, axis11, axis12, axis13};

    /**
     * Instance variables
     */

    /** Stores all moves that have been done */
    private Stack<MoveEntry> moves;
    /** Grid for keeping track of piece locations */
    private Grid grid;

    /**
     * Constructor
     */

    public GameState() {
        this.moves = new Stack<>();
        this.grid = new SimpleGrid();
    }

    /**
     * Queries
     */

    /**
     * Check if the grid is full
     */
    public boolean gridIsFull() {
        return grid.isFull();
    }

    /**
     * Get the color that occupies this coordinate
     */
    public Color occupiedBy(int x, int y, int z) {
        try {
            return grid.occupiedBy(x, y, z);
        } catch (Grid.InvalidCoordinatesException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get a deep copy of this game state
     */
    public GameState deepCopy() {
        GameState copy = new GameState();
        for (MoveEntry moveEntry : moves.subList(0, moves.size())) {
            copy.doMove(moveEntry.getMove());
        }
        return copy;
    }

    /**
     * Check if the last move that has been made won the game (by getting a chain of the required length)
     */
    public boolean lastMoveWasWinning() {
        /** Get the last move that has been executed */
        if (moves.size() > 0) {
            MoveEntry last = moves.peek();
            final Color color = last.getMove().getColor();
            /** Get the location that should be checked */
            final int[] origin = new int[]{last.getMove().getX(), last.getMove().getY(), last.getZ()};
            /** Check all axes for chains */
            for (int[][] axis : axes) {
                int[] direction1 = axis[0];
                int[] direction2 = axis[1];
                int chainLength = 1;
                chainLength += checkDirection(origin, direction1, color);
                chainLength += checkDirection(origin, direction2, color);
                if (chainLength >= Game.CONNECT) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Helper method of hasConnectNAt
     * Calculates the coordinates of a slot in a certain direction and distance from the origin
     */
    private int[] getLocation(final int[] origin, final int[] direction, final int multiplier) {
        return new int[]{origin[0] + multiplier * direction[0],
                origin[1] + multiplier * direction[1],
                origin[2] + multiplier * direction[2]};
    }

    /**
     * Helper method of hasConnectNAt
     * Count the chain length of the specified color at the origin in a certain direction (bounded to chain length n)
     *
     * Careful: Works for color == null as well!
     */
    private int checkDirection(final int[] origin, final int[] direction, final Color color) {
        int chainLength = 0;
        /** Look at max n - 1 slots. (More is useless) */
        for (int i = 1; i < Game.CONNECT; i++) {
            /** Calculate the coordinates of the position to look at */
            int[] coordinates = getLocation(origin, direction, i);
            /** If the coordinate is on the grid, continue, otherwise break */
            try {
                if (Grid.isValidSlot(coordinates[0], coordinates[1], coordinates[2])) {
                    Color occupiedBy = grid.occupiedBy(coordinates[0],coordinates[1],coordinates[2]);
                    /** Is the slot occupied by the specified color? */
                    if (occupiedBy == color) {
                        /** Color is the same! Increase chain length */
                        chainLength++;
                    } else {
                        /** Color is different. Stop counting */
                        break;
                    }
                } else {
                    /** Reached the end of the grid. Stop counting */
                    break;
                }
            } catch (Grid.InvalidCoordinatesException e) {
                /** Reached the end of the grid. Stop counting */
                break;
            }
        }
        return chainLength;
    }

    /**
     * Commands
     */

    /**
     * Perform a move on the grid. Return a boolean indicating if the move was successful
     * @param move -- Move to be performed
     */
    public boolean doMove(Move move) {
        try {
            int z = grid.drop(move);
            this.moves.push(new MoveEntry(move, z));
        } catch (Grid.FullColumnException | Grid.InvalidCoordinatesException e) {
            return false;
        }
        return true;
    }

    /**
     * Undo the last move that has been made
     */
    public void undoMove() {
        Move last = moves.pop().getMove();
        try {
            grid.undo(last.getX(), last.getY());
        } catch (Grid.EmptyColumnException | Grid.InvalidCoordinatesException e) {
            e.printStackTrace();
        }

    }

    /**
     * Override from super
     *
     * Equality of a game state is only determined by the current state of the grid. Not by move history
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameState gameState = (GameState) o;

        return grid != null ? grid.equals(gameState.grid) : gameState.grid == null;

    }

    @Override
    public int hashCode() {
        return grid.hashCode();
    }

    /**
     * Class for storing move information
     */

    private class MoveEntry {

        /** The move object indicating which color put a piece in which column */
        private final Move move;
        /** The z coordinate where the piece landed */
        private final int z;

        private MoveEntry(Move move, int z) {
            this.move = move;
            this.z = z;
        }

        private Move getMove() {
            return move;
        }

        private int getZ() {
            return z;
        }


    }


}
