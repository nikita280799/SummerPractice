package Logic;

import java.util.*;

import static java.lang.Math.abs;

public class AI {

    private Field field;

    public Queue<Cell> route = new ArrayDeque<>();

    public AI() {


    }

    public void setField(Field field) {
        this.field = field;
    }

    public int staticAStar(Cell startCell, Cell goalCell) {
        Map<Cell, Integer> values = new HashMap<Cell, Integer>();
        Set<Cell> visitedCells = new HashSet<>();
        values.put(startCell, statHeur(startCell, goalCell));
        Cell curCell = null;
        int countOfSteps = 0;
        while (!values.isEmpty()) {
            curCell = findBestValue(values);
            if (curCell == goalCell) break;
            for (Cell newCell : field.getPossibleSteps(curCell)) {
                if (values.get(newCell) == null && !visitedCells.contains(curCell)) {
                    countOfSteps = values.get(curCell) - statHeur(curCell, goalCell) + 1;
                    values.put(newCell, statHeur(newCell, goalCell) + countOfSteps);
                }
            }
            visitedCells.add(curCell);
            values.remove(curCell);
        }
        return values.get(curCell);
    }

    private int statHeur(Cell from, Cell to) {
        return abs(from.x - to.x) + abs(from.y - to.y);
    }


    /**
     * public List<Cell> dynamicAI(Cell startCell) {
     * List<Cell> steps = new LinkedList<>();
     * Cell curCell = field.robot;
     * while (!field.lambdas.isEmpty()) {
     * field.simulation();
     * for (Cell newCell: field.getPossibleSteps(field.robot)) {
     * parents.put(newCell, curCell);
     * if (values.get(newCell) == null && !visitedCells.contains(curCell)) {
     * countOfSteps = values.get(curCell) - heuristic(curCell, goalCell) + 1;
     * values.put(newCell, heuristic(newCell, goalCell) + countOfSteps);
     * }
     * visitedCells.add(curCell);
     * values.remove(curCell);
     * }
     * }
     * return countOfSteps;
     * }
     */

    private Cell findBestValue(Map<Cell, Integer> possibleCells) {
        int min = Integer.MAX_VALUE;
        Cell bestCell = null;
        for (Map.Entry entry : possibleCells.entrySet()) {
            if ((int) entry.getValue() < min) {
                min = (int) entry.getValue();
                bestCell = (Cell) entry.getKey();
            }
        }
        return bestCell;
    }

    public List<Cell> getListOfGoal(Cell curCell) {
        Set<Cell> visitedCells = new HashSet<>();
        Set<Cell> candidates = new HashSet<>();
        Map<Cell, Integer> priorities = new HashMap<>();
        visitedCells.add(curCell);
        candidates.add(curCell);
        int countOfSteps = 1;
        while (!candidates.isEmpty()) {
            Set<Cell> newCandidates = new HashSet<>();
            candidates.forEach(cell -> field.getPossibleSteps(cell).forEach(newCell -> {
                if (!visitedCells.contains(newCell)) newCandidates.add(newCell);
            }));
            for (Cell cell : newCandidates) {
                if (cell.isLambda()) {
                    int value = countOfSteps;
                    if (field.upCell(cell).isRock()) value += 2;
                    priorities.put(cell, value);
                }
            }
            visitedCells.addAll(newCandidates);
            candidates = new HashSet<>(newCandidates);
        }
        List<Cell> list = new ArrayList<Cell>();
        priorities.entrySet().stream()
                .sorted(Map.Entry.<Cell, Integer>comparingByValue().reversed())
                .forEach(a -> list.add(a.getKey()));
        list.add(field.lift);
        return list;
    }

    public Queue<Cell> dynamicAI(List<Cell> goalCellList) {
        Tree tree = new Tree(new Node(null, 0, 0, field.robot));
        Set<Node> possibleStates = new HashSet<>();
        Set<Node> visitedStates = new HashSet<>();
        possibleStates.add(tree.root);
        Node curState = tree.root;
        while (true) {
            int bestHeur = Integer.MAX_VALUE;
            Node bestState = null;
            for (Node newState : possibleStates) {
                if (newState.heuristic < bestHeur && !visitedStates.contains(newState)) {
                    bestHeur = newState.heuristic;
                    bestState = newState;
                }
            }
            if (curState != bestState) {
                Stack<Node> stack = new Stack<>();
                stack.push(bestState);
                while (bestState.parent != tree.root) {
                    bestState = bestState.parent;
                    stack.push(bestState);
                }
                while (!stack.empty()) {
                    curState = stack.pop();
                    field.rollForward(curState.transitions);
                }
            }
            for (Cell newCell : field.getPossibleSteps(field.robot)) {
                Map<Cell, Cell> transitions = new HashMap<>();
                transitions.put(field.robot, new Cell(field.robot.y, field.robot.x, Value.EMPTY));
                transitions.put(newCell, new Cell(newCell.y, newCell.x, Value.ROBOT));
                if (newCell.isRock()) {
                    if (newCell.x > field.robot.x) {
                        transitions.put(new Cell(newCell.y, newCell.x + 1, Value.EMPTY),
                                new Cell(newCell.y, newCell.x + 1, Value.ROCK));
                    } else {
                        transitions.put(new Cell(newCell.y, newCell.x - 1, Value.EMPTY),
                                new Cell(newCell.y, newCell.x - 1, Value.ROCK));
                    }
                }
                field.robotStep(newCell);
                field.transitionsSimulation(transitions, true);
                Node newState = new Node(transitions, staticAStar(field.robot,
                        goalCellList.get(curState.numberOfGoalCell)) + curState.level + 1, curState.numberOfGoalCell, newCell);
                if (field.isRobotDead) newState.heuristic = 1000;
                curState.addChild(newState);
                if (newCell == goalCellList.get(curState.numberOfGoalCell)) {
                    if (newCell == goalCellList.get(goalCellList.size() - 1)) return buildRoute(tree, newState);
                    newState.numberOfGoalCell++;
                }
                possibleStates.add(newState);
                field.rollBack(transitions);
            }
            visitedStates.add(curState);
            possibleStates.remove(curState);
            while (curState.parent != null) {
                field.rollBack(curState.transitions);
                curState = curState.parent;
            }
        }
    }

    public Queue<Cell> buildRoute(Tree tree, Node end) {
        Queue<Cell> subRoute = new ArrayDeque<>();
        while(end != tree.root) {
            subRoute.offer(end.robotCell);
            end = end.parent;
        }
        while (!subRoute.isEmpty()) {
            route.add(subRoute.remove());
        }
        return route;
    }
}
