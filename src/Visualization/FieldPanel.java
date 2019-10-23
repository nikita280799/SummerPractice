package Visualization;

import Logic.AI;
import Logic.Cell;
import Logic.Field;

import javax.swing.*;
import java.awt.*;
import java.util.Deque;

public class FieldPanel extends JPanel {

    private Field field;

    private static final int CELL_WIDTH = 30;

    private AI ai = new AI();

    private Deque<Cell> route;

    private int step = 0;

    private int points = 0;

    private int lambdaCollected = 0;

    FieldPanel() {
        setPreferredSize(new Dimension(1000, 1000));
    }

    void setMap(Field field) {
        this.field = field;
        ai.setField(new Field(field));
        step = 0;
        lambdaCollected = 0;
        points = 0;
        repaint();
    }

    void findSolution() {
        route = ai.findBestRoute();
        System.out.println(route);
        repaint();
    }

    void showSolution() {
        if (route != null) {
            step++;
            points += route.getLast().isLambda() ? 49 : -1;
            lambdaCollected += route.getLast().isLambda() ? 1 : 0;
            if (route.getLast().isLift()) points += lambdaCollected * 25;
            field.robotStep(route.pollLast(), null, false);
            field.simulation(null, false);
        }
        repaint();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (field != null) {
            for (int i = 0; i < field.getYsize(); i++) {
                for (int j = 0; j < field.getXsize(); j++) {
                    g.drawImage(field.getCell(j, i).val.getImage(), field.getCell(j, i).x * CELL_WIDTH,
                            (field.getYsize() - field.getCell(j, i).y - 1) * CELL_WIDTH, null);
                }
            }
            g.drawString("Step: " + step + "   Lambda collected: " +
                    lambdaCollected + "   Points: " + points, 0, field.getYsize() * CELL_WIDTH + 20);
        }
    }
}