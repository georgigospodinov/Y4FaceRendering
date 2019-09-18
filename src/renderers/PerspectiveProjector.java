package renderers;

import model.Homogeneous3DPolygon;
import org.jblas.DoubleMatrix;

import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 * Represents a Perspective Projector.
 *
 * @author 150009974
 * @version 1.1
 */
public class PerspectiveProjector implements Projector {

    /** The projection matrix to use. */
    private DoubleMatrix projectionMatrix = DoubleMatrix.zeros(3, 4);

    /**
     * Creates a {@link PerspectiveProjector} for a screen at Z coordinate = f.
     *
     * @param f the Z coordinate of the target screen
     */
    public PerspectiveProjector(final double f) {
        projectionMatrix.put(0, 0, 1);
        projectionMatrix.put(1, 1, 1);
        projectionMatrix.put(2, 2, 1 / f);
    }

    /** @return the Z coordinate of the target screen */
    public double getF() {
        return 1 / projectionMatrix.get(2, 2);
    }

    @Override
    public Shape project(final Homogeneous3DPolygon polygon) {
        Path2D.Double shape = new Path2D.Double();
        DoubleMatrix first = polygon.getVertex(0).getMatrix();
        DoubleMatrix start = projectionMatrix.mmul(first);
        double startX = start.get(0, 0) / start.get(2, 0);
        double startY = start.get(1, 0) / start.get(2, 0);
        shape.moveTo(startX, startY);
        polygon.forEachVertex(v -> {
            DoubleMatrix p = projectionMatrix.mmul(v.getMatrix());
            if (p.get(2, 0) == 0) {
                // Infinitely far away, do not render.
                return;
            }
            double x = p.get(0, 0) / p.get(2, 0);
            double y = p.get(1, 0) / p.get(2, 0);
            shape.lineTo(x, y);
        });
        shape.lineTo(startX, startY);
        shape.closePath();
        return shape;
    }

}
