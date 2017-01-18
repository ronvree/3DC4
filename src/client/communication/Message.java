package client.communication;

import misc.ColumnCoordinate;

public abstract class Message {

    public abstract String toJSON();

    public static String moveToString(ColumnCoordinate move) {
        return null; // TODO
    }

    public static ColumnCoordinate parseMove(String string) {
        return null; // TODO
    }

    public static class FreeLobby {

        private final String opponent;
        private final int roomNumber;

        FreeLobby(String opponent, int roomNumber) {
            this.opponent = opponent;
            this.roomNumber = roomNumber;
        }

        @Override
        public String toString() {
            return String.format("Room %d - %s", roomNumber, opponent);
        }

        public String getOpponent() {
            return opponent;
        }

        public int getRoomNumber() {
            return roomNumber;
        }

    }

}
