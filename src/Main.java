import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;

import javafx.scene.paint.Color;

public class Main extends Application {

    private static int bombPercent = 10;
    private static int gridSize = 10;
    private static Tile[][] grid;
    private static Stage main;
    private static VBox vbox = new VBox();

    static int numBombs, foundBombs;

    private static int secondsPassed;

    public static Timer timer;

    static Image mine = new Image("mine.png");

    static boolean sound = true;

    @Override
    public void start(Stage stage) {

        grid = new Tile[gridSize][gridSize];

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                secondsPassed++;
            }
        };

        timer = new Timer();

        timer.scheduleAtFixedRate(task, 1000, 1000);

        main = stage;

        main.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        main.getIcons().add(mine);
        main.setTitle("Minesweeper - By Robert Sanders");

        MenuBar menuBar = new MenuBar();

        Menu menuFile = new Menu("File");
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> {
            Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION,
                    "Created by Robert Sanders. \n" + "sanry030@mymail.unisa.edu.au \n" + "v 1.1.0", ButtonType.CLOSE);
            aboutAlert.setTitle("About");
            aboutAlert.setHeaderText("Minesweeper");
            aboutAlert.showAndWait();
        });
        MenuItem help = new MenuItem("Help");
        help.setOnAction(e -> {
            Alert helpAlert = new Alert(Alert.AlertType.INFORMATION,
                    "The aim of minesweeper is to identify all the sqaures which contain mines.\n\n"
                            + "Left click on a square to reveal a number. This number indicates how many of the adjacent squares contain mines. By using these numbers you can deduce which sqaures contain mines. \n\n"
                            + "Right click on a square to mark it as containing a mine. You can right click the sqaure again to unmark it if you made a mistake.\n\n"
                            + "After all mines have successfully been marked the game is over and you win! Be careful though. Left clicking a square with a mine will result in a game over.");
            helpAlert.setTitle("Help");
            helpAlert.setHeaderText("How to play.");
            helpAlert.showAndWait();
        });
        MenuItem quit = new MenuItem("Quit");
        quit.setOnAction(e -> {
            Platform.exit();
        });
        menuFile.getItems().addAll(about, help, quit);

        Menu menuSize = new Menu("Size");
        MenuItem ten = new MenuItem("10x10");
        ten.setOnAction(e -> {
            gridSize = 10;
            reload();
        });
        MenuItem fifteen = new MenuItem("15x15");
        fifteen.setOnAction(e -> {
            gridSize = 15;
            reload();
        });
        MenuItem twenty = new MenuItem("20x20");
        twenty.setOnAction(e -> {
            gridSize = 20;
            reload();
        });
        menuSize.getItems().addAll(ten, fifteen, twenty);

        Menu menuDifficulty = new Menu("Difficulty");
        MenuItem easy = new MenuItem("Easy - 10% Bombs");
        easy.setOnAction(e -> {
            bombPercent = 10;
            reload();
        });
        MenuItem medium = new MenuItem("Medium - 15% Bombs");
        medium.setOnAction(e -> {
            bombPercent = 15;
            reload();
        });
        MenuItem hard = new MenuItem("Hard - 20% Bombs");
        hard.setOnAction(e -> {
            bombPercent = 20;
            reload();
        });
        menuDifficulty.getItems().addAll(easy, medium, hard);

        Menu menuSound = new Menu("Sound");
        RadioMenuItem soundOn = new RadioMenuItem("On");
        soundOn.setOnAction(e -> {
            sound = true;
        });
        RadioMenuItem soundOff = new RadioMenuItem("Off");
        soundOff.setOnAction(e -> {
            sound = false;
        });
        ToggleGroup soundToggle = new ToggleGroup();
        soundToggle.getToggles().addAll(soundOn, soundOff);
        soundToggle.selectToggle(soundOn);

        menuSound.getItems().addAll(soundOn, soundOff);

        menuBar.getMenus().addAll(menuFile, menuSize, menuDifficulty, menuSound);

        vbox.getChildren().addAll(menuBar, createContent());

        Scene scene = new Scene(vbox);

        scene.getStylesheets().add("style.css");
        main.setScene(scene);
        main.setResizable(false);
        main.sizeToScene();
        main.show();
    }

    private static void reload() {

        grid = new Tile[gridSize][gridSize];

        secondsPassed = 0;

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                secondsPassed++;
            };
        };
        timer.cancel();
        timer = new Timer();
        timer.schedule(task, 1000, 1000);

        vbox.getChildren().remove(1);
        vbox.getChildren().add(createContent());
        main.sizeToScene();
    }

    /**
     * Create all the tiles and assign bombs accordingly
     *
     * @return root - The playing field
     */
    private static Parent createContent() {

        // Reset to zero in case of new game.
        numBombs = 0;
        foundBombs = 0;

        Pane root = new Pane();
        root.setPrefSize(gridSize * 35, gridSize * 35);

        // Create all tile and buttons on the grid
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {

                Tile tile = new Tile(x, y, false);

                grid[x][y] = tile;
                root.getChildren().add(tile);
            }
        }

        // Assign bombs randomly to tiles.
        for(int i = 0; i < gridSize*gridSize / bombPercent; i++){

            Random rand = new Random();

            int x = rand.nextInt(gridSize);
            int y = rand.nextInt(gridSize);

            if(grid[x][y].hasBomb){
                if (i == 0) {
                    i = 0;
                } else {
                    i--;
                }
            }
            else{
                grid[x][y].hasBomb = true;
                numBombs++;
            }
        }

        // Add values to the tiles and set their colours accordingly.
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {

                int numNeighboursBomb = 0;

                ArrayList<Tile> neighbours = new ArrayList<Tile>();

                int[] neighboursLocs = new int[] { -1, -1, -1, 0, -1, 1, 0, -1, 0, 1, 1, -1, 1, 0, 1, 1 };

                for (int i = 0; i < neighboursLocs.length; i++) {
                    int dx = neighboursLocs[i];
                    int dy = neighboursLocs[++i];

                    int newX = x + dx;
                    int newY = y + dy;

                    if (newX >= 0 && newX < gridSize && newY >= 0 && newY < gridSize) {
                        neighbours.add(grid[newX][newY]);
                        if (grid[newX][newY].hasBomb) {
                            numNeighboursBomb++;
                        }
                    }
                }

                grid[x][y].numBombs = numNeighboursBomb;
                grid[x][y].neighbours = neighbours;

                Color[] colors = { null, Color.BLUE, Color.GREEN, Color.RED, Color.DARKBLUE, Color.DARKRED, Color.CYAN,
                        Color.BLACK, Color.DARKGRAY };

                grid[x][y].color = colors[grid[x][y].numBombs];

            }
        }
        return root;
    }

    /**
     * Runs when a player left clicks a bomb. Reveals all bomb tiles and displays
     * message. Calls to reload the game.
     */
    public static void gameOver() {
        if (sound) {
            AudioClip explosion = new AudioClip(Main.class.getResource("explosion.wav").toString());
            explosion.play();
        }
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                if (grid[x][y].hasBomb) {
                    grid[x][y].btn.setGraphic(new ImageView(mine));
                    grid[x][y].btn.setDisable(true);
                }
            }
        }

        Alert gameOver = new Alert(AlertType.INFORMATION);
        gameOver.setTitle("Game Over!");
        gameOver.setGraphic(new ImageView(mine));
        gameOver.setHeaderText("Bomb Exploded!");
        gameOver.setContentText(
                "Oh no! You clicked on a bomb and caused all the bombs to explode! Better luck next time.");
        gameOver.showAndWait();

        reload();

    }

    /**
     * Player win. Displays message. Calls to reload the game.
     */
    public static void win() {

        if (sound) {
            AudioClip winSound = new AudioClip(Main.class.getResource("win.wav").toString());
            winSound.play();
        }
        Alert win = new Alert(AlertType.CONFIRMATION);
        win.setTitle("Win!");
        win.setGraphic(new ImageView(Tile.flag));
        win.setHeaderText("Congratulations!");
        win.setContentText("You found all the bombs in " + secondsPassed + " seconds.");
        win.showAndWait();
        reload();
    }

    public static void main(String[] args) {
        launch(args);
    }

}