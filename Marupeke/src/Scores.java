import java.io.*;
import java.util.ArrayList;

/**
 * The Scores class is used to save by serialization the scores that are made in the game.
 * The scores are objects made in the inner class of this class, naturally called Score.
 */
public class Scores implements Serializable {

    private ArrayList<Score> scores = new ArrayList<>(); // the list of scores that will be serialized
    private static final long serialVersionUID = -7345708816006332655L;
    private static String FILE = "src/listscores.ser"; // the link to the file that will be serialized
    File file = new File("src/listscores.ser");


    /**
     * The constructor that sets the path to the Scores file and loads the scores.
     *
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public Scores() throws ClassNotFoundException, IOException {
        FILE = "src/listscores.ser";
        try {
            loadItemList();
        } catch (FileNotFoundException e) {
            FILE = "listscores.ser";
            loadItemList();
        }
    }

    /**
     * An inner class that creates a new "score".
     * A score is made of a the name of the player, the time taken to solve
     * the grid, the size and difficulty of the grid.
     */
    public static class Score implements Serializable {
        private static final long serialVersionUID = 6529685098267757690L;

        private String name;
        private int time;
        private int size;
        private Difficulty difficulty;

        /**
         * Constructor for a score object, used when the user finishes a grid and
         * wants to save his score.
         *
         * @param name       the name of the player
         * @param time       the time (in seconds) taken for the player to finish the grid
         * @param size       the size of the grid
         * @param difficulty the difficulty of the grid
         */
        public Score(String name, int time, int size, Difficulty difficulty) {
            this.name = name;
            this.time = time;
            this.size = size;
            this.difficulty = difficulty;
        }

        /**
         * Get method for the size of the grid that user wants to save the score of.
         *
         * @return size of current MarupekeGrid
         */
        public int getSize() {
            return size;
        }

        /**
         * Get method for the difficulty of the grid that user wants to save the score of.
         *
         * @return difficulty of current MarupekeGrid
         */
        public Difficulty getDifficulty() {
            return difficulty;
        }

        @Override
        /**
         * Print method to print out a score. Used for testing if the correct variables of the score are saved.
         */
        public String toString() {
            return "Name: " + name + " time: " + time + " size: " + size + " difficulty: " + difficulty;
        }
    }

    /**
     * This method is to add a score to the serialized list of scores.
     * It creates a new Score object and stores in into the arraylist.
     *
     * @param name       the name of the player
     * @param time       the time (in seconds) taken for the player to finish the grid
     * @param size       the size of the grid
     * @param difficulty the difficulty of the grid
     */
    public void addItem(String name, int time, int size, Difficulty difficulty) {
        scores.add(new Score(name, time, size, difficulty));
    }

    /**
     * This method is to retrieve the serialized list of scores and set the
     * current arraylist of scores to the retrieved list.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void loadItemList() throws IOException, ClassNotFoundException {
        InputStream inputStream = new FileInputStream(FILE);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            scores = (ArrayList<Score>) objectInputStream.readObject();
        } catch (EOFException ignored) {

        }
    }

    /**
     * This method is called to save the current list as a serialized list.
     *
     * @throws IOException
     */
    public void saveItemList() throws IOException {
        OutputStream outputStream = new FileOutputStream(FILE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(scores);
    }

    /**
     * This is a get method for the current arraylist of score objects.
     *
     * @return ArrayList of score objects
     */
    public ArrayList<Score> getScoresList() {
        return scores;
    }

}