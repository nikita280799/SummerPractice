package Logic;

import javax.swing.*;
import java.awt.*;

public enum Value {
    ROBOT(new ImageIcon("files/robot.png").getImage()),
    ROCK(new ImageIcon("files/rock.png").getImage()),
    WALL(new ImageIcon("files/wall.png").getImage()),
    EARTH(new ImageIcon("files/earth.png").getImage()),
    LAMBDA(new ImageIcon("files/lambda.png").getImage()),
    LIFT(new ImageIcon("files/lift.png").getImage()),
    EMPTY(new ImageIcon("files/empty.png").getImage());

    private final Image image;

    Value(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return this.image;
    }

    public static Value getValue(char symbol) {
        switch (symbol) {
            case 'R': return ROBOT;
            case '*': return ROCK;
            case 'L': return LIFT;
            case '.': return EARTH;
            case '#': return WALL;
            case '\\': return LAMBDA;
            case ' ': return EMPTY;
        }
        return null;
    }
}