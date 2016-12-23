package misc.player.computer.barry;

import misc.Color;
import misc.player.computer.ComputerPlayer;

/**
 *
 */
public class Barry extends ComputerPlayer {

    public static final int DEPTH = 8;

    public Barry(Color color) {
        super("Barry", color, new NegaMaxHeuristic(DEPTH, color)); // TODO
    }



}
