package map;

import map.uicommon.Help;
import map.uicommon.SuggestionDropDownDecorator;
import map.uicommon.TextComponentSuggestionClient;
import map.uicommon.TextComponentWordSuggestionClient;
import map.util.RandomUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

public class MapLayout extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    private JFrame frameAbout, frameHelp;
    private String data[][], head[];

    private JComboBox<String> cbbBeginPoint = new JComboBox<String>();
    private JComboBox<String> cbbEndPoint = new JComboBox<String>();
    private JComboBox<String> cbbGraphDemo = new JComboBox<String>();

    private JButton btnRunAll, btnRunStep;
    private JPanel drawPanel = new JPanel();
    private MyDraw myDraw = new MyDraw();

    private int indexBeginPoint = 0, indexEndPoint = 0;
    private int step = 0;
    private boolean mapType = false;

    int WIDTH_SELECT, HEIGHT_SELECT;
    MyDijkstra dijkstra = new MyDijkstra();

    public MapLayout(String title) {
        setTitle(title);
        setLayout(new BorderLayout(5, 5));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(creatMenu(), BorderLayout.PAGE_START);
        add(creatSelectPanel(), BorderLayout.WEST);
        add(creatPaintPanel(), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        drawDemo();
    }

    private JMenuBar creatMenu() {
        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuFile.addSeparator();
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

        JPanel panelSelectPointTemp = new JPanel(new GridLayout(1, 2, 15, 5));
        panelSelectPointTemp.setBorder(new EmptyBorder(0, 15, 0, 5));
        panelSelectPointTemp.add(cbbBeginPoint = CustomComboxBox("Bắt đầu"));
        panelSelectPointTemp.add(cbbEndPoint = CustomComboxBox("Kết thúc"));
        JPanel panelSelectPoint = new JPanel(new BorderLayout());
        panelSelectPoint.setBorder(new TitledBorder("Vị trí"));
        panelSelectPoint.add(panelSelectPointTemp);

        JPanel panelRunTemp = new JPanel(new GridLayout(1, 2, 15, 5));
        panelRunTemp.setBorder(new EmptyBorder(0, 15, 0, 5));
        panelRunTemp.add(btnRunAll = CustomButton("Tìm"));
        panelRunTemp.add(btnRunStep = CustomButton("Từng Bước"));
        JPanel panelRun = new JPanel(new BorderLayout());
        panelRun.setBorder(new TitledBorder("Tìm đường"));
        panelRun.add(panelRunTemp);

        panelTop.add(panelSelectPoint);
        panelTop.add(panelRun);

        makeJTextFieldGoFrom(panelTop, "Điểm đi");
        makeJTextFieldGoFrom(panelTop, "Điểm đến");

        panel.add(panelTop, BorderLayout.PAGE_START);
        panel.add(panelBottom, BorderLayout.CENTER);
        panel.setBorder(new EmptyBorder(0, 5, 0, 0));
        WIDTH_SELECT = (int) panel.getPreferredSize().getWidth();
        HEIGHT_SELECT = (int) panel.getPreferredSize().getHeight();
        return panel;
    }

    private void makeJTextFieldGoFrom(JPanel panelTop, String title) {
        JPanel panelSmall = new JPanel(new GridLayout(1, 2, 15, 5));
        panelSmall.setBorder(new EmptyBorder(0, 15, 0, 5));
        setupSuggestJTextField(panelSmall);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder(title));
        panel.add(panelSmall);
        panelTop.add(panel);
    }

    private void setupSuggestJTextField(JPanel panel) {
        JTextField textField = new JTextField(10);
        SuggestionDropDownDecorator.decorate(textField,
                new TextComponentSuggestionClient(MapLayout::getSuggestions));
        JTextPane textPane = new JTextPane();
        SuggestionDropDownDecorator.decorate(textPane,
                new TextComponentWordSuggestionClient(MapLayout::getSuggestions));
        panel.add(textField);
    }

    private JPanel creatPaintPanel() {
        drawPanel.setLayout(new BoxLayout(drawPanel, BoxLayout.Y_AXIS));
        drawPanel.setBorder(new TitledBorder(""));
        drawPanel.setBackground(null);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(drawPanel, BorderLayout.WEST);
        panel.add(myDraw, BorderLayout.CENTER);
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
        updateListPoint();
        resetDataDijkstra();
        setDrawResultOrStep(false);
        reDraw();
    }

    private void actionDrawPoint() {
        myDraw.setDraw(1);
        setDrawResultOrStep(false);
    }

    private void actionDrawLine() {
        myDraw.setDraw(2);
        setDrawResultOrStep(false);
    }

    private void actionChoosePoint() {
        resetDataDijkstra();
        setDrawResultOrStep(false);
        reDraw();
    }

    private void updateListPoint() {
        int size = myDraw.getData().getArrMyPoint().size();
        String listPoint[] = new String[size];
        listPoint[0] = "Begin";
        for (int i = 1; i < listPoint.length; i++) {
            listPoint[i] = String.valueOf(i);
        }

        cbbBeginPoint.setModel(new DefaultComboBoxModel<String>(listPoint));
        cbbBeginPoint.setMaximumRowCount(5);

        if (size > 1) {
            listPoint = new String[size + 1];
            listPoint[0] = "End";
            for (int i = 1; i < listPoint.length; i++) {
                listPoint[i] = String.valueOf(i);
            }
            listPoint[listPoint.length - 1] = "All";
        } else {
            listPoint = new String[1];
            listPoint[0] = "End";
        }

        cbbEndPoint.setModel(new DefaultComboBoxModel<String>(listPoint));
        cbbEndPoint.setMaximumRowCount(5);
    }

    private void setEnableDraw(boolean check, String matrix) {
        cbbGraphDemo.setEnabled(!check);
    }

    private void setEnableMapType(boolean mapType) {
        this.mapType = mapType;
        myDraw.setTypeMap(mapType);
        setDrawResultOrStep(false);
        myDraw.repaint();
        resetDataDijkstra();
    }

    private void setDrawResultOrStep(boolean check) {
        myDraw.setDrawResult(check);
        myDraw.setDrawStep(check);
    }

    private void resetDataDijkstra() {
        step = 0;
        dijkstra = new MyDijkstra();
        dijkstra.setMapType(mapType);
        dijkstra.setArrMyPoint(myDraw.getData().getArrMyPoint());
        dijkstra.setArrMyLine(myDraw.getData().getArrMyLine());
        dijkstra.input();
        dijkstra.processInput();
    }

    private void reDraw() {
        myDraw.setReDraw(true);
        myDraw.repaint();
    }

    private void drawDemo() {
        int demo = cbbGraphDemo.getSelectedIndex();
        myDraw.readDemoTest(demo);
        updateView();
    }

    private boolean checkRun() {
        int size = myDraw.getData().getArrMyPoint().size() - 1;
        indexBeginPoint = cbbBeginPoint.getSelectedIndex();
        indexEndPoint = cbbEndPoint.getSelectedIndex();
        if (indexEndPoint == size + 1) {
            indexEndPoint = -1;
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
        myDraw.setIndexBeginPoint(indexBeginPoint);
        myDraw.setIndexEndPoint(indexEndPoint);
        dijkstra.setBeginPoint(indexBeginPoint);
        dijkstra.setEndPoint(indexEndPoint);
    }

    private void runAll() {
        if (checkRun()) {
            resetDataDijkstra();
            setBeginEndPoint();
            dijkstra.dijkstra();
            myDraw.setDrawStep(false);
            myDraw.setDrawResult(true);
            myDraw.setA(dijkstra.getA());
            myDraw.setP(dijkstra.getP());
            myDraw.setInfinity(dijkstra.getInfinity());
            myDraw.setLen(dijkstra.getLen());
            myDraw.setCheckedPointMin(dijkstra.getCheckedPointMin());
            myDraw.repaint();
        }
    }

    private void runStep() {
        if (checkRun()) {
            setBeginEndPoint();
            dijkstra.dijkstraStep(++step);
            myDraw.setDrawStep(true);
            myDraw.setDrawResult(false);
            myDraw.setA(dijkstra.getA());
            myDraw.setP(dijkstra.getP());
            myDraw.setArrPointResultStep(dijkstra.getArrPointResultStep());
            myDraw.setLen(dijkstra.getLen());
            myDraw.setCheckedPointMin(dijkstra.getCheckedPointMin());
            myDraw.setInfinity(dijkstra.getInfinity());
            myDraw.repaint();
        }
    }

    private void showHelp() {
        if (frameHelp == null) {
            frameHelp = new Help(0, "Help");
        }
        frameHelp.setVisible(true);
    }

    private void showAbout() {
        if (frameAbout == null) {
            frameAbout = new Help(1, "About");
        }
        frameAbout.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (e.getSource() == cbbBeginPoint || e.getSource() == cbbEndPoint) {
            actionChoosePoint();
        }

        if (e.getSource() == btnRunStep) {
            runStep();
        }

        if (e.getSource() == btnRunAll) {
            runAll();
        }

        if (command == "Exit") {
            System.exit(0);
        }

        if (command == "About") {
            showAbout();
        }

        if (command == "Move") {
            myDraw.setDraw(3);
        }

        if (command == "Update") {
            updateView();
        }

        if (command == "Help") {
            showHelp();
        }

    }

    private static List<String> words =
            RandomUtil.getAddress();

    private static List<String> getSuggestions(String input) {
        if (input.isEmpty()) {
            return null;
        }
        return words.stream()
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