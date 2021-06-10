/**
 * This class checks if there are three Xs or three Os in a grid
 * that are next to each other in a horizontal line.
 *
 * @author Candidate number: 236636
 */
public class HorizontalViolation extends Reason {

    private MarupekeTile[][] tiles;

    /**
     * Constructor that sets the tiles variable
     * @param tiles the tiles array representing the grid
     */
    public HorizontalViolation(MarupekeTile[][] tiles) {
        this.tiles = tiles;
    }

    /**
     * Loops through the grid checking if there are three of the specified state
     * in a horizontal line next to each other in the grid.
     *
     * @param state the state to check the illegality for (X, O, SOLID or BLANK)
     * @return a string with the coordinates at which the horizontal illegality arises
     */
    public String check(MarupekeTile.State state) {
        String s = "";
        int count;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (i + 2 < tiles.length) {
                    count = 0;
                    for (int x = 0; x < 3; x++) {
                        if (tiles[j][i+x].getState() == state) {
                            count++;
                        }
                        if (count == 3) {
                            s += " (" + (i + 1) + "," + (j + 1) + ")";
                            break;
                        }
                    }
                }
            }
        }
        return s;
    }

    @Override
    /**
     * Overrides the toString method to return console readable illegalities specifying the coordinates
     * at which the horizontal illegality arises.
     */
    public String toString() {
        String s = "";

        if (!check(MarupekeTile.State.X).equals("")) {
            s += "\n";
            s += "3 Xs horizontally in a row at:" + check(MarupekeTile.State.X);
        }

        if (!check(MarupekeTile.State.O).equals("")) {
            s += "\n";
            s += "3 Os horizontally in a row at:" + check(MarupekeTile.State.O);
        }

        return s;
    }
}