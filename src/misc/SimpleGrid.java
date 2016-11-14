package misc;

import java.util.Arrays;

/**
 * Basic grid implementation
 */
public class SimpleGrid implements Grid {

    /**
     * A matrix of columns
     */
    private final Column[][] grid;

    /**
     * Constructor
     */
    public SimpleGrid() {
        this.grid = new Column[Grid.XRANGE][Grid.YRANGE];
        for (int x = 0; x < XRANGE; x++) {
            for (int y = 0; y < YRANGE; y++) {
                this.grid[x][y] = new Column(x, y);
            }
        }
    }

    /**
     * Drop a piece in the specified column
     */
    @Override
    public int drop(Color color, int x, int y) throws FullColumnException, InvalidCoordinatesException{
        if (Grid.isValidColumn(x, y)) {
            return grid[x][y].insert(color);
        } else {
            throw new InvalidCoordinatesException(x, y);
        }
    }

    /**
     * Drop a piece in the specified column
     */
    @Override
    public int drop(Move move) throws FullColumnException, InvalidCoordinatesException{
        return drop(move.getColor(), move.getX(), move.getY());
    }

    /**
     * Check which color occupies this slot
     */
    @Override
    public Color occupiedBy(int x, int y, int z) throws InvalidCoordinatesException {
        if (Grid.isValidSlot(x, y, z)) {
            return grid[x][y].getColorAt(z);
        } else {
            throw new InvalidCoordinatesException(x, y, z);
        }
    }

    /**
     * Check if the specified column is full
     */
    @Override
    public boolean isFull(int x, int y) {
        return grid[x][y].isFull();
    }

    /**
     * Check if all columns are full
     */
    @Override
    public boolean isFull() {
        boolean full = true;
        for (int x = 0; x < Grid.XRANGE; x++) {
            for (int y = 0; y < Grid.YRANGE; y++) {
                full = full && grid[x][y].isFull();
            }
        }
        return full;
    }

    /**
     * Get a deep copy of this grid
     */
    @Override
    public Grid deepCopy() {
        SimpleGrid copy = new SimpleGrid();
        for (int x = 0; x < Grid.XRANGE; x++) {
            for (int y = 0; y < Grid.YRANGE; y++) {
                copy.grid[x][y] = this.grid[x][y].deepCopy();
            }
        }
        return copy;
    }

    /**
     * Clear the grid
     */
    @Override
    public void reset() {
        for (int x = 0; x < Grid.XRANGE; x++) {
            for (int y = 0; y < Grid.YRANGE; y++) {
                grid[x][y].reset();
            }
        }
    }

    /**
     * Undo the last move made in this column
     */
    @Override
    public void undo(final int x, final int y) throws EmptyColumnException, InvalidCoordinatesException{
        if (Grid.isValidColumn(x, y)) {
            grid[x][y].undo();
        } else {
            throw new InvalidCoordinatesException(x, y);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleGrid that = (SimpleGrid) o;

        return Arrays.deepEquals(grid, that.grid);

    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }

    /**
     * Column class
     *
     * Represents a column in the grid
     */
    private class Column {

        /** A column consist out of an array of slots */
        private final Color[] slots;
        /** Column coordinates */
        private final int x,y;

        /**
         * Create a new column
         */
        private Column(int x, int y)    {
            this.x = x;
            this.y = y;
            slots = new Color[Grid.ZRANGE];
        }

        /**
         * @return the color that occupies the slot at the specified z coordinate
         */
        private Color getColorAt(int z) {
            return slots[z];
        }

        /**
         * Insert a piece in this column
         * @param color -- Color of the piece
         * @return the z coordinate of the piece that was placed
         */
        private int insert(Color color) throws FullColumnException {
            for (int z = 0; z < ZRANGE; z++) {
                if (slots[z] == null) {
                    slots[z] = color;
                    return z;
                }
            }
            throw new FullColumnException(x, y);
        }

        /**
         * Undo the last move made in this column. Return a boolean indicating if any move has been undone
         */
        private void undo() throws EmptyColumnException {
            for (int z = ZRANGE - 1; z >= 0; z--) {
                if (slots[z] != null) {
                    slots[z] = null;
                    return;
                }
            }
            throw new EmptyColumnException(x, y);
        }

        /**
         * @return if the column is full
         */
        private boolean isFull() {
            boolean full = true;
            for (Color slot : slots) {
                full = full && slot != null;
            }
            return full;
        }

        /**
         * Reset the column
         */
        private void reset() {
            for (int z = 0; z < ZRANGE; z++) {
                slots[z] = null;
            }
        }

        /**
         * Get a deep copy of this column
         */
        private Column deepCopy() {
            Column copy = new Column(x, y);
            System.arraycopy(this.slots, 0, copy.slots, 0, ZRANGE);
            return copy;
        }

        /**
         * Column equality is determined by its content
         */

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Column column = (Column) o;

            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(slots, column.slots);

        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(slots);
        }
    }


}
