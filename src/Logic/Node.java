package Logic;

import java.util.List;

import java.util.*;

public class Node {

    Node parent = null;

    List<Node> childs = new ArrayList<>();

    int heuristic;

    int level = 0;

    int numberOfGoalCell;

    Cell robotCell;

    Map<Cell, Cell> transitions;

    public Node(Map<Cell, Cell> transitions, int heuristic, int numberOfGoalCell,  Cell robotCell) {
        this.transitions = transitions;
        this.heuristic = heuristic;
        this.robotCell = robotCell;
        this.numberOfGoalCell = numberOfGoalCell;
    }

    public void addChild(Node child) {
        child.parent = this;
        childs.add(child);
        child.level = this.level + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return heuristic == node.heuristic &&
                level == node.level &&
                Objects.equals(parent, node.parent) &&
                Objects.equals(childs, node.childs) &&
                Objects.equals(transitions, node.transitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, transitions);
    }
}
