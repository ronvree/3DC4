package misc.player.computer.strategy.minimax;

import misc.*;
import misc.player.computer.ComputerPlayer;
import misc.player.computer.strategy.Strategy;
import misc.player.human.input.MoveInput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 *
 */
public class NegaMaxDynamicTest {

    private static final int DEPTH = 7;

    private static final int STATES = 50;

    private ComputerPlayer alphaBetaNega;
    private ComputerPlayer dynamicNega;

    private List<GameState> gameStates;

    @Before
    public void setUp() throws Exception {
        System.out.println("Set-up...");
        alphaBetaNega = new TestPlayer("DAlphaBetaNegaMax", Color.RED, new NegaMaxAlphaBetaTestVersion(DEPTH, Color.RED));
        dynamicNega = new TestPlayer("DynamicNegaMax", Color.RED, new NegaMaxDynamicTestVersion(DEPTH, Color.RED));
        System.out.println("Generating Game States...");
        gameStates = NegaMaxAlphaBetaTest.generateRandomGameStates(STATES);
        System.out.println("Finished Generating Game States...");
        System.out.println("Finished set-up");
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

    private class NegaMaxAlphaBetaTestVersion extends NegaMaxAlphaBeta {

        NegaMaxAlphaBetaTestVersion(int depth, Color color) {
            super(depth, color);
        }

        @Override
        protected int score(GameState state, Color color) {
            return NegaMaxAlphaBetaTest.score(state, color);
        }

        @Override
        protected void orderMoves(List<Move> moves) {
            NegaMaxAlphaBetaTest.orderMoves(moves);
        }
    }

    private class NegaMaxDynamicTestVersion extends NegaMaxDynamic {

        NegaMaxDynamicTestVersion(int depth, Color color) {
            super(depth, color);
        }

        @Override
        protected int score(GameState state, Color color) {
            return NegaMaxAlphaBetaTest.score(state, color);
        }

        @Override
        protected void orderMoves(List<Move> moves) {
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