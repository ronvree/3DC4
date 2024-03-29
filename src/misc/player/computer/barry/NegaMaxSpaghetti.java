package misc.player.computer.barry;

import misc.*;
import misc.player.computer.strategy.Strategy;
import misc.player.computer.strategy.minimax.NegaMax;
import misc.player.human.input.MoveInput;

import java.util.*;

import static misc.Game.CONNECT;
import static misc.Grid.XRANGE;
import static misc.Grid.YRANGE;
import static misc.Grid.ZRANGE;

/**
 *
 */
class NegaMaxSpaghetti extends NegaMax {

    /** State that memorizes additional information about the game */
    private final ExtendedGameState extendedState;
    /** Transposition table. Store game states as hashes */
    private final HashMap<Integer, TableEntry> tt;

    /** Principal variation transposition table. Keep the scores of the moves under the best move's node. Used for move ordering */
    private final Map<Integer, Map<Integer, Integer>> pvtt; // TODO -- Benchmark for improvement

    /**
     * Constructor
     */
    NegaMaxSpaghetti(int depth, Color color) {
        super(depth, color);
        extendedState = new ExtendedGameState();
        tt = new HashMap<>();

        pvtt = new HashMap<>();
    }

    /** Use the extended game state to determine a move */
    @Override
    public MoveInput determineMove(GameState state) {
        setBestMove(null);
        /** Apply move of opponent to the extended game state */
        Move moveByOpponent = state.getLastMove();
        if (moveByOpponent != null) {
            extendedState.doMove(moveByOpponent.getColor(), moveByOpponent.getX(), moveByOpponent.getY());
        }
        /** Run the mini-max algorithm */
        negamax(extendedState, getDepth(), getMaximizingColor(), -Integer.MAX_VALUE, Integer.MAX_VALUE);
        /** Apply own move to private game copy */
        extendedState.doMove(getMaximizingColor(), getBestMove().getX(), getBestMove().getY());

        /** Set move ordering for next move */
        pvtt.clear();
        /**  - Iterate through all moves that can be made by opponent */
        for (MoveSuggestion opponentSuggestion : Strategy.generatePossibleMoves(extendedState, getMaximizingColor().other())) {
            extendedState.doMove(opponentSuggestion.getColor(), opponentSuggestion.getX(), opponentSuggestion.getY());
            Map<Integer, Integer> optionScores = new HashMap<>();
            pvtt.put(extendedState.hashCode(), optionScores);
            /** Store all branches that have already been evaluated */
            for (MoveSuggestion suggestion : Strategy.generatePossibleMoves(extendedState, getMaximizingColor())) {
                extendedState.doMove(suggestion.getColor(), suggestion.getX(), suggestion.getY());
                Integer branchHash = extendedState.hashCode();
                /** Obtain branch */
                TableEntry e = tt.get(branchHash);
                if (e != null) {
                    optionScores.put(branchHash, e.getValue());
                }
                /** Undo move for reuse of grid */
                extendedState.undoMove();
            }
            /** Undo move for reuse of grid */
            extendedState.undoMove();
        }

        /** Reset the transposition table */
        tt.clear();
        /** Return the decision */
        return new MoveInput(getBestMove().getX(), getBestMove().getY());
    }

    /** Assign a score to the game state */
    @Override
    protected int score(GameState state, Color color) {
        if (state instanceof ExtendedGameState) {
            int score = 0;
            /** Sum a score for all chains which are winnable */
            for (Chain chain : ((ExtendedGameState) state).getWinnableChains()) {
                /** Get the chain length for each color */
                int redLength = chain.length(Color.RED);
                int yelLength = chain.length(Color.YELLOW);
                /** Assign a score to the chain */
                int redScore = (int) Math.pow(2, redLength);
                int yelScore = (int) Math.pow(2, yelLength);
                /** Add score to total */
                if (color == Color.RED) {
                    score += redScore;
                    score -= yelScore;
                } else {
                    score -= redScore;
                    score += yelScore;
                }
            }
            return score;
        }
        return 0;
    }

    /** Determine the order in which moves are evaluated */
    @Override
    protected void orderMoves(List<MoveSuggestion> moves, GameState state) {
        if (state instanceof ExtendedGameState) {
            /** Keep a mapping of the direct scores that result from applying the move */
            Map<MoveSuggestion, Integer> scoreMap = new HashMap<>();
            /** Calculate the direct effect on the grid by applying each move */
            for (MoveSuggestion suggestion : moves) {
                scoreMap.put(suggestion, directEffectOf(suggestion, (ExtendedGameState) state));
            }
            /** Sort moves based on their direct effect. Best moves go first */
            moves.sort((o1, o2) -> {
                if (scoreMap.get(o1) > scoreMap.get(o2)) {
                    return -1;
                } else if (scoreMap.get(o1) < scoreMap.get(o2)) {
                    return 1;
                } else {
                    return 0;
                }
            });
        }
    }

    /** Get the score of the grid after applying the move */
    private int directEffectOf(MoveSuggestion move, ExtendedGameState state) {
        state.doMove(move.getColor(), move.getX(), move.getY());
        int score = score(state, move.getColor());
        state.undoMove();
        return score;
    }

    /** Sort moves based on the principal variation of the previous turn */
    private void orderMovesByPV(List<MoveSuggestion> moves, ExtendedGameState state) {
        Map<Integer, Integer> branches = pvtt.get(state.hashCode());
        if (branches != null) {
            /** Generate all hashes that result from making the suggested moves. This way scores can be obtained from the transposition table */
            Map<MoveSuggestion, Integer> hashes = new HashMap<>();
            for (MoveSuggestion suggestion : moves) {
                state.doMove(suggestion.getColor(), suggestion.getX(), suggestion.getY());
                hashes.put(suggestion, state.hashCode());
                state.undoMove();
            }
            /** Put the most promising moves first in the list */
            Map<MoveSuggestion, Integer> directScores = new HashMap<>(); // Keep a table of direct scores by applying a move so they are only calculated once
            moves.sort((o1, o2) -> {
                /** Obtain the values as calculated in the previous call of negamax */
                Integer value1 = branches.get(hashes.get(o1));
                Integer value2 = branches.get(hashes.get(o2));
                /** Compare values */
                if (value1 != null) {
                    if (value2 != null) {
                        /** Both branches have already been evaluated -> check value */
                        return -Integer.compare(value1, value2); // Results are negated because larger values must appear first in the result
                    } else {
                        /** Only branch 1 has been evaluated -> branch 1 > branch 2 */
                        return 1;
                    }
                } else {
                    if (value2 != null) {
                        /** Only branch 2 has been evaluated -> branch 1 < branch 2 */
                        return -1;
                    } else {
                        /** Neither branches have been evaluated -> determine score based on direct effect */
                        if (directScores.containsKey(o1)) {
                            /** Obtain the direct score from the table */
                            value1 = directScores.get(o1);
                        } else {
                            /** Calculate the score and put it in the table for later use */
                            int score = directEffectOf(o1, state);
                            directScores.put(o1, score);
                            value1 = score;
                        }
                        if (directScores.containsKey(o2)) {
                            /** Obtain the direct score from the table */
                            value2 = directScores.get(o2);
                        } else {
                            /** Calculate the score and put it in the table for later use */
                            int score = directEffectOf(o2, state);
                            directScores.put(o2, score);
                            value2 = score;
                        }
                        /** Compare direct scores */
                        return -Integer.compare(value1, value2);
                    }
                }
            });
        } else {
            /** No previous evaluation present. Evaluate by direct effect */
            orderMoves(moves, state);
        }
    }

    /**
     * Negamax algorithm
     */
    private int negamax(ExtendedGameState state, int depth, Color color, int alpha, int beta) {
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
        if (depth == this.getDepth()) {
            /** Use result of previous evaluation to determine order */
//            orderMoves(moveOptions, state);
            orderMovesByPV(moveOptions, state);
        } else {
            /** Order moves based on recalculated heuristics */
            orderMoves(moveOptions, state);
        }
        /** Evaluate all possible moves. Minimize loss for maximal result */
        int bestScore = Integer.MIN_VALUE;
        for (MoveSuggestion move : moveOptions) {
            /** Apply move */
            state.doMove(move.getColor(), move.getX(), move.getY());
            /** Determine score */
            int score = -negamax(state, depth - 1, color.other(), -beta, -alpha);
            /** Compare with previous results */
            if (bestScore < score) {
                bestScore = score;
                if (depth == getDepth()) {
                    setBestMove(move);
                }
            }

            if (depth == getDepth()) {
                System.out.printf("Rated move %s as %d\n\r", move.toString(), score);
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
     * Extended Game State class which stores more information of the game
     */
    private class ExtendedGameState extends GameState {

        /** A mapping of all slots to which chains they might contribute */
        private final ArrayList<Chain>[][][] chains;
        /** A list of all winnable chains */
        private final ArrayList<Chain> winnableChains;

        private ExtendedGameState() {
            winnableChains = new ArrayList<>();
            /** Initialize the chain mapping */
            chains = new ArrayList[Grid.XRANGE][Grid.YRANGE][Grid.ZRANGE];
            for (int x = 0; x < Grid.XRANGE; x++) {
                for (int y = 0; y < Grid.YRANGE; y++) {
                    for (int z = 0; z < Grid.ZRANGE; z++) {
                        chains[x][y][z] = new ArrayList<>();
                    }
                }
            }
            /** Add horizontal chains along x axis */
            for (int y = 0; y < YRANGE; y++) {
                for (int z = 0; z < ZRANGE; z++) {
                    for (int startIndex = 0; startIndex <= XRANGE - CONNECT; startIndex++) {
                        Coordinate[] slots = new Coordinate[CONNECT];
                        for (int i = 0; i < CONNECT; i++) {
                            slots[i] = new Coordinate(i + startIndex, y, z);
                        }
                        Chain chain = new Chain(this, slots);
                        for (Coordinate slot : slots) {
                            chains[slot.getX()][slot.getY()][slot.getZ()].add(chain);
                        }
                    }
                }
            }
            /** Add horizontal chains along y axis */
            for (int x = 0; x < XRANGE; x++) {
                for (int z = 0; z < ZRANGE; z++) {
                    for (int startIndex = 0; startIndex <= YRANGE - CONNECT; startIndex++) {
                        Coordinate[] slots = new Coordinate[CONNECT];
                        for (int i = 0; i < CONNECT; i++) {
                            slots[i] = new Coordinate(x, i + startIndex, z);
                        }
                        Chain chain = new Chain(this, slots);
                        for (Coordinate slot : slots) {
                            chains[slot.getX()][slot.getY()][slot.getZ()].add(chain);
                        }
                    }
                }
            }
            /** Add vertical chains along z axis */
            for (int x = 0; x < XRANGE; x++) {
                for (int y = 0; y < YRANGE; y++) {
                    for (int startIndex = 0; startIndex <= ZRANGE - CONNECT; startIndex++) {
                        Coordinate[] slots = new Coordinate[CONNECT];
                        for (int i = 0; i < CONNECT; i++) {
                            slots[i] = new Coordinate(x, y, i + startIndex);
                        }
                        Chain chain = new Chain(this, slots);
                        for (Coordinate slot : slots) {
                            chains[slot.getX()][slot.getY()][slot.getZ()].add(chain);
                        }
                    }
                }
            }
            /** Add diagonal chains along xy plane */
            for (int z = 0; z < ZRANGE; z++) { // TODO -- over y
                for (int xStart = 0; xStart <= XRANGE - CONNECT; xStart++) {
                    for (int yStart = 0; yStart <= YRANGE - CONNECT; yStart++) {
                        Coordinate[] slots = new Coordinate[CONNECT];
                        for (int i = 0; i < CONNECT; i++) {
                            slots[i] = new Coordinate(xStart + i, yStart + i, z);
                        }
                        Chain chain = new Chain(this, slots);
                        for (Coordinate slot : slots) {
                            chains[slot.getX()][slot.getY()][slot.getZ()].add(chain);
                        }
                    }
                    for (int yStart = YRANGE - 1; yStart >= CONNECT - 1; yStart--) {
                        Coordinate[] slots = new Coordinate[CONNECT];
                        for (int i = 0; i < CONNECT; i++) {
                            slots[i] = new Coordinate(xStart + i, yStart - i, z);
                        }
                        Chain chain = new Chain(this, slots);
                        for (Coordinate slot : slots) {
                            chains[slot.getX()][slot.getY()][slot.getZ()].add(chain);
                        }
                    }
                }
            }
            /** Add diagonal chains along xz plane */
            for (int y = 0; y < YRANGE; y++) { // TODO -- over z
                for (int xStart = 0; xStart <= XRANGE - CONNECT; xStart++) {
                    for (int zStart = 0; zStart <= ZRANGE - CONNECT; zStart++) {
                        Coordinate[] slots = new Coordinate[CONNECT];
                        for (int i = 0; i < CONNECT; i++) {
                            slots[i] = new Coordinate(xStart + i, y, zStart + i);
                        }
                        Chain chain = new Chain(this, slots);
                        for (Coordinate slot : slots) {
                            chains[slot.getX()][slot.getY()][slot.getZ()].add(chain);
                        }
                    }
                    for (int zStart = ZRANGE - 1; zStart >= CONNECT - 1; zStart--) {
                        Coordinate[] slots = new Coordinate[CONNECT];
                        for (int i = 0; i < CONNECT; i++) {
                            slots[i] = new Coordinate(xStart + i, y, zStart - i);
                        }
                        Chain chain = new Chain(this, slots);
                        for (Coordinate slot : slots) {
                            chains[slot.getX()][slot.getY()][slot.getZ()].add(chain);
                        }
                    }
                }
            }
            /** Add diagonal chains along yz plane */
            for (int x = 0; x < XRANGE; x++) { // TODO -- over y
                for (int zStart = 0; zStart <= ZRANGE - CONNECT; zStart++) {
                    for (int yStart = 0; yStart <= YRANGE - CONNECT; yStart++) {
                        Coordinate[] slots = new Coordinate[CONNECT];
                        for (int i = 0; i < CONNECT; i++) {
                            slots[i] = new Coordinate(x, yStart + i, zStart + i);
                        }
                        Chain chain = new Chain(this, slots);
                        for (Coordinate slot : slots) {
                            chains[slot.getX()][slot.getY()][slot.getZ()].add(chain);
                        }
                    }
                    for (int yStart = YRANGE - 1; yStart >= CONNECT - 1; yStart--) {
                        Coordinate[] slots = new Coordinate[CONNECT];
                        for (int i = 0; i < CONNECT; i++) {
                            slots[i] = new Coordinate(x, yStart - i, zStart + i);
                        }
                        Chain chain = new Chain(this, slots);
                        for (Coordinate slot : slots) {
                            chains[slot.getX()][slot.getY()][slot.getZ()].add(chain);
                        }
                    }
                }
            }

            /** Add diagonal chains in xyz space */
            // TODO -- not just main diagonal

            int x, y, z;
            Coordinate[] slots;
            Chain chain;

            x = 0;
            y = 0;
            z = 0;
            slots = new Coordinate[CONNECT];
            for (int i = 0; i < CONNECT; i++) {
                slots[i] = new Coordinate(x + i, y + i, z + i);
            }
            chain = new Chain(this, slots);
            for (Coordinate slot : slots) {
                chains[slot.getX()][slot.getY()][slot.getZ()].add(chain);
            }

            x = XRANGE - 1;
            y = 0;
            z = 0;
            slots = new Coordinate[CONNECT];
            for (int i = 0; i < CONNECT; i++) {
                slots[i] = new Coordinate(x - i, y + i, z + i);
            }
            chain = new Chain(this, slots);
            for (Coordinate slot : slots) {
                chains[slot.getX()][slot.getY()][slot.getZ()].add(chain);
            }

            x = 0;
            y = YRANGE - 1;
            z = 0;
            slots = new Coordinate[CONNECT];
            for (int i = 0; i < CONNECT; i++) {
                slots[i] = new Coordinate(x + i, y - i, z + i);
            }
            chain = new Chain(this, slots);
            for (Coordinate slot : slots) {
                chains[slot.getX()][slot.getY()][slot.getZ()].add(chain);
            }

            x = XRANGE - 1;
            y = YRANGE - 1;
            z = 0;
            slots = new Coordinate[CONNECT];
            for (int i = 0; i < CONNECT; i++) {
                slots[i] = new Coordinate(x - i, y - i, z + i);
            }
            chain = new Chain(this, slots);
            for (Coordinate slot : slots) {
                chains[slot.getX()][slot.getY()][slot.getZ()].add(chain);
            }


            /** Set all chains to winnable */
            for (ArrayList<Chain>[][] i : chains) {
                for (ArrayList<Chain>[] j : i) {
                    for (ArrayList<Chain> chains : j) {
                        winnableChains.addAll(chains);
                    }
                }
            }

        }

        /** Perform a move on the grid. Update chains */
        @Override
        public boolean doMove(Color color, int x, int y) {
            boolean succeeded = super.doMove(color, x, y);
            if (succeeded) {
                Move move = getLastMove();
                List<Chain> affectedChains = chains[move.getX()][move.getY()][move.getZ()];
                /** Update all affected chains */
                for (Chain chain : affectedChains) {
                    if (chain.isWinnable()) {
                        chain.update();
                        if (!chain.isWinnable()) {
                            /** The chain is not winnable anymore. Remove from list */
                            winnableChains.remove(chain);
                        }
                    } else {
                        /** The chain was not winnable to begin with */
                        chain.update();
                    }
                }
            }
            return succeeded;
        }

        /** Undo a move on the grid. Update chains */
        @Override
        public void undoMove() {
            Move move = getLastMove();
            super.undoMove();
            List<Chain> affectedChains = chains[move.getX()][move.getY()][move.getZ()];
            /** Update all affected chains */
            for (Chain chain : affectedChains) {
                if (!chain.isWinnable()) {
                    chain.update();
                    if (chain.isWinnable()) {
                        /** The chain has become winnable. Add to list */
                        winnableChains.add(chain);
                    }
                } else {
                    /** The chain will remain winnable */
                    chain.update();
                }
            }
        }

        /** Convenience method */
        private Color colorOccupying(Coordinate coordinate) {
            return colorOccupying(coordinate.getX(), coordinate.getY(), coordinate.getZ());
        }

        /** Get all chains that can still be used to obtain victory */
        private List<Chain> getWinnableChains() {
            return winnableChains;
        }


    }

    /**
     * Points to all colors that could potentially form a chain
     */
    private class Chain {

        /** Coordinates of the slots forming this chain */
        private final Coordinate[] slots;
        /** GameState in which this chain could be formed */
        private final ExtendedGameState state;
        /** Indicates if the chain is winnable by a player */
        private boolean winnable;
        /** Amount of red pieces in the chain */
        private int redCount;
        /** Amount of yellow pieces in the chain */
        private int yelCount;

        /** Construct a new chain in a certain GameState, consisting out of the specified slots */
        private Chain(ExtendedGameState state, Coordinate... slots) {
            this.slots = slots;
            this.state = state;
            this.winnable = true;
            this.redCount = 0;
            this.yelCount = 0;
        }

        /** Update the chain's instance variables */
        private void update() { // TODO? -- only one slot needs checking
            redCount = 0;
            yelCount = 0;
            winnable = true;
            for (Coordinate slot : slots) {
                Color color = state.colorOccupying(slot);
                if (color == Color.RED) {
                    redCount++;
                } else if (color == Color.YELLOW) {
                    yelCount++;
                }
            }
            /** Check if both players have pieces in this chain */
            if (redCount > 0 && yelCount > 0) {
                winnable = false;
            }
        }

        /** Return how many pieces of the specified color are in this chain */
        private int length(Color color) {
            switch (color)  {
                case RED:
                    return redCount;
                case YELLOW:
                    return yelCount;
                default:
                    return -1;
            }
        }

        /** Return if the chain is winnable by a player */
        private boolean isWinnable() {
            return winnable;
        }


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
