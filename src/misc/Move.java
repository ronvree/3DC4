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
    /** z-coordinate in which the piece landed */
    private final int z;

    /**
     * Create a new move
     */
    public Move(Color color, int x, int y, int z) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Move) {
            boolean sameX = ((Move) object).getX() == this.getX();
            boolean sameY = ((Move) object).getY() == this.getY();
            boolean sameZ = ((Move) object).getZ() == this.getZ();
            boolean sameColor = ((Move) object).getColor().equals(this.color);
            return sameX && sameY && sameZ && sameColor;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s in %d, %d", color.toString(), x, y);
    }

    /**
     * Getters
     */

    public Color getColor() { return color; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }

}
