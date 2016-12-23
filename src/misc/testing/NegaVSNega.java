package misc.testing;

import misc.Color;
import misc.Game;
import misc.player.computer.barry.Barry;
import misc.player.computer.NegaPlayer;

/**
 *
 */
public class NegaVSNega {

    public static void main(String[] args) {

        NegaPlayer p1 = new NegaPlayer(Color.RED, 8);
        NegaPlayer p2 = new NegaPlayer(Color.YELLOW, 8);

        Barry badpak = new Barry(Color.YELLOW);
        Game game = new Game(p1, badpak);
//        Game game = new Game(badpak, p1);

//        Game game = new Game(p1, p2);

        game.play();

    }


}
