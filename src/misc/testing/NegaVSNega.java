package misc.testing;

import misc.Color;
import misc.Game;
import misc.player.computer.NegaPlayer;

/**
 *
 */
public class NegaVSNega {

    public static void main(String[] args) {

        NegaPlayer p1 = new NegaPlayer(Color.RED, 8);
        NegaPlayer p2 = new NegaPlayer(Color.YELLOW, 8);

        Game game = new Game(p1, p2);

        game.play();

    }


}
