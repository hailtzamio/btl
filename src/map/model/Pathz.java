package map.model;

import java.awt.*;
import java.awt.geom.Line2D;
import java.io.Serializable;

public class Pathz implements Serializable {

    private Line2D.Double l = new Line2D.Double();
    private int positionA, positionB;
    private int path;
    private String streetName;

    public boolean isShowStreetName() {
        return isShowStreetName;
    }

    public void setShowStreetName(boolean showStreetName) {
        isShowStreetName = showStreetName;
    }

    private boolean isShowStreetName;
    final int radius = 10;

    public Pathz(Line2D.Double l, int positionA, int positionB, int path, String streetName, boolean isShowStreetName) {
        this.path = path;
        this.positionA = positionA;
        this.positionB = positionB;
        this.l = l;
        this.streetName = streetName;
        this.isShowStreetName = isShowStreetName;
    }

    public void drawPath(Graphics2D g, Point p1, Point p2, Color colorkm,
                         Color colorLine, int size, boolean type, boolean isShowStreetName) {
        String streetName = "";
        String km = "";
        if (path <= 0) {
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
            double x = p2.x - radius * Math.cos(theta);
            double y = p2.y - radius * Math.sin(theta);
        }

        g.setColor(colorkm);

        // Show Street name
        if (isShowStreetName) {
//            g.drawString(streetName, (int) (Math.abs(p1.x + p2.x) / 2),
//                    (int) (p1.y + p2.y) / 2);


            int centerX = p1.x + ((p2.x - p1.x) / 2);
            int centerY = p1.y + ((p2.y - p1.y) / 2);
            double pRote = Math.toDegrees(Math.atan2(centerY - p2.y, centerX - p2.x) + Math.PI);
            if ((pRote > 90) && (pRote < 270)) {
                pRote += 180;
            }

            double angle = Math.toRadians(pRote);
            Font smallFont = new Font("Monospaced", Font.PLAIN, 12);
            FontMetrics fm = g.getFontMetrics(smallFont);
            int sw = fm.stringWidth(streetName);
            g.rotate(angle, centerX, centerY);
            g.drawString(streetName, centerX - (sw / 2), centerY - 10);
            g.rotate(-angle, centerX, centerY);
        }

        // Show km
//        g.drawString(km, (int) (Math.abs(p1.x + 10 + p2.x + 10) / 2),
//                (int) (p1.y + 10 + p2.y + 10) / 2);
    }


    public Line2D.Double getL() {
        return l;
    }

    public void setL(Line2D.Double l) {
        this.l = l;
    }

    public int getPositionA() {
        return positionA;
    }

    public void setPositionA(int positionA) {
        this.positionA = positionA;
    }

    public int getPositionB() {
        return positionB;
    }

    public void setPositionB(int positionB) {
        this.positionB = positionB;
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