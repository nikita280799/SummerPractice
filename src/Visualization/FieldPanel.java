package Visualization;

import Logic.AI;
import Logic.Field;

import javax.swing.*;
import java.awt.*;

public class FieldPanel extends JPanel {

     private Field field;

     private static final int CELL_WIDTH = 30;

     private AI ai = new AI();

     FieldPanel() {
          setPreferredSize(new Dimension(1000, 1000));
     }

     void setMap(Field field) {
          this.field = field;
          ai.setField(field);
          repaint();
     }

     void step(){
          ai.dynamicAI(ai.foundNearestGoal(field.robot));
          ai.dynamicAI(ai.foundNearestGoal(field.robot));
          ai.dynamicAI(ai.foundNearestGoal(field.robot));

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
          }
     }
}
