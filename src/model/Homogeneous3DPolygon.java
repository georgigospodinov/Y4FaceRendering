package model;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Represents a polygon of {@link Homogeneous3DPoint}s.
 *
 * @author 150009974
 * @version 2.1
 */
public class Homogeneous3DPolygon implements Comparable<Homogeneous3DPolygon> {

    /** The vertexes of this {@link Homogeneous3DPolygon}. */
    private Homogeneous3DPoint[] vertexes;

    /**
     * Uses the given {@link Homogeneous3DPoint}s
     * as vertexes for a {@link Homogeneous3DPolygon}.
     *
     * @param vs the vertexes of the {@link Homogeneous3DPolygon}
     */
    public Homogeneous3DPolygon(final Homogeneous3DPoint... vs) {
        vertexes = vs;
    }

    /**
     * Gets the {@link Homogeneous3DPoint} at the given index.
     *
     * @param index the index of the {@link Homogeneous3DPoint}
     *
     * @return the {@link Homogeneous3DPoint} at that index
     */
    public Homogeneous3DPoint getVertex(final int index) {
        return vertexes[index];
    }

    /**
     * Applies the given {@link Consumer} to all vertexes
     * of this {@link Homogeneous3DPolygon}.
     *
     * @param consumer the consumer to apply
     */
    public void forEachVertex(final Consumer<Homogeneous3DPoint> consumer) {
        for (Homogeneous3DPoint v : vertexes) {
            consumer.accept(v);
        }
    }

    /**
     * Iterates all {@link #vertexes}, summing their locations and colors
     * and divides the final sums by the number of vertexes,
     *
     * @return the mean {@link Homogeneous3DPoint} in both location and color
     */
    public Homogeneous3DPoint getMean() {
        double x = 0;
        double y = 0;
        double z = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        for (Homogeneous3DPoint vertex : vertexes) {
            x += vertex.getX();
            y += vertex.getY();
            z += vertex.getZ();
            red += vertex.getRed();
            green += vertex.getGreen();
            blue += vertex.getBlue();
        }
        x /= vertexes.length;
        y /= vertexes.length;
        z /= vertexes.length;
        red /= vertexes.length;
        green /= vertexes.length;
        blue /= vertexes.length;

        return new Homogeneous3DPoint(x, y, z, red, green, blue);
    }

    @Override
    public String toString() {
        return "Homogeneous3DPolygon{"
                + "vertexes=" + Arrays.toString(vertexes)
                + "}";
    }

    @Override
    public int compareTo(final Homogeneous3DPolygon that) {
        double thisZ = this.getMean().getZ();
        double thatZ = that.getMean().getZ();
        return Double.compare(thatZ, thisZ);
    }

}
