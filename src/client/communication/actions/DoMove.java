package client.communication.actions;

import client.communication.Action;
import client.communication.Message;
import misc.ColumnCoordinate;
import misc.Coordinate;

public class DoMove extends Action {
    public DoMove(ColumnCoordinate move) {
        super("move");
        json.put("move", Message.moveToString(move));
    }
}
