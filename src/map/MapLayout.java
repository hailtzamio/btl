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
    private JPanel drawPanel = new JPanel();
    private DrawMap grawMap = new DrawMap();

    private int indexBeginPoint = 0, indexEndPoint = 0;
    private int step = 0;
    private boolean mapType = false;

    int WIDTH_SELECT, HEIGHT_SELECT;
    Dijkstra dijkstra = new Dijkstra();

    public MapLayout(String title) {
        setTitle(title);
        setLayout(new BorderLayout(5, 5));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(creatMenu(), BorderLayout.PAGE_START);
        add(creatSelectPanel(), BorderLayout.WEST);
        add(creatPaintPanel(), BorderLayout.CENTER);
        add(creatLogPanel(), BorderLayout.PAGE_END);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        drawDemo();
    }

    private JMenuBar creatMenu() {
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

    private JPanel creatSelectPanel() {

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


    private JPanel creatPaintPanel() {
        drawPanel.setLayout(new BoxLayout(drawPanel, BoxLayout.Y_AXIS));
        drawPanel.setBorder(new TitledBorder(""));
        drawPanel.setBackground(null);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(drawPanel, BorderLayout.WEST);
        panel.add(grawMap, BorderLayout.CENTER);
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

    private JComboBox<String> CustomComboxBox(String title) {
        String list[] = {title};
        JComboBox<String> cbb = new JComboBox<String>(list);
        cbb.addActionListener(this);
        cbb.setEditable(false);
        cbb.setMaximumRowCount(5);
        return cbb;
    }

    private void updateView() {
        makeSuggestBegin();
        resetDataDijkstra();
        setDrawResultOrStep(false);
        reDraw();
    }

    private void actionDrawPoint() {
        grawMap.setDraw(1);
        setDrawResultOrStep(false);
    }

    private void actionDrawLine() {
        grawMap.setDraw(2);
        setDrawResultOrStep(false);
    }

    private void actionChoosePoint() {
        resetDataDijkstra();
        setDrawResultOrStep(false);
        reDraw();
    }

    static ArrayList<String> suggestList = new ArrayList<>();

    private void makeSuggestBegin() {
        int size = grawMap.getData().getPositions().size();
        for (int i = 0; i < size; i++) {
            suggestList.add(grawMap.getData().getPositions().get(i).getName());
        }
    }

    private void setEnableDraw(boolean check, String matrix) {
        cbbGraphDemo.setEnabled(!check);
    }

    private void setEnableMapType(boolean mapType) {
        this.mapType = mapType;
        grawMap.setTypeMap(mapType);
        setDrawResultOrStep(false);
        grawMap.repaint();
        resetDataDijkstra();
    }

    private void setDrawResultOrStep(boolean check) {
        grawMap.setDrawResult(check);
        grawMap.setDrawStep(check);
    }

    private void resetDataDijkstra() {
        step = 0;
        dijkstra = new Dijkstra();
        dijkstra.setMapType(mapType);
        dijkstra.setArrMyPoint(grawMap.getData().getPositions());
        dijkstra.setArrMyLine(grawMap.getData().getPathzs());
        dijkstra.input();
        dijkstra.processInput();
    }

    private void reDraw() {
        grawMap.setReDraw(true);
        grawMap.repaint();
    }

    private void drawDemo() {
        int demo = cbbGraphDemo.getSelectedIndex();
        grawMap.readDemoTest(demo);
        updateView();
    }

    private boolean checkRun() {


        int size = grawMap.getData().getPositions().size() - 1;

        if (Utils.isNumeric(tfBegin.getText()) && Utils.isNumeric(tfEnd.getText())) {
            indexBeginPoint = Integer.parseInt(tfBegin.getText());
            indexEndPoint = Integer.parseInt(tfEnd.getText());
            if (indexEndPoint == size + 1) {
                indexEndPoint = -1;
            }
        } else {
            for (int i = 0; i < suggestList.size(); i++) {
                if (suggestList.get(i).equals(tfBegin.getText())) {
                    indexBeginPoint = i;
                }

                if (suggestList.get(i).equals(tfEnd.getText())) {
                    indexEndPoint = i;
                }
            }
        }

        if (size < 1 || indexBeginPoint == 0 || indexEndPoint == 0) {
            JOptionPane.showMessageDialog(null,
                    "Chọn điểm đi và điểm đến",
                    "error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void setBeginEndPoint() {
        grawMap.setIndexBeginPoint(indexBeginPoint);
        grawMap.setIndexEndPoint(indexEndPoint);
        dijkstra.setBeginPoint(indexBeginPoint);
        dijkstra.setEndPoint(indexEndPoint);
    }

    private void findWay() {
        if (checkRun()) {
            resetDataDijkstra();
            setBeginEndPoint();
            dijkstra.dijkstra();
            grawMap.setDrawStep(false);
            grawMap.setDrawResult(true);
            grawMap.setA(dijkstra.getA());
            textLog.setText(dijkstra.getPath());
            grawMap.setP(dijkstra.getP());
            grawMap.setInfinity(dijkstra.getInfinity());
            grawMap.setLen(dijkstra.getLen());
            grawMap.setCheckedPointMin(dijkstra.getCheckedPointMin());
            grawMap.repaint();
        }
    }

    private JTextArea textLog;

    private JPanel creatLogPanel() {
        textLog = new JTextArea("");
        textLog.setRows(3);
        textLog.setEditable(false);
        JScrollPane scrollPath = new JScrollPane(textLog);

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
            actionOpen();
        }

        if (command == "Exit") {
            System.exit(0);
        }


        if (command == "Move") {
            grawMap.setDraw(3);
        }

        if (command == "Update") {
            updateView();
        }

        if (command == "Save") {
            actionSave();
        }

    }

    private void actionSave() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save graph");
        int select = fc.showSaveDialog(this);
        if (select == 0) {
            String path = fc.getSelectedFile().getPath();
            System.out.println(path);
            grawMap.write(path);
        }
    }

    private void actionOpen() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Open graph");
        int select = fc.showOpenDialog(this);
        if (select == 0) {
            String path = fc.getSelectedFile().getPath();
            System.out.println(path);
            grawMap.readFile(path);
            actionUpdate();
        }
    }

    private void actionUpdate() {
        resetDataDijkstra();
        setDrawResultOrStep(false);
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

    private static JFrame createFrame() {
        JFrame frame = new JFrame("Suggestion Dropdown Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(600, 300));
        return frame;
    }

}