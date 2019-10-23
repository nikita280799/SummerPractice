package Tests;
import Logic.*;
import org.junit.Test;

import java.util.*;


public class FindRouteTest {
    @Test
    public void contest1() {
        Field contest1 = Parser.parse("maps\\contest1.map");
        AI ai = new AI();
        ai.setField(contest1);
        Deque<Cell> route = ai.findBestRoute();
        System.out.println(route);
        System.out.println("Points: " + contest1.getPointsForRoute(route));
    }

    @Test
    public void contest2() {
        Field contest2 = Parser.parse("maps\\contest2.map");
        AI ai = new AI();
        ai.setField(contest2);
        Deque<Cell> route = ai.findBestRoute();
        System.out.println(route);
        System.out.println("Points: " + contest2.getPointsForRoute(route));
    }

    @Test
    public void contest3() {
        Field contest3 = Parser.parse("maps\\contest3.map");
        AI ai = new AI();
        ai.setField(contest3);
        Deque<Cell> route = ai.findBestRoute();
        System.out.println(route);
        System.out.println("Points: " + contest3.getPointsForRoute(route));
    }

    @Test
    public void contest4() {
        Field contest4 = Parser.parse("maps\\contest4.map");
        AI ai = new AI();
        ai.setField(contest4);
        Deque<Cell> route = ai.findBestRoute();
        System.out.println(route);
        System.out.println("Points: " + contest4.getPointsForRoute(route));
    }

    @Test
    public void contest5() {
        Field contest5 = Parser.parse("maps\\contest5.map");
        AI ai = new AI();
        ai.setField(contest5);
        Deque<Cell> route = ai.findBestRoute();
        System.out.println(route);
        System.out.println("Points: " + contest5.getPointsForRoute(route));
    }

    @Test
    public void contest6() {
        Field contest6 = Parser.parse("maps\\contest6.map");
        AI ai = new AI();
        ai.setField(contest6);
        Deque<Cell> route = ai.findBestRoute();
        System.out.println(route);
        System.out.println("Points: " + contest6.getPointsForRoute(route));
    }

    @Test
    public void contest7() {
        Field contest7 = Parser.parse("maps\\contest7.map");
        AI ai = new AI();
        ai.setField(contest7);
        Deque<Cell> route = ai.findBestRoute();
        System.out.println(route);
        System.out.println("Points: " + contest7.getPointsForRoute(route));
    }

    @Test
    public void contest8() {
        Field contest8 = Parser.parse("maps\\contest8.map");
        AI ai = new AI();
        ai.setField(contest8);
        Deque<Cell> route = ai.findBestRoute();
        System.out.println(route);
        System.out.println("Points: " + contest8.getPointsForRoute(route));
    }

    @Test
    public void contest9() {
        Field contest9 = Parser.parse("maps\\contest9.map");
        AI ai = new AI();
        ai.setField(contest9);
        Deque<Cell> route = ai.findBestRoute();
        System.out.println(route);
        System.out.println("Points: " + contest9.getPointsForRoute(route));
    }

    @Test
    public void contest10() {
        Field contest10 = Parser.parse("maps\\contest10.map");
        AI ai = new AI();
        ai.setField(contest10);
        Deque<Cell> route = ai.findBestRoute();
        System.out.println(route);
        System.out.println("Points: " + contest10.getPointsForRoute(route));
    }

}
