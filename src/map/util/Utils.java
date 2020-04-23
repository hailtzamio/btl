package map.util;

import map.model.MyData;
import map.model.Pathz;
import map.model.Position;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    ArrayList<Position> myPoints = new ArrayList<>();
    public MyData getDataFromFile() {
        MyData myData = new MyData();
        try {

            Path pathToFile = Paths.get("test.txt");
            System.out.println(pathToFile.toAbsolutePath());

            List<String> productLines = Files.readAllLines(pathToFile.toAbsolutePath(), StandardCharsets.UTF_8);

            ArrayList<Pathz> myLines = new ArrayList<>();
            for (String line : productLines) {

                String[] tokens = line.split("-");

                if (tokens[0].startsWith("line")) {
                    float x1 = Float.parseFloat(tokens[1]);
                    float y1 = Float.parseFloat(tokens[2]);
                    float x2 = Float.parseFloat(tokens[3]);
                    float y2 = Float.parseFloat(tokens[4]);
                    int pointA = Integer.parseInt(tokens[5]);
                    int pointB = Integer.parseInt(tokens[6]);
                    int km = Integer.parseInt(tokens[7]);
//                    int km2 = Math.round(km*100);

                    Pathz myLine = new Pathz(new Line2D.Double(x1, y1, x2, y2), pointA, pointB, km, tokens[8]);
                    myLines.add(myLine);
                }

                if (tokens[0].startsWith("point")) {
                    float x = Float.parseFloat(tokens[1]) + 100;
                    float y = Float.parseFloat(tokens[2]) + 100;
                    String name = tokens[3];
//                    float h = Float.parseFloat(tokens[4]);
                    Position myPoint = new Position(new Ellipse2D.Float(x, y, 10, 10), name);
                    myPoints.add(myPoint);
                }

            }

            myData.setPathzs(myLines);
            myData.setPositions(myPoints);

            for (Position product : myPoints) {
                System.out.println(product.getName());
            }

        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return myData;
    }

    public ArrayList<Position> getPositions() {
        return myPoints;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

}


