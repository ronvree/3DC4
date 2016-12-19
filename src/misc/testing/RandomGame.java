package misc.testing;

import misc.Color;
import misc.Game;
import misc.GameState;
import misc.player.computer.ComputerPlayer;
import misc.player.computer.strategy.NaiveStrategy;

/**
 *
 */
public class RandomGame {

    public static void main(String[] args) {

        ComputerPlayer c1 = new ComputerPlayer("Bert", Color.RED, new NaiveStrategy()) {};
        ComputerPlayer c2 = new ComputerPlayer("Ernie", Color.YELLOW, new NaiveStrategy()) {};

        Game game = new Game(c1, c2);

        game.play();

    }

}
