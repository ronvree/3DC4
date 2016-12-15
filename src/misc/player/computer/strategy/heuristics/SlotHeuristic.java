package misc.player.computer.strategy.heuristics;

import misc.Color;
import misc.GameState;

import static misc.Grid.XRANGE;
import static misc.Grid.YRANGE;
import static misc.Grid.ZRANGE;

/**
 * Assign a score based on slot location
 */
public class SlotHeuristic implements Heuristic {

    private static final int[][][] SLOT_SCORES;
    static {
        SLOT_SCORES = new int[XRANGE][YRANGE][ZRANGE]; // TODO -- make instance variable

        int xmid = XRANGE/2;
        int ymid = YRANGE/2;
        int zmid = ZRANGE/2;

        for (int x = 0; x < XRANGE; x++) {
            for (int y = 0; y < YRANGE; y++) {
                for (int z = 0; z < ZRANGE; z++) {
                    SLOT_SCORES[x][y][z] = (XRANGE - Math.abs(xmid - x)) + (YRANGE - Math.abs(ymid - y)) + (ZRANGE - Math.abs(zmid - z));
                }
            }
        }
//
//        for (int[][] plane : SLOT_SCORES) {
//            for (int[] line : plane) {
//                String s = String.format("%2d %2d %2d %2d", line[0], line[1], line[2], line[3]);
//                System.out.println(s);
//            }
//            System.out.println();
//        }

    }

    @Override
    public int score(GameState state, Color color) {
        int score = 0;
        for (int x = 0; x < XRANGE; x++) {
            for (int y = 0; y < YRANGE; y++) {
                for (int z = 0; z < ZRANGE; z++) {
                    Color c = state.colorOccupying(x, y, z);
                    if (color == c) {
                        score += SLOT_SCORES[x][y][z];
                    } else if (color.other() == c) {
                        score -= SLOT_SCORES[x][y][z];
                    }
                }
            }
        }
        return score;
    }


}
