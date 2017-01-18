package client.communication.actions;

import client.communication.Action;

public class JoinGame extends Action {
    public JoinGame(int roomNumber) {
        super("join");
        json.put("room number", roomNumber);
    }
}
