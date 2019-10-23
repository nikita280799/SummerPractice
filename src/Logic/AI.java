package Logic;

import java.util.*;

import static java.lang.Math.abs;

public class AI {

    public Field field;

    public AI() {
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Deque<Cell> findBestRoute() {
        List<Cell> curGoalList = getListOfGoal(field.robot, true, false);
        Deque<Cell> bestRoute = null;
        int bestLambdaCollected = 0;
        Deque<Cell> route = dynamicAI(curGoalList);
        if (route.getFirst().isLift()) return route;
        if (bestLambdaCollected <= curGoalList.indexOf(route.getFirst())){
            bestLambdaCollected = curGoalList.indexOf(route.getFirst());
            bestRoute = route;
        }
        curGoalList = getListOfGoal(field.robot, false, false);
        Deque<Cell> route2 = dynamicAI(curGoalList);
        if (route2.getFirst().isLift()) return route2;
        if (bestLambdaCollected < curGoalList.indexOf(route2.getFirst())){
            bestLambdaCollected = curGoalList.indexOf(route2.getFirst());
            bestRoute = route2;
        }
        curGoalList = getListOfGoal(field.robot, true, true);
        Deque<Cell> route3 = dynamicAI(curGoalList);
        if (route3.getFirst().isLift()) return route3;
        if (bestLambdaCollected < curGoalList.indexOf(route3.getFirst())){
            bestRoute = route3;
        }
        return bestRoute;
    }

    public int staticAStar(Cell startCell, Cell goalCell) {
        Map<Cell, Integer> values = new HashMap<Cell, Integer>();
        Set<Cell> visitedCells = new HashSet<>();
        values.put(startCell, manhattanDistance(startCell, goalCell));
        Cell curCell = null;
        int countOfSteps = 0;
        while (!values.isEmpty()) {
            curCell = findBestValue(values);
            if (curCell.x == goalCell.x && curCell.y == goalCell.y) return values.get(curCell);
            for (Cell newCell : field.getPossibleSteps(curCell)) {
                if (values.get(newCell) == null && !visitedCells.contains(curCell)) {
                    countOfSteps = values.get(curCell) - manhattanDistance(curCell, goalCell) + 1;
                    values.put(newCell, manhattanDistance(newCell, goalCell) + countOfSteps);
                }
            }
            visitedCells.add(curCell);
            values.remove(curCell);
        }
        return 5000;
    }

    private int manhattanDistance(Cell from, Cell to) {
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

    public List<Cell> getListOfGoal(Cell curCell, boolean isRock, boolean isRandom) {
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
        List<Cell> list = new ArrayList<Cell>();
        priorities.entrySet().stream()
                .sorted(Map.Entry.<Cell, Integer>comparingByValue())
                .forEach(a -> list.add(a.getKey()));
        list.add(field.lift);
        return list;
    }

    public Deque<Cell> dynamicAI(List<Cell> goalCellList) {
        StateTree tree = new StateTree(new State(null, 0, 0, field.robot));
        Set<State> possibleStates = new HashSet<>();
        Set<State> visitedStates = new HashSet<>();
        possibleStates.add(tree.root);
        State curState = tree.root;
        State bestCurRouteState = null;
        int heurLimit = field.getXsize() * field.getYsize();
        Date startDate = new Date();
        while (true) {
            Date curDate = new Date();
            long timeOfWork = curDate.getTime() - startDate.getTime();
            if (timeOfWork > 3000) return buildRoute(tree, bestCurRouteState);
            State bestState = null;
            for (State newState : possibleStates) {
                if (!visitedStates.contains(newState) && (newState.heuristic < heurLimit && bestState == null || newState.heuristic < heurLimit &&
                        (newState.numberOfGoalCell > bestState.numberOfGoalCell
                                || newState.numberOfGoalCell == bestState.numberOfGoalCell && newState.heuristic < bestState.heuristic))) {
                    bestState = newState;
                }
            }
            if (bestState == null) return buildRoute(tree, bestCurRouteState);
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
                if (curState.robotCell.x == newCell.x && curState.robotCell.y == newCell.y && !field.isAnyRockMove()) {
                    continue;
                }
                Map<Cell, Cell> transitions = new HashMap<>();
                field.robotStep(new Cell(newCell.y, newCell.x, field.getValue(newCell.x, newCell.y)), transitions, true);
                field.simulation(transitions, true);
                if (!field.isRobotDead) {
                    State newState = new State(transitions, staticAStar(field.robot,
                            goalCellList.get(curState.numberOfGoalCell)) + curState.level + 1, curState.numberOfGoalCell, newCell);
                    curState.addChild(newState);
                    if (newCell.x == goalCellList.get(curState.numberOfGoalCell).x
                            && newCell.y == goalCellList.get(curState.numberOfGoalCell).y) {
                        if (newCell.x == goalCellList.get(goalCellList.size() - 1).x &&
                                newCell.y == goalCellList.get(goalCellList.size() - 1).y)
                            return buildRoute(tree, newState);
                        newState.numberOfGoalCell++;
                        Cell curGoal = goalCellList.get(newState.numberOfGoalCell);
                        while (!field.getCell(curGoal.x, curGoal.y).isLambda()
                                && newState.numberOfGoalCell != goalCellList.size() - 1) {
                            newState.numberOfGoalCell++;
                            curGoal = goalCellList.get(newState.numberOfGoalCell);
                        }
                        if (bestCurRouteState == null || newState.numberOfGoalCell > bestCurRouteState.numberOfGoalCell ||
                                newState.numberOfGoalCell == bestCurRouteState.numberOfGoalCell && newState.level < bestCurRouteState.level)
                            bestCurRouteState = newState;
                    }
                    possibleStates.add(newState);
                }
                field.rollBack(transitions, true);
            }
            //System.out.println(curState.robotCell.toString() + curState.level + " " + curState.numberOfGoalCell + " " + curState.heuristic);
            visitedStates.add(curState);
            possibleStates.remove(curState);
            while (curState.parent != null) {
                field.rollBack(curState.transitions, true);
                curState = curState.parent;
            }
        }
    }

    public Deque<Cell> buildRoute(StateTree tree, State end) {
        Deque<Cell> route = new ArrayDeque<>();
        while (end != tree.root) {
            route.addLast(end.robotCell);
            end = end.parent;
        }
        return route;
    }
}
