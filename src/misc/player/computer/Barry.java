package misc.player.computer;

import misc.Color;
import misc.player.computer.strategy.minimax.NegaMaxHeuristic;

/**
 *
 */
public class Barry extends ComputerPlayer {

    public static final int DEPTH = 5;

    public Barry(Color color) {
        super("Barry", color, new NegaMaxHeuristic(DEPTH, color)); // TODO
    }



}
