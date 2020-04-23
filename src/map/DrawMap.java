package map;

import map.model.Pathz;
import map.model.MyData;
import map.model.Position;
import map.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.*;
import java.util.ArrayList;

class DrawMap extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private static final long serialVersionUID = 1L;
    private MyData data = new MyData();
    private ArrayList<Integer> arrPointResultStep = new ArrayList<Integer>();
    private int len[];
    private int a[][];
    private int p[];
    private int infinity;
    private int x = 0, y = 0, r = 15, r2 = 2 * r;
    private int indexPointBeginLine, indexPointEndLine, indexTemp;
    private Point pointBeginLine;
    private Point point;
    boolean checkDrawLine = false, isFindPoint = true;
    private int draw = 0;
    private Color bgColor = Color.lightGray, kmColor = Color.BLACK,
            indexColor = Color.black, lineColor = Color.white,
            stepColor = Color.getHSBColor(50, 50, 50),
            colorStepMin = Color.blue, colorResult = Color.GREEN;
    private int sizeLine = 6, sizeLineResult = 6;
    private boolean drawResult = false;
    private boolean drawStep = false;
    private boolean reDraw = false;
    private boolean resetGraph = false;
    private boolean typeMap = false;
    private boolean checkedPointMin[];
    private int indexBeginPoint, indexEndPoint;
    private int drawWith, drawHeight;
    Utils utils = new Utils();
    public DrawMap() {
        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(bgColor);
        Graphics2D g2d = (Graphics2D) g;
        reDraw(g2d, false);
        if (drawResult) {
            if (indexEndPoint == -1) {
                drawResultAllPoint(g2d);
            } else {
                drawResult(g2d);
            }
        }

        if (drawStep) {
            drawResultStep(g2d);
        }

        if (reDraw) {
            reDraw(g2d, true);
            reDraw = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        if (draw == 1) {
            Ellipse2D.Float el = new Ellipse2D.Float(x - r, y - r, r2, r2);
            Position mp = new Position(el);
            data.getPositions().add(mp);
            repaint();
        }

        if (e.getButton() == MouseEvent.BUTTON3) {
            isRightClick = true;
            pointRight = e.getPoint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pointBeginLine = e.getPoint();
        point = e.getPoint();
        e.getPoint();
        e.getPoint();
        data.getPositions().get(indexTemp).getEl().x = e.getX() - r;
        data.getPositions().get(indexTemp).getEl().y = e.getY() - r;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        data.getPathzs()
                .get(indexTemp)
                .setIndexPointA(
                        data.getPathzs().get(indexTemp).getIndexPointB());
        updateLine();
        repaint();
        isFindPoint = true;
    }

    private double zoomFactor = 1;
    private double prevZoomFactor = 1;
    private boolean zoomer;
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //Zoom in
        zoomer = true;
        //Zoom in
        if (e.getWheelRotation() < 0) {
            zoomFactor *= 1.1;
            repaint();
        }
        //Zoom out
        if (e.getWheelRotation() > 0) {
            zoomFactor /= 1.1;
            repaint();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isFindPoint) {
            indexPointBeginLine = indexPointContain(pointBeginLine);
            if (indexPointBeginLine > 0) {
                isFindPoint = false;
            }
        }

        if (draw == 2 || draw == 1 || indexPointBeginLine >= 0) {
            int dx = e.getX() - point.x;
            int dy = e.getY() - point.y;

            if ((draw == 1 || draw == 3) && indexPointBeginLine > 0) {
                Ellipse2D.Float el = data.getPositions()
                        .get(indexPointBeginLine).getEl();

                el.x += dx;
                el.y += dy;
                data.getPositions().get(indexPointBeginLine).setEl(el);
            }

            if (draw == 2 && indexPointBeginLine >= 0) {
                checkDrawLine = true;
                data.getPathzs().get(indexTemp)
                        .setIndexPointA(indexPointBeginLine);
                Ellipse2D.Float el = data.getPositions().get(indexTemp)
                        .getEl();
                el.x += dx;
                el.y += dy;
                data.getPositions().get(indexTemp).setEl(el);
            }
            updateLine();
            repaint();
            point.x += dx;
            point.y += dy;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    public void write(String path) {
        try {


            for (int i = 0; i < data.getPositions().size(); i++) {
                data.getPositions().get(i).setName("NAme");
            }

            path += ".dij";
            FileOutputStream f = new FileOutputStream(path);
            ObjectOutputStream oStream = new ObjectOutputStream(f);
            oStream.writeObject(data);
            oStream.close();
            JOptionPane.showMessageDialog(null, "Save success", "Save success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Save", "Error save file",
                    JOptionPane.OK_OPTION);
            System.out.println("Error save file\n" + e.toString());
        }
    }

    public void readFile(String path) {
        MyData data = null;
        FileInputStream fi;
        try {
            fi = new FileInputStream(path);
            ObjectInputStream oiStream = new ObjectInputStream(fi);
            data = (MyData) oiStream.readObject();
            oiStream.close();
            this.data = data;

            for (int i = 0; i < data.getPathzs().size(); i++) {
                int A =  data.getPathzs().get(i).getIndexPointA();
                int B =  data.getPathzs().get(i).getIndexPointB();
                double x1 =  data.getPathzs().get(i).getL().x1;
                double y1 =  data.getPathzs().get(i).getL().y1;
                double x2 =  data.getPathzs().get(i).getL().x2;
                double y2 =  data.getPathzs().get(i).getL().x2;
                int km =  data.getPathzs().get(i).getPath();
                System.out.println("line-" + x1 + "-" + y1 + "-" + x2 + "-" + y2 + "-" + A + "-" + B + "-" + km + "-" + "name");
            }

            for (int i = 0; i < data.getPositions().size(); i++) {
                float w =  data.getPositions().get(i).getEl().width;
                float h =  data.getPositions().get(i).getEl().height;
                double x1 =  data.getPositions().get(i).getEl().x;
                double y1 =  data.getPositions().get(i).getEl().y;
                System.out.println("point-" + x1 + "-" + y1 + "-" + w + "-" + h +"-"+ "name");
            }

            repaint();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File not found", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error read file\nFile open must is *.dij", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error read class", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("done read");
    }

    protected int indexPointContain(Point point) {
        for (int i = 1; i < data.getPositions().size(); i++) {
            if (data.getPositions().get(i).getEl().getBounds2D()
                    .contains(point)) {
                return i;
            }
        }
        return -1;
    }

    private Line2D.Double creatLine(Point p1, Point p2) {
        Line2D.Double l = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
        return l;
    }

    private void updateLine() {
        for (int i = 0; i < data.getPathzs().size(); i++) {
            data.getPathzs()
                    .get(i)
                    .setL(creatLine(
                            data.getPositions()
                                    .get(data.getPathzs().get(i)
                                            .getIndexPointA()).getP(),
                            data.getPositions()
                                    .get(data.getPathzs().get(i)
                                            .getIndexPointB()).getP()));
        }
    }

    public void resetGraph(Graphics2D g2d) {
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, 600, 600);
    }

    private void reDraw(Graphics2D g2d, boolean checkReDraw) {
        resetGraph(g2d);
        for (int i = 0; i < data.getPathzs().size(); i++) {
            data.getPathzs().get(i).drawLine(g2d, data.getPositions().get(data.getPathzs().get(i).getIndexPointA()).getP(), data.getPositions().get(data.getPathzs().get(i).getIndexPointB()).getP(),
                    kmColor, lineColor, sizeLine, typeMap);
        }

        for (int i = 1; i < data.getPositions().size(); i++) {
            data.getPositions().get(i).draw(g2d, i, lineColor, indexColor);
        }
    }

    private void drawResult(Graphics2D g2d) {
        if (checkedPointMin[indexEndPoint]) {
            String cost;
            int i = indexEndPoint;
            while (i != indexBeginPoint) {
                cost = String.valueOf(len[i]);
                Pathz ml = new Pathz(creatLine(data.getPositions().get(p[i])
                        .getP(), data.getPositions().get(i).getP()), i, p[i],
                        a[p[i]][i],"");

                ml.drawLine(g2d, data.getPositions().get(p[i]).getP(), data
                                .getPositions().get(i).getP(), kmColor, colorResult,
                        sizeLineResult, typeMap);

                data.getPositions()
                        .get(i)
                        .drawResult(g2d, i, colorResult, indexColor, cost,
                                colorResult);

                i = p[i];
            }

            cost = String.valueOf(len[i]);
            data.getPositions()
                    .get(indexBeginPoint)
                    .drawResult(g2d, indexBeginPoint, colorResult, indexColor,
                            cost, colorResult);
        }
    }

    private void drawResultAllPoint(Graphics2D g2d) {
        int size = data.getPositions().size() - 1;
        String cost;
        for (int i = 1; i <= size; i++) {
            if (i != indexBeginPoint && a[p[i]][i] < infinity && p[i] > 0) {
                cost = len[i] + "";
                Pathz ml = new Pathz(creatLine(data.getPositions().get(p[i])
                        .getP(), data.getPositions().get(i).getP()), i, p[i],
                        a[p[i]][i], "");

                ml.drawLine(g2d, data.getPositions().get(p[i]).getP(), data
                                .getPositions().get(i).getP(), kmColor, colorResult,
                        sizeLineResult, typeMap);

                data.getPositions()
                        .get(i)
                        .drawResult(g2d, i, colorResult, indexColor, cost,
                                colorResult);
            }

        }

        cost = "0";
        data.getPositions()
                .get(indexBeginPoint)
                .drawResult(g2d, indexBeginPoint, colorResult, indexColor,
                        cost, colorResult);
    }

    private void drawResultStep(Graphics2D g2d) {
        String cost;
        // draw update cost
        for (int i = 0; i < arrPointResultStep.size(); i++) {
            if ((indexEndPoint != -1 && !checkedPointMin[indexEndPoint])
                    || indexEndPoint == -1) {
                for (int j = 1; j < data.getPositions().size(); j++) {
                    cost = String.valueOf(len[j]);
                    if (p[j] > 0 && a[p[j]][j] < infinity
                            && a[arrPointResultStep.get(i)][j] < infinity
                            && !checkedPointMin[j]) {

                        Pathz ml = new Pathz(creatLine(data.getPositions()
                                .get(p[j]).getP(), data.getPositions().get(j)
                                .getP()), p[j], j,
                                a[arrPointResultStep.get(i)][j], "");

                        ml.drawLine(
                                g2d,
                                data.getPositions()
                                        .get(arrPointResultStep.get(i)).getP(),
                                data.getPositions().get(j).getP(), kmColor,
                                stepColor, sizeLine, typeMap);

                        data.getPositions()
                                .get(j)
                                .drawResult(g2d, j, stepColor, indexColor,
                                        cost, stepColor);
                    }
                }

            }

            if (p[arrPointResultStep.get(i)] > 0) {
                cost = String.valueOf(len[arrPointResultStep.get(i)]);
                Pathz ml = new Pathz(creatLine(
                        data.getPositions().get(p[arrPointResultStep.get(i)])
                                .getP(),
                        data.getPositions().get(arrPointResultStep.get(i))
                                .getP()), p[arrPointResultStep.get(i)], i,
                        a[p[arrPointResultStep.get(i)]][arrPointResultStep
                                .get(i)],"");

                ml.drawLine(g2d,
                        data.getPositions().get(p[arrPointResultStep.get(i)])
                                .getP(),
                        data.getPositions().get(arrPointResultStep.get(i))
                                .getP(), colorStepMin, colorStepMin, sizeLine,
                        typeMap);

            }

        }

        // draw point cost is min
        for (int i = 0; i < arrPointResultStep.size(); i++) {
            if (p[arrPointResultStep.get(i)] < infinity) {
                cost = String.valueOf(len[arrPointResultStep.get(i)]);
                data.getPositions()
                        .get(arrPointResultStep.get(i))
                        .drawResult(g2d, arrPointResultStep.get(i),
                                colorStepMin, indexColor, cost, colorStepMin);
            }
        }

        // draw result
        if (indexEndPoint != -1 && checkedPointMin[indexEndPoint]) {
            int i = indexEndPoint;
            while (i != indexBeginPoint) {
                cost = String.valueOf(len[i]);
                Pathz ml = new Pathz(creatLine(data.getPositions().get(i)
                        .getP(), data.getPositions().get(p[i]).getP()), i,
                        p[i], a[p[i]][i],  "hailt");

                ml.drawLine(g2d, data.getPositions().get(p[i]).getP(), data
                                .getPositions().get(i).getP(), kmColor, colorResult,
                        sizeLineResult, typeMap);

                data.getPositions()
                        .get(i)
                        .drawResult(g2d, i, colorResult, indexColor, cost,
                                colorResult);

                i = p[i];
            }
            cost = String.valueOf(len[i]);
            data.getPositions()
                    .get(indexBeginPoint)
                    .drawResult(g2d, indexBeginPoint, colorResult, indexColor,
                            cost, colorResult);
        }
    }

    public void readDemoTest(int demo) {
        this.data = utils.getDataFromFile();
        updateLine();
    }

    public int getDrawWith() {
        return drawWith;
    }

    public void setDrawWith(int drawWith) {
        this.drawWith = drawWith;
    }

    public int getDrawHeight() {
        return drawHeight;
    }

    public void setDrawHeight(int drawHeight) {
        this.drawHeight = drawHeight;
    }

    protected boolean isRightClick = false;
    protected Point pointRight;

    public MyData getData() {
        return data;
    }

    public void setData(MyData data) {
        this.data = data;
    }

    public boolean isResetGraph() {
        return resetGraph;
    }

    public void setResetGraph(boolean resetGraph) {
        this.resetGraph = resetGraph;
    }

    public boolean isReDraw() {
        return reDraw;
    }

    public void setReDraw(boolean reDraw) {
        this.reDraw = reDraw;
    }

    public void setIndexBeginPoint(int indexBeginPoint) {
        this.indexBeginPoint = indexBeginPoint;
    }

    public int getIndexBeginPoint() {
        return indexBeginPoint;
    }

    public void setIndexEndPoint(int indexEndPoint) {
        this.indexEndPoint = indexEndPoint;
    }

    public int getIndexEndPoint() {
        return indexEndPoint;
    }

    public boolean[] getCheckedPointMin() {
        return checkedPointMin;
    }

    public void setCheckedPointMin(boolean[] checkedPointMin) {
        this.checkedPointMin = checkedPointMin;
    }

    public boolean isDrawStep() {
        return drawStep;
    }

    public void setDrawStep(boolean drawStep) {
        this.drawStep = drawStep;
    }

    public ArrayList<Integer> getArrPointResultStep() {
        return arrPointResultStep;
    }

    public void setArrPointResultStep(ArrayList<Integer> arrPointResultStep) {
        this.arrPointResultStep = arrPointResultStep;
    }

    public int[] getP() {
        return p;
    }

    public void setP(int[] p) {
        this.p = p;
    }

    public int[][] getA() {
        return a;
    }

    public void setA(int[][] a) {
        this.a = a;
    }

    public int getInfinity() {
        return infinity;
    }

    public void setInfinity(int infinity) {
        this.infinity = infinity;
    }

    public int[] getLen() {
        return len;
    }

    public void setLen(int[] len) {
        this.len = len;
    }

    public boolean isDrawResult() {
        return drawResult;
    }

    public void setDrawResult(boolean drawResult) {
        this.drawResult = drawResult;
    }

    public boolean isTypeMap() {
        return typeMap;
    }

    public void setTypeMap(boolean typeMap) {
        this.typeMap = typeMap;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }


}