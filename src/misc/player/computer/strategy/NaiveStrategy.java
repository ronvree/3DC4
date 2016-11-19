package misc.player.computer.strategy;

import misc.GameState;
import misc.player.human.input.MoveInput;

import java.util.Random;

/**
 * Pick a random move
 */
public class NaiveStrategy implements Strategy {

    /**
     * Random object for move generation
     */
    private Random random;

    /**
     * Create a new strategy
     */
    public NaiveStrategy() {
        this.random = new Random();
    }

    /**
     * Return a random location to do a move
     */
    @Override
    public MoveInput determineMove(GameState state) {
        return new MoveInput(random.nextInt(Grid.XRANGE), random.nextInt(Grid.YRANGE));
    }

}
