package client.communication.actions;

import client.communication.Action;

public class Connect extends Action {
    public Connect(String name) {
        super("connect");
        json.put("name", name);
    }

}
