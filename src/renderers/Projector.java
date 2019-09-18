package renderers;

import model.Homogeneous3DPolygon;

import java.awt.Shape;

/**
 * Provides projection of 3D objects onto 2D image.
 * Deals only with coordinates.
 *
 * @author 150009974
 * @version 1.0
 */
public interface Projector {

    /**
     * Creates a 2D {@link Shape} projection of the given {@link Homogeneous3DPolygon}.
     *
     * @param p the {@link Homogeneous3DPolygon} to project
     *
     * @return the projected {@link Shape}
     */
    Shape project(Homogeneous3DPolygon p);

}
