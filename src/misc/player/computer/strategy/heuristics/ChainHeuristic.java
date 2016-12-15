package misc.player.computer.strategy.heuristics;

import misc.Color;
import misc.GameState;
import misc.Grid;

/**
 * Score the grid based on chain lengths that have been formed
 */
public class ChainHeuristic implements Heuristic {

    @Override
    public int score(GameState state, Color color) {
        int score = 0;
        score += direction1(state, color);
        score += direction2(state, color);
        score += direction3(state, color);
        score += direction4(state, color);
        return score;
    }

    private int direction1(GameState state, Color color) {
        int score = 0;
        // Iterate through all levels
        for (int z = 0; z < Grid.ZRANGE; z++) {
            // Iterate through each row
            for (int x = 0; x < Grid.XRANGE; x++) {
                int chain = 0;
                for (int y = 0; y < Grid.YRANGE; y++) {
                    if (state.colorOccupying(x, y, z) == color) {
                        chain++;
                    } else {
                        score += scoreForChainOfLength(chain);
                        chain = 0;
                    }
                    if (y == Grid.YRANGE - 1) {
                        score += scoreForChainOfLength(chain);
                    }
                }
            }
        }
        return score;
    }

    private int direction2(GameState state, Color color) {
        int score = 0;
        // Iterate through all levels
        for (int z = 0; z < Grid.ZRANGE; z++) {
            // Iterate through each row
            for (int y = 0; y < Grid.YRANGE; y++) {
                int chain = 0;
                for (int x = 0; x < Grid.XRANGE; x++) {
                    if (state.colorOccupying(x, y, z) == color) {
                        chain ++;
                    } else {
                        score += scoreForChainOfLength(chain);
                        chain = 0;
                    }
                    if (x == Grid.XRANGE - 1) {
                        score += scoreForChainOfLength(chain);
                    }
                }
            }
        }
        return score;
    }

    private int direction3(GameState state, Color color) {
        int score = 0;
        for (int x = 0; x < Grid.XRANGE; x++) {
            int chain = 0;
            for (int y = 0,z = 0; y < Grid.YRANGE && z < Grid.ZRANGE; y++,z++) {
                if (state.colorOccupying(x, y, z) == color) {
                    chain++;
                } else {
                    score += scoreForChainOfLength(chain);
                    chain = 0;
                }
                if (y < Grid.YRANGE && z < Grid.ZRANGE) {

                }
            }
        }
        return score;
    }

    private int direction4(GameState state, Color color) {
        // Check the 4 diagonals
        int score = 0;

        int chain = 0;
        for (int x = 0, y = 0, z = 0; x < Grid.XRANGE && y < Grid.YRANGE && z < Grid.ZRANGE; x++, y++, z++) {
            if (state.colorOccupying(x, y, z) == color) {
                chain++;
            } else {
                score += scoreForChainOfLength(chain);
                chain = 0;
            }
        }

        chain = 0;
        for (int x = 0, y = Grid.YRANGE - 1, z = 0; x < Grid.XRANGE && y >= 0 && z < Grid.ZRANGE; x++, y--, z++) {
            if (state.colorOccupying(x, y, z) == color) {
                chain++;
            } else {
                score += scoreForChainOfLength(chain);
                chain = 0;
            }
        }


        return score;
    }

    private int scoreForChainOfLength(int length) { // TODO -- make function of direction and z as well
        return length; // TODO
    }


}
