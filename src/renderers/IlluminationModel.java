package renderers;

import model.Homogeneous3DPolygon;

import java.awt.Graphics2D;

/**
 * Provides a way to configure a {@link Graphics2D} object
 * for coloring a given a {@link Homogeneous3DPolygon}.
 * {@link LightSource}s have to be provided.
 *
 * @author 150009974
 * @version 2.0
 */
public interface IlluminationModel {

    /**
     * Adds the given {@link LightSource} to
     * the collection of sources to account for.
     *
     * @param s the {@link LightSource} to account for
     */
    void addSource(LightSource s);

    /**
     * Removes the given {@link LightSource} from
     * the collection of sources to account for.
     *
     * @param s the {@link LightSource} to remove
     */
    void removeSource(LightSource s);

    /**
     * Configures the given {@link Graphics2D}
     * such that it will correctly color the given {@link Homogeneous3DPolygon}.
     *
     * @param g the {@link Graphics2D} to configure
     * @param p the {@link Homogeneous3DPolygon} to configure for
     */
    void configure(Graphics2D g, Homogeneous3DPolygon p);

}
