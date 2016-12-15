package misc.player.computer;

import misc.Color;
import misc.GameState;
import misc.Move;
import misc.MoveSuggestion;
import misc.player.computer.strategy.heuristics.SlotHeuristic;
import misc.player.computer.strategy.minimax.NegaMaxDynamic;

import java.util.List;

/**
 * A computer player utilizing the Negamax algorithm to determine its moves
 */
public class NegaPlayer extends ComputerPlayer {

    public NegaPlayer(Color color, int depth) {
        super("Nega", color, new NegaMaxDynamic(depth, color) {

            private SlotHeuristic slotHeuristic = new SlotHeuristic();

            @Override
            protected int score(GameState state, Color color) {
                int score = 0;
                score += slotHeuristic.score(state, color);
                return score;
            }

            @Override
            protected void orderMoves(List<MoveSuggestion> moves) {

            }

        });
    }

    @Override
    public boolean acceptDraw(GameState state) {
        return false;
    }

}
