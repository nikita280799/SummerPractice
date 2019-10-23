package Logic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Parser {

    public static Field parse(String input) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(input), StandardCharsets.UTF_8);
            int max = Collections.max(lines, Comparator.comparing(String::length)).length();
            Cell[][] field = new Cell[lines.size()][max];
            int countOfLambda = 0;
            Cell robot = null;
            Cell lift = null;
            Value val;
            for (int i = 0; i < lines.size(); i++) {
                for (int j = 0; j < max; j++) {
                    if (lines.get(i).length() > j) {
                        val = Value.getValue(lines.get(i).charAt(j));
                    } else {
                        val = Value.EMPTY;
                    }
                    Cell curCell = new Cell(lines.size() - i - 1, j, val);
                    switch (val) {
                        case ROBOT:
                            robot = curCell;
                            break;
                        case LIFT:
                            lift = curCell;
                            break;
                        case LAMBDA:
                            countOfLambda++;
                            break;
                        default:
                            break;
                    }
                    field[lines.size() - i - 1][j] = curCell;
                }
            }
            if (robot != null) return new Field(field, robot, lift, countOfLambda);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}