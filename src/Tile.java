import javafx.scene.paint.Color;

import java.util.ArrayList;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;

class Tile extends StackPane {

    Button btn = new Button();
    int x, y = 0;
    boolean hasBomb;
    int numBombs = 0;
    Color color = null;
    boolean flagged = false;
    ArrayList<Tile> neighbours = new ArrayList<Tile>();
    boolean active = true;

    static Image flag = new Image("flag.png");

    public Tile(int x, int y, boolean hasBomb) {
        this.x = x;
        this.y = y;
        this.hasBomb = hasBomb;

        if (hasBomb) {
            Main.numBombs++;
        }

        btn.setMinHeight(35);
        btn.setMinWidth(35);

        btn.setOnMouseClicked(e -> {
            onClick(e);
        });

        getChildren().addAll(btn);

        setTranslateX(x * 35);
        setTranslateY(y * 35);

    }

    private void onClick(MouseEvent e) {

        if (Main.sound) {
            AudioClip click = new AudioClip(Main.class.getResource("click.wav").toString());
            click.play();
        }

        // Left Click
        if (e.getButton() == MouseButton.PRIMARY) {
            if(!flagged) {

                btn.setBackground(null);
                btn.setDisable(true);
                active = false;

                if (hasBomb) {
                    Main.gameOver();
                } else {
                    // Blank
                    if (this.numBombs == 0) {
                        blankClick(this);
                    } else {
                        btn.setText(Integer.toString(numBombs));
                        btn.setTextFill(color);
                    }
                }
            }
        }
        // Right Click
        else {
            if (!flagged) {
                flagged = true;
                btn.setGraphic(new ImageView(flag));
                if (this.hasBomb) {
                    Main.foundBombs++;
                    if (Main.foundBombs == Main.numBombs) {
                        Main.win();
                    }
                }
            } else {
                if (hasBomb) {
                    Main.foundBombs--;
                }
                btn.setGraphic(null);
                flagged = false;
            }
        }
    }

    private void blankClick(Tile tile) {

        for (int i = 0; i < tile.neighbours.size(); i++) {
            if (tile.neighbours.get(i).active) {
                tile.neighbours.get(i).btn.setDisable(true);
                tile.neighbours.get(i).btn.setGraphic(null);
                tile.neighbours.get(i).btn.setText(Integer.toString(tile.neighbours.get(i).numBombs));
                tile.neighbours.get(i).btn.setTextFill(tile.neighbours.get(i).color);
                tile.neighbours.get(i).active = false;
                if (tile.neighbours.get(i).numBombs == 0) {
                    blankClick(tile.neighbours.get(i));
                }

            }
        }
        return;
    }

}