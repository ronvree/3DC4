package misc.player.computer;

import misc.Color;
import misc.GameState;
import misc.player.computer.strategy.NaiveStrategy;

/**
 *
 */
public class NaivePlayer extends ComputerPlayer {

    public NaivePlayer(Color color) {
        super(String.format("Naive Player (%s)", color.toString()), color, new NaiveStrategy());
    }

    @Override
    public boolean acceptDraw(GameState state) {
        return false;
    }
}
