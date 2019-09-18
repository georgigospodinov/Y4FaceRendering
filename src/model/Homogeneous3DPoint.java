package model;

import org.jblas.DoubleMatrix;

import java.awt.Color;
import java.util.Objects;

/**
 * Represents a Homogeneous Point in 3D space.
 *
 * @author 150009974
 * @version 2.4
 */
public class Homogeneous3DPoint {

    /**
     * Subtracts b from a and returns the resulting {@link Homogeneous3DPoint}.
     * This operation does not affect the given points.
     * The resulting {@link Homogeneous3DPoint}'s location is equal to
     * the differences in coordinates.
     * Its color is equal to the differences in RGB.
     *
     * @param a the point to subtract from
     * @param b the point to subtract
     *
     * @return the difference (a - b)
     */
    public static Homogeneous3DPoint subtract(final Homogeneous3DPoint a,
                                              final Homogeneous3DPoint b) {
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();
        double z = a.getZ() - b.getZ();
        int red = limitColor(a.getRed() - b.getRed());
        int green = limitColor(a.getGreen() - b.getGreen());
        int blue = limitColor(a.getBlue() - b.getBlue());
        return new Homogeneous3DPoint(x, y, z, red, green, blue);
    }

    /**
     * Calculates and returns the cross product (a x b).
     * The resulting {@link Homogeneous3DPoint} has the color of a.
     *
     * @param a the first {@link Homogeneous3DPoint} to multiply
     * @param b the second {@link Homogeneous3DPoint} to multiply
     *
     * @return a x b
     */
    public static Homogeneous3DPoint cross(final Homogeneous3DPoint a,
                                           final Homogeneous3DPoint b) {
        double x = a.getY() * b.getZ() - a.getZ() * b.getY();
        double y = a.getZ() * b.getX() - a.getX() * b.getZ();
        double z = a.getX() * b.getY() - a.getY() * b.getX();
        return new Homogeneous3DPoint(x, y, z, a.c);
    }

    /**
     * Calculates and returns the dot product (a · b).
     *
     * @param a the first {@link Homogeneous3DPoint} to multiply
     * @param b the second {@link Homogeneous3DPoint} to multiply
     *
     * @return a · b
     */
    public static double dot(final Homogeneous3DPoint a,
                             final Homogeneous3DPoint b) {
        return a.dot(b);
    }

    /**
     * Encloses the given value in the color range 0 to 255.
     * If the given value is less than 0, this method returns 0.
     * Else, if it is greater than 255, it returns 255.
     * Otherwise, it returns the given value, because it is in color range.
     *
     * @param value the value to limit
     *
     * @return a number between 0 and 255 (inclusive)
     */
    private static int limitColor(int value) {
        if (value < 0) {
            return 0;
        } else if (value > 255) {
            return 255;
        } else {
            return value;
        }
    }

    /** Stores the coordinates of this {@link Homogeneous3DPoint}. */
    private DoubleMatrix coordinates;

    /** The color of this {@link Homogeneous3DPoint}. */
    private Color c;

    /**
     * Creates a {@link Homogeneous3DPoint} at the given location
     * with the given color.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @param c the color of the point
     */
    public Homogeneous3DPoint(final double x, final double y,
                              final double z, final Color c) {
        coordinates = DoubleMatrix.zeros(4, 1);
        coordinates.put(0, 0, x);
        coordinates.put(1, 0, y);
        coordinates.put(2, 0, z);
        coordinates.put(3, 0, 1);  // set W to 1
        this.c = c;
    }

    /**
     * Creates a {@link Homogeneous3DPoint} at the given location
     * with the given color.
     *
     * @param x     the X coordinate
     * @param y     the Y coordinate
     * @param z     the Z coordinate
     * @param red   the red intensity
     * @param green the green intensity
     * @param blue  the blue intensity
     */
    public Homogeneous3DPoint(final double x, final double y, final double z,
                              final int red, final int green, final int blue) {
        this(x, y, z, new Color(red, green, blue));
    }

    /**
     * Creates a gray {@link Homogeneous3DPoint} at the given location.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     */
    public Homogeneous3DPoint(final double x, final double y, final double z) {
        this(x, y, z, Color.GRAY);
    }

    /** @return the X coordinate of this {@link Homogeneous3DPoint} */
    public double getX() {
        return coordinates.get(0, 0) / coordinates.get(3, 0);
    }

    /** @return the Y coordinate of this {@link Homogeneous3DPoint} */
    public double getY() {
        return coordinates.get(1, 0) / coordinates.get(3, 0);
    }

    /** @return the Z coordinate of this {@link Homogeneous3DPoint} */
    public double getZ() {
        return coordinates.get(2, 0) / coordinates.get(3, 0);
    }

    /** @return the color of this {@link Homogeneous3DPoint} */
    public Color getColor() {
        return c;
    }

    /** @return the red value of this {@link Homogeneous3DPoint} */
    public int getRed() {
        return c.getRed();
    }

    /** @return the green value of this {@link Homogeneous3DPoint} */
    public int getGreen() {
        return c.getGreen();
    }

    /** @return the blue value of this {@link Homogeneous3DPoint} */
    public int getBlue() {
        return c.getBlue();
    }

    /** @return the {@link DoubleMatrix} representing this point */
    public DoubleMatrix getMatrix() {
        return coordinates;
    }

    /**
     * Calculates and returns the dot product (this · that).
     *
     * @param that the second {@link Homogeneous3DPoint} to multiply
     *
     * @return this · that
     */
    public double dot(final Homogeneous3DPoint that) {
        return this.getX() * that.getX()
                + this.getY() * that.getY()
                + this.getZ() * that.getZ();
    }

    /**
     * Scales this {@link Homogeneous3DPoint} by the given scalar.
     *
     * @param scalar the scalar to scale by
     */
    public void scale(final double scalar) {
        DoubleMatrix scaleMatrix = DoubleMatrix.eye(4);
        scaleMatrix.put(0, 0, scalar);
        scaleMatrix.put(1, 1, scalar);
        scaleMatrix.put(2, 2, scalar);
        coordinates = scaleMatrix.mmul(coordinates);
    }

    /**
     * Translates this {@link Homogeneous3DPoint}
     * by the given {@link Homogeneous3DPoint}.
     *
     * @param that the {@link Homogeneous3DPoint} to translate by
     */
    public void translate(final Homogeneous3DPoint that) {
        DoubleMatrix translationMatrix = DoubleMatrix.eye(4);
        translationMatrix.put(0, 3, that.getX());
        translationMatrix.put(1, 3, that.getY());
        translationMatrix.put(2, 3, that.getZ());
        this.coordinates = translationMatrix.mmul(this.coordinates);
    }

    /** Normalizes this {@link Homogeneous3DPoint}. */
    public void normalize() {
        double length = getLength();
        double w = coordinates.get(3, 0);
        // Simply changing the W value means that getX, getY, and getZ will return correctly.
        coordinates.put(3, 0, w * length);
    }

    /** @return the length of this vector */
    public double getLength() {
        return Math.sqrt(dot(this));
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates, c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Homogeneous3DPoint)) {
            return false;
        }
        Homogeneous3DPoint that = (Homogeneous3DPoint) o;
        return coordinates.equals(that.coordinates)
                && Objects.equals(c, that.c);
    }

    @Override
    public String toString() {
        return "Homogeneous3DPoint{" + coordinates + "," + c + "}";
    }

}
