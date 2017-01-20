package client;

import client.communication.Event;
import client.communication.Message;
import client.communication.actions.*;
import misc.*;
import misc.player.computer.barry.Barry;
import misc.player.human.input.MoveInput;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BarryClient {

    private static final String NAME = "Barry";

    private final BufferedReader reader;
    private final PrintWriter out;

    private final Socket socket;

    private BarryClient() throws IOException {
        InetAddress inetAddress = InetAddress.getByName(JOptionPane.showInputDialog("Please select IP address: "));
        int port = Integer.parseInt(JOptionPane.showInputDialog("Please select port number: "));
        socket = new Socket(inetAddress, port);
        reader = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    }

    public static void main(String[] args) {
        try {
            BarryClient client = new BarryClient();
//            client.playGame();
            client.startSession();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    private void playGame() {
//        OnlineGame game = new OnlineGame();
//        game.play();
//    }

    private void startSession() {
        new Session();
    }

    /**
     * Server communication
     */

    private boolean isConnected() {
        return socket.isConnected();
    }

    private Event getServerMessage() {
        Event event = null;
        String input = null;
        try {
            while (input == null && isConnected()) {
                input = reader.readLine();
                event = Event.parseEvent(input);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return event;
    }

    private void sendServerMessage(client.communication.Action action) {
        out.println(action.toJSON());
        out.flush();
    }

    /**
     * Game session
     */

    private class Session {

        private static final int WAITING_PERIOD = 500;

        private Session() {
            /**
             * Connect to server
             */
            Event message = null;
            boolean connected = false;
            while (!connected) {
                BarryClient.wait(WAITING_PERIOD);
                System.out.println("Attempting server connection");
                connect(NAME);
                message = getServerMessage();
                connected = message instanceof Event.PlacedInLobby;
            }
            System.out.println("Connection succeeded!");
            /**
             * Connected
             */
            while (true) {
                /** Joined the lobby */
                if (message instanceof Event.PlacedInLobby) {
                    /** Display free rooms */
                    showLobbies(((Event.PlacedInLobby) message).getFreeLobbies());
                    /** Determine which room to join */
                    int roomNumber = Integer.parseInt(JOptionPane.showInputDialog("Select room number: "));
                    if (roomNumber == -1) { break; }
                    /** Join room */
                    join(roomNumber);
                    System.out.println("Joined room " + roomNumber);
                }
                /** Joined a game */
                if (message instanceof Event.PlacedInGame) {
                    new Game().play();
                }
                message = getServerMessage();
            }
            /**
             * Stopped playing, disconnect
             */
            disconnect();
        }

        private void showLobbies(List<Message.FreeLobby> lobbies) {
            for (Message.FreeLobby lobby : lobbies) {
                System.out.println(lobby.toString());
            }
        }

        private void connect(String name) {
            sendServerMessage(new Connect(name));
        }

        private void join(int gameNumber) {
            sendServerMessage(new JoinGame(gameNumber));
        }

        private void disconnect() {
            sendServerMessage(new Disconnect());
        }

    }

    /**
     * Game logic
     */
    private class Game {

        private final GameState gameState;

        private Game() {
            gameState = new GameState();
        }

        private void play() {
            // Start game
            boolean started = false;
            while (!started) {
                BarryClient.wait(500);
                startGame();
                started = getServerMessage() instanceof Event.GameHasStarted;
            }
            // Perform moves/Read moves
            Event message = getServerMessage();
            boolean first = message instanceof Event.MakeMove;
            Color color = first? Color.RED:Color.YELLOW;
            Barry barry = new Barry(color);

            boolean gameEnded = false;
            boolean playerTurn = first;
            while (!gameEnded) {
                if (message instanceof Event.MakeMove && playerTurn) {
                    ColumnCoordinate move = makeMove(barry);
                    gameState.doMove(color, move.getX(), move.getY());
                    playerTurn = false;
                } else if (message instanceof Event.OpponentMoved && !playerTurn) {
                    ColumnCoordinate move = ((Event.OpponentMoved) message).getMove();
                    gameState.doMove(color.other(), move.getX(), move.getY());
                    playerTurn = true;
                } else if (message instanceof Event.GameOver) {
                    gameEnded = true;
                    System.out.println(((Event.GameOver) message).getWinnerName());
                }
                TUI.prettyPrint(gameState);
                message = getServerMessage();
            }
            // Exit game
            exitGame();
        }

        /**
         * Helper methods
         */

        private void startGame() {
            sendServerMessage(new StartGame());
        }

        private ColumnCoordinate makeMove(Barry barry) {
            MoveInput move = (MoveInput) barry.decide(gameState.deepCopy());
            ColumnCoordinate decision = new ColumnCoordinate(move.getX(), move.getY());
            sendServerMessage(new DoMove(decision));
            return decision;
        }

        private void exitGame() {
            sendServerMessage(new ExitGame());
        }

    }
//
//    private class OnlineGame {
//
//        private final GameState gameState;
//
//        OnlineGame() {
//            this.gameState = new GameState();
//        }
//
//        private void play() {
//            // Join server
//            boolean connected = false;
//            List<Message.FreeLobby> lobbies = null;
//            while (!connected) {
//                BarryClient.wait(500);
//                connect("Barry");
//                Event message = getServerMessage();
//                connected = message instanceof Event.PlacedInLobby;
//                System.out.println(message);
//                if (connected) {
//                    lobbies = ((Event.PlacedInLobby) message).getFreeLobbies();
//                }
//            }
//            System.out.printf("Connected to server");
//            // Join game
//            showLobbies(lobbies);
//            boolean joined = false;
//            while (!joined) {
//                BarryClient.wait(500);
//                int roomNumber = Integer.parseInt(JOptionPane.showInputDialog("Select room number: "));
//                join(roomNumber);
//                joined = getServerMessage() instanceof Event.PlacedInGame;
//            }
//            // Start game
//            boolean started = false;
//            while (!started) {
//                BarryClient.wait(500);
//                startGame();
//                started = getServerMessage() instanceof Event.GameHasStarted;
//            }
//            // Perform moves/Read moves
//            Event message = getServerMessage();
//            boolean first = message instanceof Event.MakeMove;
//            Color color = first? Color.RED:Color.YELLOW;
//            Barry barry = new Barry(color);
//
//            boolean gameEnded = false;
//            boolean playerTurn = first;
//            while (!gameEnded) {
//                if (message instanceof Event.MakeMove && playerTurn) {
//                    ColumnCoordinate move = makeMove(barry);
//                    gameState.doMove(color, move.getX(), move.getY());
//                    playerTurn = false;
//                } else if (message instanceof Event.OpponentMoved && !playerTurn) {
//                    ColumnCoordinate move = ((Event.OpponentMoved) message).getMove();
//                    gameState.doMove(color.other(), move.getX(), move.getY());
//                    playerTurn = true;
//                } else if (message instanceof Event.GameOver) {
//                    gameEnded = true;
//                    System.out.println(((Event.GameOver) message).getWinnerName());
//                }
//                TUI.prettyPrint(gameState);
//                message = getServerMessage();
//            }
//            // Exit game/Restart
//            exitGame();
//            // Disconnect
//            disconnect();
//        }
//
//        private void showLobbies(List<Message.FreeLobby> lobbies) {
//            for (Message.FreeLobby lobby : lobbies) {
//                System.out.println(lobby.toString());
//            }
//        }
//
//        private void connect(String name) {
//            sendServerMessage(new Connect(name));
//        }
//
//        private void join(int gameNumber) {
//            sendServerMessage(new JoinGame(gameNumber));
//        }
//
//        private void startGame() {
//            sendServerMessage(new StartGame());
//        }
//
//        private ColumnCoordinate makeMove(Barry barry) {
//            MoveInput move = (MoveInput) barry.decide(gameState.deepCopy());
//            ColumnCoordinate decision = new ColumnCoordinate(move.getX(), move.getY());
//            sendServerMessage(new DoMove(decision));
//            return decision;
//        }
//
//        private void exitGame() {
//            sendServerMessage(new ExitGame());
//        }
//
//        private void disconnect() {
//            sendServerMessage(new Disconnect());
//        }
//
//    }

}
