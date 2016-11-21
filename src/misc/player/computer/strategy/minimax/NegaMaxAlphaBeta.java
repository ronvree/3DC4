package misc.player.computer.strategy.minimax;

import misc.*;
import misc.player.computer.strategy.Strategy;
import misc.player.human.input.MoveInput;

import java.util.List;

/**
 * Negamax strategy implementation with alpha beta pruning
 */
public abstract class NegaMaxAlphaBeta extends NegaMax {

    /**
     * Create a new negamax strategy with alpha beta pruning
     */
    public NegaMaxAlphaBeta(int depth, Color color) {
        super(depth, color);
    }

    /**
     * Perform the alpha-beta pruned negamax algorithm to obtain the best move
     */
    @Override
    public MoveInput determineMove(GameState state) {
        this.setBestMove(null);
        int score = negamaxAlphaBeta(state, this.getDepth(), this.getMaximizingColor(), -Integer.MAX_VALUE, Integer.MAX_VALUE);
        System.out.printf("Rated %s as %d\n\r", getBestMove().toString(), score);
        return new MoveInput(getBestMove().getX(), getBestMove().getY());
    }

    /**
     * Negamax algorithm
     */
    private int negamaxAlphaBeta(GameState state, int depth, Color color, int alpha, int beta) {
        /** Check base cases */
        if (state.lastMoveWasWinning()) {
            return -WIN;
        }
        if (depth == 0 || state.gridIsFull()) {
            return score(state, color);
        }
        /** Generate move options */
        List<Move> moveOptions = Strategy.generatePossibleMoves(state, color);
        /** Calculate order in which moves should be evaluated */
        orderMoves(moveOptions);
        /** Evaluate all possible moves. Minimize loss for maximal result */
        int bestScore = Integer.MIN_VALUE;
        for (Move move : moveOptions) {
            /** Apply move */
            state.doMove(move);
            /** Determine score */
            int score = -negamaxAlphaBeta(state, depth - 1, color.other(), -beta, -alpha);
            /** Compare with previous results */
            if (bestScore < score) {
                bestScore = score;
                if (depth == this.getDepth()) {
                    this.setBestMove(move);
                }
            }
            /** Undo move for reuse of grid */
            state.undoMove();
            /** Prune! */
            alpha = Math.max(alpha, score);
            if (alpha >= beta) {
                break;
            }
        }
        return bestScore;
    }


}
