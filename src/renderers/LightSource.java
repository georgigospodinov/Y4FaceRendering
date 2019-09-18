package renderers;

import model.Homogeneous3DPoint;

/**
 * Provides a way to determine how much light reaches a specific point.
 * Both angle and intensity.
 *
 * @author 150009974
 * @version 1.0
 */
public interface LightSource {

    /**
     * Calculates and returns the direction of light
     * coming from this {@link LightSource}
     * and reaching the given {@link Homogeneous3DPoint}.
     *
     * @param p the {@link Homogeneous3DPoint} to reach
     *
     * @return the direction of incoming light as a tuple of (x,y,z)
     */
    Homogeneous3DPoint getIncomingLightDirection(Homogeneous3DPoint p);

    /** @return the red color intensity of this {@link LightSource} */
    double getRed();

    /** @return the green color intensity of this {@link LightSource} */
    double getGreen();

    /** @return the blue color intensity of this {@link LightSource} */
    double getBlue();

}
