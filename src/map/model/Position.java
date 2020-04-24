package map.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

public class Position implements Serializable {
	private static final long serialVersionUID = 1L;
	private Ellipse2D.Float el = new Ellipse2D.Float();
	private java.awt.Point p = new java.awt.Point();
	private  String name;
	final int radius = 5;
	public void drawPositionName(Graphics2D g, int index, Color colorIndex) {
		g.setColor(colorIndex);
		int len = (int) g.getFontMetrics()
				.getStringBounds(String.valueOf(index), g).getWidth();
		int stringHeight = (int) g.getFontMetrics()
				.getStringBounds(String.valueOf(index), g).getHeight();
		int startX = -len / 2;
		int startY = stringHeight / 2;
		g.drawString(String.valueOf(index), startX + (int) p.x, (int) p.y
				+ startY);
	}

	public void drawPath(Graphics2D g, String km, Color kmColor) {
//		g.setColor(kmColor);
//		g.drawString(String.valueOf(10), (int) p.x - r / 5, (int) p.y - r);
	}

	public void drawPositionPoint(Graphics2D g, int index, Color colorPoint) {
		g.setColor(colorPoint);
		g.fill(el);
	}

	public void drawFinalMap(Graphics2D g, int index, Color colorPoint, Color colorIndex, String cost, Color colorCostResult) {
		drawPositionPoint(g, index, colorPoint);
		drawPositionName(g, index, colorIndex);
		drawPath(g, cost, colorCostResult);
	}

	public void draw(Graphics2D g, int index, Color colorPoint, Color colorIndex) {
		drawPositionPoint(g, index, colorPoint);
		drawPositionName(g, index, colorIndex);
	}

	public Ellipse2D.Float getEl() {
		return el;
	}

	public void setEl(Ellipse2D.Float el) {
		this.el = el;
		this.p.x = (int) (el.x + radius);
		this.p.y = (int) (el.y + radius);
	}

	public java.awt.Point getP() {
		return p;
	}

	public void setP(java.awt.Point p) {
		this.p = p;
	}

	public Position(Ellipse2D.Float el,String name) {
		super();
		setEl(el);
		this.name = name;
	}

	public Position(Ellipse2D.Float el) {
		super();
		setEl(el);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
