package map.uicommon;

import javax.swing.*;
import java.awt.*;

public class Popup extends JPopupMenu {

    private Point point;

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Popup() {
        super();
    }

    public void show(Component invoker, int x, int y) {
        point = new Point(x, y);
        super.show(invoker, x, y);
    }
}
