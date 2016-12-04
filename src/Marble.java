import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

/**
 * 
 * @author ericzeng
 *
 */
public class Marble extends JComponent {
    private Color c;
    private final int SIZE;

    public Marble(Color color, int s) {
        setColor(color);
        SIZE = s;
        setFocusable(true);
    }

    /**
     * Outputs the Color of the marble
     * 
     * @return the color of the marble
     */
    public Color getColor() {
        return c;
    }

    /**
     * Sets the color of this marble
     * 
     * @param color
     *            the color this marble should be
     */
    public void setColor(Color color) {
        this.c = color;
    }

    /**
     * Draws the marble with x y top left corner
     * 
     * @param g
     *            graphics
     * @param x
     *            x top left
     * @param y
     *            y top left
     * @param diameter
     *            diameter of circular marble
     */
    public void draw(Graphics g, int x, int y) {
        if (c == null) {
            g.setColor(Color.BLACK);
            g.drawOval(x, y, SIZE, SIZE);
        } else {
            g.setColor(c);
            g.fillOval(x, y, SIZE, SIZE);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (c == null) {
            g.setColor(Color.BLACK);
            g.drawOval(0, 0, SIZE, SIZE);
        } else {
            g.setColor(c);
            g.fillOval(0, 0, SIZE, SIZE);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(SIZE, SIZE);
    }
}
