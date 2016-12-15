package misc.testing;

import misc.Color;
import misc.Game;
import misc.GameState;
import misc.player.computer.ComputerPlayer;
import misc.player.computer.NegaPlayer;
import misc.player.human.HumanPlayer;

/**
 *
 */
public class VersusComputer {

    public static void main(String[] args) {

        HumanPlayer p1 = new HumanPlayer("Henk", Color.RED);

        ComputerPlayer p2 = new NegaPlayer(Color.YELLOW, 8);

        Game game = new Game(p1, p2);
        game.play();

    }



}
