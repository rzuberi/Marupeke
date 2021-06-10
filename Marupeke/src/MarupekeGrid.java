import java.util.*;

/**
 * An enum class for the different difficulties a grid can have.
 */
enum Difficulty {
    BLANK,
    EASY,
    MEDIUM,
    HARD
}

/**
 * This class creates a Marupeke grid with squares that can be filled with
 * Xs, Os and #s. This is meant to be used to play the Marupeke game.
 * (Parts of the part 1 solution were implemented.)
 *
 * @author Candidate number: 236636, ianw@sussex.ac.uk, bernhard@sussex.ac.uk
 */
public class MarupekeGrid {

    private static int size;
    private Difficulty difficulty;
    private MarupekeTile[][] tiles;

    public MarupekeGrid() {
    }


    /**
     * The constructor initialises the tiles array that contains all information
     * about each tile as all editable blank tiles. The inputted size is adjusted
     * to be between 4 and 10 if the inputted size isn't in these boundaries.
     *
     * @param size size of the grid
     */
    public MarupekeGrid(int size) {
        if (size < 4) {
            size = 4;
        } else if (size > 10) {
            size = 10;
        }
        this.size = size;

        tiles = new MarupekeTile[this.size][this.size];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                tiles[i][j] = new MarupekeTile(true, MarupekeTile.State.BLANK);
            }
        }
    }

    /**
     * A getter method for the difficulty of the MarupekeGrid object.
     *
     * @return the current difficulty of the grid.
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Getter method for the size of the grid
     *
     * @return the size of the grid
     */
    public int getSize() {
        return size;
    }

    /**
     * Getter method for the tiles array of the grid
     *
     * @param
     */
    public MarupekeTile[][] getTiles() {
        return tiles;
    }

    /**
     * Returns the state of the tile at the given coordinates
     *
     * @param x column of the tile
     * @param y row of the tile
     * @return the state of the tile
     */
    public MarupekeTile.State getState(int x, int y) {
        return tiles[y][x].getState();
    }

    /**
     * Returns if the tile can be edited or not
     *
     * @param x column of the tile
     * @param y row of the tile
     * @return true if the tile is editable, false otherwise
     */
    public boolean getEditable(int x, int y) {
        return tiles[y][x].isEditable();
    }

    /**
     * Sets the tile as # (solid) and uneditable
     *
     * @param x column of the tile
     * @param y row of the tile
     * @return true if able to edit and set as solid, false otherwise
     */
    public boolean setSolid(int x, int y) {
        return setGrid(x, y, false, MarupekeTile.State.SOLID);
    }

    /**
     * Set the tile as X and uneditable
     *
     * @param x column of the tile
     * @param y row of the tile
     * @return true if able to edit and set as X, false otherwise
     */
    public boolean setX(int x, int y) {
        return setGrid(x, y, false, MarupekeTile.State.X);
    }

    /**
     * Set the tile as O and uneditable
     *
     * @param x column of the tile
     * @param y row of the tile
     * @return true if able to edit and set as O, false otherwise
     */
    public boolean setO(int x, int y) {
        return setGrid(x, y, false, MarupekeTile.State.O);
    }

    /**
     * Set the tile as X and editable
     *
     * @param x column of the tile
     * @param y row of the tile
     */
    public void markX(int x, int y) {
        setGrid(x, y, true, MarupekeTile.State.X);
    }

    /**
     * Set the tile as O and editable
     *
     * @param x column of the tile
     * @param y row of the tile
     */
    public void markO(int x, int y) {
        setGrid(x, y, true, MarupekeTile.State.O);
    }

    /**
     * Remove the X or O (whatever is on the tile) if the tile is editable.
     * The tile stays editable.
     *
     * @param x column of the tile
     * @param y row of the tile
     * @return true if able to edit and change to blank, false otherwise
     */
    public boolean unmark(int x, int y) {
        return setGrid(x, y, true, MarupekeTile.State.BLANK);
    }

    /**
     * Check that the grid is full and that there are no other illegalities.
     * If yes to both then the puzzle is complete.
     *
     * @return true if the grid is full and there are no illegalities.
     */
    public boolean isPuzzleComplete() {
        boolean flag = true;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j].getState() == MarupekeTile.State.BLANK) {
                    flag = false;
                }
            }
        }
        return flag && isLegalGrid();
    }

    /**
     * Sets a tile at the given coordinates as the given state and can be chosen
     * if it will still be editable after that.
     * Note that this method gets called by set and mark methods. Set methods (setX
     * for example) are only called by the system as when it sets it they can't be
     * edited anymore. Mark methods (markO for example) are only called by the user
     * as he can unmark them or change what he wants to mark.
     *
     * @param x       row of the tile
     * @param y       column of the tile
     * @param canEdit if the tile will still be editable after
     * @param s       the state to set the tile as (X, O, SOLID or BLANK)
     * @return true if the tile is editable and got edited, false otherwise
     */
    public boolean setGrid(int x, int y, boolean canEdit, MarupekeTile.State s) {
        if (!(tiles[y][x].isEditable())) {
            return false;
        }

        tiles[y][x].setState(s);
        tiles[y][x].setEditable(canEdit);
        return true;
    }

    /**
     * Returns the number of times the inputted character appears in the grid.
     *
     * @param s the state of the Marupeke tile
     * @return the number of times the character appears in the grid
     */
    public int count(MarupekeTile.State s) {
        int count = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                if (getState(i, j) == s) {
                    ++count;
                }
            }
        }
        return count;
    }

    public boolean isLegalGrid() {
        VerticalViolation vv = new VerticalViolation(tiles);
        HorizontalViolation hv = new HorizontalViolation(tiles);
        DiagonalViolation dv = new DiagonalViolation(tiles);

        // if they all return true, then there are no illegalities so the grid is legal
        return vv.violation() && hv.violation() && dv.violation();
    }

    /**
     * If there are illegalities in the grid, it will add it to the list
     * and return the list
     *
     * @return the list of reasons the grid is illegal, if there are none it returns null
     */
    public List<Reason> illegalitiesInGrid() {
        if (!isLegalGrid()) {
            List<Reason> reasons = new ArrayList();
            VerticalViolation vv = new VerticalViolation(tiles);
            if (!vv.violation()) {
                reasons.add(vv);
            }
            HorizontalViolation hv = new HorizontalViolation(tiles);
            if (!hv.violation()) {
                reasons.add(hv);
            }
            DiagonalViolation dv = new DiagonalViolation(tiles);
            if (!dv.violation()) {
                reasons.add(dv);
            }
            return reasons;
        }
        return null;
    }

    /**
     * Sets the editable state of every tile to uneditable.
     */
    public void setPuzzleUneditable() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                tiles[i][j].setEditable(false);
            }
        }
    }

    /**
     * Makes every editable tile blank.
     */
    public void clear() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                // still checks if the tile is editable before unmarking it
                if (tiles[i][j].isEditable()) unmark(i, j);
            }
        }
    }

    /**
     * Overrides the toString method to return a console readable grid
     * from the tiles array.
     *
     * @returns multi-line string representing a grid
     */
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                switch (tiles[i][j].getState()) {
                    case BLANK:
                        s += "_";
                        break;
                    case SOLID:
                        s += "#";
                        break;
                    case X:
                        s += "X";
                        break;
                    case O:
                        s += "O";
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + tiles[i][j].getState());
                }

            }
            s += "\n";
        }
        return s;
    }

    /**
     * Checks if a grid is solvable by calling the DFS solve method and returning true
     * if it was able to complete the grid and false if it wasn't. Any grid above size 7 will
     * return true because we don't bother to solve large grids as they take too long for the
     * algorithm.
     *
     * @param grid the grid to be solved
     * @return true if the clone of the grid was successfully solved
     */
    public static boolean isSolvable(MarupekeGrid grid) {
        if (size > 7) return true;
        MarupekeGrid mg = grid.clone();
        mg.solvePuzzle(0, 0);
        return mg.isPuzzleComplete();
    }

    /**
     * A DFS algorithm that solves a MarupekeGrid. Also referred to as a 'backtracking' algorithm.
     *
     * @param row the row at which to start the solving
     * @param col the column at which to start the solving
     * @return true if it completes the puzzle or if marking a state at a particular position is legal
     */
    public boolean solvePuzzle(int row, int col) {
        // solving grids that have more than 49 squares with the algorithm is very long so w limit it
        if (row == getSize()) {
            ++col;
            row = 0;
        }

        if (isPuzzleComplete()) return true;

        if (isSafe(row, col, MarupekeTile.State.X)) {
            markX(row, col);
            if (solvePuzzle(row + 1, col)) return true;
            unmark(row, col);
        }
        if (isSafe(row, col, MarupekeTile.State.O)) {
            markO(row, col);
            if (solvePuzzle(row + 1, col)) return true;
            unmark(row, col);
        }

        return false;
    }

    /**
     * Clones the current grid by making a new MarupekeGrid object of the same size and copying
     * all the sates of the the original grid into the cloned grid.
     *
     * @return a clone of the current MarupekeGrid
     */
    public MarupekeGrid clone() {
        MarupekeGrid cloned = new MarupekeGrid(size);
        MarupekeTile[][] clonedTiles = cloned.getTiles();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                clonedTiles[i][j].setState(tiles[i][j].getState());
                if (clonedTiles[i][j].getState() != MarupekeTile.State.BLANK) {
                    clonedTiles[i][j].setEditable(false);
                }
            }
        }

        return cloned;
    }

    public boolean isSafe(int row, int column, MarupekeTile.State s) {
        if (s == MarupekeTile.State.X) {
            markX(row, column);
        } else if (s == MarupekeTile.State.O) {
            markO(row, column);
        }

        if (isLegalGrid()) {
            unmark(row, column);
            return true;
        } else {
            unmark(row, column);
            return false;
        }
    }

    /**
     * Generates a random puzzle of the indicated size. The initial number
     * of tiles that are set and to what state is randomly determined.
     *
     * @param size the size of the grid
     * @return the grid with randomly filled tiles
     */
    public static MarupekeGrid randomPuzzle(int size, int numFill, int numX, int numO) {
        MarupekeGrid mp;

        if (size < 4) {
            size = 4;
        } else if (size > 10) {
            size = 10;
        }

        Random rand = new Random();

        if ((numFill + numX + numO) > (size * size) / 2) throw new TooManyMarkedSquares("Too many marked squares!");

        do {
            mp = new MarupekeGrid(size);
            int countSolid = 0;
            while (countSolid < numFill) {
                if (mp.setSolid(rand.nextInt(size), rand.nextInt(size))) {
                    countSolid++;
                }
            }
            int countX = 0;
            while (countX < numX) {
                if (mp.setX(rand.nextInt(size), rand.nextInt(size))) {
                    countX++;
                }
            }
            int countO = 0;
            while (countO < numO) {
                if (mp.setO(rand.nextInt(size), rand.nextInt(size))) {
                    countO++;
                }
            }
        } while (!(mp.isLegalGrid()));

        return mp;
    }

    /**
     * Builds a new MarupekeGrid with the specified size and difficulty. The size needs to be
     * between 4 and 10 otherwise an exception is thrown. The difficulty will define by how much
     * the grid will be filled. Easy will fill half with random Xs, Os and Solids. Medium will fill
     * a third with random Xs, Os and Solids. Hard will fill a third with random Solids.
     *
     * @param gridSize the size of the grid to be built
     * @param difficulty the difficulty of the grid to be built
     * @return a MarupekeGrid object with a grid of the specified size and difficulty
     * @throws TooManyMarkedSquares
     * @throws IllegalGridSize
     */
    public static MarupekeGrid buildGameGrid(int gridSize, Difficulty difficulty) throws TooManyMarkedSquares, IllegalGridSize {
        MarupekeGrid grid;
        if (gridSize < 4 || gridSize > 10) throw new IllegalGridSize();
        size = gridSize;
        do {
            int nuFi = 0, nuX = 0, nuO = 0;
            Random rand = new Random();
            int totalCount = 0;

            if (difficulty == Difficulty.EASY) {
                while (totalCount != size * size / 2) {
                    nuFi = rand.nextInt(size * size / 2);
                    nuX = rand.nextInt(size * size / 2);
                    nuO = rand.nextInt(size * size / 2);
                    totalCount = nuFi + nuX + nuO;
                }
            } else if (difficulty == Difficulty.MEDIUM) {
                while (totalCount != size * size / 3) {
                    nuFi = rand.nextInt(size * size / 2);
                    nuX = rand.nextInt(size * size / 2);
                    nuO = rand.nextInt(size * size / 2);
                    totalCount = nuFi + nuX + nuO;
                }
            } else if (difficulty == Difficulty.HARD) {
                while (nuFi != size * size / 3) {
                    nuFi = rand.nextInt(size * size);
                }
            }
            grid = randomPuzzle(size, nuFi, nuX, nuO);
        } while (!grid.isLegalGrid() || !isSolvable(grid));
        grid.setDifficulty(difficulty);

        return grid;
    }

    /**
     * A setter method for the difficulty of the grid.
     *
     * @param difficulty the difficulty of the grid
     */
    private void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * An exception class for when there are too many marked squares in the grid.
     * It extends RuntimeException so that it can throw an unchecked exception.
     * There are "too many marked squares" if the total number of marked tiles is
     * more than half the squared size of the grid.
     */
    public static class TooManyMarkedSquares extends RuntimeException {
        /**
         * The exception constructor with the string message as input
         */
        public TooManyMarkedSquares(String s) {
            super(s);
        }
    }

}