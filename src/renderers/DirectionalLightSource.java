package renderers;

import model.Homogeneous3DPoint;

import java.awt.Color;

/**
 * Represents a Directional Light Source.
 * Light hits all points in the scene at the same angle.
 *
 * @author 150009974
 * @version 1.0
 */
public class DirectionalLightSource implements LightSource {

    /** Effectively, this {@link DirectionalLightSource}. */
    private Homogeneous3DPoint source;


    /**
     * Creates a {@link DirectionalLightSource} with
     * the specified direction and the given {@link Color}.
     *
     * @param x the X coordinate of the direction vector
     * @param y the Y coordinate of the direction vector
     * @param z the Z coordinate of the direction vector
     * @param c the color of the light
     */
    public DirectionalLightSource(double x, double y, double z, Color c) {
        this(new Homogeneous3DPoint(x, y, z, c));
    }

    /**
     * Uses the given {@link Homogeneous3DPoint}
     * as a {@link DirectionalLightSource}.
     * It is treated as vector and this is the object returned by this
     * implementation of {@link #getIncomingLightDirection(Homogeneous3DPoint)}.
     *
     * @param direction the direction from which light is coming
     */
    public DirectionalLightSource(final Homogeneous3DPoint direction) {
        source = direction;
    }

    @Override
    public Homogeneous3DPoint getIncomingLightDirection(final Homogeneous3DPoint p) {
        return source;
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
