package misc;

import misc.player.computer.NaivePlayer;
import misc.player.human.input.MoveInput;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 *
 */
public class GameStateTest {


    @Test
    public void testWinCheck() throws Exception {
        // TODO
    }

    /**
     * Test if grid configurations produce unique hashes
     */
    @Test
    public void testHash() throws Exception {

        /** Empty grids */

        GameState state1 = new GameState();
        GameState state2 = new GameState();

        Assert.assertEquals(state1.hashCode(), state2.hashCode());

        /** After one move */

        final MoveSuggestion move = new MoveSuggestion(Color.RED, Grid.XRANGE/2, Grid.YRANGE/2);
        state1.doMove(move.getColor(), move.getX(), move.getY());
        state2.doMove(move.getColor(), move.getX(), move.getY());

        Assert.assertEquals(state1.hashCode(), state2.hashCode());

        /** After moves are undone */

        state1.undoMove();
        state1.doMove(move.getColor(), move.getX(), move.getY());

        Assert.assertEquals(state1.hashCode(), state2.hashCode());

        /** For same random grid configs */

        final int nrOfTests = 10000; // Note: a LOT of tests (millions) need to be run to find a reliable hash function
        final Random random = new Random();

        for (int test = 0; test < nrOfTests; test++) {
            state1 = new GameState();
            state2 = new GameState();
            NaivePlayer red = new NaivePlayer(Color.RED);
            NaivePlayer yel = new NaivePlayer(Color.YELLOW);
            Color current = Color.RED;
            int numberOfMoves = random.nextInt(Grid.XRANGE * Grid.YRANGE * Grid.ZRANGE - 1);
            for (int moveIndex = 0; moveIndex < numberOfMoves; moveIndex++) {
                MoveSuggestion decision;
                MoveInput input;
                if (current == Color.RED) {
                    input = (MoveInput) red.decide(state1);
                    decision = new MoveSuggestion(Color.RED, input.getX(), input.getY());
                } else {
                    input = (MoveInput) yel.decide(state1);
                    decision = new MoveSuggestion(Color.YELLOW, input.getX(), input.getY());
                }
                state1.doMove(decision.getColor(), decision.getX(), decision.getY());
                state2.doMove(decision.getColor(), decision.getX(), decision.getY());
                current = current.other();
            }
            Assert.assertEquals(state1.hashCode(), state2.hashCode());
        }

        /** For different random grid configs */

        for (int test = 0; test < nrOfTests; test++) {
            state1 = new GameState();
            state2 = new GameState();
            NaivePlayer red = new NaivePlayer(Color.RED);
            NaivePlayer yel = new NaivePlayer(Color.YELLOW);
            Color current = Color.RED;
            int numberOfMoves = random.nextInt(Grid.XRANGE * Grid.YRANGE * Grid.ZRANGE - 1);
            for (int moveIndex = 0; moveIndex < numberOfMoves; moveIndex++) {
                MoveSuggestion decision1;
                MoveSuggestion decision2;
                MoveInput input1;
                MoveInput input2;

                if (current == Color.RED) {
                    input1 = (MoveInput) red.decide(state1);
                    input2 = (MoveInput) red.decide(state1);
                    decision1 = new MoveSuggestion(Color.RED, input1.getX(), input1.getY());
                    decision2 = new MoveSuggestion(Color.RED, input2.getX(), input2.getY());
                } else {
                    input1 = (MoveInput) yel.decide(state1);
                    input2 = (MoveInput) yel.decide(state1);
                    decision1 = new MoveSuggestion(Color.YELLOW, input1.getX(), input1.getY());
                    decision2 = new MoveSuggestion(Color.YELLOW, input2.getX(), input2.getY());
                }

                if (decision1.equals(decision2)) {
                    moveIndex--;
                    continue;
                }

                state1.doMove(decision1.getColor(), decision1.getX(), decision1.getY());
                state2.doMove(decision2.getColor(), decision2.getX(), decision2.getY());

                current = current.other();
            }

            if (state1.equals(state2)) {
                test--;
                continue;
            }

            Assert.assertNotEquals(state1.hashCode(), state2.hashCode());
        }



    }






}