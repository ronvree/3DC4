package misc.player.human.input;

/**
 * Move input representation
 */
public class MoveInput extends PlayerInput{

    private final int x;
    private final int y;

    public MoveInput(int x, int y)   {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString()    {
        return String.format("(%d,%d)", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MoveInput input = (MoveInput) o;

        return x == input.x && y == input.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

}
