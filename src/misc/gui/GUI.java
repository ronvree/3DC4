package misc.gui;

import misc.GameState;

import javax.swing.*;
import java.awt.*;

import static java.lang.Thread.sleep;

/**
 * Created by Rogier on 17-11-16 in Enschede.
 */



public class GUI extends JFrame {
    public GUI() throws HeadlessException {
        super();
        init();
    }

    public void init() {
        this.setTitle("Hello World!");
        this.setResizable(false);
        Dimension d = new Dimension(800,600);
        this.setPreferredSize(d);
        JPanel panel = new JPanel();
        this.setContentPane(panel);
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public void update(GameState gameState){
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.setContentPane(new GUIPanel(gameState));
        this.pack();
    }
}

