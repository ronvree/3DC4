package client.communication;

import misc.ColumnCoordinate;

public abstract class Message {

    public abstract String toJSON();

    public static String moveToString(ColumnCoordinate move) {
        String s = "";
        switch (move.getX()) {
            case 0:
                s = "a";
                break;
            case 1:
                s = "b";
                break;
            case 2:
                s = "c";
                break;
            case 3:
                s = "d";
        }
        return s + move.getY();
    }

    public static ColumnCoordinate parseMove(String string) {
        char s = string.charAt(0);
        int x;
        switch (s) {
            case 'a':
                x = 0;
                break;
            case 'b':
                x = 1;
                break;
            case 'c':
                x = 2;
                break;
            case 'd':
                x = 3;
                break;
            default:
                x = 0;
        }
        return new ColumnCoordinate(x, Character.getNumericValue(string.charAt(1)));
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
