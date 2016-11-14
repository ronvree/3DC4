package misc.player;

import misc.Color;
import misc.GameState;
import misc.Grid;
import misc.player.human.input.PlayerInput;

/**
 * A connect-four player
 */
public abstract class Player {

    /**
     * Instance variables
     */

    /** Player name */
    private final String name;
    /** Player color */
    private final Color color;

    /**
     * Constructor
     */

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Methods
     */

    /**
     * Decide which move to make
     * @param state -- Grid on which the decision is based
     * @return player decision
     */
    public abstract PlayerInput decide(GameState state);

    /**
     * @return whether the player accepts resignation
     */
    public abstract boolean acceptDraw(GameState state);

    /**
     * String representation of a player
     */
    @Override
    public String toString() {
        return String.format("Player %s (%s)", this.name, this.color.toString());
    }

    /**
     * Getters
     */

    public String getName() {
        return this.name;
    }

    public Color getColor() {
        return this.color;
    }


}
