package misc;

/**
 *
 */
public final class Coordinate {

    private final int x, y, z;

    public Coordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        return x == that.x && y == that.y && z == that.z;

    }

    /** Hash code is the index in a linear array */
    @Override
    public int hashCode() {
        return x + y * Grid.XRANGE + Grid.XRANGE * Grid.YRANGE * z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

}
