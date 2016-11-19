package misc.testing;

import misc.Color;
import misc.GameState;
import misc.Grid;
import misc.Move;
import misc.player.computer.strategy.minimax.NegaMax;

import java.util.List;

/**
 *
 */
public class NegaMaxTestVersion extends NegaMax {

    /**
     * Constants
     */

    private static final int[] HEURISTICS = new int[]{0, 1, 1, 0};

    /**
     * Create a new negamax strategy
     */
    public NegaMaxTestVersion(int depth, Color color) {
        super(depth, color);
    }

    /**
     * Give a score to this grid. Slots closer to middle give more points
     */
    @Override
    public int score(GameState state, Color color) {
        int score = 0;
        for (int x = 0; x < Grid.XRANGE; x++) {
            for (int y = 0; y < Grid.YRANGE; y++) {
                for (int z = 0; z < Grid.ZRANGE; z++) {
                    if (color == state.occupiedBy(x, y, z)) {
                        score++;
                        score += score(x, y, z);
                    }
                }
            }
        }
        return score;
    }

    /**
     * Helper method of score(Grid grid)
     */
    private static int score(final int x, final int y, final int z) {
        return HEURISTICS[x] + HEURISTICS[y] + HEURISTICS[z];
    }

    /**
     * Favor middle moves
     */
    @Override
    protected void orderMoves(List<Move> moves) {    }

}
