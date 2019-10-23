package Logic;

import java.util.List;

import java.util.*;

public class State {

    State parent = null;

    Set<State> childs = new HashSet<>();

    int heuristic;

    int level = 0;

    int numberOfGoalCell;

    Cell robotCell;

    Map<Cell, Cell> transitions;

    public State(Map<Cell, Cell> transitions, int heuristic, int numberOfGoalCell, Cell robotCell) {
        this.transitions = transitions;
        this.heuristic = heuristic;
        this.robotCell = robotCell;
        this.numberOfGoalCell = numberOfGoalCell;
    }

    public void addChild(State child) {
        child.parent = this;
        childs.add(child);
        child.level = this.level + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return heuristic == state.heuristic &&
                level == state.level &&
                Objects.equals(robotCell, state.robotCell) &&
                Objects.equals(transitions, state.transitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, transitions);
    }
}
