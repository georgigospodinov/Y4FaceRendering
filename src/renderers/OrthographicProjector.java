package renderers;

import model.Homogeneous3DPolygon;
import org.jblas.DoubleMatrix;

import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 * Represents a Orthographic Projector.
 * Projects {@link Homogeneous3DPolygon}s by dropping the Z coordinate.
 *
 * @author 150009974
 * @version 1.0
 */
public class OrthographicProjector implements Projector {

    /** The standard Orthogonal Projection Matrix. */
    private static final DoubleMatrix PROJECTION_MATRIX = new DoubleMatrix(3, 4);

    static {
        PROJECTION_MATRIX.put(0, 0, 1);
        PROJECTION_MATRIX.put(1, 1, 1);
        PROJECTION_MATRIX.put(2, 3, 1);
    }

    @Override
    public Shape project(final Homogeneous3DPolygon polygon) {
        Path2D.Double shape = new Path2D.Double();
        DoubleMatrix first = polygon.getVertex(0).getMatrix();
        DoubleMatrix start = PROJECTION_MATRIX.mmul(first);
        double startX = start.get(0, 0) / start.get(2, 0);
        double startY = start.get(1, 0) / start.get(2, 0);
        shape.moveTo(startX, startY);
        polygon.forEachVertex(v -> {
            DoubleMatrix p = PROJECTION_MATRIX.mmul(v.getMatrix());
            double x = p.get(0, 0) / p.get(2, 0);
            double y = p.get(1, 0) / p.get(2, 0);
            shape.lineTo(x, y);
        });
        shape.lineTo(startX, startY);
        shape.closePath();
        return shape;
    }

}
