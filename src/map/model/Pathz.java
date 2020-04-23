package map.model;

import java.awt.*;
import java.awt.geom.Line2D;
import java.io.Serializable;

public class Pathz implements Serializable {
    private static final long serialVersionUID = 1L;
    private Line2D.Double l = new Line2D.Double();
    private int indexPointA, indexPointB;
    private int path;
    private String streetName;
    final int barb = 10;
    final int r = 10;
    final double phi = Math.PI / 6;

    public Pathz(Line2D.Double l, int indexPointA, int indexPointB, int path, String streetName) {
        this.path = path;
        this.indexPointA = indexPointA;
        this.indexPointB = indexPointB;
        this.l = l;
        this.streetName = streetName;
    }

    private void drawArrow(Graphics2D g, double theta, double x0, double y0,
                           Color colorLine, int size) {
        double x = x0 - barb * Math.cos(theta + phi);
        double y = y0 - barb * Math.sin(theta + phi);
        g.setStroke(new BasicStroke(size));
        g.draw(new Line2D.Double(x0, y0, x, y));
        x = x0 - barb * Math.cos(theta - phi);
        y = y0 - barb * Math.sin(theta - phi);
        g.draw(new Line2D.Double(x0, y0, x, y));
    }

    public void drawLine(Graphics2D g, Point p1, Point p2, Color colorCost,
                         Color colorLine, int size, boolean type) {
        String streetName = "";
        String km = "";
        if (path < 0) {
            streetName = "";
            km = "";
        } else {
            km = path + " m";
            streetName = this.streetName;
        }

        g.setColor(colorLine);
        g.setStroke(new BasicStroke(size));
        double theta = Math.atan2(p2.y - p1.y, p2.x - p1.x);
        g.draw(l);
        if (type && path >= 0) {
            double x = p2.x - r * Math.cos(theta);
            double y = p2.y - r * Math.sin(theta);
            drawArrow(g, theta, x, y, colorLine, size);
        }

        g.setColor(colorCost);
        g.drawString(streetName, (int) (Math.abs(p1.x + p2.x) / 2),
                (int) (p1.y + p2.y) / 2);
        g.drawString(km, (int) (Math.abs(p1.x + 10 + p2.x + 10) / 2),
                (int) (p1.y + 10 + p2.y + 10) / 2);
        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 0.95F);
        g.setFont(newFont);
    }

    public boolean containerPoint(Point p) {
        Polygon poly = createPolygon(l);
        for (int i = 0; i < poly.npoints; i++) {
            double temp = (p.x - poly.xpoints[i])
                    * (poly.ypoints[(i + 1) % poly.npoints] - poly.ypoints[i])
                    - (p.y - poly.ypoints[i])
                    * (poly.xpoints[(i + 1) % poly.npoints] - poly.xpoints[i]);
            if (temp < 0)
                return false;
        }
        return true;
    }

    private Polygon createPolygon(Line2D line) {
        int barb = 5;
        double phi = Math.PI / 2;
        double theta = Math.atan2(line.getY2() - line.getY1(), line.getX2()
                - line.getX1());
        int x[] = new int[4];
        int y[] = new int[4];
        x[0] = (int) (line.getX1() - barb * Math.cos(theta + phi));
        y[0] = (int) (line.getY1() - barb * Math.sin(theta + phi));
        x[1] = (int) (line.getX1() - barb * Math.cos(theta - phi));
        y[1] = (int) (line.getY1() - barb * Math.sin(theta - phi));

        x[2] = (int) (line.getX2() - barb * Math.cos(theta - phi));
        y[2] = (int) (line.getY2() - barb * Math.sin(theta - phi));
        x[3] = (int) (line.getX2() - barb * Math.cos(theta + phi));
        y[3] = (int) (line.getY2() - barb * Math.sin(theta + phi));
        Polygon poly = new Polygon(x, y, 4);
        return poly;
    }

    public Line2D.Double getL() {
        return l;
    }

    public void setL(Line2D.Double l) {
        this.l = l;
    }

    public int getIndexPointA() {
        return indexPointA;
    }

    public void setIndexPointA(int indexPointA) {
        this.indexPointA = indexPointA;
    }

    public int getIndexPointB() {
        return indexPointB;
    }

    public void setIndexPointB(int indexPointB) {
        this.indexPointB = indexPointB;
    }

    public int getPath() {
        return path;
    }

    public void setPath(int path) {
        this.path = path;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
}