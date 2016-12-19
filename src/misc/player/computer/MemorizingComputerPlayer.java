package misc.player.computer;

import misc.Color;
import misc.GameState;
import misc.Grid;
import misc.Move;
import misc.player.computer.strategy.Strategy;
import misc.player.human.input.MoveInput;
import misc.player.human.input.PlayerInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static misc.Game.CONNECT;
import static misc.Grid.XRANGE;
import static misc.Grid.YRANGE;
import static misc.Grid.ZRANGE;

/**
 *
 */
public abstract class MemorizingComputerPlayer extends ComputerPlayer {

    /** State that memorizes additional information about the game */
    private ExtendedGameState extendedState;

    /**
     * Constructor
     */

    public MemorizingComputerPlayer(String name, Color color, Strategy strategy) {
        super(name, color, strategy);
        extendedState = new ExtendedGameState();
    }


    @Override
    public PlayerInput decide(GameState state) {
        MoveInput decision = strategy.determineMove(state);
        extendedState.doMove(getColor(), decision.getX(), decision.getY());
        return decision;
    }

    /**
     * Extended Game State
     */
    private class ExtendedGameState extends GameState { // TODO -- extended game state should be part of strategy

        /** A mapping of all slots to which chains they might contribute */
        private final Map<Color, List<Chain>> chainMap;

        public ExtendedGameState() {

            /** Initialize chain map */
            chainMap = new HashMap<>();
            for (int x = 0; x < Grid.XRANGE; x++) {
                for (int y = 0; y < Grid.YRANGE; y++) {
                    for (int z = 0; z < Grid.ZRANGE; z++) {
                        Color color = this.colorOccupying(x, y, z);
                        chainMap.put(color, new ArrayList<>());
                    }
                }
            }
            /** Add horizontal chains along x axis */
            for (int y = 0; y < YRANGE; y++) {
                for (int z = 0; z < ZRANGE; z++) {
                    for (int startIndex = 0; startIndex <= XRANGE - CONNECT; startIndex++) {
                        Color[] slots = new Color[CONNECT];
                        for (int i = 0; i < CONNECT; i++) {
                            slots[i] = colorOccupying(i + startIndex, y, z);
                        }
                        Chain chain = new Chain(slots);
                        for (Color slot : slots) {
                            chainMap.get(slot).add(chain);
                        }
                    }
                }
            }
            /** Add horizontal chains along y axis */
            for (int x = 0; x < XRANGE; x++) {
                for (int z = 0; z < ZRANGE; z++) {
                    for (int startIndex = 0; startIndex <= YRANGE - CONNECT; startIndex++) {
                        Color[] slots = new Color[CONNECT];
                        for (int i = 0; i < CONNECT; i++) {
                            slots[i] = colorOccupying(x, i + startIndex, z);
                        }
                        Chain chain = new Chain(slots);
                        for (Color slot : slots) {
                            chainMap.get(slot).add(chain);
                        }
                    }
                }
            }
            /** Add vertical chains along z axis */
            for (int x = 0; x < XRANGE; x++) {
                for (int y = 0; y < YRANGE; y++) {
                    for (int startIndex = 0; startIndex <= ZRANGE - CONNECT; startIndex++) {
                        Color[] slots = new Color[CONNECT];
                        for (int i = 0; i < CONNECT; i++) {
                            slots[i] = colorOccupying(x, y, i + startIndex);
                        }
                        Chain chain = new Chain(slots);
                        for (Color slot : slots) {
                            chainMap.get(slot).add(chain);
                        }
                    }
                }
            }
            /** Add diagonal chains along xy plane */

            for (int z = 0; z < ZRANGE; z++) { // TODO -- this is wrong
                for (int x = XRANGE - CONNECT; x >= 0; x--) {
                    for (int startIndex = 0; startIndex <= XRANGE - CONNECT && startIndex <= YRANGE - CONNECT; startIndex++) {
                        Color[] slots1 = new Color[CONNECT];
                        Color[] slots2 = new Color[CONNECT];
                        for (int i = 0, y = YRANGE - 1; i < CONNECT && y >= 0; i++, y--) {
                            slots1[i] = colorOccupying(x - startIndex - i, y - startIndex, z);
                        }
                        for (int i = 0, y = 0; i < CONNECT && y < YRANGE; i++, y++) {
                            slots2[i] = colorOccupying(x - startIndex - i, y + startIndex, z);
                        }
                        Chain chain1 = new Chain(slots1);
                        Chain chain2 = new Chain(slots2);
                        for (Color slot : slots1) {
                            chainMap.get(slot).add(chain1);
                        }
                        for (Color slot : slots2) {
                            chainMap.get(slot).add(chain2);
                        }
                    }
                }
            }
            /** Add diagonal chains along xz plane */
            // TODO
            /** Add diagonal chains along yz plane */
            // TODO
            /** Add diagonal chains in xyz space */
            // TODO




        }

        /** Perform a move on the grid. Update chains */
        @Override
        public boolean doMove(Color color, int x, int y) {
            boolean succeeded = super.doMove(color, x, y);
            if (succeeded) {
                Move move = getLastMove();
                List<Chain> affectedChains = chainMap.get(colorOccupying(move.getX(), move.getY(), move.getZ()));
                affectedChains.forEach(Chain::update);
            }
            return succeeded;
        }

        /** Undo a move on the grid. Update chains */
        @Override
        public void undoMove() {
            Move move = getLastMove();
            super.undoMove();
            List<Chain> affectedChains = chainMap.get(colorOccupying(move.getX(), move.getY(), move.getZ()));
            affectedChains.forEach(Chain::update);
        }


    }


    /**
     * Points to all colors that could potentially form a chain
     */
    private class Chain {

        private final Color[] slots;

        private boolean winnable;
        private int redCount;
        private int yelCount;

        public Chain(Color... slots) {
            this.slots = slots;
            this.winnable = true;
            this.redCount = 0;
            this.yelCount = 0;
        }

        public void update() { // TODO? -- only one slot needs checking
            redCount = 0;
            yelCount = 0;
            winnable = true;
            for (Color slot : slots) {
                if (slot == Color.RED) {
                    redCount++;
                } else if (slot == Color.YELLOW) {
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


}
