package misc.player.computer;

import misc.Color;
import misc.GameState;
import misc.Move;
import misc.player.computer.strategy.minimax.NegaMaxDynamic;

import java.util.List;

/**
 * A computer player utilizing the Negamax algorithm to determine its moves
 */
public abstract class NegaPlayer extends ComputerPlayer {

    public NegaPlayer(Color color, int depth) {
        super("Nega", color, new NegaMaxDynamic(depth, color) {


            @Override
            protected int score(GameState state, Color color) {
                return 0;
            }

            @Override
            protected void orderMoves(List<Move> moves) {

            }
        });
    }

    @Override
    public boolean acceptDraw(GameState state) {
        return false;
    }
}
