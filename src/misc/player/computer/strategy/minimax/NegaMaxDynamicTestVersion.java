package misc.player.computer.strategy.minimax;

import misc.Color;
import misc.GameState;
import misc.Grid;
import misc.Move;
import misc.player.human.input.MoveInput;

import java.util.List;

/**
 *
 */
public class NegaMaxDynamicTestVersion extends NegaMaxDynamic {

    private static final int[] HEURISTICS = new int[]{0, 1, 1, 0};

    /**
     * Create a new negamax strategy with alpha beta pruning and transposition tables
     */
    public NegaMaxDynamicTestVersion(int depth, Color color) {
        super(depth, color);
    }

    @Override
    public MoveInput determineMove(GameState state) {
        MoveInput res = super.determineMove(state);
        System.out.println(String.format("%s decided to drop a piece in %d, %d", this.getMaximizingColor().toString(), res.getX(), res.getY()));
        return res;
    }

    /**
     * Give a score to this grid. Slots closer to middle give more points
     */
    @Override
    protected int score(GameState state, Color color) {
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
    private int score(final int x, final int y, final int z) {
        return HEURISTICS[x] + HEURISTICS[y] + HEURISTICS[z];
    }

    /**
     * Favor middle moves
     */
    @Override
    protected void orderMoves(List<Move> moves) {
        moves.sort((m1, m2) -> {

            int m1score = HEURISTICS[m1.getX()] + HEURISTICS[m1.getY()];
            int m2score = HEURISTICS[m2.getX()] + HEURISTICS[m2.getY()];

            if (m1score > m2score) {
                return 1;
            }
            if (m1score < m2score) {
                return -1;
            }
            return 0;
        });
    }
}
