package Logic;

import java.util.Objects;

public class Cell {
    public int x;

    public int y;

    public Value val;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return x == cell.x &&
                y == cell.y &&
                val == cell.val;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, val);
    }

    public Cell(int y, int x, Value val) {
        this.x = x;
        this.y = y;
        this.val = val;
    }

    public boolean isEmpty() {
        return val == Value.EMPTY;
    }

    public boolean isRock() {
        return val == Value.ROCK;
    }

    public boolean isLambda() {
        return val == Value.LAMBDA;
    }

    public boolean isWall() {
        return val == Value.WALL;
    }

    public boolean isLift() {
        return val == Value.LIFT;
    }

    public boolean isRobot() {
        return val == Value.ROBOT;
    }

}