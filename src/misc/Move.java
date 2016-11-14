package misc;

/**
 * Move representation (immutable)
 */
public class Move {

    /** Color of the player that made the move */
    private final Color color;
    /** x-coordinate of the column in which the piece was dropped */
    private final int x;
    /** y-coordinate of the column in which the piece was dropped */
    private final int y;

    /**
     * Create a new move
     */
    public Move(Color color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Move) {
            return ((Move) object).getX() == this.getX() && ((Move) object).getY() == this.getY() && ((Move) object).getColor().equals(this.color);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s dropped a piece in %d, %d", color.toString(), x, y);
    }

    /**
     * Getters
     */

    public Color getColor() { return color; }
    public int getX() { return x; }
    public int getY() { return y; }

}
