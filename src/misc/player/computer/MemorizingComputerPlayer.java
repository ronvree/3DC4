package misc.player.computer;

import misc.Color;
import misc.GameState;
import misc.Grid;
import misc.player.computer.strategy.Strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class MemorizingComputerPlayer extends ComputerPlayer {

    /**
     * Constructor
     */

    public MemorizingComputerPlayer(String name, Color color, Strategy strategy) {
        super(name, color, strategy);
    }

    /**
     * Extended Game State
     *
     */
    private class ExtendedGameState extends GameState {


        private final Map<Color, List<Chain>> chainMap;


        public ExtendedGameState() {
            chainMap = new HashMap<>();
            for (int x = 0; x < Grid.XRANGE; x++) {
                for (int y = 0; y < Grid.YRANGE; y++) {
                    for (int z = 0; z < Grid.ZRANGE; z++) {
                        Color color = this.colorOccupying(x, y, z);

                    }
                }
            }


        }




    }


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

        public void update() { // TODO -- only one slot needs checking
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
