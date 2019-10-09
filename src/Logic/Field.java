package Logic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Field {
    private Cell[][] cells;

    public Cell robot;

    public Cell lift;

    public int countOfLambda;

    public boolean isLiftOpen;


    public Field(Cell[][] cells, Cell robot, Cell lift, int countOfLambda, boolean isLiftOpen) {
        this.cells = cells;
        this.robot = robot;
        this.lift = lift;
        this.isLiftOpen = isLiftOpen;
        this.countOfLambda = countOfLambda;
    }

    public Cell getCell(int x, int y) {
        return cells[y][x];
    }

    public int getXsize() {
        return cells[0].length;
    }

    public int getYsize() {
        return cells.length;
    }

    public void setCell(int x, int y, Value val) {
        cells[y][x] = new Cell(y, x, val);
    }

    public void setCell(Cell cell, Value newValue) {
        cells[cell.y][cell.x] = new Cell(cell.y, cell.x, newValue);
    }

    public void setCell(Cell cell) {
        cells[cell.y][cell.x] = cell;
    }

    public Set<Cell> getPossibleSteps(Cell curCell) {
        Set<Cell> steps = new HashSet<>();
        if (isItPossibleStep(curCell, upCell(curCell))) steps.add(upCell(curCell));
        if (isItPossibleStep(curCell, downCell(curCell))) steps.add(downCell(curCell));
        if (isItPossibleStep(curCell, rightCell(curCell))) steps.add(rightCell(curCell));
        if (isItPossibleStep(curCell, leftCell(curCell))) steps.add(leftCell(curCell));
        steps.add(curCell);
        return steps;
    }

    public boolean isItPossibleStep(Cell from, Cell to) {
        return (abs(from.x - to.x + from.y - to.y) == 1 && (!to.isLift() || isLiftOpen) && !to.isWall()
                && (!to.isRock() ||
                (from.x + 1 == to.x && rightCell(to).isEmpty()) || (from.x - 1 == to.x && leftCell(to).isEmpty())));
    }

    public void robotStep(Cell to) {
        if (to.isLambda()) {
            countOfLambda--;
        }
        if (to.isRock()) {
            if (to.x > robot.x) {
                cells[to.y][to.x + 1] = new Cell(to.y, to.x + 1, Value.ROCK);
            } else {
                cells[to.y][to.x - 1] = new Cell(to.y, to.x - 1, Value.ROCK);
            }
        }
        setCell(robot, Value.EMPTY);
        setCell(to, Value.ROBOT);
        robot = new Cell(to.y, to.x, Value.ROBOT);
    }

    public boolean isLambdaAccessible(Cell lambda) {
        return true;
    }

    public void simulation() {
        for (int i = 0; i < getYsize(); i++) {
            for (int j = 0; j < getXsize(); j++) {
                if (getCell(j, i).isRock()) {
                    Cell rock = getCell(j, i);
                    if (getCell(rock.x, rock.y - 1).isEmpty()) {
                        setCell(rock.x, rock.y, Value.EMPTY);
                        rock.y = rock.y - 1;
                        setCell(rock);
                        break;
                    }
                    if (getCell(rock.x, rock.y - 1).isRock()) {
                        if (getCell(rock.x + 1, rock.y).isEmpty() && getCell(rock.x + 1, rock.y - 1).isEmpty()) {
                            setCell(rock.x, rock.y, Value.EMPTY);
                            rock.y = rock.y - 1;
                            rock.x = rock.x + 1;
                            setCell(rock);
                            break;
                        }
                        if (getCell(rock.x - 1, rock.y).isEmpty() && getCell(rock.x - 1, rock.y - 1).isEmpty()) {
                            setCell(rock.x, rock.y, Value.EMPTY);
                            rock.y = rock.y - 1;
                            rock.x = rock.x - 1;
                            setCell(rock);
                            break;
                        }
                    }
                    if (getCell(rock.x, rock.y - 1).isLambda() &&
                            getCell(rock.x + 1, rock.y).isEmpty() && getCell(rock.x + 1, rock.y - 1).isEmpty()) {
                        setCell(rock.x, rock.y, Value.EMPTY);
                        rock.y = rock.y - 1;
                        rock.x = rock.x + 1;
                        setCell(rock);
                        break;
                    }
                }
            }
        }
        if (countOfLambda == 0) isLiftOpen = true;
    }

    public void transitionsSimulation(Map<Cell, Cell> transitions) {
        for (int i = 0; i < getYsize(); i++) {
            for (int j = 0; j < getXsize(); j++) {
                if (getCell(j, i).isRock()) {
                    Cell rock = getCell(j, i);
                    if (getCell(rock.x, rock.y - 1).isEmpty()) {
                        transitions.put(rock, new Cell(rock.y, rock.x, Value.EMPTY));
                        transitions.put(new Cell(rock.y - 1, rock.x, Value.EMPTY), new Cell(rock.y - 1, rock.x, Value.ROCK));
                        setCell(rock.x, rock.y, Value.EMPTY);
                        rock.y = rock.y - 1;
                        setCell(rock);
                        break;
                    }
                    if (getCell(rock.x, rock.y - 1).isRock()) {
                        if (getCell(rock.x + 1, rock.y).isEmpty() && getCell(rock.x + 1, rock.y - 1).isEmpty()) {
                            transitions.put(rock, new Cell(rock.y, rock.x, Value.EMPTY));
                            transitions.put(new Cell(rock.y - 1, rock.x + 1, Value.EMPTY), new Cell(rock.y - 1, rock.x + 1, Value.ROCK));
                            setCell(rock.x, rock.y, Value.EMPTY);
                            rock.y = rock.y - 1;
                            rock.x = rock.x + 1;
                            setCell(rock);
                            break;
                        }
                        if (getCell(rock.x - 1, rock.y).isEmpty() && getCell(rock.x - 1, rock.y - 1).isEmpty()) {
                            setCell(rock.x, rock.y, Value.EMPTY);
                            transitions.put(rock, new Cell(rock.y, rock.x, Value.EMPTY));
                            transitions.put(new Cell(rock.y - 1, rock.x - 1, Value.EMPTY), new Cell(rock.y - 1, rock.x - 1, Value.ROCK));
                            rock.y = rock.y - 1;
                            rock.x = rock.x - 1;
                            setCell(rock);
                            break;
                        }
                    }
                    if (getCell(rock.x, rock.y - 1).isLambda() &&
                            getCell(rock.x + 1, rock.y).isEmpty() && getCell(rock.x + 1, rock.y - 1).isEmpty()) {
                        setCell(rock.x, rock.y, Value.EMPTY);
                        transitions.put(rock, new Cell(rock.y, rock.x, Value.EMPTY));
                        transitions.put(new Cell(rock.y - 1, rock.x + 1, Value.EMPTY), new Cell(rock.y - 1, rock.x + 1, Value.ROCK));
                        rock.y = rock.y - 1;
                        rock.x = rock.x + 1;
                        setCell(rock);
                        break;
                    }
                }
            }
        }
        if (countOfLambda == 0) isLiftOpen = true;
    }

    public void rollBack(Map<Cell, Cell> trasitions) {
        for (Map.Entry<Cell, Cell> entry : trasitions.entrySet()) {
            if (!entry.getKey().isRobot()) {
                setCell(entry.getKey());
            } else {
                setCell(entry.getKey());
                robot = entry.getKey();
            }
        }
    }

    public void rollForward(Map<Cell, Cell> trasitions) {
        for (Map.Entry<Cell, Cell> entry : trasitions.entrySet()) {
            if (!entry.getValue().isRobot()) {
                setCell(entry.getValue());
            } else {
                setCell(entry.getValue());
                robot = entry.getValue();
            }
        }
    }

    public Cell upCell(Cell curCell) {
        return cells[curCell.y + 1][curCell.x];
    }

    public Cell downCell(Cell curCell) {
        return cells[curCell.y - 1][curCell.x];
    }

    public Cell rightCell(Cell curCell) {
        return cells[curCell.y][curCell.x + 1];
    }

    public Cell leftCell(Cell curCell) {
        return cells[curCell.y][curCell.x - 1];
    }
}
