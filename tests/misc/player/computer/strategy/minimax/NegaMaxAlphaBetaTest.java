package misc.player.computer.strategy.minimax;

import misc.*;
import misc.player.computer.ComputerPlayer;
import misc.player.computer.NaivePlayer;
import misc.player.computer.strategy.Strategy;
import misc.player.human.input.MoveInput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Assumes correctness of the standard NegaMax implementation
 *
 * Tests the correctness of the alpha beta pruned NegaMax implementation by checking if it makes the same decisions
 * as the default NegaMax implementation at equal max depth.
 *
 * Random grids are generated and given to both players.
 *
 */
public class NegaMaxAlphaBetaTest {

    private static final int DEPTH = 5;

    private static final int STATES = 50;

    private ComputerPlayer regularNega;
    private ComputerPlayer alphaBetaNega;

    private List<GameState> gameStates;

    @Before
    public void setUp() throws Exception {
        System.out.println("set-up...");
        regularNega = new TestPlayer("DefaultNegaMax", Color.RED, new NegaMaxTestVersion(DEPTH, Color.RED));
        alphaBetaNega = new TestPlayer("AlphaBetaNegaMax", Color.RED, new NegaMaxAlphaBetaTestVersion(DEPTH, Color.RED));
        System.out.println("Generating Game States...");
        gameStates = generateRandomGameStates(STATES);
        System.out.println("Finished Generating Game States...");
        System.out.println("Finished set-up");
    }

    @Test
    public void testDecisionEquality() throws Exception {
        for (GameState state : gameStates) {
            MoveInput m1 = (MoveInput) regularNega.decide(state);
            MoveInput m2 = (MoveInput) alphaBetaNega.decide(state);
            Assert.assertEquals(m1.getX(), m2.getX());
            Assert.assertEquals(m1.getY(), m2.getY());
        }
    }

    /**
     * Utility Functions
     */

    /**
     * Generate an amount of random game states. All game states are legal
     */
    static List<GameState> generateRandomGameStates(int amount) {
        List<GameState> states = new ArrayList<>();
        NaivePlayer p1 = new NaivePlayer(Color.RED);
        NaivePlayer p2 = new NaivePlayer(Color.YELLOW);
        Random random = new Random();

        for (int i = 0; i < amount; i++) {
            GameState state = new GameState();
            int turns = random.nextInt(Grid.XRANGE * Grid.YRANGE * Grid.ZRANGE);
            Color playing = Color.RED;
            for (int turn = 0; turn < turns; turn++) {
                if (playing == Color.RED) {
                    MoveInput m = (MoveInput) p1.decide(state);
                    state.doMove(playing, m.getX(), m.getY());
                } else {
                    MoveInput m = (MoveInput) p2.decide(state);
                    state.doMove(playing, m.getX(), m.getY());
                }
                if (state.lastMoveWasWinning()) {
                    state.undoMove();
                    continue;
                }
                playing = playing.other();
            }
            states.add(state);
        }
        return states;
    }

    /**
     * Determine a score for a grid
     */
    static int score(GameState state, Color color) {
        int score = 0;
        for (int x = 0; x < Grid.XRANGE; x++) {
            for (int y = 0; y < Grid.YRANGE; y++) {
                for (int z = 0; z < Grid.ZRANGE; z++) {
                    if (color == state.colorOccupying(x, y, z)) {
                        score++;
                        score += score(x, y, z);
                    }
                }
            }
        }
        return score;
    }

    /**
     * Determine a score for a slot
     */
    private static int score(final int x, final int y, final int z) {
        final int[] HEURISTICS = new int[]{0, 1, 1, 0};
        return HEURISTICS[x] + HEURISTICS[y] + HEURISTICS[z];
    }

    /**
     * Determine move ordering before evaluation
     */
    static void orderMoves(List<MoveSuggestion> moves) {}

    /**
     * Convenience classes
     */

    private class NegaMaxTestVersion extends NegaMax {

        NegaMaxTestVersion(int depth, Color color) {
            super(depth, color);
        }

        @Override
        protected int score(GameState state, Color color) {
            return NegaMaxAlphaBetaTest.score(state, color);
        }

        @Override
        protected void orderMoves(List<MoveSuggestion> moves) {
            NegaMaxAlphaBetaTest.orderMoves(moves);
        }
    }

    private class NegaMaxAlphaBetaTestVersion extends NegaMaxAlphaBeta {

        NegaMaxAlphaBetaTestVersion(int depth, Color color) {
            super(depth, color);
        }

        @Override
        protected int score(GameState state, Color color) {
            return NegaMaxAlphaBetaTest.score(state, color);
        }

        @Override
        protected void orderMoves(List<MoveSuggestion> moves) {
            NegaMaxAlphaBetaTest.orderMoves(moves);
        }
    }

    private class TestPlayer extends ComputerPlayer {

        public TestPlayer(String name, Color color, Strategy strategy) {
            super(name, color, strategy);
        }

        @Override
        public boolean acceptDraw(GameState state) {
            return false;
        }
    }

}