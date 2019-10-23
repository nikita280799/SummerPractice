package Tests;
import Logic.*;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UnitTests {

    @Test
    public void parserTest() {
        Field contest1 = Parser.parse("maps\\contest1.map");
        assertEquals(new Cell(4, 4, Value.ROBOT), contest1.robot);
        assertEquals(new Cell(1, 0, Value.LIFT), contest1.lift);
        assertTrue(contest1.getCell(4, 4).isRobot());
        assertTrue(contest1.getCell(0, 1).isLift());
        assertTrue(contest1.getCell(1, 2).isLambda());
        assertTrue(contest1.getCell(4, 1).isLambda());
        assertTrue(contest1.getCell(3, 1).isEarth());
        assertTrue(contest1.getCell(3, 2).isRock());
        assertTrue(contest1.getCell(3, 3).isLambda());
        assertTrue(contest1.getCell(3, 4).isRock());
        assertTrue(contest1.getCell(1, 1).isEmpty());
        assertTrue(contest1.getCell(0, 3).isWall());
        assertEquals(3, contest1.countOfLambda);
        assertEquals(6, contest1.getYsize());
        assertEquals(6, contest1.getXsize());
        Field contest3 =  Parser.parse("maps\\contest3.map");
        assertEquals(new Cell(7, 3, Value.ROBOT), contest3.robot);
        assertEquals(new Cell(4, 7, Value.LIFT), contest3.lift);
        assertTrue(contest3.getCell(3, 7).isRobot());
        assertTrue(contest3.getCell(7, 4).isLift());
        assertTrue(contest3.getCell(1, 1).isLambda());
        assertTrue(contest3.getCell(1, 2).isLambda());
        assertTrue(contest3.getCell(3, 1).isEarth());
        assertTrue(contest3.getCell(4, 1).isRock());
        assertTrue(contest3.getCell(2, 4).isLambda());
        assertTrue(contest3.getCell(4, 4).isLambda());
        assertTrue(contest3.getCell(3, 6).isRock());
        assertTrue(contest3.getCell(5, 1).isEmpty());
        assertTrue(contest3.getCell(0, 6).isWall());
        assertTrue(contest3.getCell(3, 3).isWall());
        assertTrue(contest3.getCell(1, 3).isWall());
        assertEquals(4, contest3.countOfLambda);
        assertEquals(9, contest3.getYsize());
        assertEquals(8, contest3.getXsize());
    }

    @Test
    public void stepAndSimulationTest() {
        Field test1 = Parser.parse("maps\\test1.map");
        Set<Cell> expectedSteps = new HashSet<>();
        expectedSteps.add(new Cell(1, 2, Value.EMPTY));
        expectedSteps.add(new Cell(2, 2, Value.ROBOT));
        expectedSteps.add(new Cell(2, 1, Value.EARTH));
        assertEquals(expectedSteps, test1.getPossibleSteps(test1.robot));
        Map<Cell, Cell> transitions = new HashMap<>();
        Map<Cell, Cell> expectedTransitions = new HashMap<>();
        expectedTransitions.put(new Cell(2, 2 , Value.ROBOT), new Cell(2, 2 , Value.EMPTY));
        expectedTransitions.put(new Cell(2, 1, Value.EARTH), new Cell(2, 1, Value.ROBOT));
        test1.robotStep(new Cell(2, 1, Value.EARTH), transitions, true);
        assertEquals(expectedTransitions, transitions);
        assertEquals(new Cell(2, 1, Value.ROBOT),test1.robot);
        expectedTransitions.put(new Cell(4, 4 , Value.ROCK), new Cell(4, 4 , Value.EMPTY));
        expectedTransitions.put(new Cell(3, 4 , Value.EMPTY), new Cell(3, 4 , Value.ROCK));
        expectedTransitions.put(new Cell(2, 3 , Value.ROCK), new Cell(2, 3 , Value.EMPTY));
        expectedTransitions.put(new Cell(1, 2 , Value.EMPTY), new Cell(1, 2 , Value.ROCK));
        test1.simulation(transitions, true);
        assertEquals(expectedTransitions, transitions);
        Set<Cell> expectedSteps2 = new HashSet<>();
        expectedSteps2.add(new Cell(2, 2, Value.EMPTY));
        expectedSteps2.add(new Cell(2, 1, Value.ROBOT));
        expectedSteps2.add(new Cell(3, 1, Value.LAMBDA));
        assertEquals(expectedSteps2, test1.getPossibleSteps(test1.robot));
        Map<Cell, Cell> transitions2 = new HashMap<>();
        Map<Cell, Cell> expectedTransitions2 = new HashMap<>();
        expectedTransitions2.put(new Cell(2, 1 , Value.ROBOT), new Cell(2, 1 , Value.EMPTY));
        expectedTransitions2.put(new Cell(3, 1, Value.LAMBDA), new Cell(3, 1, Value.ROBOT));
        test1.robotStep(new Cell(3, 1, Value.LAMBDA), transitions2, true);
        assertEquals(new Cell(3, 1, Value.ROBOT),test1.robot);
        assertEquals(expectedTransitions2, transitions2);
        expectedTransitions2.put(new Cell(3, 4 , Value.ROCK), new Cell(3, 4 , Value.EMPTY));
        expectedTransitions2.put(new Cell(2, 3 , Value.EMPTY), new Cell(2, 3 , Value.ROCK));
        test1.simulation(transitions2, true);
        assertEquals(expectedTransitions2, transitions2);
        test1.rollBack(transitions2);
        test1.rollBack(transitions);
        assertEquals(Parser.parse("maps\\test1.map"), test1);
        Field test2 = Parser.parse("maps\\test2.map");
        Map<Cell, Cell> transitions3 = new HashMap<>();
        Map<Cell, Cell> expectedTransitions3 = new HashMap<>();
        expectedTransitions3.put(new Cell(2, 2 , Value.ROBOT), new Cell(2, 2 , Value.ROCK));
        expectedTransitions3.put(new Cell(2, 3, Value.EMPTY), new Cell(2, 3, Value.ROBOT));
        expectedTransitions3.put(new Cell(3, 2, Value.ROCK), new Cell(3, 2, Value.EMPTY));
        test2.robotStep(new Cell(2, 3, Value.EMPTY), transitions3, true);
        assertEquals(new Cell(2, 3, Value.ROBOT),test2.robot);
        test2.simulation(transitions3, true);
        assertEquals(expectedTransitions3, transitions3);
        Set<Cell> expectedSteps3 = new HashSet<>();
        expectedSteps3.add(new Cell(2, 4, Value.ROCK));
        expectedSteps3.add(new Cell(2, 2, Value.ROCK));
        expectedSteps3.add(new Cell(3, 3, Value.LAMBDA));
        expectedSteps3.add(new Cell(2, 3, Value.ROBOT));
        assertEquals(expectedSteps3, test2.getPossibleSteps(test2.robot));
        Map<Cell, Cell> transitions4 = new HashMap<>();
        Map<Cell, Cell> expectedTransitions4 = new HashMap<>();
        expectedTransitions4.put(new Cell(3, 3, Value.LAMBDA), new Cell(3, 3, Value.ROBOT));
        expectedTransitions4.put(new Cell(2, 3, Value.ROBOT), new Cell(2, 3, Value.EMPTY));
        test2.robotStep(new Cell(3, 3, Value.LAMBDA), transitions4, true);
        assertEquals(expectedTransitions4, transitions4);
        test2.simulation(transitions3, true);
        assertEquals(expectedTransitions4, transitions4);
        assertTrue(test2.isLiftOpen);
        test2.rollBack(transitions4);
        test2.rollBack(transitions3);
        test2.rollForward(transitions3);
        test2.rollForward(transitions4);
        assertEquals(new Cell(3, 3, Value.ROBOT), test2.robot);
    }

    @Test
    public void rockMoveAndRobotDeadTest() {
        Field test3 = Parser.parse("maps\\test3.map");
        assertTrue(test3.isAnyRockMove());
        assertEquals(new Cell(3, 2, Value.EMPTY),test3.upCell(test3.robot));
        assertEquals(new Cell(1, 2, Value.LAMBDA),test3.downCell(test3.robot));
        assertEquals(new Cell(2, 3, Value.WALL),test3.rightCell(test3.robot));
        assertEquals(new Cell(2, 1, Value.EARTH),test3.leftCell(test3.robot));
        test3.robotStep(new Cell(2, 2, Value.ROBOT), null, false);
        test3.simulation(null, false);
        assertTrue(test3.isRobotDead);
    }

    @Test
    public void pointsForRouteTest() {
        Deque<Cell> route = new ArrayDeque<>();
        route.addLast(new Cell(0, 1, Value.EARTH));
        route.addLast(new Cell(0, 1, Value.LAMBDA));
        route.addLast(new Cell(0, 1, Value.ROCK));
        route.addLast(new Cell(0, 1, Value.EMPTY));
        route.addLast(new Cell(0, 1, Value.LAMBDA));
        route.addLast(new Cell(0, 1, Value.EMPTY));
        route.addLast(new Cell(0, 1, Value.EMPTY));
        route.addLast(new Cell(0, 1, Value.LIFT));
        assertEquals(92, Field.getPointsForRoute(route));
        route = new ArrayDeque<>();
        route.addLast(new Cell(0, 1, Value.EARTH));
        route.addLast(new Cell(0, 1, Value.LAMBDA));
        route.addLast(new Cell(0, 1, Value.ROCK));
        route.addLast(new Cell(0, 1, Value.EMPTY));
        route.addLast(new Cell(0, 1, Value.LAMBDA));
        route.addLast(new Cell(0, 1, Value.LAMBDA));
        route.addLast(new Cell(0, 1, Value.EMPTY));
        route.addLast(new Cell(0, 1, Value.EARTH));
        route.addLast(new Cell(0, 1, Value.LAMBDA));
        route.addLast(new Cell(0, 1, Value.EMPTY));
        route.addLast(new Cell(0, 1, Value.LAMBDA));
        assertEquals(239, Field.getPointsForRoute(route));
    }

    @Test
    public void buildRouteTest() {
        State root = new State(null, 50, 0, new Cell(1, 1, Value.ROBOT));
        StateTree tree = new StateTree(root);
        AI ai = new AI();
        State state1 = new State(null, 0, 0, new Cell(1, 2, Value.EARTH));
        State state2 = new State(null, 0, 0, new Cell(1, 1, Value.ROBOT));
        State state11 = new State(null, 0, 0, new Cell(1, 3, Value.ROCK));
        State state21 = new State(null, 0, 0, new Cell(1, 1, Value.ROBOT));
        State state12 = new State(null, 0, 0, new Cell(2, 2, Value.LAMBDA));
        State state13 = new State(null, 0, 0, new Cell(1, 2, Value.ROBOT));
        State state121 = new State(null, 0, 0, new Cell(2, 3, Value.EMPTY));
        State state122 = new State(null, 0, 0, new Cell(2, 2, Value.ROBOT));
        State state1211 = new State(null, 0, 0, new Cell(3, 3, Value.LIFT));
        root.addChild(state1);
        root.addChild(state2);
        state1.addChild(state11);
        state1.addChild(state12);
        state1.addChild(state13);
        state2.addChild(state21);
        state12.addChild(state121);
        state12.addChild(state122);
        state121.addChild(state1211);
        Deque<Cell> route = ai.buildRoute(tree, state1211);
        assertEquals(state1.robotCell, route.pollLast());
        assertEquals(state12.robotCell, route.pollLast());
        assertEquals(state121.robotCell, route.pollLast());
        assertEquals(state1211.robotCell, route.pollLast());
    }


}
