package misc.player.computer.barry;

import misc.Color;
import misc.player.computer.ComputerPlayer;

/**
 *
 */
public class Barry extends ComputerPlayer {

    public static final int DEPTH = 9;

    public Barry(Color color) {
        super("Barry", color, new NegaMaxSpaghetti(DEPTH, color));
    }

    public Barry(Color color, int depth) {
        super("Barry", color, new NegaMaxSpaghetti(depth, color));
    }



}
