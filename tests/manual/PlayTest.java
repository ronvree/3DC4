package manual;

import misc.Color;
import misc.Game;
import misc.GameState;
import misc.player.computer.ComputerPlayer;
import misc.player.human.HumanPlayer;

/**
 *
 */
public class PlayTest {

    public static void main(String[] args) {

        HumanPlayer p1 = new HumanPlayer("Henk", Color.RED);
        HumanPlayer p2 = new HumanPlayer("Toos", Color.YELLOW);

        Game game = new Game(p1, p2);

        game.play();

    }

}
