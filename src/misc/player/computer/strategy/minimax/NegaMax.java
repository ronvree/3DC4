package misc.player.computer.strategy.minimax;

import misc.*;
import misc.player.computer.strategy.Strategy;
import misc.player.human.input.MoveInput;

import java.util.List;

/**
 * Negamax strategy implementation
 */
public abstract class NegaMax implements Strategy {

    /** Score for winning game */
    protected static final int WIN = 10000;

    /** Color of maximizing player */
    private final Color maximizingColor;
    /** How many moves this strategy will look ahead */
    private int depth;
    /** Store best move globally */
    private MoveSuggestion bestMove;

    /**
     * Create a new negamax strategy
     */
    public NegaMax(int depth, Color color) {
        this.depth = Math.max(1, depth);
        this.maximizingColor = color;
    }

    /**
     * Give a score to this grid
     */
    protected abstract int score(GameState state, Color color);

    /**
     * Option to explore certain moves first
     */
    protected abstract void orderMoves(List<MoveSuggestion> moves, GameState state);

    /**
     * Run the negamax algorithm to determine the best move
     */
    @Override
    public MoveInput determineMove(GameState state) {
        setBestMove(null);
        negamax(state, depth, maximizingColor);
        return new MoveInput(bestMove.getX(), bestMove.getY());
    }

    /**
     * Negamax algorithm
     */
    private int negamax(GameState state, int depth, Color color)   {
        /** Check base cases */
        if (state.lastMoveWasWinning()) {
            return -WIN;
        }
        if (depth == 0 || state.gridIsFull()) {
            return score(state, color);
        }
        /** Generate move options */
        List<MoveSuggestion> moveOptions = Strategy.generatePossibleMoves(state, color);
        /** Calculate order in which moves should be evaluated */
        orderMoves(moveOptions, state);
        /** Evaluate all possible moves. Minimize loss for maximal result */
        int bestScore = Integer.MIN_VALUE;
        for (MoveSuggestion move : moveOptions) {
            /** Apply move */
            state.doMove(move.getColor(), move.getX(), move.getY());
            /** Determine score */
            int score = -negamax(state, depth - 1, color.other());
            /** Compare with previous results */
            if (bestScore < score) {
                bestScore = score;
                if (depth == this.depth) {
                    this.bestMove = move;
                }
            }
            /** Undo move for reuse of grid */
            state.undoMove();
        }
        return bestScore;
    }

    /**
     * Getters and Setters
     */

    protected MoveSuggestion getBestMove() {
        return bestMove;
    }

    public int getDepth() {
        return depth;
    }

    public Color getMaximizingColor() {
        return maximizingColor;
    }

    public void setDepth(int depth) {
        this.depth = Math.max(1, depth);
    }

    protected void setBestMove(MoveSuggestion bestMove) {
        this.bestMove = bestMove;
    }

}
