package misc.player.computer.strategy.minimax;

import misc.*;
import misc.player.computer.strategy.Strategy;
import misc.player.human.input.MoveInput;

import java.util.List;

/**
 * Negamax strategy implementation
 */
public abstract class NegaMax implements Strategy {

    /**
     * Constants
     */

    /** Score for winning game */
    protected static final int WIN = 10000;

    /**
     * Instance variables
     */

    /** Color of maximizing player */
    private final Color maximizingColor;
    /** How many moves this strategy will look ahead */
    private int depth;
    /** Store best move globally */
    private Move bestMove;

    /**
     * Constructor
     */

    /**
     * Create a new negamax strategy
     */
    public NegaMax(int depth, Color color) {
        this.depth = Math.max(1, depth);
        this.maximizingColor = color;
    }

    /**
     * Abstract methods
     */

    /**
     * Give a score to this grid
     */
    protected abstract int score(GameState state, Color color);

    /**
     * Option to explore certain moves first
     */
    protected abstract void orderMoves(List<Move> moves);

    /**
     * Methods
     */

    /**
     * Run the negamax algorithm to determine the best move
     */
    @Override
    public MoveInput determineMove(GameState state) {
        this.setBestMove(null);
        negamax(state, this.depth, this.maximizingColor, 1);
        return new MoveInput(bestMove.getX(), bestMove.getY());
    }

    /**
     * Negamax algorithm
     */
    private int negamax(GameState state, int depth, Color color, int c)   {
        /** Check base cases */
        if (state.lastMoveWasWinning()) {
            return c * WIN;
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
            int score = -negamax(state, depth - 1, color.other(), -c);
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

    protected Move getBestMove() {
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

    protected void setBestMove(Move bestMove) {
        this.bestMove = bestMove;
    }

}
