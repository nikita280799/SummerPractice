package Visualization;

import Logic.Parser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class MainFrame extends JFrame {

    private JFileChooser fileChooser;

    private JButton loadMap, findSolution, showSolution;

    private FieldPanel fieldPanel = new FieldPanel();

    public MainFrame(String title) {
        super(title);
        setSize(800, 600);
        setResizable(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JScrollPane scrollPanel = new JScrollPane(fieldPanel);
        scrollPanel.setMinimumSize(new Dimension(200, 200));
        scrollPanel.setPreferredSize(new Dimension(500, 500));
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPanel.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPanel, BorderLayout.CENTER);
        JPanel butPanel = new JPanel();
        butPanel.setLayout(new BoxLayout(butPanel, BoxLayout.Y_AXIS));
        fileChooser = new JFileChooser("D:\\Studying\\Прога\\4 семестр\\SummerPractice\\maps");
        loadMap = new JButton("Load Field");
        findSolution = new JButton("Find solution");
        showSolution = new JButton("Show solution");
        butPanel.add(loadMap);
        butPanel.add(findSolution);
        butPanel.add(showSolution);
        butPanel.setVisible(true);
        this.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPanel, butPanel),
                BorderLayout.CENTER);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addListeners();
    }

    private void addListeners() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Field", "map");
        fileChooser.setFileFilter(filter);
        loadMap.addActionListener(e -> {
            fileChooser.setDialogTitle("Выбор карты");
            int result = fileChooser.showOpenDialog(MainFrame.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                this.fieldPanel.setMap(Parser.parse(fileChooser.getSelectedFile().getAbsolutePath()));
            }
        });
        findSolution.addActionListener(e -> fieldPanel.findSolution());
        showSolution.addActionListener(e -> fieldPanel.showSolution());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame("Lambda Lifter"));
    }
}