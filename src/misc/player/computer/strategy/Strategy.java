package misc.player.computer.strategy;

import misc.Color;
import misc.GameState;
import misc.Move;
import misc.player.human.input.MoveInput;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public interface Strategy {

    /**
     * Obtains all moves that are possible
     */
    static List<Move> generatePossibleMoves(GameState state, Color color) {
        ArrayList<Move> moves = new ArrayList<>();
        for (int x = 0; x < Grid.XRANGE; x++) {
            for (int y = 0; y < Grid.YRANGE; y++) {
                if (state.occupiedBy(x, y, Grid.ZRANGE - 1) == null) {
                    moves.add(new Move(color, x, y));
                }
            }
        }
        return moves;
    }

    /**
     * Determine which move should be made according to this strategy
     * @param state -- Game state upon which the move is based
     * @return -- the move to be done
     */
    MoveInput determineMove(GameState state);


}
