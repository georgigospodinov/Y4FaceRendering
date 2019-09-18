package renderers;

import model.Homogeneous3DPoint;
import model.Homogeneous3DPolygon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;

/**
 * Represents Lambert's Illumination Model.
 *
 * @author 150009974
 * @version 1.3
 */
public class LambertianIlluminationModel implements IlluminationModel {

    /** The sources of light. */
    private HashSet<LightSource> sources = new HashSet<>();

    /** The diffusion coefficient. */
    private double K;

    /** The direction from which the object is viewed. Used with normals. */
    private Homogeneous3DPoint viewingDirection;

    /**
     * Creates a {@link LambertianIlluminationModel} with
     * the given diffuse coefficient.
     * The given viewing direction is only used
     * to determine the normal.
     *
     * @param k    the diffuse coefficient
     * @param view the viewing direction
     */
    public LambertianIlluminationModel(final double k, final Homogeneous3DPoint view) {
        K = k;
        viewingDirection = view;
    }

    @Override
    public void addSource(final LightSource s) {
        sources.add(s);
    }

    @Override
    public void removeSource(final LightSource s) {
        sources.remove(s);
    }

    @Override
    public void configure(Graphics2D g, Homogeneous3DPolygon p) {
        double redIntensity = 0;
        double greenIntensity = 0;
        double blueIntensity = 0;
        Homogeneous3DPoint mean = p.getMean();
        Homogeneous3DPoint normal = getNormal(p, mean);

        for (LightSource source : sources) {
            Homogeneous3DPoint wi = source.getIncomingLightDirection(mean);
            wi.normalize();
            double dot = Homogeneous3DPoint.dot(normal, wi);
            if (dot <= 0) {
                // This light source does not illuminate the triangle.
                continue;
            }
            redIntensity += dot * source.getRed() * K;
            greenIntensity += dot * source.getGreen() * K;
            blueIntensity += dot * source.getBlue() * K;
        }
        int red = Math.min((int) (mean.getRed() * redIntensity), 255);
        int green = Math.min((int) (mean.getGreen() * greenIntensity), 255);
        int blue = Math.min((int) (mean.getBlue() * blueIntensity), 255);
        Color c = new Color(red, green, blue);
        g.setColor(c);
    }

    /**
     * Calculates and returns the normalized normal of the plane
     * defined by the given {@link Homogeneous3DPolygon}.
     * A plane has two normals. This method returns the one that points
     * toward the viewing direction.
     * That normal is identified by a positive dot product
     * with the {@link #viewingDirection}.
     *
     * @param p the {@link Homogeneous3DPolygon} to calculate normal for
     * @param mean the mean point of the polygon
     *
     * @return the normalized normal towards the viewing direction
     */
    private Homogeneous3DPoint getNormal(final Homogeneous3DPolygon p,
                                         final Homogeneous3DPoint mean) {
        Homogeneous3DPoint v0 = p.getVertex(0);
        Homogeneous3DPoint v1 = p.getVertex(1);
        Homogeneous3DPoint a = Homogeneous3DPoint.subtract(v0, mean);
        Homogeneous3DPoint b = Homogeneous3DPoint.subtract(v1, mean);
        Homogeneous3DPoint nAB = Homogeneous3DPoint.cross(a, b);
        nAB.normalize();
        if (nAB.dot(viewingDirection) > 0) {
            return nAB;
        }
        Homogeneous3DPoint nBA = Homogeneous3DPoint.cross(b, a);
        nBA.normalize();
        return nBA;
    }

}
