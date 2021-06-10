import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

/**
 * The MarupekeLayout class is the main class of the program as it is the one that is launched
 * to start playing the game. It includes a main menu with different functionalities inside it, the main one
 * being the ability to play the Marupeke game.
 *
 * @author Candidate number: 236636, 30/04/21
 */
public class MarupekeLayout extends Application implements Serializable {
    private Stage primaryStage;
    private Scene scene;
    private BorderPane pane;
    private static StackPane[][] emptySquares; // this is where the buttons that represent the tiles will be
    private Button[][] buttons; // buttons array for the grid to be displayed
    private static ComboBox optionsSize; // to choose the size of the grid
    private static ComboBox optionsDifficulty; // to choose the difficulty of the grid
    private final RadioButton[] difficultyList = new RadioButton[3]; // for when the radio buttons of difficulty are images
    private final RadioButton easy = new RadioButton(); // choosing easy as a difficulty
    private final RadioButton medium = new RadioButton(); // choosing medium as a difficulty
    private final RadioButton hard = new RadioButton(); // choosing hard as a difficulty
    private MarupekeGrid mg;
    private ToggleGroup tg;
    private int sizeOfSquare; // the size of the an individual tile changes depending on what size of the grid is chosen
    private RadioButton selected = null;
    private boolean hover; // if hovering animations are to be played or not, when the grid is finished, they shouldn't be
    private boolean finished; // if the grid is finished in order to allow the subsequent animations that follow and deactivate some functions such as editing the grid
    private Text illegalitiesText; // the text that will hold the user-readable current illegalities in the text
    private AnimationTimer timer; // the timer in seconds that will show how long the user has been taking to solve a grid
    private final Text display = new Text("0");  // the initial timer
    private final File saveGrids = new File("savedgrids/MarupekeGrids.txt"); // path to the saved grids text file
    private RadioButton[] rbList; // for when the radio buttons are images
    private String currentTheme = "blue"; // the initial theme of the game is the blue theme
    private AudioClip markLegalSound, markIllegalSound, winSound, clickSound; // the sound effects that will be played
    private final Scores scores = new Scores(); // a scores object to hold the serialized scores in
    private static int defaultSize; // the defaultsize is specified by the argument passed when launching the program
    // all of the imageViews for the images that will be used throughout the program
    private ImageView logoView,
            candnoView,
            size4uView,
            size4View,
            size5uView,
            size5View,
            size6uView,
            size6View,
            size7uView,
            size7View,
            size8uView,
            size8View,
            size9uView,
            size9View,
            size10uView,
            size10View,
            easyuView,
            easyView,
            mediumuView,
            mediumView,
            harduView,
            hardView,
            eraserToolView,
            xToolView,
            oToolView,
            miniBlackSquareView;
    private MediaPlayer backgroundMusic;

    // imageView is a node and a node can appear only once at a time, so we have to declare them as images
    // and later one individually make the imageViews
    private Image xSquareImage,
            xSquareBlueImage,
            xSquareRedImage,
            xSquareGreyImage,
            xSquareGreenImage,
            oSquareImage,
            oSquareBlueImage,
            oSquareRedImage,
            oSquareGreyImage,
            oSquareGreenImage,
            solidSquareImage,
            emptySquareImage,
            eraserToolImage,
            xToolImage,
            oToolImage,
            miniBlackSquareImage;

    /**
     * An empty constructor that is needed for the exceptions this class may throw.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public MarupekeLayout() throws IOException, ClassNotFoundException {
    }

    /**
     * The main method that receives the arguments and passes them trough.
     *
     * @param args
     * @throws IllegalGridSize
     */
    public static void main(String[] args) throws IllegalGridSize {
        // call to static method launch from Application
        int arg; // the passed argument
        if (args.length > 0) { // check that an argument was passed
            try {
                arg = Integer.parseInt(args[0]); // parse argument as an integer
                if (arg < 4 || arg > 10) { // check that the argument is an integer between 4 and 10
                    throw new IllegalGridSize(); // throw exception that terminates the program
                } else {
                    defaultSize = arg; // our defaultSize for the grid is set!
                    launch();
                }
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer."); // to indicate in the console what went wrong
                System.exit(1);
            }
        } else {
            System.err.println("Needs an integer argument between 4 and 10."); // to indicate in the console what went wrong
            System.exit(1);
        }
    }

    /**
     * The start method that initialises the stage and the scene to be displayed as the program.
     *
     * @param primaryStage the stage of the program.
     * @throws IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        // create pane that will have the contents (logo, menu etc.) and set the style
        pane = new BorderPane();
        pane.setPadding(new Insets(10, 60, 150, 60));
        pane.setStyle("-fx-background-color: DAE6F3;");

        // create the images
        createImages();

        // set background music (if a method is called twice, the music plays twice, so better place it in a method that
        // will only get called once, such as the start)
        backgroundMusic = new MediaPlayer(new Media(new File("sounds/backgroundmusic.mp3").toURI().toString()));
        backgroundMusic.setVolume(0.2);
        backgroundMusic.setOnEndOfMedia(() -> backgroundMusic.seek(Duration.ZERO));
        backgroundMusic.play();

        // add the menu
        createMenu();

        // put difficulties in array
        difficultyList[0] = easy;
        difficultyList[1] = medium;
        difficultyList[2] = hard;

        // set the timer
        // taken from here: https://stackoverflow.com/questions/40821849/creating-simple-stopwatch-javafx
        timer = new AnimationTimer() {
            private long timestamp;
            public long time = 0;
            private long fraction = 0;

            @Override
            public void start() {
                // current time adjusted by remaining time from last run
                time = 0;
                timestamp = System.currentTimeMillis() - fraction;
                super.start();
            }

            @Override
            public void stop() {
                super.stop();
                // save leftover time not handled with the last update
                fraction = System.currentTimeMillis() - timestamp;
            }

            @Override
            public void handle(long now) {
                long newTime = System.currentTimeMillis();
                if (timestamp + 1000 <= newTime) {
                    long deltaT = (newTime - timestamp) / 1000;
                    time += deltaT;
                    timestamp += 1000 * deltaT;
                    // to display the time will it's ticking
                    display.setText(Long.toString(time));
                    // here we set its font size and color according to the theme
                    display.setFont(Font.font("Verdana", 15));
                    if (currentTheme.equals("black")) display.setFill(Color.WHITE);
                    else if (currentTheme.equals("white") || currentTheme.equals("blue")) display.setFill(Color.BLACK);
                }
            }
        };

        // create and show the scene
        scene = new Scene(pane);
        primaryStage.setTitle("MARUPEKE"); // title of window
        primaryStage.setResizable(false); // some of the elements don't move in accordance with the resizing so better not to allow it
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates the menu, its buttons, its images and everything that appears on the main menu of the program.
     * The main menu is the first scene that appears when the program is launched.
     *
     * @throws FileNotFoundException
     */
    private void createMenu() throws FileNotFoundException {
        pane.setRight(null); // reset these parts of the pane as they are modified in certain functionalities of the program
        pane.setBottom(null);

        // add the logo
        logoView = new ImageView(new Image(new FileInputStream("images/MarupekeLogo.png")));
        TranslateTransition translateTransitionLogo = new TranslateTransition(Duration.millis(1750), logoView);
        translateTransitionLogo.setFromX(-1000); // out of frame
        translateTransitionLogo.setToX(0); // center of frame
        translateTransitionLogo.play();

        // add the creator name
        candnoView = new ImageView(new Image(new FileInputStream("images/candno.png")));
        TranslateTransition translateTransitionCand = new TranslateTransition(Duration.millis(1750), candnoView);
        translateTransitionCand.setFromX(850);
        translateTransitionCand.setToX(175);
        candnoView.setFitWidth(175);
        candnoView.setFitHeight(20);

        VBox topBox = new VBox(logoView, candnoView);
        topBox.setSpacing(0);
        pane.setTop(topBox);
        translateTransitionLogo.play();
        translateTransitionCand.play();

        Button newGame = new Button("New Game");
        Tooltip tooltip1 = new Tooltip("Play a game of Marupeke.");
        hackTooltipStartTiming(tooltip1);
        newGame.setTooltip(tooltip1);

        Button solving = new Button("Solving");
        solving.setTooltip(new Tooltip("Test out the solving algorithm on random grids.\nYou can specify the size and the number of grids."));

        Button scores = new Button("High Scores");
        scores.setTooltip(new Tooltip("Check out the scores (time taken) of people who played this game!"));

        newGame.setMaxWidth(100); // making all the buttons the same size
        solving.setMaxWidth(100);
        scores.setMaxWidth(100);

        newGame.setOnAction(event -> {
            clickSound.play(); // a sound to tell the player "hey you've clicked it"
            newGame();
        });

        solving.setOnAction(event -> {
            clickSound.play();
            newSolving();
        });

        scores.setOnAction(event -> {
            clickSound.play();
            highScores();
        });

        Button blue = new Button("Blue");
        blue.setMaxWidth(100);
        blue.setTooltip(new Tooltip("Change the background color to blue.")); // a Tooltip lets the user understand better what a button will do by hovering over it
        blue.setOnMouseClicked(e -> changeTheme("blue"));

        Button black = new Button("Black");
        black.setMaxWidth(100);
        black.setTooltip(new Tooltip("Change the background color to black."));
        black.setOnMouseClicked(e -> changeTheme("black"));

        Button white = new Button("White");
        white.setMaxWidth(100);
        white.setTooltip(new Tooltip("Change the background color to white."));
        white.setOnMouseClicked(e -> changeTheme("white"));

        HBox themePicker = new HBox(blue, black, white);
        themePicker.setSpacing(10);
        themePicker.setAlignment(Pos.BOTTOM_CENTER);

        // import your own background for image
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                // setting the file extensions allowed since we only want images
                new FileChooser.ExtensionFilter("Image", "*.png"),
                new FileChooser.ExtensionFilter("Image", "*.jpg"),
                new FileChooser.ExtensionFilter("Image", "*.jpeg")
        );
        Button importButton = new Button("Import image");
        importButton.setMaxWidth(100);
        importButton.setTooltip(new Tooltip("Import your own image from your computer to be the new background."));
        importButton.setOnMouseClicked(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    String imgURL = selectedFile.toURI().toURL().toExternalForm();
                    pane.setStyle("-fx-background-image: url('" + imgURL + "'); " +
                            "-fx-background-position: center center; " +
                            "-fx-background-repeat: stretch;");
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                }
            }
        });

        // sound effects
        markLegalSound = new AudioClip(new File("sounds/markLegal.mp3").toURI().toString());
        markLegalSound.setVolume(0.5); // setting the initial volume to half
        markIllegalSound = new AudioClip(new File("sounds/markIllegal.mp3").toURI().toString());
        markIllegalSound.setVolume(0.5);
        winSound = new AudioClip(new File("sounds/winSound.mp3").toURI().toString());
        winSound.setVolume(0.5);
        clickSound = new AudioClip(new File("sounds/clickSound.mp3").toURI().toString());
        clickSound.setVolume(0.5);

        // slider for sound effects
        Slider volumeEffectsSlider = new Slider();
        volumeEffectsSlider.setMaxWidth(175);
        volumeEffectsSlider.setValue(markLegalSound.getVolume() * 100);
        volumeEffectsSlider.valueProperty().addListener(observable -> {
            markLegalSound.setVolume(volumeEffectsSlider.getValue() / 100);
            markIllegalSound.setVolume(volumeEffectsSlider.getValue() / 100);
            winSound.setVolume(volumeEffectsSlider.getValue() / 100);
            clickSound.setVolume(volumeEffectsSlider.getValue() / 100);
        });

        Text textVolumeEffects = new Text("Effects volume:");
        textVolumeEffects.setFont(Font.font("Verdana", 15));
        textVolumeEffects.setFill(Color.ORANGE);

        HBox volumeEffects = new HBox(textVolumeEffects, volumeEffectsSlider);
        volumeEffects.setSpacing(10);

        // slider for background music
        Slider volumeMusicSlider = new Slider();
        volumeMusicSlider.setMaxWidth(175);
        volumeMusicSlider.setValue(backgroundMusic.getVolume() * 100);
        volumeMusicSlider.valueProperty().addListener(observable -> backgroundMusic.setVolume(volumeMusicSlider.getValue() / 100));

        Text textVolumeMusic = new Text("Music volume:");
        textVolumeMusic.setFont(Font.font("Verdana", 15));
        textVolumeMusic.setFill(Color.ORANGE);

        HBox volumeMusic = new HBox(textVolumeMusic, volumeMusicSlider);
        volumeMusic.setSpacing(18);
        VBox volumeBox = new VBox(volumeEffects, volumeMusic);

        // set center with all the components we declared above into one vertical box
        VBox vbox = new VBox(newGame, solving, scores, importButton, themePicker, volumeBox);
        vbox.setSpacing(8);
        vbox.setAlignment(Pos.BOTTOM_CENTER);
        pane.setCenter(vbox);
        BorderPane.setMargin(vbox, new Insets(55, 0, 0, 0));

        // button to exit game
        Button closeGame = new Button("Close game");
        closeGame.setMaxWidth(90);
        closeGame.setTooltip(new Tooltip("Terminate the program."));
        closeGame.setOnMouseClicked(e -> {
            System.exit(0);
        });
        pane.setBottom(closeGame);
        BorderPane.setMargin(closeGame, new Insets(35, 0, 0, 0));
    }

    /**
     * Changes the theme of the program. The theme can be blue, black or white and passed a string. The background
     * color will change to the specified theme's color.This will changed a private variable in the scope of this
     * class called currentTheme and every text will be set with a color according to the currentTheme.
     *
     * @param themeName a string that is the name of the theme we want to set
     */
    private void changeTheme(String themeName) {
        switch (themeName) {
            case "blue":
                currentTheme = "blue";
                pane.setStyle("-fx-background-color: DAE6F3;"); // sets the background color to the specified color
                break;
            case "black":
                currentTheme = "black";
                pane.setStyle("-fx-background-color: black;");
                break;
            case "white":
                currentTheme = "white";
                pane.setStyle("-fx-background-color: white;");
                break;
        }
    }

    /**
     * A method used to display a Tooltip object and change when it starts displaying when we hover over a button.
     * A Tooltip is a text that appears when we hover over a button for 100 milliseconds. The text dissapears when
     * we stop hovering over the button. The text is usually a description for the user of the action the button
     * will do.
     * <p>
     * This method was not built by me, it was found here: https://stackoverflow.com/questions/26854301/how-to-control-the-javafx-tooltips-delay
     *
     * @param tooltip the tooltip we want to set the timing for.
     */
    public static void hackTooltipStartTiming(Tooltip tooltip) {
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(100)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method that loads the images and the imageViews with their corresponding images from the images folder in this
     * project.
     *
     * @throws FileNotFoundException
     */
    private void createImages() throws FileNotFoundException {
        xSquareImage = new Image(new FileInputStream("images/xSquare.png")); // retrieves the image from the images folder
        xSquareBlueImage = new Image(new FileInputStream("images/xSquareBlue.png"));
        xSquareRedImage = new Image(new FileInputStream("images/xSquareRed.png"));
        xSquareGreyImage = new Image(new FileInputStream("images/xSquareGrey.jpeg"));
        xSquareGreenImage = new Image(new FileInputStream("images/xSquareGreen.png"));
        oSquareImage = new Image(new FileInputStream("images/oSquare.jpeg"));
        oSquareBlueImage = new Image(new FileInputStream("images/oSquareBlue.png"));
        oSquareRedImage = new Image(new FileInputStream("images/oSquareRed.png"));
        oSquareGreyImage = new Image(new FileInputStream("images/oSquareGrey.jpeg"));
        oSquareGreenImage = new Image(new FileInputStream("images/oSquareGreen.png"));
        solidSquareImage = new Image(new FileInputStream("images/blacksquare.png"));
        emptySquareImage = new Image(new FileInputStream("images/whitesquare.png"));
        eraserToolImage = new Image(new FileInputStream("images/eraser.png"));

        xToolImage = new Image(new FileInputStream("images/xSquareTransparent.png"));
        oToolImage = new Image(new FileInputStream("images/oSquareTransparent.png"));
        miniBlackSquareImage = new Image(new FileInputStream("images/miniblacksquare.png"));

        // imageViews for solving menu
        size4uView = new ImageView(new Image(new FileInputStream("images/4u.png")));
        size4uView.setFitWidth(30); // setting the sizes as these images are sometimes very big or very small
        size4uView.setFitHeight(30);

        size4View = new ImageView(new Image(new FileInputStream("images/4.png")));
        size4View.setFitWidth(30);
        size4View.setFitHeight(30);

        size5uView = new ImageView(new Image(new FileInputStream("images/5u.png")));
        size5uView.setFitWidth(30);
        size5uView.setFitHeight(30);

        size5View = new ImageView(new Image(new FileInputStream("images/5.png")));
        size5View.setFitWidth(30);
        size5View.setFitHeight(30);

        size6uView = new ImageView(new Image(new FileInputStream("images/6u.png")));
        size6uView.setFitWidth(30);
        size6uView.setFitHeight(30);

        size6View = new ImageView(new Image(new FileInputStream("images/6.png")));
        size6View.setFitWidth(30);
        size6View.setFitHeight(30);

        size7uView = new ImageView(new Image(new FileInputStream("images/7u.png")));
        size7uView.setFitWidth(30);
        size7uView.setFitHeight(30);

        size7View = new ImageView(new Image(new FileInputStream("images/7.png")));
        size7View.setFitWidth(30);
        size7View.setFitHeight(30);

        size8uView = new ImageView(new Image(new FileInputStream("images/8u.png")));
        size8uView.setFitWidth(30);
        size8uView.setFitHeight(30);

        size8View = new ImageView(new Image(new FileInputStream("images/8.png")));
        size8View.setFitWidth(30);
        size8View.setFitHeight(30);

        size9uView = new ImageView(new Image(new FileInputStream("images/9u.png")));
        size9uView.setFitWidth(30);
        size9uView.setFitHeight(30);

        size9View = new ImageView(new Image(new FileInputStream("images/9.png")));
        size9View.setFitWidth(30);
        size9View.setFitHeight(30);

        size10uView = new ImageView(new Image(new FileInputStream("images/10u.png")));
        size10uView.setFitWidth(30);
        size10uView.setFitHeight(30);

        size10View = new ImageView(new Image(new FileInputStream("images/10.png")));
        size10View.setFitWidth(30);
        size10View.setFitHeight(30);

        easyuView = new ImageView(new Image(new FileInputStream("images/easyu.png")));
        easyuView.setFitWidth(70);
        easyuView.setFitHeight(30);

        easyView = new ImageView(new Image(new FileInputStream("images/easy.png")));
        easyView.setFitWidth(70);
        easyView.setFitHeight(30);

        mediumuView = new ImageView(new Image(new FileInputStream("images/mediumu.png")));
        mediumuView.setFitWidth(70);
        mediumuView.setFitHeight(30);

        mediumView = new ImageView(new Image(new FileInputStream("images/medium.png")));
        mediumView.setFitWidth(70);
        mediumView.setFitHeight(30);

        harduView = new ImageView(new Image(new FileInputStream("images/hardu.png")));
        harduView.setFitWidth(70);
        harduView.setFitHeight(30);

        hardView = new ImageView(new Image(new FileInputStream("images/hard.png")));
        hardView.setFitWidth(70);
        hardView.setFitHeight(30);

        eraserToolView = new ImageView(eraserToolImage);
        eraserToolView.setFitWidth(25);
        eraserToolView.setFitHeight(25);

        xToolView = new ImageView(xToolImage);
        xToolView.setFitWidth(25);
        xToolView.setFitHeight(25);

        oToolView = new ImageView(oToolImage);
        oToolView.setFitWidth(25);
        oToolView.setFitHeight(25);

        miniBlackSquareView = new ImageView(miniBlackSquareImage);
        miniBlackSquareView.setFitWidth(25);
        miniBlackSquareView.setFitHeight(25);
    }

    /**
     * This method changes the scene to the scores scene. There will be displayed the serialized scores in a table.
     * A "high" score is a low time taken to solve a grid.
     */
    private void highScores() {
        ArrayList<Scores.Score> scoresList = scores.getScoresList(); // get the serialized list of scores

        TableView tableView = new TableView(); // table that will display the specified scores
        tableView.minHeight(100);
        tableView.maxHeight(100);

        tableView.translateYProperty();
        tableView.setPlaceholder(new Label()); // set it as empty so no text is shown
        tableView.setFixedCellSize(25);
        tableView.prefHeightProperty().bind(Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize()).add(30)); // bind the height of the table to the number of items

        // the table columns
        TableColumn<Scores.Score, String> column1 = new TableColumn<>("Name");
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Scores.Score, Integer> column2 = new TableColumn<>("Time");
        column2.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<Scores.Score, Integer> column3 = new TableColumn<>("Size");
        column3.setCellValueFactory(new PropertyValueFactory<>("size"));

        TableColumn<Scores.Score, Difficulty> column4 = new TableColumn<>("Difficulty");
        column4.setCellValueFactory(new PropertyValueFactory<>("difficulty"));


        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);
        tableView.getColumns().add(column4);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        column1.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3)); // different widths depending on the length of the title of the column
        column2.prefWidthProperty().bind(tableView.widthProperty().multiply(0.2)); // this is just to make it look good
        column3.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        column4.prefWidthProperty().bind(tableView.widthProperty().multiply(0.34));
        column1.setResizable(false); // we don't want the columns to resize themselves depending on the data, that could lead to an ugly table
        column2.setResizable(false);
        column3.setResizable(false);
        column4.setResizable(false);

        Text t = new Text("Here you can view the high scores. The highest\n" +
                "score is the lowest time taken to solve a grid.\n" +
                "Beware that if you built or imported a grid,\n" +
                "your score won't show up here.");
        t.setFont(Font.font("Verdana", 13));
        if (currentTheme.equals("black")) t.setFill(Color.WHITE);
        else if (currentTheme.equals("white") || currentTheme.equals("blue")) t.setFill(Color.BLACK);
        t.setTextAlignment(TextAlignment.CENTER);
        HBox textBox = new HBox(t);
        textBox.setAlignment(Pos.CENTER);

        // a rectangle for design/clarity purposes
        final Rectangle blackBorder = new Rectangle(0, 0, Color.TRANSPARENT);
        blackBorder.setStroke(Color.BLACK);
        blackBorder.setManaged(false);
        blackBorder.setLayoutX(0);
        blackBorder.setLayoutY(1);
        blackBorder.setWidth(365);
        blackBorder.setArcWidth(30.0);
        blackBorder.setArcHeight(30.0);
        blackBorder.setHeight(t.getBoundsInParent().getHeight());

        Button showScores = new Button("Show scores");
        showScores.setOnMouseClicked(e -> {
            clickSound.play();
            if (!optionsSize.getSelectionModel().isEmpty() && !optionsDifficulty.getSelectionModel().isEmpty()) { // check if there is a size and difficulty chosen
                tableView.getItems().clear();
                for (Scores.Score s : scoresList) { // for each score in the scores list
                    if (s.getSize() == (int) optionsSize.getValue() && s.getDifficulty().equals(optionsDifficulty.getValue())) { // if the score has the properties we specified
                        tableView.getItems().add(s); // add it to the table
                    }
                }
                tableView.getSortOrder().setAll(column2);
            }
        });

        ObservableList<Integer> options =
                FXCollections.observableArrayList(
                        4,
                        5,
                        6,
                        7,
                        8,
                        9,
                        10
                );
        optionsSize = new ComboBox(options); // to hold the sizes the user can choose from

        ObservableList<Difficulty> optionsDiff =
                FXCollections.observableArrayList(
                        Difficulty.EASY,
                        Difficulty.MEDIUM,
                        Difficulty.HARD
                );
        optionsDifficulty = new ComboBox(optionsDiff); // to hold the difficulties the user can choose from

        Button backToMenu = new Button("Go back to the menu");
        backToMenu.setOnAction(event -> {
            clickSound.play();
            try {
                createMenu();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        VBox vbox = new VBox();
        pane.setCenter(vbox);

        Text chooseSizeText = new Text("Select a size");
        chooseSizeText.setFont(Font.font("Verdana", 13));
        HBox optionsSizeBox = new HBox(optionsSize, chooseSizeText);
        optionsSizeBox.setSpacing(10);

        Text chooseDiffText = new Text("Select a difficulty");
        chooseDiffText.setFont(Font.font("Verdana", 13));
        HBox optionsDiffBox = new HBox(optionsDifficulty, chooseDiffText);
        optionsDiffBox.setSpacing(10);

        VBox boxOptions = new VBox(textBox, blackBorder, optionsSizeBox, optionsDiffBox, showScores, backToMenu);
        boxOptions.setSpacing(10);
        boxOptions.setAlignment(Pos.TOP_LEFT);

        vbox.getChildren().add(boxOptions);
        vbox.setSpacing(20);

        pane.setBottom(tableView);
        BorderPane.setMargin(tableView, new Insets(12, 0, 0, 0)); // margins help in design to keep things spaced
    }

    /**
     * This method changes the scene to the new game scenes. There the user will be prompted with different buttons
     * and choices to make on the size of the grid he wants, the difficulty, if he wants a random grid, to build a grid
     * or to import a grid.
     */
    private void newGame() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);

        Text t = new Text("Select a size");
        t.setFont(Font.font("Verdana", 20));
        if (currentTheme.equals("black")) t.setFill(Color.WHITE);
        else if (currentTheme.equals("white") || currentTheme.equals("blue")) t.setFill(Color.BLACK);

        ObservableList<Integer> options =
                FXCollections.observableArrayList(4, 5, 6, 7, 8, 9, 10);
        optionsSize = new ComboBox(options); // to choose the size of the box

        switch (defaultSize) {
            case 4:
                optionsSize.getSelectionModel().select(0);
                break;
            case 5:
                optionsSize.getSelectionModel().select(1);
                break;
            case 6:
                optionsSize.getSelectionModel().select(2);
                break;
            case 7:
                optionsSize.getSelectionModel().select(3);
                break;
            case 8:
                optionsSize.getSelectionModel().select(4);
                break;
            case 9:
                optionsSize.getSelectionModel().select(5);
                break;
            case 10:
                optionsSize.getSelectionModel().select(6);
                break;
        } // the size passed in the arguments when launching will become the default selected size here

        // difficulty buttons
        easy.getStyleClass().remove("radio-button");
        easy.getStyleClass().add("toggle-button");
        easy.setMaxSize(70, 30);
        easy.setMinSize(70, 30);
        easy.setGraphic(easyuView); // setting its image
        easy.setContentDisplay(ContentDisplay.CENTER);
        easy.setOnMouseClicked(e -> {
            clickSound.play();
            changeSelectedImages("difficulty", easy); // change its image to visibly show its been selected
        });

        medium.getStyleClass().remove("radio-button");
        medium.getStyleClass().add("toggle-button");
        medium.setMaxSize(70, 30);
        medium.setMinSize(70, 30);
        medium.setGraphic(mediumuView);
        medium.setContentDisplay(ContentDisplay.CENTER);
        medium.setOnMouseClicked(e -> {
            clickSound.play();
            changeSelectedImages("difficulty", medium);
        });

        hard.getStyleClass().remove("radio-button");
        hard.getStyleClass().add("toggle-button");
        hard.setMaxSize(70, 30);
        hard.setMinSize(70, 30);
        hard.setGraphic(harduView);
        hard.setContentDisplay(ContentDisplay.CENTER);
        hard.setOnMouseClicked(e -> {
            clickSound.play();
            changeSelectedImages("difficulty", hard);
        });

        tg = new ToggleGroup();
        easy.setToggleGroup(tg);
        medium.setToggleGroup(tg);
        hard.setToggleGroup(tg);
        HBox difficultyBox = new HBox(easy, medium, hard);
        difficultyBox.setSpacing(3);
        difficultyBox.setAlignment(Pos.CENTER);

        // let the user import his own grid
        Button importGrid = new Button("Import Grid");
        importGrid.setTooltip(new Tooltip("Size and difficulty doesn't matter.\n" +
                "Will open the file with saved grids and you can choose which to import.\n" +
                "You can save a grid when playing it and import it here."));
        importGrid.setOnAction(event -> {
            clickSound.play();
            try {
                importAGrid();
            } catch (IOException | IllegalGridSize e) {
                e.printStackTrace();
            }
        });

        // let the user build a grid
        // this calls the buildAGrid method that will handle all the building grid functionality
        Button buildGrid = new Button("Build a grid");
        buildGrid.setTooltip(new Tooltip("Requires a size, difficulty doesn't matter.\n" +
                "Will set a blank grid of specified size that you can build before playing."));
        buildGrid.setOnAction(event -> {
            clickSound.play();
            if (!optionsSize.getSelectionModel().isEmpty()) {
                try {
                    buildAGrid();
                } catch (IllegalGridSize illegalGridSize) {
                    illegalGridSize.printStackTrace();
                }
            }
        });

        // then generate the new game
        Button startGame = new Button("Randomise grid");
        startGame.setTooltip(new Tooltip("Requires a size and a difficulty.\n" +
                "Will randomise the placement of Xs, Os and solids."));
        startGame.setOnAction(event -> {
            clickSound.play();
            if (!optionsSize.getSelectionModel().isEmpty()) {
                if ((int) optionsSize.getValue() > 7) {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setTitle("Warning");
                    a.setHeaderText("This size of grid isn't guaranteed to be solvable!");
                    a.setContentText("Grids of size 8, 9 and 10 won't be check for solvability.\n" +
                            "This is because the algorithm that checks for it takes a while for these sizes.\n" +
                            "Therefore, you might not be able to solve that grid, but you're welcome to try.\n");
                    a.show();
                }
                if (easy.isSelected()) {
                    try {
                        playGame(Difficulty.EASY, false, null);
                    } catch (FileNotFoundException | IllegalGridSize e) {
                        e.printStackTrace();
                    }
                } else if (medium.isSelected()) {
                    try {
                        playGame(Difficulty.MEDIUM, false, null);
                    } catch (FileNotFoundException | IllegalGridSize e) {
                        e.printStackTrace();
                    }
                } else if (hard.isSelected()) {
                    try {
                        playGame(Difficulty.HARD, false, null);
                    } catch (FileNotFoundException | IllegalGridSize e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Button backToMenu = new Button("Go back to the menu");
        HBox backToMenuBox = new HBox(backToMenu);
        backToMenuBox.setAlignment(Pos.BOTTOM_LEFT);
        backToMenu.setOnAction(event -> {
            clickSound.play();
            try {
                createMenu();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        hbox.getChildren().addAll(t, optionsSize);
        hbox.setSpacing(10);
        HBox startButtonsBox = new HBox(startGame, buildGrid);
        startButtonsBox.setSpacing(5);
        startButtonsBox.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(hbox, difficultyBox, startButtonsBox, importGrid, backToMenuBox);
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        pane.setCenter(vbox);
    }

    /**
     * This method changes the scene to a dropdown menu with the number of the grids available from the savedgrids folder
     * where lies the MarupekeGrids text file that contains all the saved grids. The user can choose one and that is
     * the one he will be able to play. When playing any kind of grid, the user can choose to save the grid with the
     * button on the right hand side "save grid" and it will be put into the MarupekeGrids text file.
     *
     * @throws IOException
     * @throws IllegalGridSize
     */
    private void importAGrid() throws IOException, IllegalGridSize {
        // count the number of grids
        int savedGridNum = 0;
        try (LineNumberReader r = new LineNumberReader(new FileReader(saveGrids))) {
            String line;
            while ((line = r.readLine()) != null) {
                for (String element : line.split(" ")) {
                    if (element.equalsIgnoreCase("Grid")) {
                        savedGridNum++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Make a combobox of all the grids with one to select
        Text label = new Text("Choose a grid");
        label.setFont(Font.font("Verdana", 15));
        if (currentTheme.equals("black")) label.setFill(Color.WHITE);
        else if (currentTheme.equals("white") || currentTheme.equals("blue")) label.setFill(Color.BLACK);
        ObservableList<String> options = FXCollections.observableArrayList();
        for (int i = 1; i <= savedGridNum; i++) {
            options.add("Grid " + i);
        }
        ComboBox optionsGrid = new ComboBox(options);
        HBox hbox = new HBox(label, optionsGrid);
        hbox.setSpacing(5);

        // each button will lead to the built grid
        Button startGame = new Button("Start Game");
        startGame.setOnMouseClicked(e -> {
            clickSound.play();
            String selected = optionsGrid.getValue().toString();
            MarupekeGrid mg = null;
            try {
                mg = getGridInFile(selected);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            try {
                playGame(null, true, mg);
            } catch (FileNotFoundException | IllegalGridSize fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });

        VBox vbox = new VBox(hbox, startGame);
        pane.setCenter(vbox);
    }

    /**
     * This method changes the scene to a blank grid of size specified in the dropdown menu in the "new game" scene.
     * The user can then place as he wishes (without making any illegalities) Xs, Os and Solids and erase them as he
     * pleases. Effectively, building his own Marupeke grid. There is a button on the right-hand side "play game" to
     * play the grid he just built.
     *
     * @throws IllegalGridSize
     */
    private void buildAGrid() throws IllegalGridSize {
        finished = false;

        // set the tools
        RadioButton toolX = new RadioButton();
        toolX.setUserData("X");
        toolX.getStyleClass().remove("radio-button");
        toolX.getStyleClass().add("toggle-button");
        toolX.setGraphic(xToolView);
        toolX.setOnMouseClicked(e -> {
            // changing the cursor to show to the user the tool he has chosen
            Image imageCursor = null;
            try {
                imageCursor = new Image(new FileInputStream("images/xSquareTransparent.png"), 30, 30, false, true);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            scene.setCursor(new ImageCursor(imageCursor));
        });

        RadioButton toolO = new RadioButton();
        toolO.setUserData("O");
        toolO.getStyleClass().remove("radio-button");
        toolO.getStyleClass().add("toggle-button");
        toolO.setGraphic(oToolView);
        toolO.setOnMouseClicked(e -> {
            Image imageCursor = null;
            try {
                imageCursor = new Image(new FileInputStream("images/oSquareTransparent.png"), 30, 30, false, true);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            scene.setCursor(new ImageCursor(imageCursor));
        });

        RadioButton toolSolid = new RadioButton();
        toolSolid.setUserData("Solid");
        toolSolid.getStyleClass().remove("radio-button");
        toolSolid.getStyleClass().add("toggle-button");
        toolSolid.setGraphic(miniBlackSquareView);
        toolSolid.setOnMouseClicked(e -> {
            Image imageCursor = null;
            try {
                imageCursor = new Image(new FileInputStream("images/miniblacksquare.png"), 30, 30, false, true);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            scene.setCursor(new ImageCursor(imageCursor));
        });

        RadioButton toolErase = new RadioButton();
        toolErase.setUserData("Erase");
        toolErase.getStyleClass().remove("radio-button");
        toolErase.getStyleClass().add("toggle-button");
        toolErase.setGraphic(eraserToolView);
        toolErase.setOnMouseClicked(e -> {
            Image imageCursor = null;
            try {
                imageCursor = new Image(new FileInputStream("images/eraser.png"), 30, 30, false, true);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            scene.setCursor(new ImageCursor(imageCursor));
        });

        tg = new ToggleGroup();
        toolX.setToggleGroup(tg);
        toolO.setToggleGroup(tg);
        toolSolid.setToggleGroup(tg);
        toolErase.setToggleGroup(tg);
        hover = true;

        // get the size of the grid
        mg = MarupekeGrid.buildGameGrid((int) optionsSize.getValue(), Difficulty.BLANK);
        MarupekeTile[][] tile = mg.getTiles();

        // download the grid as a txt file
        Button saveGrid = new Button("Save grid");

        saveGrid.setOnMouseClicked(event -> {
            clickSound.play();
            try {
                PrintWriter out = new PrintWriter(new FileWriter(saveGrids, true)); // to write to the text file
                int savedGridNum = 1;
                try (LineNumberReader r = new LineNumberReader(new FileReader(saveGrids))) {
                    String line;
                    while ((line = r.readLine()) != null) {
                        for (String element : line.split(" ")) {
                            if (element.equalsIgnoreCase("Grid")) {
                                savedGridNum++;
                            }
                        }
                    }
                }
                out.append("Grid " + savedGridNum);
                out.append(System.lineSeparator());
                // each item on the grid will be translated from a state to a char to be placed in the text file
                for (int i = 0; i < mg.getSize(); i++) {
                    for (int j = 0; j < mg.getSize(); j++) {
                        if (mg.getState(i, j) == MarupekeTile.State.X) out.append('X');
                        else if (mg.getState(i, j) == MarupekeTile.State.O) out.append('O');
                        else if (mg.getState(i, j) == MarupekeTile.State.SOLID) out.append('#');
                        else out.append('_');
                    }
                    out.append(System.lineSeparator());
                }
                out.append(System.lineSeparator());
                out.append(System.lineSeparator());
                out.close();
            } catch (IOException e) {
                System.out.println("Couldn't add the grid");
            }
        });

        // button to play game
        Button playGame = new Button("Play");
        playGame.setOnMouseClicked(e -> {
            scene.setCursor(Cursor.DEFAULT);
            clickSound.play();
            // set every marked square as uneditable
            for (int i = 0; i < mg.getSize(); i++) {
                for (int j = 0; j < mg.getSize(); j++) {
                    if (tile[i][j].getState() != MarupekeTile.State.BLANK) {
                        tile[i][j].setEditable(false);
                    }
                }
            }
            try {
                playGame(null, true, mg);
            } catch (FileNotFoundException | IllegalGridSize fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });

        // everything that's on the right, the tools and other buttons
        VBox vbox = new VBox(toolX, toolO, toolErase, toolSolid, saveGrid, playGame);
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.TOP_RIGHT);
        pane.setRight(vbox);
        BorderPane.setMargin(vbox, new Insets(50, 5, 5, 5));

        // add a Text above the grid that will signal any illegalities
        illegalitiesText = new Text("Illegalities: none for now");
        illegalitiesText.setFont(Font.font("Verdana", 12));
        if (currentTheme.equals("black")) illegalitiesText.setFill(Color.WHITE);
        else if (currentTheme.equals("white") || currentTheme.equals("blue")) illegalitiesText.setFill(Color.BLACK);
        illegalitiesText.setTextAlignment(TextAlignment.LEFT);
        HBox textBox = new HBox(illegalitiesText);
        textBox.setMinHeight(35);
        textBox.setAlignment(Pos.BOTTOM_CENTER);

        // grid pane to be placed in the centre
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));

        emptySquares = new StackPane[mg.getSize()][mg.getSize()]; // will hold the coordinates to indicate to the buttons

        buttons = new Button[mg.getSize()][mg.getSize()]; // will hold the buttons that represent the tiles
        sizeOfSquare = 250 / mg.getSize(); // to make sure the grid stays the same length and width no matter what the size chosen is

        for (int y = 0; y < mg.getSize(); y++) {
            for (int x = 0; x < mg.getSize(); x++) {
                Button buttonSquare = new Button();
                buttonSquare.setMinSize(sizeOfSquare, sizeOfSquare); // set size for button
                buttonSquare.setMaxSize(sizeOfSquare, sizeOfSquare);
                buttons[y][x] = buttonSquare;
                ImageView emptySquareImageView = new ImageView(emptySquareImage);
                ImageView squareImageView = emptySquareImageView;
                emptySquares[y][x] = new StackPane();
                if (mg.getState(x, y) == MarupekeTile.State.X) {
                    squareImageView = new ImageView(xSquareImage); // set image for button depending on state of the tile it represents
                } else if (mg.getState(x, y) == MarupekeTile.State.O) {
                    squareImageView = new ImageView(oSquareImage);
                } else if (mg.getState(x, y) == MarupekeTile.State.SOLID) {
                    squareImageView = new ImageView(solidSquareImage);
                }
                squareImageView.setFitHeight(sizeOfSquare); // set image to be same size as button
                squareImageView.setFitWidth(sizeOfSquare);
                buttonSquare.setGraphic(squareImageView);
                AtomicReference<ImageView> currentImage = new AtomicReference<>((ImageView) buttonSquare.getGraphic()); // get the current image of the button
                if (buttonSquare.getGraphic().equals(emptySquareImageView)) {
                    // for when the mouse hovers over the square, a grey image of X or O to represent what could be if the user placed it here
                    // only if the tile is editable, the game is not finished and hovering is allowed
                    buttonSquare.setOnMouseEntered(e -> {
                        if (!finished && hover && findTile(buttonSquare).isEditable()) {
                            selected = (RadioButton) tg.getSelectedToggle();
                            if (selected != null) {
                                if (selected.getUserData().equals("X")) {
                                    ImageView xSquareGreyImageView = new ImageView(xSquareGreyImage);
                                    xSquareGreyImageView.setFitHeight(sizeOfSquare);
                                    xSquareGreyImageView.setFitWidth(sizeOfSquare);
                                    buttonSquare.setGraphic(xSquareGreyImageView);
                                } else if (selected.getUserData().equals("O")) {
                                    ImageView oSquareGreyImageView = new ImageView(oSquareGreyImage);
                                    oSquareGreyImageView.setFitHeight(sizeOfSquare);
                                    oSquareGreyImageView.setFitWidth(sizeOfSquare);
                                    buttonSquare.setGraphic(oSquareGreyImageView);
                                }
                            }
                        }
                    });
                    // when the mouse hovers out of the square, we want it to reset to the image it was before
                    // sometimes, it will set no images to it at all and there will just be a blank button
                    // this is because the mouse moves over to another square faster than the code can process
                    // it's rare though
                    buttonSquare.setOnMouseExited(e -> {
                        if (!finished && hover && findTile(buttonSquare).isEditable()) {
                            for (int i = 0; i < mg.getSize(); i++) {
                                for (int j = 0; j < mg.getSize(); j++) {
                                    if (buttons[i][j] == buttonSquare) {
                                        buttonSquare.setGraphic(currentImage.get());
                                    }
                                }
                            }
                        }
                    });
                }
                // when the user clicks on a square, it calls the mark method to mark the grid
                buttonSquare.setOnMouseClicked(e -> {
                    if (!finished && findTile(buttonSquare).isEditable()) {
                        if (toolX.isSelected() || toolO.isSelected() || toolSolid.isSelected() || toolErase.isSelected()) {
                            try {
                                mark(buttonSquare, mg.getSize(), "black");
                                currentImage.set((ImageView) buttonSquare.getGraphic());
                            } catch (FileNotFoundException | InterruptedException fileNotFoundException) {
                                fileNotFoundException.printStackTrace();
                            }
                        }
                    }
                });
                emptySquares[y][x].getChildren().add(buttonSquare);
                grid.add(emptySquares[y][x], x, y);
            }
        }

        grid.setAlignment(Pos.BOTTOM_CENTER);
        VBox centerBox = new VBox(textBox, grid);
        centerBox.setSpacing(3);
        pane.setCenter(centerBox);
        BorderPane.setMargin(centerBox, new Insets(20, 0, 20, 0));

        Button backToMenu = new Button("Go back to the menu");
        backToMenu.setOnAction(event -> {
            scene.setCursor(Cursor.DEFAULT);
            clickSound.play();
            try {
                createMenu();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        pane.setBottom(backToMenu);
    }

    /**
     * The inputted gridSelected String will reflect the grid from the MarupekeGrids text file for this method to retrieve.
     * The method finds the grid by reading the file and goes on to translate each character whether an X, an O, a # (solid)
     * or a _ (blank) and puts it into an actual MarupekeGrid object. That MarupekeGrid is then returned to be played
     * by the user.
     *
     * @param gridSelected the string that represents the grid in the file to find
     * @return a MarupekeGrid object that is the literal translation of the grid found in the text file
     * @throws IOException
     */
    public MarupekeGrid getGridInFile(String gridSelected) throws IOException {
        int totalLines = countLinesFile("savedGrids/MarupekeGrids.txt"); // count total lines in the file so we know when we've reached the ened
        int currentLine = 0;
        LineNumberReader reader = new LineNumberReader(new FileReader("savedGrids/MarupekeGrids.txt"));
        String currentLineText;

        while (currentLine != totalLines) {
            currentLineText = reader.readLine(); // read the line
            // once the line is equal to the one we want, the inputted String, we can stop
            if (currentLineText.equalsIgnoreCase(gridSelected)) {
                StringBuilder gridInString = new StringBuilder();
                while (currentLineText != null) {
                    currentLineText = reader.readLine();
                    gridInString.append(currentLineText).append("\n");
                }
                String gridTrimmed = gridInString.toString().trim().replaceAll("\\s", ""); // trim to not have spaces and returns
                int size = (int) Math.sqrt(gridTrimmed.length());
                MarupekeGrid mg = new MarupekeGrid(size);
                MarupekeTile[][] tiles = mg.getTiles();
                int charInGrid = 0;
                for (int i = 0; i < tiles.length; i++) {
                    for (int j = 0; j < tiles.length; j++) {
                        if (gridTrimmed.charAt(charInGrid) == 'X') {
                            tiles[i][j].setState(MarupekeTile.State.X); // builds the grid by importing the state of the tile from what it's reading in the file
                            tiles[i][j].setEditable(false);
                        } else if (gridTrimmed.charAt(charInGrid) == 'O') {
                            tiles[i][j].setState(MarupekeTile.State.O);
                            tiles[i][j].setEditable(false);
                        } else if (gridTrimmed.charAt(charInGrid) == '#') {
                            tiles[i][j].setState(MarupekeTile.State.SOLID);
                            tiles[i][j].setEditable(false);
                        } else if (gridTrimmed.charAt(charInGrid) == '_') {
                            tiles[i][j].setState(MarupekeTile.State.BLANK);
                            tiles[i][j].setEditable(true);
                        }
                        ++charInGrid;
                    }
                }
                return mg;
            }
            currentLine = reader.getLineNumber();
        }
        reader.close();
        return null;
    }

    /**
     * A method to count the number of lines in a text file.
     * <p>
     * This method was not fully built by me, it's a modified version from here:
     * https://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
     *
     * @param filename the path to the file in which to count the number of lines
     * @return the number of lines in the specified file
     * @throws IOException
     */
    public static int countLinesFile(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }

            // make it easy for the optimizer to tune this loop
            int count = 0;
            while (readChars == 1024) {
                for (int i = 0; i < 1024; ) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            // count remaining characters
            while (readChars != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            return count == 0 ? 1 : count;
        } finally {
            is.close();
        }
    }

    /**
     * Changes the image of a RadioButton depending on the value currently selected.
     * The String is to represent if the change is called for a size radiobutton or a
     * difficulty radiobutton.
     *
     * @param toChange the radiobutton category for which to change the image
     * @param selected the currently selected radiobutton
     */
    private void changeSelectedImages(String toChange, RadioButton selected) {
        if (toChange.equals("size")) {
            // go through the rbList array changing the graphics of each button to the u one
            // change the sizeX one to the non u one
            rbList[0].setGraphic(size4uView);
            rbList[1].setGraphic(size5uView);
            rbList[2].setGraphic(size6uView);
            rbList[3].setGraphic(size7uView);
            rbList[4].setGraphic(size8uView);
            rbList[5].setGraphic(size9uView);
            rbList[6].setGraphic(size10uView);
            // loop through changing only the one that is selected to indicate to the user that that is the one currently selected
            for (int i = 0; i < 7; i++) {
                if (rbList[i] == selected) {
                    if (i == 0) selected.setGraphic(size4View);
                    if (i == 1) selected.setGraphic(size5View);
                    if (i == 2) selected.setGraphic(size6View);
                    if (i == 3) selected.setGraphic(size7View);
                    if (i == 4) selected.setGraphic(size8View);
                    if (i == 5) selected.setGraphic(size9View);
                    if (i == 6) selected.setGraphic(size10View);
                }
            }
        } else if (toChange.equals("difficulty")) {
            difficultyList[0].setGraphic(easyuView);
            difficultyList[1].setGraphic(mediumuView);
            difficultyList[2].setGraphic(harduView);
            for (int i = 0; i < 3; i++) {
                if (difficultyList[i] == selected) {
                    if (i == 0) selected.setGraphic(easyView);
                    if (i == 1) selected.setGraphic(mediumView);
                    if (i == 2) selected.setGraphic(hardView);
                }
            }
        }
    }

    /**
     * Changes the scene to the solving menu scene where the user can play around with the DFS algorithm.
     * The user can specify a size, a difficulty and a number of grids to be solved and the algorithm will start
     * solving them one by one. A progress bar shows how far the program is into solving all the grids.
     */
    private void newSolving() {
        // transition to make the current logo at the top leave and have the new one appear
        TranslateTransition translateTransitionCand = new TranslateTransition(Duration.millis(2000), candnoView);
        translateTransitionCand.setFromX(175);
        translateTransitionCand.setToX(850);
        translateTransitionCand.play();
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(2000), logoView);
        translateTransition.setFromX(0);
        translateTransition.setToX(750);
        translateTransition.play();
        translateTransition.setOnFinished(e -> {
            try {
                ImageView solvingLogoView = new ImageView(new Image(new FileInputStream("images/SolvingLogo.png")));
                solvingLogoView.setFitHeight(85);
                solvingLogoView.setFitWidth(350);
                pane.setTop(solvingLogoView);
                TranslateTransition sTranslateTransition = new TranslateTransition(Duration.millis(2000), solvingLogoView);
                sTranslateTransition.setFromX(-1000);
                sTranslateTransition.setToX(0);
                sTranslateTransition.play();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);

        // make the user choose the size of the grids from a combo boxes
        VBox chooseSizeBox = new VBox();
        chooseSizeBox.setSpacing(5);
        Text t = new Text("Welcome to Solving! This helps you\n" +
                "test out the DFS algorithm that solves the\n" +
                "grids.Choose the size of grids, the\n" +
                "difficulty and the number of grids. Press\n" +
                "start and the computer will start solving\n" +
                "them in the background.\n" +
                "(Try size 5, difficulty Easy and 100 grids)\n" +
                "(Interestingly, more difficult = easier for DFS)");
        t.setFont(Font.font("Verdana", 12));
        if (currentTheme.equals("black")) t.setFill(Color.WHITE);
        else if (currentTheme.equals("white") || currentTheme.equals("blue")) t.setFill(Color.BLACK);

        rbList = new RadioButton[7];

        RadioButton size4 = new RadioButton();
        rbList[0] = size4;
        size4.getStyleClass().remove("radio-button");
        size4.getStyleClass().add("toggle-button");
        size4.setMaxSize(30, 30);
        size4.setMinSize(30, 30);
        size4.setGraphic(size4uView);
        size4.setContentDisplay(ContentDisplay.CENTER);
        size4.setUserData(4);
        size4.setOnMouseClicked(e -> {
            clickSound.play();
            changeSelectedImages("size", size4);
        });

        RadioButton size5 = new RadioButton();
        rbList[1] = size5;
        size5.getStyleClass().remove("radio-button");
        size5.getStyleClass().add("toggle-button");
        size5.setMaxSize(30, 30);
        size5.setMinSize(30, 30);
        size5.setGraphic(size5uView);
        size5.setContentDisplay(ContentDisplay.CENTER);
        size5.setUserData(5);
        size5.setOnMouseClicked(e -> {
            clickSound.play();
            changeSelectedImages("size", size5);
        });

        RadioButton size6 = new RadioButton();
        rbList[2] = size6;
        size6.getStyleClass().remove("radio-button");
        size6.getStyleClass().add("toggle-button");
        size6.setMaxSize(30, 30);
        size6.setMinSize(30, 30);
        size6.setGraphic(size6uView);
        size6.setContentDisplay(ContentDisplay.CENTER);
        size6.setUserData(6);
        size6.setOnMouseClicked(e -> {
            clickSound.play();
            changeSelectedImages("size", size6);
        });

        RadioButton size7 = new RadioButton();
        rbList[3] = size7;
        size7.getStyleClass().remove("radio-button");
        size7.getStyleClass().add("toggle-button");
        size7.setMaxSize(30, 30);
        size7.setMinSize(30, 30);
        size7.setGraphic(size7uView);
        size7.setContentDisplay(ContentDisplay.CENTER);
        size7.setUserData(7);
        size7.setOnMouseClicked(e -> {
            clickSound.play();
            changeSelectedImages("size", size7);
        });

        RadioButton size8 = new RadioButton();
        rbList[4] = size8;
        size8.getStyleClass().remove("radio-button");
        size8.getStyleClass().add("toggle-button");
        size8.setMaxSize(30, 30);
        size8.setMinSize(30, 30);
        size8.setGraphic(size8uView);
        size8.setContentDisplay(ContentDisplay.CENTER);
        size8.setUserData(8);
        size8.setOnMouseClicked(e -> {
            clickSound.play();
            changeSelectedImages("size", size8);
        });

        RadioButton size9 = new RadioButton();
        rbList[5] = size9;
        size9.getStyleClass().remove("radio-button");
        size9.getStyleClass().add("toggle-button");
        size9.setMaxSize(30, 30);
        size9.setMinSize(30, 30);
        size9.setGraphic(size9uView);
        size9.setContentDisplay(ContentDisplay.CENTER);
        size9.setUserData(9);
        size9.setOnMouseClicked(e -> {
            clickSound.play();
            changeSelectedImages("size", size9);
        });

        RadioButton size10 = new RadioButton();
        rbList[6] = size10;
        size10.getStyleClass().remove("radio-button");
        size10.getStyleClass().add("toggle-button");
        size10.setMaxSize(30, 30);
        size10.setMinSize(30, 30);
        size10.setGraphic(size10uView);
        size10.setContentDisplay(ContentDisplay.CENTER);
        size10.setUserData(10);
        size10.setOnMouseClicked(e -> {
            clickSound.play();
            changeSelectedImages("size", size10);
        });

        tg = new ToggleGroup();
        size4.setToggleGroup(tg);
        size5.setToggleGroup(tg);
        size6.setToggleGroup(tg);
        size7.setToggleGroup(tg);
        size8.setToggleGroup(tg);
        size9.setToggleGroup(tg);
        size10.setToggleGroup(tg);
        HBox chooseSize = new HBox(size4, size5, size6, size7, size8, size9, size10);
        chooseSize.setSpacing(2);

        chooseSizeBox.getChildren().addAll(t, chooseSize);

        // difficulty buttons
        easy.getStyleClass().remove("radio-button");
        easy.getStyleClass().add("toggle-button");
        easy.setMaxSize(70, 30);
        easy.setMinSize(70, 30);
        easy.setGraphic(easyuView);
        easy.setContentDisplay(ContentDisplay.CENTER);
        easy.setOnMouseClicked(e -> {
            clickSound.play();
            changeSelectedImages("difficulty", easy);
        });

        medium.getStyleClass().remove("radio-button");
        medium.getStyleClass().add("toggle-button");
        medium.setMaxSize(70, 30);
        medium.setMinSize(70, 30);
        medium.setGraphic(mediumuView);
        medium.setContentDisplay(ContentDisplay.CENTER);
        medium.setOnMouseClicked(e -> {
            clickSound.play();
            changeSelectedImages("difficulty", medium);
        });

        hard.getStyleClass().remove("radio-button");
        hard.getStyleClass().add("toggle-button");
        hard.setMaxSize(70, 30);
        hard.setMinSize(70, 30);
        hard.setGraphic(harduView);
        hard.setContentDisplay(ContentDisplay.CENTER);
        hard.setOnMouseClicked(e -> {
            clickSound.play();
            changeSelectedImages("difficulty", hard);
        });

        ToggleGroup tg2 = new ToggleGroup();
        easy.setToggleGroup(tg2);
        medium.setToggleGroup(tg2);
        hard.setToggleGroup(tg2);
        HBox difficultyBox = new HBox(easy, medium, hard);
        difficultyBox.setSpacing(3);
        difficultyBox.setAlignment(Pos.CENTER);

        // let the user type in the number of grids he wants (recommend one so that it's not too long)
        TextField textField = new TextField();

        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([1-9][0-9]*)?")) {
                return change;
            }
            return null;
        };
        textField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));

        // make a start button to start the solving
        Button start = new Button("Start solving");
        start.setAlignment(Pos.CENTER_RIGHT);

        // have a progress bar that shows how close we are to finishing the grids
        Text lblRole1 = new Text("The number of grids solved is ");
        lblRole1.setFont(Font.font("Verdana", 15));
        if (currentTheme.equals("black")) lblRole1.setFill(Color.WHITE);
        else if (currentTheme.equals("white") || currentTheme.equals("blue")) lblRole1.setFill(Color.BLACK);
        Label lblRole2 = new Label();
        HBox textSolvedBox = new HBox(lblRole1, lblRole2);

        // start the solving
        ProgressBar progressBar = new ProgressBar(0);
        start.setOnAction(event -> new Thread(() -> {
            clickSound.play();
            progressBar.setProgress(0);
            int solved = 0;
            Text solvedGrids = new Text(String.valueOf(solved));
            solvedGrids.setFont(Font.font("Verdana", 15));
            if (currentTheme.equals("black")) solvedGrids.setFill(Color.WHITE);
            else if (currentTheme.equals("white") || currentTheme.equals("blue")) solvedGrids.setFill(Color.BLACK);
            int numOfGrids = Integer.parseInt(textField.getText());
            // set the difficulty
            Difficulty difficulty = null;
            if (easy.isSelected()) {
                difficulty = Difficulty.EASY;
            } else if (medium.isSelected()) {
                difficulty = Difficulty.MEDIUM;
            } else if (hard.isSelected()) {
                difficulty = Difficulty.HARD;
            }
            for (int i = 0; i < numOfGrids; i++) {
                MarupekeGrid grid = null;
                try {
                    grid = MarupekeGrid.buildGameGrid((int) tg.getSelectedToggle().getUserData(), difficulty);
                } catch (IllegalGridSize illegalGridSize) {
                    illegalGridSize.printStackTrace();
                }
                if (grid.solvePuzzle(0, 0)) {
                    ++solved;
                }
                double p = i * 100 / numOfGrids;
                int finalSolved = solved;
                Platform.runLater(() -> {
                    solvedGrids.setText(String.valueOf(finalSolved));
                    lblRole2.textProperty().bind(solvedGrids.textProperty());
                    progressBar.setProgress(p / 100);
                });
            }
        }).start());

        vbox.getChildren().addAll(chooseSizeBox, textField, difficultyBox, start, progressBar, textSolvedBox);
        vbox.setSpacing(5);
        pane.setCenter(vbox);

        Button backToMenu = new Button("Go back to the menu");
        backToMenu.setOnAction(event -> {
            clickSound.play();
            try {
                createMenu();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        pane.setBottom(backToMenu);
    }

    /**
     * Changes the scene to play a game of Marupeke with either a random grid, an imported grid or a built grid with
     * the tools to play on the right side. A stackpane inside a gridpane displays buttons in the middle of the
     * borderpane that will represent with their images the MarupekeGrid. Text right above the grid is used to
     * display the illegalities if the user makes any.
     *
     * @param difficulty   the specified difficulty of the grid
     * @param gridImport   true if the grid is an import, false otherwise
     * @param importedGrid the imported grid, null if its not a built or imported grid
     * @throws FileNotFoundException
     * @throws IllegalGridSize
     */
    private void playGame(Difficulty difficulty, boolean gridImport, MarupekeGrid importedGrid) throws FileNotFoundException, IllegalGridSize {
        // first generate the command collector
        finished = false;

        // set the tools
        RadioButton toolX = new RadioButton();
        toolX.setUserData("X");
        toolX.getStyleClass().remove("radio-button");
        toolX.getStyleClass().add("toggle-button");
        toolX.setGraphic(xToolView);
        toolX.setOnMouseClicked(e -> {
            Image imageCursor = null;
            try {
                imageCursor = new Image(new FileInputStream("images/xSquareTransparent.png"), 30, 30, false, true);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            scene.setCursor(new ImageCursor(imageCursor));
        });

        RadioButton toolO = new RadioButton();
        toolO.setUserData("O");
        toolO.getStyleClass().remove("radio-button");
        toolO.getStyleClass().add("toggle-button");
        toolO.setGraphic(oToolView);
        toolO.setOnMouseClicked(e -> {
            Image imageCursor = null;
            try {
                imageCursor = new Image(new FileInputStream("images/oSquareTransparent.png"), 30, 30, false, true);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            scene.setCursor(new ImageCursor(imageCursor));
        });

        RadioButton toolErase = new RadioButton();
        toolErase.setUserData("Erase");
        toolErase.getStyleClass().remove("radio-button");
        toolErase.getStyleClass().add("toggle-button");
        toolErase.setGraphic(eraserToolView);
        toolErase.setOnMouseClicked(e -> {
            Image imageCursor = null;
            try {
                imageCursor = new Image(new FileInputStream("images/eraser.png"), 30, 30, false, true);
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            scene.setCursor(new ImageCursor(imageCursor));
        });

        tg = new ToggleGroup();
        toolX.setToggleGroup(tg);
        toolO.setToggleGroup(tg);
        toolErase.setToggleGroup(tg);
        hover = true;

        //hint button
        Button hint = new Button("Hint");
        hint.setOnMouseClicked(e -> {
            scene.setCursor(Cursor.DEFAULT); // resets the cursor to its original image
            clickSound.play();
            if (!finished) {
                try {
                    solve(true);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });

        //solve button
        Button solve = new Button("Solve");
        solve.setOnMouseClicked(e -> {
            scene.setCursor(Cursor.DEFAULT);
            clickSound.play();
            if (!finished) {
                try {
                    solve(false);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        });

        Text timerLabel = new Text("Timer:");
        timerLabel.setFont(Font.font("Verdana", 15));
        if (currentTheme.equals("black")) timerLabel.setFill(Color.WHITE);
        else if (currentTheme.equals("white") || currentTheme.equals("blue")) timerLabel.setFill(Color.BLACK);

        // get the size of the grid
        if (!gridImport) {
            mg = MarupekeGrid.buildGameGrid((int) optionsSize.getValue(), difficulty);
        } else {
            mg = importedGrid;
        }

        // start the timer
        timer.start();

        // download the grid as a txt file
        Button saveGrid = new Button("Save grid");

        saveGrid.setOnMouseClicked(event -> {
            clickSound.play();
            try {
                // saves the grid in the text file by taking each state and translating it to a character
                PrintWriter out = new PrintWriter(new FileWriter(saveGrids, true));
                int savedGridNum = 1;
                try (LineNumberReader r = new LineNumberReader(new FileReader(saveGrids))) {
                    String line;
                    while ((line = r.readLine()) != null) {
                        for (String element : line.split(" ")) {
                            if (element.equalsIgnoreCase("Grid")) {
                                savedGridNum++;
                            }
                        }
                    }
                }
                out.append("Grid " + savedGridNum);
                out.append(System.lineSeparator());
                for (int i = 0; i < mg.getSize(); i++) {
                    for (int j = 0; j < mg.getSize(); j++) {
                        if (mg.getState(i, j) == MarupekeTile.State.X) out.append('X');
                        else if (mg.getState(i, j) == MarupekeTile.State.O) out.append('O');
                        else if (mg.getState(i, j) == MarupekeTile.State.SOLID) out.append('#');
                        else out.append('_');
                    }
                    out.append(System.lineSeparator());
                }
                out.append(System.lineSeparator());
                out.append(System.lineSeparator());
                out.close();
            } catch (IOException e) {
                System.out.println("COULD NOT LOG!!");
            }
        });

        // everything that's on the right
        VBox vbox = new VBox(toolX, toolO, toolErase, hint, solve, saveGrid, timerLabel, display);
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.TOP_RIGHT);
        pane.setRight(vbox);
        BorderPane.setMargin(vbox, new Insets(50, 5, 5, 5));

        // add a Text above the grid that will signal any illegalities
        illegalitiesText = new Text("Illegalities: none for now");
        illegalitiesText.setFont(Font.font("Verdana", 12));
        if (currentTheme.equals("black")) illegalitiesText.setFill(Color.WHITE);
        else if (currentTheme.equals("white") || currentTheme.equals("blue")) illegalitiesText.setFill(Color.BLACK);
        illegalitiesText.setTextAlignment(TextAlignment.LEFT);
        HBox textBox = new HBox(illegalitiesText);
        textBox.setMinHeight(35);
        textBox.setAlignment(Pos.BOTTOM_CENTER);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 5, 5, 5));

        emptySquares = new StackPane[mg.getSize()][mg.getSize()];

        buttons = new Button[mg.getSize()][mg.getSize()];
        sizeOfSquare = 250 / mg.getSize();

        for (int y = 0; y < mg.getSize(); y++) {
            for (int x = 0; x < mg.getSize(); x++) {
                Button buttonSquare = new Button();
                buttonSquare.setMinSize(sizeOfSquare, sizeOfSquare);
                buttonSquare.setMaxSize(sizeOfSquare, sizeOfSquare);
                buttons[y][x] = buttonSquare;
                ImageView emptySquareImageView = new ImageView(emptySquareImage);
                ImageView squareImageView = emptySquareImageView;
                emptySquares[y][x] = new StackPane();
                if (mg.getState(x, y) == MarupekeTile.State.X) {
                    squareImageView = new ImageView(xSquareImage);
                } else if (mg.getState(x, y) == MarupekeTile.State.O) {
                    squareImageView = new ImageView(oSquareImage);
                } else if (mg.getState(x, y) == MarupekeTile.State.SOLID) {
                    squareImageView = new ImageView(solidSquareImage);
                }
                squareImageView.setFitHeight(sizeOfSquare);
                squareImageView.setFitWidth(sizeOfSquare);
                buttonSquare.setGraphic(squareImageView);
                AtomicReference<ImageView> currentImage = new AtomicReference<>((ImageView) buttonSquare.getGraphic());
                if (buttonSquare.getGraphic().equals(emptySquareImageView)) {
                    // for when the mouse hovers over the square, a grey image of X or O to represent what could be if the user placed it here
                    // only if the tile is editable, the game is not finished and hovering is allowed
                    buttonSquare.setOnMouseEntered(e -> {
                        if (!finished && hover && findTile(buttonSquare).isEditable()) {
                            selected = (RadioButton) tg.getSelectedToggle();
                            if (selected != null) {
                                if (selected.getUserData().equals("X")) {
                                    ImageView xSquareGreyImageView = new ImageView(xSquareGreyImage);
                                    xSquareGreyImageView.setFitHeight(sizeOfSquare);
                                    xSquareGreyImageView.setFitWidth(sizeOfSquare);
                                    buttonSquare.setGraphic(xSquareGreyImageView);
                                } else if (selected.getUserData().equals("O")) {
                                    ImageView oSquareGreyImageView = new ImageView(oSquareGreyImage);
                                    oSquareGreyImageView.setFitHeight(sizeOfSquare);
                                    oSquareGreyImageView.setFitWidth(sizeOfSquare);
                                    buttonSquare.setGraphic(oSquareGreyImageView);
                                }
                            }
                        }
                    });
                    // when the mouse hovers out of the square, we want it to reset to the image it was before
                    // sometimes, it will set no images to it at all and there will just be a blank button
                    // this is because the mouse moves over to another square faster than the code can process
                    // it's rare though
                    buttonSquare.setOnMouseExited(e -> {
                        if (!finished && hover && findTile(buttonSquare).isEditable()) {
                            for (int i = 0; i < mg.getSize(); i++) {
                                for (int j = 0; j < mg.getSize(); j++) {
                                    if (buttons[i][j] == buttonSquare) {
                                        buttonSquare.setGraphic(currentImage.get());
                                    }
                                }
                            }
                        }
                    });
                }
                // when the user clicks on a square, it calls the mark method to mark the grid
                buttonSquare.setOnMouseClicked(e -> {
                    if (!finished && findTile(buttonSquare).isEditable()) {
                        if (toolX.isSelected() || toolO.isSelected() || toolErase.isSelected()) {
                            try {
                                mark(buttonSquare, mg.getSize(), "blue");
                                currentImage.set((ImageView) buttonSquare.getGraphic());
                            } catch (FileNotFoundException | InterruptedException fileNotFoundException) {
                                fileNotFoundException.printStackTrace();
                            }
                        }
                    }
                });
                emptySquares[y][x].getChildren().add(buttonSquare);
                grid.add(emptySquares[y][x], x, y);
            }
        }

        // rectangles for design purposes
        final Rectangle blackBorderFirst = new Rectangle(0, 0, Color.TRANSPARENT);
        blackBorderFirst.setStroke(Color.BLACK);
        blackBorderFirst.setManaged(false);
        blackBorderFirst.setLayoutX(15);
        blackBorderFirst.setLayoutY(-12);
        blackBorderFirst.setWidth(257);
        blackBorderFirst.setHeight(53);
        blackBorderFirst.setArcWidth(15.0);
        blackBorderFirst.setArcHeight(15.0);

        final Rectangle blackBorderSecond = new Rectangle(0, 0, Color.TRANSPARENT);
        blackBorderSecond.setStroke(Color.BLACK);
        blackBorderSecond.setManaged(false);
        blackBorderSecond.setLayoutX(15);
        blackBorderSecond.setLayoutY(62);
        blackBorderSecond.setWidth(257);
        blackBorderSecond.setHeight(254);
        blackBorderSecond.setArcWidth(15.0);
        blackBorderSecond.setArcHeight(15.0);

        grid.setAlignment(Pos.BOTTOM_CENTER);
        VBox centerBox = new VBox(blackBorderFirst, blackBorderSecond, textBox, grid);
        centerBox.setSpacing(20);
        pane.setCenter(centerBox);
        BorderPane.setMargin(centerBox, new Insets(20, 0, 20, 0));

        Button backToMenu = new Button("Go back to the menu");
        backToMenu.setOnAction(event -> {
            scene.setCursor(Cursor.DEFAULT);
            clickSound.play();
            try {
                createMenu();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        pane.setBottom(backToMenu);
    }

    /**
     * Method that solves the currently played MarupekeGrid by calling the solve method of the MarupekeGrid class on a
     * clone of the current MarupekeGrid. Then, every blank tile of the original MarupekeGrid will be marked with the
     * corresponding states of the cloned solved MarupekeGrid. If hint it true, only one blank tile of the original
     * MarupekeGrid will be solved.
     *
     * @param hint true if we only want one tile to be solved, not the entire grid
     * @throws InterruptedException
     */
    private void solve(boolean hint) throws InterruptedException {
        if (!mg.isPuzzleComplete()) { // if the puzzle is already complete, we don't want to try and solve it
            if (mg.getSize() > 7) {
                Alert a = new Alert(Alert.AlertType.ERROR); // in case the size is too large to be solved
                a.setTitle("Solving & Hint Unavailable");
                a.setHeaderText("Solving & Hint Unavailable!");
                a.setContentText("Grids of size about 7 won't be solved or hinted because it takes too long for the algorithm.\n" +
                        "Sorry :(");
                a.show();
                return;
            }

            MarupekeGrid unsolvedGrid = mg;
            MarupekeGrid solvedGrid = mg.clone();

            solvedGrid.solvePuzzle(0, 0);
            if (!solvedGrid.isPuzzleComplete()) { // if the first solving didn't work, this time, clear the grid of all the states on the editable tiles and try solving it again
                unsolvedGrid.clear();
                solvedGrid = unsolvedGrid.clone();
                solvedGrid.solvePuzzle(0, 0);
            }

            if (!solvedGrid.isPuzzleComplete()) {
                Alert a = new Alert(Alert.AlertType.ERROR); // if the solving still didn't work we warn the user
                a.setTitle("Solving Error");
                a.setHeaderText("Couldn't solve!");
                a.setContentText("This grid couldn't be solved!\n" +
                        "This could be because you built or imported an impossible grid.\n" +
                        "It could also be a bug in the program...please save this grid if that's the case for future reference.\n" +
                        "You can go back to the menu now.");
                a.show();
            }

            if (!hint) hover = false; // we stop the hovering as the grid is "finished" and we don't want to allow more edits

            ImageView imageView;
            // here, we copy each state of the cloned solved grid to the original grid
            // every mark will be green to show what was solved
            for (int i = 0; i < solvedGrid.getSize(); i++) {
                for (int j = 0; j < solvedGrid.getSize(); j++) {
                    if (unsolvedGrid.getState(j, i) == MarupekeTile.State.BLANK) {
                        Button buttonToSet = buttons[i][j];
                        if (solvedGrid.getState(j, i) == MarupekeTile.State.X) {
                            imageView = new ImageView(xSquareGreenImage);
                            imageView.setFitHeight(sizeOfSquare);
                            imageView.setFitWidth(sizeOfSquare);
                            buttonToSet.setGraphic(imageView);
                            mg.markX(j, i);
                            findTile(buttonToSet).setEditable(false);
                            if (mg.isPuzzleComplete()) gameFinished();
                            if (hint) return;
                        }
                        if (solvedGrid.getState(j, i) == MarupekeTile.State.O) {
                            imageView = new ImageView(oSquareGreenImage);
                            imageView.setFitHeight(sizeOfSquare);
                            imageView.setFitWidth(sizeOfSquare);
                            buttonToSet.setGraphic(imageView);
                            mg.markO(j, i);
                            findTile(buttonToSet).setEditable(false);
                            if (mg.isPuzzleComplete()) gameFinished();
                            if (hint) return;
                        }
                    }
                }
            }
        }
    }

    /**
     * First finds the current button's corresponding tile in the MarupekeGrid and modifies it according to the currently
     * selected tool. If its erase, the image of the button will change to blank if its editable, otherwise nothing
     * will happen. If its X or O, if the marking is legal, the image of the button will change to blue X or blue O or
     * black X or black O, the color depends on if the user is playing the game or building a grid. If the marking is
     * not legal, the square will change to a red X or red O and blink before returning to a blank image. The illegality
     * text above the grid will change accordingly to indicate what illegality the user made.
     *
     * @param buttonSquare
     * @param size
     * @param color
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    private void mark(Button buttonSquare, int size, String color) throws FileNotFoundException, InterruptedException {
        selected = (RadioButton) tg.getSelectedToggle();
        MarupekeTile.State state = null;
        // how we interact with the MarupekeGrid class depends on the current tool selected
        if (selected.getUserData().equals("Erase")) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (buttons[i][j] == buttonSquare) {
                        mg.unmark(i, j);
                        ImageView emptySquareImageView = new ImageView(emptySquareImage);
                        emptySquareImageView.setFitHeight(sizeOfSquare);
                        emptySquareImageView.setFitWidth(sizeOfSquare);
                        buttonSquare.setGraphic(emptySquareImageView);
                    }
                }
            }
        } else {
            if (selected.getUserData().equals("X")) state = MarupekeTile.State.X;
            if (selected.getUserData().equals("O")) state = MarupekeTile.State.O;
            if (selected.getUserData().equals("Solid")) state = MarupekeTile.State.SOLID;
            ImageView squareImageView = new ImageView(emptySquareImage);
            if (state == MarupekeTile.State.X) {
                // the color depends on if we're building a grid or playing
                if (color.equals("blue")) {
                    squareImageView = new ImageView(xSquareBlueImage);
                } else if (color.equals("black")) {
                    squareImageView = new ImageView(xSquareImage);
                }
            } else if (state == MarupekeTile.State.O) {
                if (color.equals("blue")) {
                    squareImageView = new ImageView(oSquareBlueImage);
                } else if (color.equals("black")) {
                    squareImageView = new ImageView(oSquareImage);
                }
            } else if (state == MarupekeTile.State.SOLID) {
                squareImageView = new ImageView(solidSquareImage);
            }
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (buttons[i][j] == buttonSquare) {
                        // displaying the illegalities
                        illegalitiesText.setText("Illegalities: " + getIllegalities(state, i, j));
                        if (mark(state, i, j)) {
                            markLegalSound.play();
                            squareImageView.setFitHeight(sizeOfSquare);
                            squareImageView.setFitWidth(sizeOfSquare);
                            buttonSquare.setGraphic(squareImageView);
                            if (color.equals("black")) {
                                // if a solid square is placed, it should be set back to editable to be erased in case
                                // the user wants to remove it when building the grid
                                findTile(buttonSquare).setEditable(true);
                            }
                        } else {
                            // place the red squareImage and play animation before reverting to blank square
                            // this is played when the user makes an illegal move
                            markIllegalSound.play();
                            if (squareImageView.getImage().equals(xSquareBlueImage) || squareImageView.getImage().equals(xSquareImage))
                                squareImageView = new ImageView(xSquareRedImage);
                            else squareImageView = new ImageView(oSquareRedImage);
                            squareImageView.setFitHeight(sizeOfSquare);
                            squareImageView.setFitWidth(sizeOfSquare);
                            buttonSquare.setGraphic(squareImageView);
                            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), squareImageView);
                            fadeOut.setFromValue(10);
                            fadeOut.setToValue(0);
                            fadeOut.setCycleCount(2);
                            fadeOut.setAutoReverse(false);
                            fadeOut.play();
                            fadeOut.setOnFinished((finish) -> {
                                ImageView imageView = new ImageView(emptySquareImage);
                                imageView.setFitHeight(sizeOfSquare);
                                imageView.setFitWidth(sizeOfSquare);
                                buttonSquare.setGraphic(imageView);
                                FadeTransition fadeIn = new FadeTransition(Duration.millis(10), imageView);
                                fadeIn.setFromValue(0);
                                fadeIn.setToValue(10);
                                fadeIn.play();
                            });
                        }
                    }
                }
            }
            if (mg.isPuzzleComplete()) gameFinished();
        }
    }

    /**
     * This is the mark method that is called to mark the MarupekeGrid. It returns true if the marking is legal and only
     * marks according to the state that is inputted. If the marking is not legal, it unmarks the tile and returns false.
     *
     * @param state the state in which to mark the tile
     * @param x     the x-coordinate of the tile
     * @param y     the y-coordinate of the tile
     * @return true if the marking is legal, false if it isn't
     */
    public boolean mark(MarupekeTile.State state, int x, int y) {
        if (state == MarupekeTile.State.X) mg.markX(y, x);
        else if (state == MarupekeTile.State.O) mg.markO(y, x);
        else if (state == MarupekeTile.State.SOLID) mg.setSolid(y, x);
        if (!(mg.isLegalGrid())) {
            mg.unmark(y, x);
            return false;
        }
        return true;
    }

    /**
     * This method is called once the game is completed. It checks if the grid is completed and if it is, it makes
     * each square of the grid spin, it plays a trumpet winning sound and displays the "save score and play again" scene
     * that consists of a text field to input the player's name and a play again that makes the user play a new random
     * grid of the same size.
     */
    private void gameFinished() {
        if (mg.isPuzzleComplete()) {
            scene.setCursor(Cursor.DEFAULT);
            winSound.play();
            finished = true;
            timer.stop();
            // make a pop up window to ask for name to save score
            // if this is an imported grid, no scores will be saved
            for (int i = 0; i < mg.getSize(); i++) {
                for (int j = 0; j < mg.getSize(); j++) {
                    Button button = buttons[i][j];
                    RotateTransition rt = new RotateTransition(Duration.millis(1500), button);
                    rt.setByAngle(180);
                    rt.setCycleCount(1);
                    rt.setAutoReverse(false);
                    rt.play();
                    rt.setOnFinished(event -> {

                        VBox vbox = new VBox();
                        vbox.setSpacing(10);

                        try {
                            ImageView imageView = new ImageView(new Image(new FileInputStream("images/congratulations.png")));
                            imageView.setFitWidth(210);
                            imageView.setPreserveRatio(true);
                            vbox.getChildren().add(imageView);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        Text youFinish = new Text("You finisehd the grid in");
                        youFinish.setFont(Font.font("Verdana", 12));
                        if (currentTheme.equals("black")) youFinish.setFill(Color.WHITE);
                        else if (currentTheme.equals("white") || currentTheme.equals("blue"))
                            youFinish.setFill(Color.BLACK);

                        Text youFinish2 = new Text("seconds");
                        youFinish2.setFont(Font.font("Verdana", 12));
                        if (currentTheme.equals("black")) youFinish2.setFill(Color.WHITE);
                        else if (currentTheme.equals("white") || currentTheme.equals("blue"))
                            youFinish2.setFill(Color.BLACK);

                        HBox hbox = new HBox(youFinish, display, youFinish2);
                        hbox.setSpacing(5);

                        Text enterName = new Text("Enter your name to save your score");
                        enterName.setFont(Font.font("Verdana", 12));
                        if (currentTheme.equals("black")) enterName.setFill(Color.WHITE);
                        else if (currentTheme.equals("white") || currentTheme.equals("blue"))
                            enterName.setFill(Color.BLACK);
                        TextField textField = new TextField();
                        textField.setMaxWidth(100);

                        Button saveScore = new Button("Save score");
                        saveScore.setAlignment(Pos.BOTTOM_RIGHT);
                        saveScore.setOnMouseClicked(e -> {
                            clickSound.play();
                            scores.addItem(textField.getText(), Integer.parseInt(display.getText()), mg.getSize(), mg.getDifficulty());
                            try {
                                scores.saveItemList();
                                Alert savedScoreAlert = new Alert(Alert.AlertType.INFORMATION);
                                savedScoreAlert.setTitle("Score saved");
                                savedScoreAlert.setHeaderText("Score saved");
                                savedScoreAlert.setContentText("Your score was successfully saved!\n" +
                                        "You can check it out in the high score section from the menu.");
                                savedScoreAlert.show();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        });

                        Button playAgain = new Button("Play Again");
                        playAgain.setTooltip(new Tooltip("Play again with a random grid of the same size."));
                        playAgain.setOnMouseClicked(e -> {
                            clickSound.play();
                            if (!optionsSize.getSelectionModel().isEmpty()) {
                                if ((int) optionsSize.getValue() > 7) {
                                    Alert a = new Alert(Alert.AlertType.ERROR);
                                    a.setTitle("Warning");
                                    a.setHeaderText("This size of grid isn't guaranteed to be solvable!");
                                    a.setContentText("Grids of size 8, 9 and 10 won't be check for solvability.\n" +
                                            "This is because the algorithm that checks for it takes a while for these sizes.\n" +
                                            "Therefore, you might not be able to solve that grid, but you're welcome to try.\n");
                                    a.show();
                                }
                                if (easy.isSelected()) {
                                    try {
                                        playGame(Difficulty.EASY, false, null);
                                    } catch (FileNotFoundException | IllegalGridSize f) {
                                        f.printStackTrace();
                                    }
                                } else if (medium.isSelected()) {
                                    try {
                                        playGame(Difficulty.MEDIUM, false, null);
                                    } catch (FileNotFoundException | IllegalGridSize f) {
                                        f.printStackTrace();
                                    }
                                } else if (hard.isSelected()) {
                                    try {
                                        playGame(Difficulty.HARD, false, null);
                                    } catch (FileNotFoundException | IllegalGridSize f) {
                                        f.printStackTrace();
                                    }
                                }
                            }
                        });

                        vbox.getChildren().addAll(hbox, enterName, textField, saveScore, playAgain);
                        pane.setCenter(vbox);
                    });

                }
            }
            Button backToMenu = new Button("Go back to the menu");
            backToMenu.setOnAction(event -> {
                clickSound.play();
                try {
                    createMenu();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
            pane.setBottom(backToMenu);
        }
    }

    /**
     * This method is used to find the tile that corresponds to the inputted square.
     *
     * @param buttonSquare the square we want to find the tile of
     * @return MarupekeTile that is the square the button references
     */
    private MarupekeTile findTile(Button buttonSquare) {
        // loop through the buttons array looking for the one that is equal to the inputted button
        // return the corresponding tile with those coordinates
        for (int i = 0; i < mg.getSize(); i++) {
            for (int j = 0; j < mg.getSize(); j++) {
                if (buttons[i][j] == buttonSquare) {
                    return mg.getTiles()[i][j];
                }
            }
        }

        return null;
    }

    /**
     * This method returns the illegalities that are currently encountered in the grid in a readable format.
     * If there are no illegalities produced with the markings we are making, "none for now" is returned to indicated
     * that there are no illegalities right now.
     *
     * @param state the state we want to mark the tile with
     * @param x     the x-coordinate of the tile we want to mark
     * @param y     the y-coordinate of the tile we want to mark
     * @return String of the illegalities in a user-readable format
     */
    public String getIllegalities(MarupekeTile.State state, int x, int y) {
        // mark the grid with it, get the illegality it produces and unmark it
        if (state == MarupekeTile.State.X) mg.markX(y, x);
        else if (state == MarupekeTile.State.O) mg.markO(y, x);
        if (!(mg.isLegalGrid())) {
            // make it readable by removing the extra spaces and other characters that prevent readability
            String illegalities = mg.illegalitiesInGrid().toString().trim();
            String returnedIllegalities = illegalities.replaceAll("\\[", "").replaceAll("\\]", "");
            return returnedIllegalities;
        }
        mg.unmark(y, x);
        return "none for now";
    }

}