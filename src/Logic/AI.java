package Logic;

import java.util.*;

import static java.lang.Math.abs;

public class AI {

    private Field field;

    public AI() {}

    public void setField(Field field) {
        this.field = field;
    }

    public Deque<Cell> findBestRoute() {
        List<Cell> curGoalList = getListOfGoal(field.robot, true, false);
        Deque<Cell> bestRoute = dynamicAI(curGoalList);
        int bestLambdaCollected = curGoalList.indexOf(bestRoute.getFirst());
        curGoalList = getListOfGoal(field.robot, false, false);
        Deque<Cell> someRoute = dynamicAI(curGoalList);
        if (bestLambdaCollected < curGoalList.indexOf(someRoute.getFirst())) {
            bestLambdaCollected = curGoalList.indexOf(someRoute.getFirst());
            bestRoute = someRoute;
        }
        curGoalList = getListOfGoal(field.robot, true, true);
        someRoute = dynamicAI(curGoalList);
        if (bestLambdaCollected < curGoalList.indexOf(someRoute.getFirst())) {
            bestRoute = someRoute;
        }
        return bestRoute;
    }

    private int staticAStar(Cell startCell, Cell goalCell) {
        Map<Cell, Integer> values = new HashMap<>();
        Set<Cell> visitedCells = new HashSet<>();
        values.put(startCell, manhattanDistance(startCell, goalCell));
        while (!values.isEmpty()) {
            Cell curCell = findBestValue(values);
            if (curCell.x == goalCell.x && curCell.y == goalCell.y) return values.get(curCell);
            for (Cell newCell : field.getPossibleSteps(curCell)) {
                if (values.get(newCell) == null && !visitedCells.contains(curCell)) {
                    int countOfSteps = values.get(curCell) - manhattanDistance(curCell, goalCell) + 1;
                    values.put(newCell, manhattanDistance(newCell, goalCell) + countOfSteps);
                }
            }
            visitedCells.add(curCell);
            values.remove(curCell);
        }
        return 100000;
    }

    private int manhattanDistance(Cell from, Cell to) {
        return abs(from.x - to.x) + abs(from.y - to.y);
    }

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

    private List<Cell> getListOfGoal(Cell curCell, boolean isRock, boolean isRandom) {
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
                    if (isRock && field.upCell(cell).isRock()) value += field.upCell(cell).y;
                    priorities.put(cell, value);
                }
            }
            visitedCells.addAll(newCandidates);
            candidates = new HashSet<>(newCandidates);
            if (isRandom) countOfSteps++;
        }
        List<Cell> list = new ArrayList<>();
        priorities.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(a -> list.add(a.getKey()));
        list.add(field.lift);
        return list;
    }

    private Deque<Cell> dynamicAI(List<Cell> goalList) {
        StateTree tree = new StateTree(new State(null, 0, 0, field.robot));
        Set<State> possibleStates = new HashSet<>();
        possibleStates.add(tree.root);
        State curState = tree.root;
        State bestRouteState = null;
        int heurLimit = field.getXsize() * field.getYsize();
        Date startDate = new Date();
        while (true) {
            Date curDate = new Date();
            long timeOfWork = curDate.getTime() - startDate.getTime();
            State bestState = findBestState(possibleStates, heurLimit);
            if (timeOfWork > 5000 || bestState == null) return buildRoute(tree, bestRouteState);
            if (curState != bestState) {
                Stack<State> stack = new Stack<>();
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
                if (curState.robotCell == newCell && !field.isAnyRockMove()) {
                    continue;
                }
                Map<Cell, Cell> trans = new HashMap<>();
                field.robotStep(new Cell(newCell.y, newCell.x, field.getValue(newCell.x, newCell.y)), trans, true);
                field.simulation(trans, true);
                if (!field.isRobotDead) {
                    State newState = new State(trans, staticAStar(field.robot,
                            goalList.get(curState.goalNumber)) + curState.level + 1, curState.goalNumber, newCell);
                    curState.addChild(newState);
                    if (newCell.isCoordsEqual(goalList.get(curState.goalNumber))) {
                        newState.goalNumber++;
                        if (newState.goalNumber == goalList.size()) return buildRoute(tree, newState);
                        checkIfAnyGoalLambdasCollected(newState, goalList);
                        if (bestRouteState == null || newState.goalNumber > bestRouteState.goalNumber ||
                                newState.goalNumber == bestRouteState.goalNumber && newState.level < bestRouteState.level)
                            bestRouteState = newState;
                    }
                    possibleStates.add(newState);
                }
                field.rollBack(trans);
            }
            possibleStates.remove(curState);
            while (curState.parent != null) {
                field.rollBack(curState.transitions);
                curState = curState.parent;
            }
        }
    }

    private State findBestState(Set<State> possibleStates, int heurLimit) {
        State bestState = null;
        for (State newState : possibleStates) {
            if (newState.heuristic < heurLimit && bestState == null || newState.heuristic < heurLimit
                    && (newState.goalNumber > bestState.goalNumber || newState.goalNumber == bestState.goalNumber
                    && newState.heuristic < bestState.heuristic)) {
                bestState = newState;
            }
        }
        return bestState;
    }

    private void checkIfAnyGoalLambdasCollected(State curState, List<Cell> goalList) {
        Cell curGoal = goalList.get(curState.goalNumber);
        while (!field.getCell(curGoal.x, curGoal.y).isLambda()
                && curState.goalNumber != goalList.size() - 1) {
            curState.goalNumber++;
            curGoal = goalList.get(curState.goalNumber);
        }
    }

    public Deque<Cell> buildRoute(StateTree tree, State end) {
        Deque<Cell> route = new ArrayDeque<>();
        if (end == null) {
            route.addLast(tree.root.robotCell);
            return route;
        }
        while (end != tree.root) {
            route.addLast(end.robotCell);
            end = end.parent;
        }
        return route;
    }
}