package Logic;

import java.util.*;

import static java.lang.Math.abs;

public class Field {
    private Cell[][] cells;

    public Cell robot;

    public Cell lift;

    public int countOfLambda;

    public boolean isLiftOpen = false;

    public boolean isRobotDead = false;

    Field(Cell[][] cells, Cell robot, Cell lift, int countOfLambda) {
        this.cells = cells;
        this.robot = robot;
        this.lift = lift;
        this.countOfLambda = countOfLambda;
    }

    public Field(Field otherField) {
        Cell[][] cells = new Cell[otherField.getYsize()][otherField.getXsize()];
        for (int i = 0; i < otherField.getYsize(); i++) {
            for (int j = 0; j < otherField.getXsize(); j++) {
                cells[i][j] = new Cell(i, j, otherField.cells[i][j].val);
            }
        }
        this.cells = cells;
        this.robot = new Cell(otherField.robot.y, otherField.robot.x, Value.ROBOT);
        this.lift = new Cell(otherField.lift.y, otherField.lift.x, Value.LIFT);
        this.countOfLambda = otherField.countOfLambda;
        this.isLiftOpen = false;
        this.isRobotDead = false;
    }

    public Cell getCell(int x, int y) {
        return cells[y][x];
    }

    public Value getValue(int x, int y) {
        return cells[y][x].val;
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
        if (curCell.y < getYsize() - 1 && isItPossibleStep(curCell, upCell(curCell))) steps.add(upCell(curCell));
        if (curCell.y > 0 && isItPossibleStep(curCell, downCell(curCell))) steps.add(downCell(curCell));
        if (curCell.x < getXsize() - 1 && isItPossibleStep(curCell, rightCell(curCell))) steps.add(rightCell(curCell));
        if (curCell.x > 0 &&isItPossibleStep(curCell, leftCell(curCell))) steps.add(leftCell(curCell));
        steps.add(curCell);
        return steps;
    }

    public boolean isItPossibleStep(Cell from, Cell to) {
        return (abs(from.x - to.x + from.y - to.y)
                == 1 && (!to.isLift() || isLiftOpen) && !to.isWall() && (!to.isRock() || (from.y == to.y &&
                ((from.x + 1 == to.x && rightCell(to).isEmpty()) || (from.x - 1 == to.x && leftCell(to).isEmpty())))));
    }

    public void robotStep(Cell to, Map<Cell, Cell> transitions, boolean isTransitionNeeded) {
        to = getCell(to.x, to.y);
        if (to.isLambda()) {
            countOfLambda--;
        }
        if (isTransitionNeeded) {
            addTransition(transitions, robot, new Cell(robot.y, robot.x, Value.EMPTY));
            addTransition(transitions, to, new Cell(to.y, to.x, Value.ROBOT));
        }
        if (to.isRock()) {
            if (to.x > robot.x) {
                cells[to.y][to.x + 1] = new Cell(to.y, to.x + 1, Value.ROCK);
                if (isTransitionNeeded) addTransition(transitions, new Cell(to.y, to.x + 1, Value.EMPTY),
                        new Cell(to.y, to.x + 1, Value.ROCK));
            } else {
                cells[to.y][to.x - 1] = new Cell(to.y, to.x - 1, Value.ROCK);
                if (isTransitionNeeded) addTransition(transitions, new Cell(to.y, to.x - 1, Value.EMPTY),
                        new Cell(to.y, to.x - 1, Value.ROCK));
            }
        }
        setCell(robot, Value.EMPTY);
        setCell(to, Value.ROBOT);
        robot = new Cell(to.y, to.x, Value.ROBOT);
    }

    public boolean isRockMove(Cell rock) {
        return (downCell(rock).isEmpty() || (rightCell(rock).isEmpty() && getCell(rock.x + 1, rock.y - 1).isEmpty())
                || (leftCell(rock).isEmpty() && getCell(rock.x + 1, rock.y - 1).isEmpty()) || (downCell(rock).isLambda() &&
                rightCell(rock).isEmpty() && getCell(rock.x + 1, rock.y - 1).isEmpty()));
    }

    public boolean isAnyRockMove() {
        for (int i = 0; i < getYsize(); i++) {
            for (int j = 0; j < getXsize(); j++) {
                if (getCell(j, i).isRock()) {
                    Cell rock = getCell(j, i);
                    if (downCell(rock).isEmpty() || (rightCell(rock).isEmpty() && getCell(rock.x + 1, rock.y - 1).isEmpty())
                            || (leftCell(rock).isEmpty() && getCell(rock.x + 1, rock.y - 1).isEmpty()) || (downCell(rock).isLambda() &&
                            rightCell(rock).isEmpty() && getCell(rock.x + 1, rock.y - 1).isEmpty())) return true;
                }
            }
        }
        return false;
    }

    public void simulation(Map<Cell, Cell> transitions, boolean isTransitionNeeded) {
        for (int i = 0; i < getYsize(); i++) {
            for (int j = 0; j < getXsize(); j++) {
                if (getCell(j, i).isRock()) {
                    Cell rock = getCell(j, i);
                    if (getCell(rock.x, rock.y - 1).isEmpty()) {
                        if (isTransitionNeeded) {
                            addTransition(transitions, new Cell(rock.y, rock.x, Value.ROCK), new Cell(rock.y, rock.x, Value.EMPTY));
                            addTransition(transitions, new Cell(rock.y - 1, rock.x, Value.EMPTY), new Cell(rock.y - 1, rock.x, Value.ROCK));
                        }
                        setCell(rock.x, rock.y, Value.EMPTY);
                        setCell(rock.x, rock.y - 1, Value.ROCK);
                        if (getCell(rock.x, rock.y - 2).isRobot()) isRobotDead = true;
                        break;
                    }
                    if (getCell(rock.x, rock.y - 1).isRock()) {
                        if (getCell(rock.x + 1, rock.y).isEmpty() && getCell(rock.x + 1, rock.y - 1).isEmpty()) {
                            if (isTransitionNeeded) {
                                addTransition(transitions, new Cell(rock.y, rock.x, Value.ROCK), new Cell(rock.y, rock.x, Value.EMPTY));
                                addTransition(transitions, new Cell(rock.y - 1, rock.x + 1, Value.EMPTY), new Cell(rock.y - 1, rock.x + 1, Value.ROCK));
                            }
                            setCell(rock.x, rock.y, Value.EMPTY);
                            setCell(rock.x + 1, rock.y - 1, Value.ROCK);
                            if (getCell(rock.x + 1, rock.y - 2).isRobot()) isRobotDead = true;
                            break;
                        }
                        if (getCell(rock.x - 1, rock.y).isEmpty() && getCell(rock.x - 1, rock.y - 1).isEmpty()) {
                            if (isTransitionNeeded) {
                                addTransition(transitions, new Cell(rock.y, rock.x, Value.ROCK), new Cell(rock.y, rock.x, Value.EMPTY));
                                addTransition(transitions, new Cell(rock.y - 1, rock.x - 1, Value.EMPTY), new Cell(rock.y - 1, rock.x - 1, Value.ROCK));
                            }
                            setCell(rock.x, rock.y, Value.EMPTY);
                            setCell(rock.x - 1, rock.y - 1, Value.ROCK);
                            if (getCell(rock.x - 1, rock.y - 2).isRobot()) isRobotDead = true;
                            break;
                        }
                    }
                    if (getCell(rock.x, rock.y - 1).isLambda() &&
                            getCell(rock.x + 1, rock.y).isEmpty() && getCell(rock.x + 1, rock.y - 1).isEmpty()) {
                        setCell(rock.x, rock.y, Value.EMPTY);
                        if (isTransitionNeeded) {
                            addTransition(transitions, new Cell(rock.y, rock.x, Value.ROCK), new Cell(rock.y, rock.x, Value.EMPTY));
                            addTransition(transitions, new Cell(rock.y - 1, rock.x + 1, Value.EMPTY), new Cell(rock.y - 1, rock.x + 1, Value.ROCK));
                        }
                        setCell(rock.x, rock.y, Value.EMPTY);
                        setCell(rock.x + 1, rock.y - 1, Value.ROCK);
                        if (getCell(rock.x + 1, rock.y - 2).isRobot()) isRobotDead = true;
                        break;
                    }
                }
            }
        }
        if (countOfLambda == 0) isLiftOpen = true;
    }

    private void addTransition(Map<Cell, Cell> transitons, Cell from, Cell to) {
        Cell trans = null;
        if (from.x != to.x || to.y != from.y) System.out.println("Something wrong");
        for (Map.Entry entry : transitons.entrySet()) {
            trans = (Cell) entry.getKey();
            if (trans.x == from.x && trans.y == from.y && trans.val != from.val) {
                if (trans.val != to.val) transitons.put(new Cell(trans.y, trans.x, trans.val), new Cell(to.y, to.x, to.val));
                else transitons.remove(trans);
                return;
            }
        }
        transitons.put(new Cell(from.y, from.x, from.val), new Cell(to.y, to.x, to.val));
    }

    public void rollBack(Map<Cell, Cell> trasitions, boolean isLambdaCounting) {
        for (Map.Entry<Cell, Cell> entry : trasitions.entrySet()) {

            if (entry.getValue().x != entry.getKey().x || entry.getValue().y != entry.getKey().y) System.out.println("Back " + entry.getValue() + " " + entry.getKey());

            if (!entry.getKey().isRobot()) {
                if (entry.getKey().isLambda() && isLambdaCounting) {
                    countOfLambda++;
                    if (isLiftOpen) isLiftOpen = false;
                }
                setCell(entry.getKey());
            } else {
                setCell(entry.getKey());
                robot = entry.getKey();
            }
        }
        if (isRobotDead) isRobotDead = false;
    }

    public void rollForward(Map<Cell, Cell> trasitions) {
        for (Map.Entry<Cell, Cell> entry : trasitions.entrySet()) {
            if (entry.getValue().x != entry.getKey().x || entry.getValue().y != entry.getKey().y) System.out.println("Forward" + entry.getValue() + " " + entry.getKey());

            if (!entry.getValue().isRobot()) {
                setCell(entry.getValue());
            } else {
                if (entry.getKey().isLambda()) {
                    countOfLambda--;
                    if (countOfLambda == 0) isLiftOpen = true;
                }
                setCell(entry.getValue());
                robot = entry.getValue();
            }
        }
    }

    public int getPointsForStep(Cell step){
        return step.isLambda() ? 49 : -1;
    }

    public int getPointsForRoute(Deque<Cell> route) {
        int lambdaCollected = 0;
        int points = -route.size();
        while (!route.isEmpty()) {
            if (route.getLast().isLambda()) {
                lambdaCollected++;
                if (route.getFirst().isLift()) lambdaCollected++;
            }
            route.pollLast();
        }
        return (points + lambdaCollected * 50);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        if (countOfLambda == field.countOfLambda && isLiftOpen == field.isLiftOpen &&
                isRobotDead == field.isRobotDead && robot.equals(field.robot) &&
                lift.equals(field.lift) && getXsize() == field.getXsize() && getYsize() == field.getXsize()) {
            for (int i = 0; i < getYsize(); i++) {
                for (int j = 0; j < getXsize(); j++) {
                    if (!getCell(j, i).equals(field.getCell(j, i))) {
                        System.out.println("unequal: " + getCell(j, i) + "  " + field.getCell(j, i));
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(robot, lift, countOfLambda, isLiftOpen, isRobotDead);
        result = 31 * result + Arrays.hashCode(cells);
        return result;
    }
}
