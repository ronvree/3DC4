package misc.player.computer.strategy.minimax;

import misc.*;
import misc.player.computer.ComputerPlayer;
import misc.player.computer.strategy.NaiveStrategy;
import misc.player.computer.strategy.Strategy;
import misc.player.human.input.MoveInput;
import misc.testing.NegaMaxAlphaBetaTestVersion;
import misc.testing.NegaMaxDynamicTestVersion;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class NegaMaxDynamicTest {

    private static final int DEPTH = 7;

    private static final int STATES = 50;

    private ComputerPlayer alphaBetaNega;
    private ComputerPlayer dynamicNega;

    private List<GameState> gameStates;

    private Random random;

    @Before
    public void setUp() throws Exception {
        System.out.println("Initializing...");
        alphaBetaNega = new TestPlayer("DAlphaBetaNegaMax", Color.RED, new NegaMaxAlphaBetaTestVersion(DEPTH, Color.RED));
        dynamicNega = new TestPlayer("DynamicNegaMax", Color.RED, new NegaMaxDynamicTestVersion(DEPTH, Color.RED));
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
                    state.undoMove();
                    continue;
                }
                playing = playing.other();
            }
            gameStates.add(state);
        }
    }

    @Test
    public void testDecisionEquality() throws Exception {
        System.out.println("Comparing decisions...");
        int i = 0;
        for (GameState state : gameStates) {
            MoveInput m1 = (MoveInput) alphaBetaNega.decide(state);
            MoveInput m2 = (MoveInput) dynamicNega.decide(state);

            Assert.assertEquals("\n" + TUI.prettyPrint(state), m1, m2);
            System.out.println(String.format("%d/%d passed", ++i, STATES));
        }
        System.out.println("\nFinished comparing decisions");
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