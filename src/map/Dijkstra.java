package map;

import map.model.Pathz;
import map.model.Position;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Dijkstra {
    private int coList[][];
    private int[] minPath, previous;
    private int[][] checkLen, checkP;
    private boolean[] checkRightPositionShortest;
    private int infinity, positionSize = 0;
    private ArrayList<Position> positions = new ArrayList<Position>();
    private ArrayList<Pathz> pathzs = new ArrayList<Pathz>();
    private int fromPosition = 0, toPosition = 0;
    private int positionCheck = 0;
    private boolean mapType = false;
    private String path = "";

    public Dijkstra() {

    }

    public void setData() {
        infinity = 1;
        positionSize = positions.size();
        coList = new int[positionSize][positionSize];
        minPath = new int[positionSize];
        previous = new int[positionSize];
        checkRightPositionShortest = new boolean[positionSize];

        for (int i = 1; i < pathzs.size(); i++) {
            coList[pathzs.get(i).getPositionA()][pathzs.get(i)
                    .getPositionB()] = pathzs.get(i).getPath();
            if (!mapType) {
                coList[pathzs.get(i).getPositionB()][pathzs.get(i)
                        .getPositionA()] = pathzs.get(i).getPath();
            }
            infinity += pathzs.get(i).getPath();
        }

        for (int i = 1; i < positionSize; i++) {
            for (int j = 1; j < positionSize; j++) {
                if (coList[i][j] == 0 && i != j) {
                    coList[i][j] = infinity;
                }
            }
        }
    }

    private void initValue() {
        checkLen = new int[positionSize][positionSize];
        checkP = new int[positionSize][positionSize];
        for (int i = 1; i < positionSize; i++) {
            minPath[i] = infinity;
            checkRightPositionShortest[i] = false;
            previous[i] = 0;
        }
        checkLen[0] = minPath;
        checkP[0] = previous;
        minPath[fromPosition] = 0;
    }

    public int dijkstra() {
        initValue();
        int i = 1, k = 0;
        while (checkContinue(k)) {
            for (i = 1; i < positionSize; i++)
                if (!checkRightPositionShortest[i] && minPath[i] < infinity)
                    break;
            if (i >= positionSize)
                break;
            for (int j = 1; j < positionSize; j++)
                if (!checkRightPositionShortest[j] && minPath[i] > minPath[j])
                    i = j;

            checkRightPositionShortest[i] = true;
            for (int j = 1; j < positionSize; j++) {
                if (!checkRightPositionShortest[j] && minPath[i] + coList[i][j] < minPath[j]) {
                    minPath[j] = minPath[i] + coList[i][j];
                    previous[j] = i;
                }

                checkLen[k][j] = minPath[j];
                checkP[k][j] = previous[j];
            }

            k++;
        }

        if (toPosition == -1) {
            positionCheck = positions.size();
            return 0;
        }

        positionCheck = k;
        return minPath[toPosition];
    }

    private boolean checkContinue(int k) {
        if (toPosition != -1) {
            return (!checkRightPositionShortest[toPosition]);
        }
        return (k < positions.size() - 1);
    }

    public String getPath() {
        path = "";
        String finalPath = "";
        ArrayList<Integer> mlist = new ArrayList<>();

        if (toPosition > 0 && minPath[toPosition] < infinity) {
            int i = toPosition;
            while (i != fromPosition) {
                path = " > " + i + path;
                mlist.add(i);
                i = previous[i];
            }

            String pathList = "";
            for (int i1 = mlist.size() - 1; i1 >= 0; i1--) {
                if (mlist.size() > i1 + 1) {
                    pathList = pathList + "-" + mlist.get(i1) + "," + mlist.get(i1 + 1) + "-";
                }
            }

            if ((pathList.length() > 0)) {
                pathList = pathList.substring(0, pathList.length() - 1);
                pathList = pathList.substring(1);
            }

            List<String> enoughPathList = new ArrayList<String>(Arrays.asList(pathList.split("--")));
            String[] temps = enoughPathList.get(0).split(",");

            if (temps.length > 1) {
                enoughPathList.add(0, fromPosition + "," + temps[0]);
                enoughPathList.add(0, fromPosition + "," + temps[1]);
            }

            Map<String, Integer> map = new HashMap<String, Integer>();
            ArrayList<String> pathNameList = new ArrayList<>();
            ArrayList<Way> ways = new ArrayList<>();
            for (int i1 = 0; i1 < enoughPathList.size(); i1++) {
                for (int i2 = 0; i2 < pathzs.size(); i2++) {
                    String compare1 = pathzs.get(i2).getPositionA() + "," + pathzs.get(i2).getPositionB();
                    String compare2 = pathzs.get(i2).getPositionB() + "," + pathzs.get(i2).getPositionA();
                    if (enoughPathList.get(i1).equals(compare1) || enoughPathList.get(i1).equals(compare2)) {
                        Pathz pathz = pathzs.get(i2);
                        ways.add(new Way(pathz.getStreetName(), pathz.getPath()));
                        pathNameList.add(pathz.getStreetName());
                    }
                }
            }

            pathNameList = removeDuplicates(pathNameList);
            for (int i2 = 0; i2 < pathNameList.size(); i2++) {
                String name = pathNameList.get(i2);
                int path = 0;
                for (int i1 = 0; i1 < ways.size(); i1++) {
                    if (ways.get(i1).getName().equals(name)) {
                        path = path + ways.get(i1).getPath();
                        map.put(name,path);
                    }
                }
            }

            String finalWay = "";
            for (int i1 = 0; i1 < pathNameList.size(); i1++) {
                finalWay = finalWay + " \n > " + pathNameList.get(i1) + " ( "  + map.get(pathNameList.get(i1)) + "m )";
            }

            finalPath = "Chiều dài " + minPath[toPosition] + "m" + " :  \n" + finalWay;

        } else {
            finalPath = "Không thể đi";
        }
        return finalPath;
    }

    class Way {
        String name;
        int path;

        public Way(String name, int path) {
            this.name = name;
            this.path = path;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPath() {
            return path;
        }

        public void setPath(int path) {
            this.path = path;
        }
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        ArrayList<T> newList = new ArrayList<T>();
        for (T element : list) {
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }
        return newList;
    }

    public String getPathTemp() {
        path = "";
        String path2 = "";
        String finalPath = "";
        ArrayList<Integer> mlist = new ArrayList<>();

        if (toPosition > 0 && minPath[toPosition] < infinity) {
            int i = toPosition;
            while (i != fromPosition) {
                path = " > " + i + path;
                mlist.add(i);
                i = previous[i];
            }

            for (int i1 = mlist.size() - 1; i1 >= 0; i1--) {
                path2 = path2 + " > " + positions.get(mlist.get(i1)).getName();
            }

            finalPath = "Chiều dài " + minPath[toPosition] + "m" + " : " + path2;
        } else {
            finalPath = "Không thể đi";
        }
        return finalPath;
    }

    public int getPositionCheck() {
        return positionCheck;
    }

    public void setPositionCheck(int positionCheck) {
        this.positionCheck = positionCheck;
    }

    public int[][] getCheckLen() {
        return checkLen;
    }

    public void setCheckLen(int[][] checkLen) {
        this.checkLen = checkLen;
    }

    public int[][] getCheckP() {
        return checkP;
    }

    public void setCheckP(int[][] checkP) {
        this.checkP = checkP;
    }

    public boolean isMapType() {
        return mapType;
    }

    public void setMapType(boolean mapType) {
        this.mapType = mapType;
    }

    public boolean[] getCheckRightPositionShortest() {
        return checkRightPositionShortest;
    }

    public void setCheckRightPositionShortest(boolean[] checkRightPositionShortest) {
        this.checkRightPositionShortest = checkRightPositionShortest;
    }

    public int[] getPrevious() {
        return previous;
    }

    public int getInfinity() {
        return infinity;
    }

    public int[] getMinPath() {
        return minPath;
    }

    public void setMinPath(int[] minPath) {
        this.minPath = minPath;
    }

    public int[][] getCoList() {
        return coList;
    }

    public void setCoList(int[][] coList) {
        this.coList = coList;
    }

    public int getFromPosition() {
        return fromPosition;
    }

    public void setFromPosition(int fromPosition) {
        this.fromPosition = fromPosition;
    }

    public int getToPosition() {
        return toPosition;
    }

    public void setToPosition(int toPosition) {
        this.toPosition = toPosition;
    }

    public void setPositions(ArrayList<Position> arrMyPoint) {
        this.positions = arrMyPoint;
    }

    public void setPathzs(ArrayList<Pathz> arrMyLine) {
        this.pathzs = arrMyLine;
    }

}
