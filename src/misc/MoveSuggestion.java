package misc;

/**
 *
 */
public class MoveSuggestion {

    /** Color of the move */
    private final Color color;
    /** X coordinate of the move */
    private final int x;
    /** Y coordinate of the move */
    private final int y;

    public MoveSuggestion(Color color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MoveSuggestion that = (MoveSuggestion) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        return color == that.color;

    }

    @Override
    public int hashCode() {
        int result = color != null ? color.hashCode() : 0;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }

    public Color getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


}
