package map.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<Position> positions;
	private ArrayList<Pathz> pathzs;

	final int r = 10, r2 = 2 * r;

	public ArrayList<Position> getPositions() {
		return positions;
	}

	public void setPositions(ArrayList<Position> positions) {
		this.positions = positions;
	}

	public ArrayList<Pathz> getPathzs() {
		return pathzs;
	}

	public void setPathzs(ArrayList<Pathz> pathzs) {
		this.pathzs = pathzs;
	}

	public Data() {
		positions = new ArrayList<Position>();
		pathzs = new ArrayList<Pathz>();
	}
}
