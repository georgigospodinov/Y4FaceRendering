package renderers;

import model.Homogeneous3DPoint;

import java.awt.Color;

/**
 * Represents a Point Light Source.
 *
 * @author 150009974
 * @version 3.1
 */
public class PointLightSource implements LightSource {

    /** Effectively, this {@link PointLightSource}. */
    private Homogeneous3DPoint source;

    /**
     * Creates a {@link PointLightSource} at the specified location
     * with the given {@link Color}.
     *
     * @param x the X coordinate of the source
     * @param y the Y coordinate of the source
     * @param z the Z coordinate of the source
     * @param c the color of the light
     */
    public PointLightSource(double x, double y, double z, Color c) {
        this(new Homogeneous3DPoint(x, y, z, c));
    }

    /**
     * Uses the given {@link Homogeneous3DPoint} as a {@link PointLightSource}.
     *
     * @param point the {@link Homogeneous3DPoint} to use
     */
    public PointLightSource(final Homogeneous3DPoint point) {
        source = point;
    }

    @Override
    public Homogeneous3DPoint getIncomingLightDirection(final Homogeneous3DPoint p) {
        // Remember that the vector should originate from p and point towards source
        return Homogeneous3DPoint.subtract(source, p);
    }

    @Override
    public double getRed() {
        return source.getRed() / 255d;
    }

    @Override
    public double getGreen() {
        return source.getGreen() / 255d;
    }

    @Override
    public double getBlue() {
        return source.getBlue() / 255d;
    }

}
