package map.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MyData implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<Position> arrMyPoint;
	private ArrayList<Pathz> arrMyLine;

	final int r = 15, r2 = 2 * r;

	public ArrayList<Position> getArrMyPoint() {
		return arrMyPoint;
	}

	public void setArrMyPoint(ArrayList<Position> arrMyPoint) {
		this.arrMyPoint = arrMyPoint;
	}

	public ArrayList<Pathz> getArrMyLine() {
		return arrMyLine;
	}

	public void setArrMyLine(ArrayList<Pathz> arrMyLine) {
		this.arrMyLine = arrMyLine;
	}

	public MyData() {
		arrMyPoint = new ArrayList<Position>();
		arrMyLine = new ArrayList<Pathz>();
	}
}
