package gameworld.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/** The independent square that marks the object under keyboard control. */
public final class SelectionFrame {
    public void draw(Graphics2D g, int x, int y, int size) {
        g.setColor(new Color(200, 43, 52));
        g.setStroke(new BasicStroke(3));
        g.drawRect(x + 2, y + 2, size - 5, size - 5);
    }
}
