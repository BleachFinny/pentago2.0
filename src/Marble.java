import java.awt.Color;
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

    public Marble(Color color) {
        setColor(color);
    }

    /**
     * Outputs the Color of the marble
     * 
     * @return black or white
     */
    public Color getColor() {
        return c;
    }

    /**
     * Outputs the Color of the marble. Mainly used by child classes and
     * constructors
     * 
     * @param color
     *            the color this marble should be
     */
    private void setColor(Color color) {
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
    public void draw(Graphics g, int x, int y, int diameter) {
        g.setColor(c);
        g.drawOval(x, y, diameter, diameter);
    }
}
