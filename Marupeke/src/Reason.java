/**
 * An abstract class that defines the violations class to get reasons why the grid
 * is illegal
 */
abstract class Reason {

    /**
     * Used to check if there is a violation with the class that calls it (horizontal, vertical or diagonal).
     * Checks with X and with O
     *
     * @return true if there aren't any violations, false otherwise
     */
    public boolean violation() {
        // if both are empty, there is no violation, so there is no violation to report back, the method returns true
        // no violation = empty string = true
        return check(MarupekeTile.State.X).equals("") && check(MarupekeTile.State.O).equals("");
    }

    /**
     * Method to check if theres an illegality of some kind (defined by the class) in the inputted grid.
     *
     * @param state the state for which to check the illegality (X or O)
     * @return a string with the coordinates at which the illegality starts
     */
    public abstract String check(MarupekeTile.State state);

    /**
     * Overrides the toString method to make console readable specifications about why the grid is
     * not legal
     *
     * @return user readable string with the specified violation and the coordinates at which it appears
     */
    @Override
    public abstract String toString();

}