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

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

}
