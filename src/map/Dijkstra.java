package map;

import map.model.Pathz;
import map.model.Position;
import map.model.Position;
import map.util.Utils;

import java.util.ArrayList;

public class Dijkstra {
    private int a[][];
    private int[] len, p;
    private int[][] logLen, logP;
    private boolean[] checkedPointMin;
    private int infinity, size = 0;
    private ArrayList<Position> positions = new ArrayList<Position>();
    private ArrayList<Pathz> pathzs = new ArrayList<Pathz>();
    private ArrayList<Integer> arrPointResult;
    private ArrayList<Integer> arrPointResultStep;
    private ArrayList<Integer> arrCostResult = new ArrayList<Integer>();
    private int beginPoint = 0, endPoint = 0;
    private int numberPointChecked = 0;
    private boolean step = false;
    private boolean stop = false;
    private boolean mapType = false;
    private String path = "";
    private ArrayList<Integer> arrTempPoint;

    public Dijkstra() {

    }

    public void input() {
        infinity = 1;
        size = positions.size();
        a = new int[size][size];
        len = new int[size];
        p = new int[size];
        checkedPointMin = new boolean[size];

        for (int i = 1; i < pathzs.size(); i++) {
            a[pathzs.get(i).getIndexPointA()][pathzs.get(i)
                    .getIndexPointB()] = pathzs.get(i).getPath();
            if (!mapType) {
                a[pathzs.get(i).getIndexPointB()][pathzs.get(i)
                        .getIndexPointA()] = pathzs.get(i).getPath();
            }
            infinity += pathzs.get(i).getPath();
        }
    }

    public void processInput() {
        for (int i = 1; i < size; i++) {
            for (int j = 1; j < size; j++) {
                if (a[i][j] == 0 && i != j) {
                    a[i][j] = infinity;
                }
            }
        }
    }

    private void initValue() {
        logLen = new int[size][size];
        logP = new int[size][size];
        for (int i = 1; i < size; i++) {
            len[i] = infinity;
            checkedPointMin[i] = false;
            p[i] = 0;
        }
        logLen[0] = len;
        logP[0] = p;
        len[beginPoint] = 0;
    }

    public int dijkstra() {
        initValue();
        int i = 1, k = 0;
        while (checkContinue(k)) {
            for (i = 1; i < size; i++)
                if (!checkedPointMin[i] && len[i] < infinity)
                    break;
            if (i >= size)
                break;
            for (int j = 1; j < size; j++)
                if (!checkedPointMin[j] && len[i] > len[j])
                    i = j;

            checkedPointMin[i] = true;
            for (int j = 1; j < size; j++) {
                if (!checkedPointMin[j] && len[i] + a[i][j] < len[j]) {
                    len[j] = len[i] + a[i][j];
                    p[j] = i;
                }

                logLen[k][j] = len[j];
                logP[k][j] = p[j];
            }
            k++;
        }
        if (endPoint == -1) {
            numberPointChecked = positions.size();
            return 0;
        }
        numberPointChecked = k;
        return len[endPoint];
    }

    private boolean checkContinue(int k) {
        if (endPoint != -1) {
            return (!checkedPointMin[endPoint]);
        }
        return (k < positions.size() - 1);
    }

    public String getPath() {
        path = "";
        String path2 = "";
        String finalPath = "";
        ArrayList<Integer> mlist = new ArrayList<>();

        if (endPoint > 0 && len[endPoint] < infinity) {
            int i = endPoint;
            while (i != beginPoint) {
                path = " > " + i + path;
                mlist.add(i);
                i = p[i];
            }

			for (int i1 = mlist.size() - 1; i1 >= 0; i1--) {
				path2 = path2 + " > " + positions.get(mlist.get(i1)).getName();
			}

            finalPath = "Chiều dài " + len[endPoint] + "m" + " : " + i + path2;
        } else {
            finalPath = "Không thể đi";
        }
        return finalPath;
    }

    public int getNumberPointChecked() {
        return numberPointChecked;
    }

    public void setNumberPointChecked(int numberPointChecked) {
        this.numberPointChecked = numberPointChecked;
    }

    public int[][] getLogLen() {
        return logLen;
    }

    public void setLogLen(int[][] logLen) {
        this.logLen = logLen;
    }

    public int[][] getLogP() {
        return logP;
    }

    public void setLogP(int[][] logP) {
        this.logP = logP;
    }

    public boolean isMapType() {
        return mapType;
    }

    public void setMapType(boolean mapType) {
        this.mapType = mapType;
    }

    public boolean[] getCheckedPointMin() {
        return checkedPointMin;
    }

    public void setCheckedPointMin(boolean[] checkedPointMin) {
        this.checkedPointMin = checkedPointMin;
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

    public ArrayList<Integer> getArrTempPoint() {
        return arrTempPoint;
    }

    public void setArrTempPoint(ArrayList<Integer> arrTempPoint) {
        this.arrTempPoint = arrTempPoint;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isStep() {
        return step;
    }

    public void setStep(boolean step) {
        this.step = step;
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

    public int[][] getA() {
        return a;
    }

    public void setA(int[][] a) {
        this.a = a;
    }

    public int getBeginPoint() {
        return beginPoint;
    }

    public void setBeginPoint(int beginPoint) {
        this.beginPoint = beginPoint;
    }

    public int getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(int endPoint) {
        this.endPoint = endPoint;
    }

    public ArrayList<Position> getArrMyPoint() {
        return positions;
    }

    public void setArrMyPoint(ArrayList<Position> arrMyPoint) {
        this.positions = arrMyPoint;
    }

    public ArrayList<Pathz> getArrMyLine() {
        return pathzs;
    }

    public void setArrMyLine(ArrayList<Pathz> arrMyLine) {
        this.pathzs = arrMyLine;
    }

    public ArrayList<Integer> getArrPointResult() {
        return arrPointResult;
    }

    public void setArrPointResult(ArrayList<Integer> arrPointResult) {
        this.arrPointResult = arrPointResult;
    }

    public ArrayList<Integer> getArrCostResult() {
        return arrCostResult;
    }

    public void setArrCostResult(ArrayList<Integer> arrCostResult) {
        this.arrCostResult = arrCostResult;
    }

}
