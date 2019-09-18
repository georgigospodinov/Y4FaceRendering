package renderers;

import model.Face;
import model.Homogeneous3DPoint;
import model.Homogeneous3DPolygon;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a a simple renderer for previewing Face.
 *
 * @author 150009974
 * @version 2.0
 */
public class PreviewRenderer extends JPanel {

    /** The {@link Projector} from 3D to 2D. */
    private Projector projector = new OrthographicProjector();

    /** The current {@link Face} to draw */
    private Face current;

    /** The {@link #current} {@link Face}'s initial center. */
    private Homogeneous3DPoint originalCenter;

    /** Creates a {@link PreviewRenderer} with default configuration. */
    public PreviewRenderer() {
        setLayout(null);
        setFocusable(true);
        setBackground(Color.WHITE);
    }

    /** @param f the {@link Face} to draw */
    public void setFace(final Face f) {
        current = f;
        originalCenter = current.getCenter();
        int x = this.getWidth() / 2;
        int y = this.getHeight() / 2;
        current.setCenter(new Homogeneous3DPoint(x, y, 0));
    }

    /**
     * Undoes all transformation done on the {@link #current} {@link Face},
     * returns it and forgets about it.
     *
     * @return the originally given {@link Face}
     */
    public Face restoreFace() {
        current.setCenter(originalCenter);
        Face f = current;
        current = null;
        return f;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        renderFace((Graphics2D) g);
    }

    /**
     * Renders the current {@link Face} using the given {@link Graphics2D} instance.
     *
     * @param g the {@link Graphics2D} to use for rendering
     */
    public void renderFace(final Graphics2D g) {
        if (current == null) {
            return;
        }
        ArrayList<Homogeneous3DPolygon> polygons = current.getPolygons();
        Collections.sort(polygons);
        for (Homogeneous3DPolygon polygon : polygons) {
            Color c = polygon.getMean().getColor();
            g.setColor(c);
            Shape projection = projector.project(polygon);
            g.fill(projection);
        }
    }

}
