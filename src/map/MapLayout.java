package map;

import map.uicommon.SuggestionDropDownDecorator;
import map.uicommon.TextComponentSuggestionClient;
import map.uicommon.TextComponentWordSuggestionClient;
import map.util.RandomUtil;
import map.util.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapLayout extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> cbbGraphDemo = new JComboBox<String>();
    private JButton btnSearch;
    private JPanel mainPanel = new JPanel();
    private DrawMap drawMap = new DrawMap();
    private int fromPosition = 0, toPosition = 0;
    private boolean mapType = false;

    int WIDTH_SELECT, HEIGHT_SELECT;
    Dijkstra dijkstra = new Dijkstra();

    public static void main(String[] args) {
        new MapLayout("Bong Map");
    }

    public MapLayout(String title) {
        setTitle(title);
        setLayout(new BorderLayout(5, 5));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(drawMenu(), BorderLayout.PAGE_START);
        add(drawFromToPosition(), BorderLayout.WEST);
        add(drawMapLayout(), BorderLayout.CENTER);
        add(drawWayLayout(), BorderLayout.PAGE_END);
        pack();
        setVisible(true);
        readTextFileAndShowMap();
    }

    private JMenuBar drawMenu() {
        JMenu menuFile = new JMenu("File");
        menuFile.add(createMenuItem("Open", KeyEvent.VK_O, Event.CTRL_MASK));
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuFile.addSeparator();
        menuFile.add(createMenuItem("Save", KeyEvent.VK_S, Event.CTRL_MASK));
        menuFile.add(createMenuItem("Exit", KeyEvent.VK_X, Event.CTRL_MASK));

        JMenu menuHelp = new JMenu("Help");
        menuHelp.setMnemonic(KeyEvent.VK_H);
        menuHelp.add(createMenuItem("About", KeyEvent.VK_A, Event.CTRL_MASK));
        menuHelp.add(createMenuItem("Move", KeyEvent.VK_A, Event.CTRL_MASK));
        menuHelp.add(createMenuItem("Update", KeyEvent.VK_A, Event.CTRL_MASK));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menuFile);
        menuBar.add(menuHelp);
        return menuBar;
    }

    private JPanel drawFromToPosition() {

        JPanel panel = new JPanel(new BorderLayout());
        JPanel panelTop = new JPanel(new GridLayout(6, 1, 5, 5));
        JPanel panelBottom = new JPanel(new BorderLayout());

        makeJTextFieldGoFrom(panelTop, "Điểm đi", tfBegin);
        makeJTextFieldGoFrom(panelTop, "Điểm đến", tfEnd);
        makeSearchButton(panelTop);

        panel.add(panelTop, BorderLayout.PAGE_START);
        panel.add(panelBottom, BorderLayout.CENTER);
        panel.setBorder(new EmptyBorder(0, 5, 0, 0));
        WIDTH_SELECT = (int) panel.getPreferredSize().getWidth();
        HEIGHT_SELECT = (int) panel.getPreferredSize().getHeight();
        return panel;
    }

    private void makeJTextFieldGoFrom(JPanel panelTop, String title, JTextField tf) {
        JPanel panelSmall = new JPanel(new GridLayout(1, 2, 15, 5));
        panelSmall.setBorder(new EmptyBorder(0, 15, 0, 5));
        setupSuggestJTextField(panelSmall, tf);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(title));
        panel.add(panelSmall);
        panelTop.add(panel);
    }

    JTextField tfBegin = new JTextField(10);
    JTextField tfEnd = new JTextField(10);

    private void setupSuggestJTextField(JPanel panel, JTextField tf) {
        SuggestionDropDownDecorator.decorate(tf,
                new TextComponentSuggestionClient(MapLayout::getSuggestions));
        JTextPane textPane = new JTextPane();
        SuggestionDropDownDecorator.decorate(textPane,
                new TextComponentWordSuggestionClient(MapLayout::getSuggestions));
        panel.add(tf);
    }

    private void makeSearchButton(JPanel panel) {
        JPanel panelRunTemp = new JPanel(new GridLayout(1, 2, 15, 5));
        panelRunTemp.setBorder(new EmptyBorder(0, 15, 0, 5));
        panelRunTemp.add(btnSearch = CustomButton("Tìm"));
        JPanel panelRun = new JPanel(new BorderLayout());
        panelRun.setBorder(new TitledBorder("Tìm đường"));
        panelRun.add(panelRunTemp);
        panel.add(panelRun);
    }

    private JPanel drawMapLayout() {
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new TitledBorder(""));
        mainPanel.setBackground(null);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(mainPanel, BorderLayout.WEST);
        panel.add(drawMap, BorderLayout.CENTER);
        return panel;
    }

    private JMenuItem createMenuItem(String title, int keyEvent, int event) {
        JMenuItem mi = new JMenuItem(title);
        mi.setMnemonic(keyEvent);
        mi.setAccelerator(KeyStroke.getKeyStroke(keyEvent, event));
        mi.addActionListener(this);
        return mi;
    }

    private JButton CustomButton(String lable) {
        JButton btn = new JButton(lable);
        btn.addActionListener(this);
        return btn;
    }

    private void updateView() {
        makeSuggestBegin();
        setupDataToDijkstra();
        reDraw();
    }

    static ArrayList<String> suggestList = new ArrayList<>();

    private void makeSuggestBegin() {
        int size = drawMap.getData().getPositions().size();
        for (int i = 0; i < size; i++) {
            suggestList.add(drawMap.getData().getPositions().get(i).getName());
        }
    }

    private void setupDataToDijkstra() {
        dijkstra = new Dijkstra();
        dijkstra.setMapType(mapType);
        dijkstra.setPositions(drawMap.getData().getPositions());
        dijkstra.setPathzs(drawMap.getData().getPathzs());
        dijkstra.setData();
    }

    private void reDraw() {
        drawMap.setReDraw(true);
        drawMap.repaint();
    }

    private void readTextFileAndShowMap() {
        int demo = cbbGraphDemo.getSelectedIndex();
        drawMap.readDemoTest(demo);
        updateView();
    }

    private boolean isStartToFindWay() {

        int positionsSize = drawMap.getData().getPositions().size() - 1;
        if (Utils.isNumeric(tfBegin.getText()) && Utils.isNumeric(tfEnd.getText())) {
            fromPosition = Integer.parseInt(tfBegin.getText());
            toPosition = Integer.parseInt(tfEnd.getText());
            if (toPosition == positionsSize + 1) {
                toPosition = -1;
            }
        } else {
            for (int i = 0; i < suggestList.size(); i++) {
                if (suggestList.get(i).equals(tfBegin.getText())) {
                    fromPosition = i;
                }

                if (suggestList.get(i).equals(tfEnd.getText())) {
                    toPosition = i;
                }
            }
        }

        if(toPosition == 44 ) {
            toPosition = 18; // Go to the Huu Nghi hospital
        }

        if(toPosition == 45 ) {
            toPosition = 4;
        }

        if (positionsSize < 1 || fromPosition == 0 || toPosition == 0) {
            JOptionPane.showMessageDialog(null,
                    "Chọn điểm đi và điểm đến",
                    "Có lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void setPositionFromTo() {
        drawMap.setFromPosition(fromPosition);
        drawMap.setToPosition(toPosition);
        dijkstra.setFromPosition(fromPosition);
        dijkstra.setToPosition(toPosition);
    }

    private void findWay() {
        if (isStartToFindWay()) {
            setupDataToDijkstra();
            setPositionFromTo();
            dijkstra.dijkstra();
            drawMap.setDrawResult(true);
            drawMap.setCoList(dijkstra.getCoList());
            tvWay.setText(dijkstra.getPath());
            drawMap.setPrevious(dijkstra.getPrevious());
            drawMap.setInfinity(dijkstra.getInfinity());
            drawMap.setMinPath(dijkstra.getMinPath());
            drawMap.setCheckedPointMin(dijkstra.getCheckRightPositionShortest());
            drawMap.repaint();
        }
    }

    private JTextArea tvWay;
    private JPanel drawWayLayout() {
        tvWay = new JTextArea("");
        tvWay.setRows(5);
        tvWay.setEditable(false);
        JScrollPane scrollPath = new JScrollPane(tvWay);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Đường đi"));
        panel.add(scrollPath, BorderLayout.PAGE_START);
        panel.setPreferredSize(new Dimension(WIDTH_SELECT * 7 / 2,
                HEIGHT_SELECT / 2));
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (e.getSource() == btnSearch) {
            findWay();
        }

        if (command == "Open") {
            openFile();
        }

        if (command == "Exit") {
            System.exit(0);
        }


        if (command == "Move") {
            drawMap.setDraw(3);
        }

        if (command == "Update") {
            updateView();
        }

        if (command == "Save") {
            storeFile();
        }

    }

    private void storeFile() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save graph");
        int select = fc.showSaveDialog(this);
        if (select == 0) {
            String path = fc.getSelectedFile().getPath();
            System.out.println(path);
            drawMap.write(path);
        }
    }

    private void openFile() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Open graph");
        int select = fc.showOpenDialog(this);
        if (select == 0) {
            String path = fc.getSelectedFile().getPath();
            System.out.println(path);
            drawMap.readFile(path);
            updateLayout();
        }
    }

    private void updateLayout() {
        setupDataToDijkstra();
        reDraw();
    }

    private static List<String> words =
            RandomUtil.getAddress();

    private static List<String> getSuggestions(String input) {
        if (input.isEmpty()) {
            return null;
        }
        return suggestList.stream()
                .filter(s -> s.startsWith(input))
                .limit(20)
                .collect(Collectors.toList());
    }
}