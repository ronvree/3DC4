package misc;

/**
 * Color indication for each player
 */
public enum Color {

    RED, YELLOW;

    /**
     * Return the other color
     */
    public Color other() {
        switch(this) {
            case RED:
                return YELLOW;
            case YELLOW:
                return RED;
            default:
                return null;
        }
    }

    /**
     * No caps toString
     */
    @Override
    public String toString() {
        switch (this) {
            case RED:
                return "Red";
            case YELLOW:
                return "Yellow";
            default:
                return super.toString();
        }
    }


}
