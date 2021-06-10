/**
 * The details of each tile in the tiles array such as its edibility and its state.
 */
public class MarupekeTile {

    /**
     * Enum with each state the tile can have
     */
    enum State {
        BLANK,
        SOLID,
        X,
        O
    }

    boolean editable;
    State state;

    /**
     * Constructor that initialises the tiles variables
     *
     * @param editable
     * @param state
     */
    public MarupekeTile(boolean editable, State state) {
        this.editable = editable;
        this.state = state;
    }

    /**
     * Getter for the edibility of the tile
     *
     * @return true if the tile is editable, false otherwise
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Setter for the edibility of the tile
     *
     * @param editable true to set the tile as editable, false to set it as non editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Getter for the current state of the tile
     *
     * @return the state of the tile (X, O, SOLID or BLANK)
     */
    public State getState() {
        return state;
    }

    /**
     * Setter for the state of the tile
     *
     * @param state the state to set the tile to (X, O, SOLID or BLANK)
     */
    public void setState(State state) {
        this.state = state;
    }
}