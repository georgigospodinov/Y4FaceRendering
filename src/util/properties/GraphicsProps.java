package util.properties;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.FileNotFoundException;

/**
 * Provides reading of ".props" files and
 * parsing of graphical configuration data.
 *
 * @version 1.1
 */
public class GraphicsProps extends BasicProps {

    /**
     * Creates a {@link GraphicsProps} instance and
     * loads the properties from the specified file.
     *
     * @param filename the name of the properties file to load
     *
     * @throws FileNotFoundException if the specified file is not found
     */
    public GraphicsProps(final String filename) throws FileNotFoundException {
        super(filename);
    }

    /** Default constructor. A file should be loaded immediately. */
    public GraphicsProps() {
        super();
    }

    /**
     * Gets the {@link Color} described by the given property.
     * Specifically, looks for the integer properties that specify
     * the {@link Color} in RGB,
     * then creates and returns the appropriate object.
     * E.G.: <code>getColor("my color")</code> will look for properties
     * "my color r", "my color g", and "my color b".
     *
     * @param property the property to look for
     *
     * @return the {@link Color} defined by that property
     */
    public Color getColor(final String property) {
        int red = getInt(property + " r");
        int green = getInt(property + " g");
        int blue = getInt(property + " b");
        return new Color(red, green, blue);
    }

    /**
     * Gets the {@link Point} described by the given property.
     * Specifically, looks for the integer properties that specify
     * the {@link Point} in 2 dimensions,
     * then creates and returns the appropriate object.
     *
     * E.G.: <code>getPoint("my location")</code> will look for properties
     * "my location x" and "my location y".
     *
     * @param property the property to look for
     *
     * @return the {@link Point} defined by that property
     */
    public Point getPoint(final String property) {
        int x = getInt(property + " x");
        int y = getInt(property + " y");
        return new Point(x, y);
    }

    /**
     * Gets the {@link Dimension} described by the given property.
     * Specifically, looks for the integer properties that specify
     * the {@link Dimension} with width and height
     * then creates and returns the appropriate object.
     *
     * E.G.: <code>getLocation("my dimension")</code> will look for properties
     * "my dimension width" and "my dimension height".
     *
     * @param property the property to look for
     *
     * @return the {@link Dimension} defined by that property
     */
    public Dimension getDimension(final String property) {
        int w = getInt(property + " width");
        int h = getInt(property + " height");
        return new Dimension(w, h);
    }

    /**
     * Gets the {@link Font} described by the given property.
     * Specifically, looks for the {@link String} and integer properties
     * that specify the {@link Font} with name, style, and size.
     * then creates and returns the appropriate object.
     *
     * E.G.: <code>getLocation("my font")</code> will look for properties
     * "my font name", "my font style", and "my font size".
     *
     * @param property the property to look for
     *
     * @return the {@link Font} defined by that property
     *
     * @see Font#Font(String, int, int)
     */
    public Font getFont(final String property) {
        String name = getString(property + " name");
        int style = getInt(property + " style");
        int size = getInt(property + " size");
        return new Font(name, style, size);
    }

}
