package misc.player.computer.strategy.minimax;

import misc.*;
import misc.player.computer.strategy.Strategy;
import misc.player.human.input.MoveInput;

import java.util.HashMap;
import java.util.List;

/**
 * Negamax alpha beta using transposition tables
 */
public abstract class NegaMaxDynamic extends NegaMaxAlphaBeta {

    /**
     * Transposition table
     *
     * Store game states as hashes because they are not immutable
     */
    private HashMap<Integer, TableEntry> tt;

    /**
     * Create a new negamax strategy with alpha beta pruning
     */
    public NegaMaxDynamic(int depth, Color color) {
        super(depth, color);
        this.tt = new HashMap<>();
    }

    /**
     * Perform the alpha-beta pruned negamax algorithm to obtain the best move
     */
    @Override
    public MoveInput determineMove(GameState state) {
        setBestMove(null);
        negamaxDynamic(state, getDepth(), getMaximizingColor(), -Integer.MAX_VALUE, Integer.MAX_VALUE);
        tt.clear();
        return new MoveInput(getBestMove().getX(), getBestMove().getY());
    }

    /**
     * Negamax algorithm
     */
    private int negamaxDynamic(GameState state, int depth, Color color, int alpha, int beta) {
        int alphaOrig = alpha;
        /** Check if an equal game state has already been evaluated */
        TableEntry entry = tt.get(state.hashCode());
        if (entry != null && entry.getDepth() >= depth) {
            if (entry.getFlag() == TableEntry.EXACT) {
                return entry.getValue();
            } else if (entry.getFlag() == TableEntry.LOWER_BOUND) {
                alpha = Math.max(alpha, entry.getValue());
            } else if (entry.getFlag() == TableEntry.UPPER_BOUND) {
                beta = Math.min(beta, entry.getValue());
            }
            if (alpha >= beta) {
                return entry.getValue();
            }
        }
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
            int score = -negamaxDynamic(state, depth - 1, color.other(), -beta, -alpha);
            /** Compare with previous results */
            if (bestScore < score) {
                bestScore = score;
                if (depth == getDepth()) {
                    setBestMove(move);
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
        /** Store state */
        TableEntry newEntry;
        if (bestScore <= alphaOrig) {
            newEntry = new TableEntry(TableEntry.UPPER_BOUND, depth, bestScore);
        } else if (bestScore >= beta) {
            newEntry = new TableEntry(TableEntry.LOWER_BOUND, depth, bestScore);
        } else {
            newEntry = new TableEntry(TableEntry.EXACT, depth, bestScore);
        }
        tt.put(state.hashCode(), newEntry);

        return bestScore;
    }

    /**
     * Data structure for storing evaluated moves
     */
    private final class TableEntry {

        private static final int UPPER_BOUND = 1;
        private static final int LOWER_BOUND = -1;
        private static final int EXACT = 0;

        private final int flag;
        private final int depth;
        private final int value;

        TableEntry(int flag, int depth, int value) {
            this.flag = flag;
            this.depth = depth;
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("TTEntry: Flag %d, Depth %d, Value %d", flag, depth, value);
        }

        int getFlag() {
            return flag;
        }

        int getDepth() {
            return depth;
        }

        int getValue() {
            return value;
        }


    }

}
