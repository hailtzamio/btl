package map;

import map.model.Pathz;
import map.model.Data;
import map.model.Position;
import map.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.*;

class DrawMap extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private static final long serialVersionUID = 1L;
    private Data data = new Data();
    private int minPath[];
    private int coList[][];
    private int previous[];
    private int infinity;
    private int x = 0, y = 0, radius = 15, r2 = 2 * radius;
    private int positionNumber;
    private Point pointBeginLine;
    private Point point;
    boolean checkDrawLine = false, movePosition = true;
    private int draw = 0;
    private Color bgColor = Color.lightGray, kmColor = Color.BLACK, indexColor = Color.black, lineColor = Color.white, foundWayColor = Color.GREEN;
    private int sizePath = 6, sizeFoundWay = 6;
    private boolean drawPath = false;
    private boolean reDraw = false;
    private boolean typeMap = false;
    private boolean checkedPointMin[];
    private int fromPosition, toPosition;
    private int drawWith, drawHeight;
    private Utils utils = new Utils();

    private double zoomFactor = 1;
    private double prevZoomFactor = 1;
    private boolean zoomer;
    private boolean dragger;
    private boolean released;
    private double xOffset = 0;
    private double yOffset = 0;
    private int xDiff;
    private int yDiff;
    private Point startPoint;

    public DrawMap() {
        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        if (zoomer) {
            AffineTransform at = new AffineTransform();

            double xRel = MouseInfo.getPointerInfo().getLocation().getX() - getLocationOnScreen().getX();
            double yRel = MouseInfo.getPointerInfo().getLocation().getY() - getLocationOnScreen().getY();
            double zoomDiv = zoomFactor / prevZoomFactor;

            xOffset = (zoomDiv) * (xOffset) + (1 - zoomDiv) * xRel;
            yOffset = (zoomDiv) * (yOffset) + (1 - zoomDiv) * yRel;

            at.translate(xOffset, yOffset);
            at.scale(zoomFactor, zoomFactor);
            prevZoomFactor = zoomFactor;
            g2.transform(at);
            zoomer = false;
        }

        if (dragger) {
            AffineTransform at = new AffineTransform();
            at.translate(xOffset + xDiff, yOffset + yDiff);
            at.scale(zoomFactor, zoomFactor);
            g2.transform(at);

            if (released) {
                xOffset += xDiff;
                yOffset += yDiff;
                dragger = false;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(bgColor);
        Graphics2D g2d = (Graphics2D) g;
        loadMap(g2d);

        if (drawPath) {
            drawMap(g2d);
        }

        if (reDraw) {
            loadMap(g2d);
            reDraw = false;
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        if (draw == 1) {
            Ellipse2D.Float el = new Ellipse2D.Float(x - radius, y - radius, r2, r2);
            Position mp = new Position(el);
            data.getPositions().add(mp);
            repaint();
        }

        if (e.getButton() == MouseEvent.BUTTON3) {
            isRightClick = true;
            pointRight = e.getPoint();
        } else {
            pointBeginLine = e.getPoint();
            positionNumber = getIndexFromPoint(pointBeginLine);
            if (positionNumber != -1 && positionNumber < data.getPositions().size()) {
                String name = data.getPositions().get(positionNumber).getName();
                System.out.println(name);
                JOptionPane.showMessageDialog(null, name, "Địa Điểm",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        startPoint = MouseInfo.getPointerInfo().getLocation();
        pointBeginLine = e.getPoint();
        point = e.getPoint();
        e.getPoint();
        e.getPoint();
        System.out.println(fromPosition + " to " + toPosition);
//        if (fromPosition == 0) {
//            fromPosition = getIndexFromPoint(pointBeginLine);
//        } else {
//            toPosition = getIndexFromPoint(pointBeginLine);
//        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        updatePath();
        repaint();
        movePosition = true;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        //Zoom in
        zoomer = true;
        //Zoom in
        if (e.getWheelRotation() < 0) {
            zoomFactor *= 1.1;
            zoomer = true;
            repaint();
        }
        //Zoom out
        if (e.getWheelRotation() > 0) {
            zoomFactor /= 1.1;
            zoomer = false;
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

        if (movePosition) {
            positionNumber = getIndexFromPoint(pointBeginLine);
            if (positionNumber > 0) {
                movePosition = false;
            }
        }

        if (draw == 2 || draw == 1 || positionNumber >= 0) {
            int dx = e.getX() - point.x;
            int dy = e.getY() - point.y;

            if ((draw == 1) && positionNumber > 0) {
                Ellipse2D.Float el = data.getPositions()
                        .get(positionNumber).getEl();

                el.x += dx;
                el.y += dy;
                data.getPositions().get(positionNumber).setEl(el);
            }

            updatePath();
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
            path += ".dij";
            FileOutputStream f = new FileOutputStream(path);
            ObjectOutputStream oStream = new ObjectOutputStream(f);
            oStream.writeObject(data);
            oStream.close();
            JOptionPane.showMessageDialog(null, "Thành công", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Lưu", "Lỗi",
                    JOptionPane.OK_OPTION);
            System.out.println("Error save file\n" + e.toString());
        }
    }

    public void readFile(String path) {
        Data data = null;
        FileInputStream fi;
        try {
            fi = new FileInputStream(path);
            ObjectInputStream oiStream = new ObjectInputStream(fi);
            data = (Data) oiStream.readObject();
            oiStream.close();
            this.data = data;

            for (int i = 0; i < data.getPathzs().size(); i++) {
                int A = data.getPathzs().get(i).getPositionA();
                int B = data.getPathzs().get(i).getPositionB();
                double x1 = data.getPathzs().get(i).getL().x1;
                double y1 = data.getPathzs().get(i).getL().y1;
                double x2 = data.getPathzs().get(i).getL().x2;
                double y2 = data.getPathzs().get(i).getL().x2;
                int km = data.getPathzs().get(i).getPath();
                String name = data.getPathzs().get(i).getStreetName();
                System.out.println("line-" + x1 + "-" + y1 + "-" + x2 + "-" + y2 + "-" + A + "-" + B + "-" + km + "-" + name);
            }

            for (int i = 0; i < data.getPositions().size(); i++) {
                float w = data.getPositions().get(i).getEl().width;
                float h = data.getPositions().get(i).getEl().height;
                double x1 = data.getPositions().get(i).getEl().x;
                double y1 = data.getPositions().get(i).getEl().y;
                String name = data.getPositions().get(i).getName();
                System.out.println("point-" + x1 + "-" + y1 + "-" + w + "-" + h + "-" + name);
            }

            repaint();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "File not found", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("finish");
    }

    protected int getIndexFromPoint(Point point) {
        for (int i = 1; i < data.getPositions().size(); i++) {
            if (data.getPositions().get(i).getEl().getBounds2D()
                    .contains(point)) {
                return i;
            }
        }
        return -1;
    }

    protected void setCheckedPosition(int i) {
        data.getPositions().get(i).setPositionCheck(true);
        repaint();
    }

    protected void resetCheckedPosition() {
        for (int i = 1; i < data.getPositions().size(); i++) {
            data.getPositions().get(i).setPositionCheck(false);
        }

        System.out.println("========== Reset Point");
    }

    private Line2D.Double drawLine(Point p1, Point p2) {
        Line2D.Double l = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
        return l;
    }

    private void updatePath() {
        for (int i = 0; i < data.getPathzs().size(); i++) {
            data.getPathzs()
                    .get(i)
                    .setL(drawLine(
                            data.getPositions()
                                    .get(data.getPathzs().get(i)
                                            .getPositionA()).getP(),
                            data.getPositions()
                                    .get(data.getPathzs().get(i)
                                            .getPositionB()).getP()));
        }
    }

    private void loadMap(Graphics2D g2d) {
        for (int i = 0; i < data.getPathzs().size(); i++) {
            data.getPathzs().get(i).drawPath(g2d, data.getPositions().get(data.getPathzs().get(i).getPositionA()).getP(), data.getPositions().get(data.getPathzs().get(i).getPositionB()).getP(),
                    kmColor, lineColor, sizePath, typeMap,data.getPathzs().get(i).isShowStreetName());
        }

        for (int i = 1; i < data.getPositions().size(); i++) {
            data.getPositions().get(i).drawFinalMap(g2d, i, "",Color.BLACK, data.getPositions().get(i).isPositionCheck());
        }
    }

    private void drawMap(Graphics2D g2d) {
        if(toPosition != -1) {
            if (checkedPointMin[toPosition]) {
                String km;
                int i = toPosition;
                while (i != fromPosition) {
                    km = String.valueOf(minPath[i]);
                    Pathz ml = new Pathz(drawLine(data.getPositions().get(previous[i])
                            .getP(), data.getPositions().get(i).getP()), i, previous[i],
                            coList[previous[i]][i], "",true);

                    ml.drawPath(g2d, data.getPositions().get(previous[i]).getP(), data
                                    .getPositions().get(i).getP(), kmColor, foundWayColor,
                            sizeFoundWay, typeMap,data.getPathzs().get(i).isShowStreetName());

                    data.getPositions()
                            .get(i)
                            .drawFinalMap(g2d, i, km,
                                    foundWayColor,data.getPositions().get(i).isPositionCheck());

                    i = previous[i];
                }

                km = String.valueOf(minPath[i]);
                data.getPositions()
                        .get(fromPosition)
                        .drawFinalMap(g2d, fromPosition,
                                km, foundWayColor,data.getPositions().get(i).isPositionCheck());

            }
        }
    }

    public void readFile() {
        this.data = utils.getDataFromFile();
        updatePath();
    }

    protected boolean isRightClick = false;
    protected Point pointRight;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public void setReDraw(boolean reDraw) {
        this.reDraw = reDraw;
    }

    public void setFromPosition(int fromPosition) {
        this.fromPosition = fromPosition;
    }

    public void setToPosition(int toPosition) {
        this.toPosition = toPosition;
    }

    public void setCheckedPointMin(boolean[] checkedPointMin) {
        this.checkedPointMin = checkedPointMin;
    }

    public void setPrevious(int[] previous) {
        this.previous = previous;
    }

    public void setCoList(int[][] coList) {
        this.coList = coList;
    }

    public void setInfinity(int infinity) {
        this.infinity = infinity;
    }

    public void setMinPath(int[] minPath) {
        this.minPath = minPath;
    }

    public void setDrawPath(boolean drawPath) {
        this.drawPath = drawPath;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

}