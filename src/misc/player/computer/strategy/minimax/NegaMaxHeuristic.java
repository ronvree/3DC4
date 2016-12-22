package misc.player.computer.strategy.minimax;

import misc.*;
import misc.player.computer.strategy.Strategy;
import misc.player.human.input.MoveInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static misc.Game.CONNECT;
import static misc.Grid.XRANGE;
import static misc.Grid.YRANGE;
import static misc.Grid.ZRANGE;

/**
 *
 */
public class NegaMaxHeuristic extends NegaMax {

    /** State that memorizes additional information about the game */
    private ExtendedGameState extendedState;
    /** Transposition table. Store game states as hashes */
    private HashMap<Integer, TableEntry> tt;

    /**
     * Constructor
     */
    public NegaMaxHeuristic(int depth, Color color) {
        super(depth, color);
        extendedState = new ExtendedGameState();
        tt = new HashMap<>();
    }

    @Override
    public MoveInput determineMove(GameState state) { // TODO
        setBestMove(null);
        Move moveByOpponent = state.getLastMove();
        if (moveByOpponent != null) {
            extendedState.doMove(moveByOpponent.getColor(), moveByOpponent.getX(), moveByOpponent.getY());
        }
        negamax(extendedState, getDepth(), getMaximizingColor(), -Integer.MAX_VALUE, Integer.MAX_VALUE);
        tt.clear();
        extendedState.doMove(getMaximizingColor(), getBestMove().getX(), getBestMove().getY());
        return new MoveInput(getBestMove().getX(), getBestMove().getY());
    }

    @Override
    protected int score(GameState state, Color color) {
        if (state instanceof ExtendedGameState) {
            int score = 0;
            for (Chain chain : ((ExtendedGameState) state).getWinnableChains()) {
                int redLength = chain.length(Color.RED);
                int yelLength = chain.length(Color.YELLOW);

                int redScore = (int) Math.pow(4, redLength);
                int yelScore = (int) Math.pow(4, yelLength);

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
        return 0; // TODO
    }

    @Override
    protected void orderMoves(List<MoveSuggestion> moves, GameState state) {
        if (state instanceof ExtendedGameState) {

        }

        // TODO
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
        orderMoves(moveOptions, state);
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

        public ExtendedGameState() {
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

        /** Helper method */
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

        private final Coordinate[] slots;
        private final ExtendedGameState state;

        private boolean winnable;
        private int redCount;
        private int yelCount;

        public Chain(ExtendedGameState state, Coordinate... slots) {
            this.slots = slots;
            this.state = state;
            this.winnable = true;
            this.redCount = 0;
            this.yelCount = 0;
        }

        public void update() { // TODO? -- only one slot needs checking
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
            if (redCount > 0 && yelCount > 0) {
                winnable = false;
            }
        }

        public int length(Color color) {
            switch (color)  {
                case RED:
                    return redCount;
                case YELLOW:
                    return yelCount;
                default:
                    return -1;
            }
        }

        public boolean isWinnable() {
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
