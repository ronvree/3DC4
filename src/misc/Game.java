package misc;

import misc.player.Player;
import misc.player.human.input.DrawRequest;
import misc.player.human.input.MoveInput;
import misc.player.human.input.PlayerInput;
import misc.player.human.input.Resignation;

/**
 * A game of 3D connect four!
 */
public class Game {

    /**
     * Constants
     */

    /** Number of players */
    public static final int NUMBER_OF_PLAYERS = 2;
    /** Number of pieces of the same color that have to form a straight line in order to win */
    public static final int CONNECT = 4;

    /**
     * Instance variables
     */

    /** The players */
    private final Player[] players;
    /** Object for keeping track of game progress */
    private GameState state;
    /** Index of the player that has to make a move */
    private int currentPlayer;

    /**
     * Constructor
     */

    public Game(Player p1, Player p2)   {
        this.players = new Player[NUMBER_OF_PLAYERS];
        this.players[0] = p1;
        this.players[1] = p2;
        this.state = new GameState();
        this.currentPlayer = 0;
    }

    /**
     * Methods
     */

    /**
     * Play the game!
     *
     * TODO -- return winning player? null for draw?
     */
    public void play() {
        /** Keep taking turns until the game ends */
        /** Note:
         *   - continue statements request new input from the current player
         *   - break statements stop the game */
        while (true) {
            /** Get current player decision based on a copy of the current grid (player cannot directly alter the game's grid instance) */
            Player player = currentPlayer();
            showGrid();
            PlayerInput input = player.decide(state.deepCopy());

            /** The current player decided to move */
            if (input instanceof MoveInput) {
                /** Interpret move */
                final int x = ((MoveInput) input).getX();
                final int y = ((MoveInput) input).getY();
                /** Check if the move is valid. If so, the move is performed */
                if (!this.state.doMove(new Move(player.getColor(), x, y))) {
                    /** The move is invalid, ask the current player for new input */
                    continue;
                }
            /** The current player has resigned. Declare the opponent as winner and break from taking turns */
            } else if (input instanceof Resignation) {
                declareWinner(waitingPlayer());
                break;
            /** The current player has requested a draw. Ask if the opponent agrees and if so declare a draw. If not, ask new input from current player */
            } else if (input instanceof DrawRequest) {
                boolean draw = waitingPlayer().acceptDraw(state.deepCopy());
                if (draw) {
                    /** Opponent accepted. The game ends in a draw */
                    declareDraw();
                    break;
                } else {
                    /** Opponent declined, ask the current player for new input */
                    continue;
                }
            }

            /** Check if the move that has been made resulted in a win for the current player */
            if (state.lastMoveWasWinning()) {
                declareWinner(currentPlayer());
                break;
            }
            /** Check if there are any valid moves left */
            if (state.gridIsFull()) {
                /** No moves left! It's a draw */
                declareDraw();
                break;
            }
            /** No game end this move, it's the next player's turn */
            switchPlayers();
        }
        /** Game ended */
        showGrid();
    }

    /**
     * Declare a winner
     * @param winner -- Winning player
     */
    private void declareWinner(Player winner) {
        // TODO
        System.out.println(String.format("%s won!", winner.toString()));
    }

    /**
     * Declare a draw
     */
    private void declareDraw()  {
        // TODO
        System.out.println("It's a draw!");
    }

    /**
     * Show the grid
     */
    private void showGrid() {
        System.out.println(TUI.prettyPrint(state));
    }

    /**
     * @return the player that has to make a move
     */
    private Player currentPlayer() {
        return this.players[currentPlayer];
    }

    /**
     * @return the player that has to wait
     */
    private Player waitingPlayer()  {
        return this.players[1 - currentPlayer];
    }

    /**
     * Switch the currentPlayer index to the next player
     */
    private void switchPlayers() {
        this.currentPlayer = 1 - this.currentPlayer;
    }


}
