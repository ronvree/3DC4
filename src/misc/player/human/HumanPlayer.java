package misc.player.human;

import misc.Color;
import misc.GameState;
import misc.Grid;
import misc.player.Player;
import misc.player.human.input.*;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A human connect four player
 */
public class HumanPlayer extends Player {

    /**
     * Constants
     */

    /** Yes command */
    private static final String YES = "yes";
    /** No command */
    private static final String NO = "no";
    /** Resignation command */
    private static final String RESIGN = "resign";
    /** Draw request command */
    private static final String DRAW = "draw";
    /** Help request */
    private static final String HELP = "help";

    /** Move input consist of two numbers, seperated by a space */
    private static final String MOVE_INPUT_FORMAT = String.format("[0-%s] [0-%s]", Integer.toString(Grid.XRANGE - 1), Integer.toString(Grid.YRANGE - 1));
    /** Compiled version of the input regex */
    private static final Pattern MOVE_INPUT_REGEX = Pattern.compile(MOVE_INPUT_FORMAT);

    /** The player is asked to make a move */
    private static final String MOVE_REQUEST = "what is your next move?";
    /** The player is asked to agree to a draw */
    private static final String DRAW_REQUEST = "The opponent proposed a draw. Accept? (" + YES + "/" + NO + ")";
    /** The player is informed their input was invalid */
    private static final String INVALID_INPUT = "Input invalid! Try again";
    /** Where the human player input is obtained from */
    private static final InputStream INPUT_SOURCE = System.in;

    /**
     * Constructor
     */
    public HumanPlayer(String name, Color color) {
        super(name, color);
    }

    /***
     * Ask the player input until it is valid
     */
    @Override
    public PlayerInput decide(GameState state) { // TODO -- show game state
        PlayerInput result;
        /** Ask the player for input */
        System.out.println(this + ", " + MOVE_REQUEST);
        Scanner scanner = new Scanner(INPUT_SOURCE);
        do {
            /** Read player input */
            String input = scanner.nextLine();
            if (input.equals(RESIGN)) {
                result = new Resignation();
                break;
            }
            if (input.equals(DRAW)) {
                result = new DrawRequest();
                break;
            }
            if (input.equals(HELP)) {
                result =  new HelpRequest();
                break;
            }
            Matcher matcher = MOVE_INPUT_REGEX.matcher(input);
            if (matcher.matches()) {
                String[] ss = input.split(" ");
                int x = Integer.parseInt(ss[0]);
                int y = Integer.parseInt(ss[1]);
                result = new MoveInput(x, y);
                break;
            }
            System.out.println(INVALID_INPUT);
        } while (true);

//        scanner.close();
        return result;
    }

    /**
     * Ask the player if they accept a draw
     */
    @Override
    public boolean acceptDraw(GameState state) { // TODO -- show game state
        System.out.println(DRAW_REQUEST);
        Scanner scanner = new Scanner(INPUT_SOURCE);
        boolean accept;
        do {
            String input = scanner.nextLine();
            if (input.equals(YES)) {
                accept = true;
                break;
            } else if (input.equals(NO)) {
                accept = false;
                break;
            }
            System.out.println(INVALID_INPUT);
        } while (true);
        scanner.close();
        return accept;
    }

    /**
     * String representation of a human player
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Player ");
        sb.append(this.getName());
        sb.append(" (");
        sb.append(this.getColor());
        sb.append(")");
        return sb.toString();
    }

}
