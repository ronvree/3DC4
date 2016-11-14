package misc.player.computer;

import misc.Color;
import misc.GameState;
import misc.Grid;
import misc.player.Player;
import misc.player.human.input.PlayerInput;
import misc.player.computer.strategy.Strategy;

/**
 * A connect four player that does not need human input
 */
public abstract class ComputerPlayer extends Player {

    /**
     * Strategy upon which moves are based
     */
    private Strategy strategy;

    /**
     * Constructor
     */
    public ComputerPlayer(String name, Color color, Strategy strategy) {
        super(name, color);
        this.strategy = strategy;
    }

    /**
     * Computers only do moves according to their strategy
     */
    @Override
    public PlayerInput decide(GameState state) {
        return strategy.determineMove(state);
    }


}
