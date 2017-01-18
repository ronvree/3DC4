package client.communication;

import misc.ColumnCoordinate;
import misc.Coordinate;
import misc.Move;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public abstract class Event extends Message {

    private final String json;
    private final String message;

    private Event(String json, String message) {
        this.json = json;
        this.message = message;
    }

    @Override
    public String toJSON() {
        return json;
    }

    public String getMessage() {
        return message;
    }

    public static Event parseEvent(String json) throws ParseException {
        JSONParser parser = new JSONParser();
        Object parsed = parser.parse(json);
        if (parsed instanceof JSONObject) {
            // Determine event type
            String type = (String) ((JSONObject) parsed).get("event");
            // Get event message
            String message = (String) ((JSONObject) parsed).get("message");
            // Return proper event
            switch (type) {
                case "lobby":
                    List<FreeLobby> freeLobbies = new ArrayList<>();
                    JSONArray lobbyArray = (JSONArray) ((JSONObject) parsed).get("free lobbies");
                    for (Object o : lobbyArray) {
                        JSONObject lobby = (JSONObject) o;
                        String opponent = (String) lobby.get("opponent");
                        int roomNumber = Integer.parseInt((String) lobby.get("room number"));
                        freeLobbies.add(new FreeLobby(opponent, roomNumber));
                    }
                    return new PlacedInLobby(json, message, freeLobbies);
                case "game":
                    String opponentName1 = (String) ((JSONObject) parsed).get("opponent");
                    return new PlacedInGame(json, message, opponentName1);
                case "started":
                    String opponentName2 = (String) ((JSONObject) parsed).get("opponent");
                    return new GameHasStarted(json, message, opponentName2);
                case "make move":
                    return new MakeMove(json, message);
                case "opponent moved":
                    ColumnCoordinate move = Message.parseMove((String) ((JSONObject) parsed).get("move"));
                    return new OpponentMoved(json, message, move);
                case "game over":
                    String winnerName = (String) ((JSONObject) parsed).get("winner");
                    ColumnCoordinate winningMove = Message.parseMove((String) ((JSONObject) parsed).get("winning move"));
                    return new GameOver(json, message, winnerName, winningMove);
                case "error":
                    String reason = (String) ((JSONObject) parsed).get("reason");
                    return new ServerError(json, message, reason);
                default:
                    System.out.printf("Event type not recognized");
                    return null;
            }
        } else {
            System.out.printf("Event could not be parsed to JSON");
            return null;
        }
    }

    /**
     * Event Types
     */

    public static class PlacedInLobby extends Event {

        private final List<FreeLobby> freeLobbies;

        private PlacedInLobby(String json, String message, List<FreeLobby> freeLobbies) {
            super(json, message);
            this.freeLobbies = freeLobbies;
        }

        public List<FreeLobby> getFreeLobbies() {
            return freeLobbies;
        }

    }

    public static class PlacedInGame extends Event {

        private final String opponentName;

        private PlacedInGame(String json, String message, String opponentName) {
            super(json, message);
            this.opponentName = opponentName;
        }

        public String getOpponentName() {
            return opponentName;
        }

    }

    public static class GameHasStarted extends Event {

        private final String opponentName;

        private GameHasStarted(String json, String message, String opponentName) {
            super(json, message);
            this.opponentName = opponentName;
        }

        public String getOpponentName() {
            return opponentName;
        }

    }

    public static class MakeMove extends Event {

        private MakeMove(String json, String message) {
            super(json, message);
        }

    }

    public static class OpponentMoved extends Event {

        private final ColumnCoordinate move;

        private OpponentMoved(String json, String message, ColumnCoordinate move) {
            super(json, message);
            this.move = move;
        }

        public ColumnCoordinate getMove() {
            return move;
        }

    }

    public static class GameOver extends Event {

        private final String winnerName;
        private final ColumnCoordinate winningMove;

        private GameOver(String json, String message, String winnerName, ColumnCoordinate winningMove) {
            super(json, message);
            this.winnerName = winnerName;
            this.winningMove = winningMove;
        }

        public String getWinnerName() {
            return winnerName;
        }

        public ColumnCoordinate getWinningMove() {
            return winningMove;
        }
    }

    public static class ServerError extends Event {

        private final String reason;

        private ServerError(String json, String message, String reason) {
            super(json, message);
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }

    }


}
