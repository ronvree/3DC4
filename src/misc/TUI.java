package misc;

/**
 *
 */
public abstract class TUI {

    /**
     * Constants
     */

    private static final String EMPTY  = "      ";
    private static final String RED    = "RED   ";
    private static final String YELLOW = "YELLOW";

    /**
     * Regular line
     */
    private static final String LINE;
    static {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < Grid.XRANGE; x++) {
            sb.append("+--------");
        }
        sb.append("+");
        sb.append("\n\r");
        LINE = sb.toString();
    }

    /**
     * Line in which pieces are displayed
     */
    private static final String PIECE_LINE;
    static {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < Grid.XRANGE; x++) {
            sb.append("| %s ");
        }
        sb.append("|");
        sb.append("\n\r");
        PIECE_LINE = sb.toString();
    }

    /**
     * Bottom line showing column indices
     */
    private static final String INDEX_LINE;
    static {
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < Grid.XRANGE; x++) {
            sb.append(String.format("|   %d    ", x));
        }
        sb.append("|");
        sb.append("\n\r");
        sb.append("\n\r");
        sb.append("\n\r");
        INDEX_LINE = sb.toString();
    }

    /**
     * Methods
     */

    public static String prettyPrint(GameState state) {
        StringBuilder sb = new StringBuilder();
        sb.append(LINE);
        for (int y = Grid.YRANGE - 1; y >= 0; y--) {
            for (int z = Grid.ZRANGE - 1; z >= 0; z--) {
                String[] row = new String[Grid.XRANGE];
                for (int x = 0; x < Grid.XRANGE; x++) {
                    row[x] = colorToString(state.occupiedBy(x, y, z));
                }
                sb.append(String.format(PIECE_LINE, row));
            }
            sb.append(LINE);
        }
        sb.append(INDEX_LINE);
        return sb.toString();
    }

    private static String colorToString(Color color) {
        if (color == null) return EMPTY;
        switch (color) {
            case YELLOW:
                return YELLOW;
            case RED:
                return RED;
            default:
                return EMPTY;
        }
    }


}
