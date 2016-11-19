package misc.gui;

import misc.GameState;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rogier on 18-11-16 in Enschede.
 */
public class GUIPanel extends JPanel {
    private GameState gameState;

    public GUIPanel(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void paintComponent(Graphics gc) {
        super.paintComponent(gc);
        for (int y = Grid.YRANGE - 1; y >= 0; y--) {
            for (int x = 0; x < Grid.XRANGE; x++) {
                gc.setColor(Color.BLACK);
                gc.fillRect(90+(200*x),450-(140*y),20,128);
                for (int z = Grid.ZRANGE - 1; z >= 0; z--) {

                    int xrect = 50 + 200*x + (8*z);
                    int yrect = 550 - (140*y) - (32*z);
                    int width = 100 - (16*z);
                    int height = 30;
                    if (gameState.occupiedBy(x,y,z) == misc.Color.RED){
                        gc.setColor(Color.RED);
                        gc.fillRect(xrect,yrect,width,height);
                    } else if (gameState.occupiedBy(x,y,z) == misc.Color.YELLOW){
                        gc.setColor(Color.YELLOW);
                        gc.fillRect(xrect,yrect,width,height);
                    }

                }
            }
        }
    }
}
