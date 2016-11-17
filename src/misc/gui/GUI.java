package misc.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import misc.GameState;
import misc.Grid;

import javax.swing.*;
import java.awt.*;

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
        this.setContentPane(new GUIPanel(gameState));
        this.pack();
    }
}

