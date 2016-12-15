package misc.player.computer.strategy.heuristics;

import misc.Color;
import misc.GameState;

/**
 * Assigns a numeric value to game state representing how good it is
 */
public interface Heuristic {

    /**
     * Assign a score to this game state
     */
    int score(GameState state, Color color);


}
