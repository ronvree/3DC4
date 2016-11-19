package misc.player.computer.strategy.minimax;

import misc.*;
import misc.player.computer.ComputerPlayer;
import misc.player.computer.strategy.NaiveStrategy;
import misc.player.computer.strategy.Strategy;
import misc.player.human.input.MoveInput;
import misc.testing.NegaMaxAlphaBetaTestVersion;
import misc.testing.NegaMaxTestVersion;
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

    private static final int DEPTH = 4;

    private static final int STATES = 50;

    private ComputerPlayer regularNega;
    private ComputerPlayer alphaBetaNega;

    private List<GameState> gameStates;

    private Random random;

    @Before
    public void setUp() throws Exception {
        System.out.println("Initializing...");
        regularNega = new TestPlayer("DefaultNegaMax", Color.RED, new NegaMaxTestVersion(DEPTH, Color.RED));
        alphaBetaNega = new TestPlayer("AlphaBetaNegaMax", Color.RED, new NegaMaxAlphaBetaTestVersion(DEPTH, Color.RED));
        random = new Random();
        gameStates = new ArrayList<>();
        System.out.println("Finished initializing");
        System.out.println("Generating Game States...");
        generateGameStates();
        System.out.println("Finished Generating Game States...");
    }

    private void generateGameStates() {
        NaiveNamelessPlayer p1 = new NaiveNamelessPlayer(Color.RED);
        NaiveNamelessPlayer p2 = new NaiveNamelessPlayer(Color.YELLOW);

        for (int stateIndex = 0; stateIndex < STATES; stateIndex++) {
            GameState state = new GameState();
            int turns = random.nextInt(Grid.XRANGE * Grid.YRANGE * Grid.ZRANGE);
            Color playing = Color.RED;
            for (int turn = 0; turn < turns; turn++) {
                if (playing == Color.RED) {
                    MoveInput m = (MoveInput) p1.decide(state);
                    state.doMove(new Move(playing, m.getX(), m.getY()));
                } else {
                    MoveInput m = (MoveInput) p2.decide(state);
                    state.doMove(new Move(playing, m.getX(), m.getY()));
                }
                if (state.lastMoveWasWinning()) {
                    turn--;
                    continue;
                }
                playing = playing.other();
            }
            gameStates.add(state);
        }
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
     * Convenience classes
     */

    private class TestPlayer extends ComputerPlayer {

        public TestPlayer(String name, Color color, Strategy strategy) {
            super(name, color, strategy);
        }

        @Override
        public boolean acceptDraw(GameState state) {
            return false;
        }
    }

    private class NaiveNamelessPlayer extends ComputerPlayer {

        public NaiveNamelessPlayer(Color color) {
            super("", color, new NaiveStrategy());
        }

        @Override
        public boolean acceptDraw(GameState state) {
            return false;
        }
    }

}