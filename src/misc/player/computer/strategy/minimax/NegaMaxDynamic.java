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
        this.setBestMove(null);
        negamaxDynamic(state, this.getDepth(), this.getMaximizingColor(), -Integer.MAX_VALUE, Integer.MAX_VALUE, 1);
        tt.clear();
        return new MoveInput(getBestMove().getX(), getBestMove().getY());
    }

    /**
     * Negamax algorithm
     */
    private int negamaxDynamic(GameState state, int depth, Color color, int alpha, int beta, int c) {
        int alphaOrig = alpha;
        TableEntry entry = tt.get(state.hashCode());
        if (entry != null && entry.getDepth() >= depth) {
            switch (entry.getFlag()) {
                case TableEntry.EXACT:
                    return entry.getValue();
                case TableEntry.LOWERBOUND:
                    alpha = Math.max(alpha, entry.getValue());
                case TableEntry.UPPERBOUND:
                    beta = Math.min(beta, entry.getValue());
            }
            if (alpha >= beta) {
                return entry.getValue();
            }
        }

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
            int score = -negamaxDynamic(state, depth - 1, color.other(), -beta, -alpha, -c);
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
        /** Store state */
        TableEntry newEntry;
        if (bestScore <= alphaOrig) {
            newEntry = new TableEntry(TableEntry.UPPERBOUND, depth, bestScore);
        } else if (bestScore >= beta) {
            newEntry = new TableEntry(TableEntry.LOWERBOUND, depth, bestScore);
        } else {
            newEntry = new TableEntry(TableEntry.EXACT, depth, bestScore);
        }
        tt.put(state.hashCode(), newEntry);

        return bestScore;
    }

    /**
     function negamax(node, depth, α, β, color)
     alphaOrig := α

     // Transposition Table Lookup; node is the lookup key for ttEntry
     ttEntry := TranspositionTableLookup( node )
     if ttEntry is valid and ttEntry.depth ≥ depth
         if ttEntry.Flag = EXACT
            return ttEntry.Value
         else if ttEntry.Flag = LOWERBOUND
            α := max( α, ttEntry.Value)
         else if ttEntry.Flag = UPPERBOUND
            β := min( β, ttEntry.Value)
         endif
         if α ≥ β
            return ttEntry.Value
     endif

     if depth = 0 or node is a terminal node
        return color * the heuristic value of node

     bestValue := -∞
     childNodes := GenerateMoves(node)
     childNodes := OrderMoves(childNodes)
     foreach child in childNodes
        v := -negamax(child, depth - 1, -β, -α, -color)
        bestValue := max( bestValue, v )
        α := max( α, v )
        if α ≥ β
            break

     // Transposition Table Store; node is the lookup key for ttEntry
     ttEntry.Value := bestValue
     if bestValue ≤ alphaOrig
        ttEntry.Flag := UPPERBOUND
     else if bestValue ≥ β
        ttEntry.Flag := LOWERBOUND
     else
        ttEntry.Flag := EXACT
     endif
     ttEntry.depth := depth
     TranspositionTableStore( node, ttEntry )

     return bestValue
     */

    /**
     * Data structure for storing evaluated moves
     */
    private class TableEntry { // TODO

        private static final int UPPERBOUND = 1;
        private static final int LOWERBOUND = -1;
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
