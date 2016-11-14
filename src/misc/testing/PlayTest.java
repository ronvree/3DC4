package misc.testing;

import misc.Color;
import misc.Game;
import misc.GameState;
import misc.Grid;
import misc.player.computer.ComputerPlayer;
import misc.player.computer.strategy.minimax.NegaMaxAlphaBetaTestVersion;
import misc.player.computer.strategy.minimax.NegaMaxDynamicTestVersion;
import misc.player.computer.strategy.minimax.NegaMaxTestVersion;
import misc.player.human.HumanPlayer;

/**
 *
 */
public class PlayTest {

    public static void main(String[] args) {

        HumanPlayer p1 = new HumanPlayer("Henk", Color.RED);
        HumanPlayer p2 = new HumanPlayer("Toos", Color.YELLOW);

//        ComputerPlayer c1 = new ComputerPlayer("Bert", Color.RED, new NegaMaxAlphaBetaTestVersion(8, Color.RED)) {
        ComputerPlayer c1 = new ComputerPlayer("Bert", Color.RED, new NegaMaxDynamicTestVersion(13, Color.RED)) {
            @Override
            public boolean acceptDraw(GameState state) {
                return false;
            }
        };
//        ComputerPlayer c2 = new ComputerPlayer("Ernie", Color.YELLOW, new NegaMaxAlphaBetaTestVersion(8, Color.YELLOW)) {
        ComputerPlayer c2 = new ComputerPlayer("Ernie", Color.YELLOW, new NegaMaxDynamicTestVersion(13, Color.YELLOW)) {
            @Override
            public boolean acceptDraw(GameState state) {
                return false;
            }
        };

//        Game game = new Game(p1, p2);
        Game game = new Game(c1, c2);

        game.play();

    }

}
