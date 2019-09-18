package model;

import util.file.editing.WrappedReader;

import java.awt.Color;
import java.util.ArrayList;

import static main.Main.L;
import static main.Main.PROPS;

/**
 * Represents a face in three dimensional space.
 *
 * @author 150009974
 * @version 2.1
 */
public final class Face {

    /** The weights of shapes of all {@link Face}s. */
    private static final ArrayList<Double> SHAPE_WEIGHTS = new ArrayList<>();

    /** The weights of colors of all {@link Face}s. */
    private static final ArrayList<Double> COLOR_WEIGHTS = new ArrayList<>();

    /** The name of the file containing the {@link #AVERAGE} shape. */
    private static final String AVERAGE_SHAPE_FILENAME =
            PROPS.getString("average face shape");

    /** The name of the file containing the {@link #AVERAGE} color. */
    private static final String AVERAGE_COLOR_FILENAME =
            PROPS.getString("average face color");

    /** The average {@link Face}. */
    private static final Face AVERAGE =
            parse(AVERAGE_SHAPE_FILENAME, AVERAGE_COLOR_FILENAME, -1);

    static {
        String shapeWeights = PROPS.getString("shape weights");
        WrappedReader shapeWeightsReader = new WrappedReader(shapeWeights, L);
        shapeWeightsReader.lines().forEach(line -> {
            double weight = Double.parseDouble(line);
            SHAPE_WEIGHTS.add(weight);
        });
        shapeWeightsReader.close();

        String colorWeights = PROPS.getString("color weights");
        WrappedReader colorWeightsReader = new WrappedReader(colorWeights, L);
        colorWeightsReader.lines().forEach(line -> {
            double weight = Double.parseDouble(line);
            COLOR_WEIGHTS.add(weight);
        });
        colorWeightsReader.close();
    }

    /**
     * Parses a {@link Face} from the specified files.
     * The parsed coordinates and color are multiplied by
     * the weight found at the given weightIndex
     * in {@link #SHAPE_WEIGHTS} and in {@link #COLOR_WEIGHTS}, respectively.
     * Then the {@link #AVERAGE} {@link Face} is added to it.
     * When the {@link #AVERAGE} {@link Face} is loaded,
     * its values are are not weighted.
     *
     * @param shapeFile   the name of the file describing the shape
     * @param colorFile   the name of the file describing the color
     * @param weightIndex the index in the weight arrays
     *
     * @return the created {@link Face}
     *
     * @see #parsePoint(int, String, String, int)
     */
    public static Face parse(final String shapeFile, final String colorFile,
                             final int weightIndex) {
        ArrayList<Homogeneous3DPoint> points = new ArrayList<>();
        WrappedReader shapeReader = new WrappedReader(shapeFile, L);
        WrappedReader textureReader = new WrappedReader(colorFile, L);
        String rawLocation = shapeReader.readLine();
        String rawColor = textureReader.readLine();
        int pointIndex = 0;
        while (rawLocation != null) {
            Homogeneous3DPoint p = parsePoint(pointIndex, rawLocation, rawColor, weightIndex);
            points.add(p);
            // Next declarations
            pointIndex++;
            rawLocation = shapeReader.readLine();
            rawColor = textureReader.readLine();
        }
        shapeReader.close();
        textureReader.close();

        return new Face(points);
    }

    /**
     * Parses a {@link Homogeneous3DPoint} from the given parameters.
     * The {@link String} location is the comma-separated coordinates x,y,z.
     * The {@link String} color is the comma-separated color values r,g,b.
     * The pointIndex is used to index
     * the {@link #points} in the {@link #AVERAGE} {@link Face}.
     * The weightIndex is used to index
     * the {@link #SHAPE_WEIGHTS} and the {@link #COLOR_WEIGHTS}.
     *
     * @param pointIndex  the index of a point in {@link #AVERAGE}
     * @param location    the comma-separated x,y,z
     * @param color       the comma-separated r,g,b
     * @param weightIndex the index of a weight
     *
     * @return the created {@link Homogeneous3DPoint}
     */
    private static Homogeneous3DPoint parsePoint(final int pointIndex,
                                                 final String location,
                                                 final String color,
                                                 final int weightIndex) {
        String[] coords = location.split(",");
        double x = Double.parseDouble(coords[0].trim());
        double y = Double.parseDouble(coords[1].trim());
        double z = Double.parseDouble(coords[2].trim());

        String[] rgb = color.split(",");
        double r = Double.parseDouble(rgb[0].trim());
        double g = Double.parseDouble(rgb[1].trim());
        double b = Double.parseDouble(rgb[2].trim());

        if (AVERAGE != null) {
            Homogeneous3DPoint point = AVERAGE.points.get(pointIndex);

            double weight = SHAPE_WEIGHTS.get(weightIndex);
            x = point.getX() + x * weight;
            y = point.getY() + y * weight;
            z = point.getZ() + z * weight;

            weight = COLOR_WEIGHTS.get(weightIndex);
            r = point.getRed() + r * weight;
            g = point.getGreen() + g * weight;
            b = point.getBlue() + b * weight;
        }

        Color c = new Color((int) r, (int) g, (int) b);
        return new Homogeneous3DPoint(x, y, z, c);

    }

    /**
     * Synthesises a new {@link Face} via a weighted addition.
     * Each {@link Face} is multiplied by the weight at the same index.
     * And their are all added together.
     * Specifically:
     * for each {@link Homogeneous3DPoint} in the output {@link Face},
     * the given {@link Face}s are iterated,
     * the {@link Homogeneous3DPoint} at that index is retrieved,
     * its fields are multiplied by the weight of the {@link Face}.
     * The inner loop creates a weighted combination of points
     * by taking the {@link Homogeneous3DPoint}s at the respective indexes.
     *
     * @param faces   the {@link Face}s to weigh
     * @param weights the weights to multiply by
     *
     * @return the synthesised {@link Face}
     */
    public static Face synthesise(final Face[] faces, final double[] weights) {
        int n = faces[0].getPoints().size();
        ArrayList<Homogeneous3DPoint> synthesised = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            double x = 0;
            double y = 0;
            double z = 0;
            double r = 0;
            double g = 0;
            double b = 0;
            for (int j = 0; j < faces.length; j++) {
                Homogeneous3DPoint point = faces[j].getPoints().get(i);
                double w = weights[j];
                x += point.getX() * w;
                y += point.getY() * w;
                z += point.getZ() * w;
                r += point.getRed() * w;
                g += point.getGreen() * w;
                b += point.getBlue() * w;
            }
            r = Math.min(r, 255);
            g = Math.min(g, 255);
            b = Math.min(b, 255);
            Homogeneous3DPoint syn = new Homogeneous3DPoint(x, y, z, (int) r, (int) g, (int) b);
            synthesised.add(syn);
        }
        return new Face(synthesised);
    }

    /** The {@link Homogeneous3DPoint}s that make up this {@link Face}. */
    private ArrayList<Homogeneous3DPoint> points;

    /** The {@link Homogeneous3DPolygon}s that make up this {@link Face}. */
    private ArrayList<Homogeneous3DPolygon> polygons;

    /** The relative grid origin for this {@link Face}. */
    private Homogeneous3DPoint center = new Homogeneous3DPoint(0, 0, 0);

    /**
     * Creates a {@link Face} from the given {@link Homogeneous3DPoint}s.
     * Follows the triangles defined in {@link Mesh}.
     *
     * @param pts the points that make up the face
     */
    public Face(final ArrayList<Homogeneous3DPoint> pts) {
        this.points = pts;
        polygons = Mesh.createTriangles(points);
    }

    /** @return the {@link Homogeneous3DPoint}s of this {@link Face} */
    public ArrayList<Homogeneous3DPoint> getPoints() {
        return points;
    }

    /** @return all {@link Homogeneous3DPolygon}s that make up this {@link Face} */
    public ArrayList<Homogeneous3DPolygon> getPolygons() {
        return polygons;
    }

    /**
     * Scales this {@link Face} by the given scalar.
     *
     * @param scalar the scalar to scale by
     */
    public void scale(final double scalar) {
        points.forEach(p -> p.scale(scalar));
    }

    /**
     * Sets the {@link Homogeneous3DPoint} from which this {@link Face}
     * should originate.
     *
     * @param c the point of origin
     */
    public void setCenter(final Homogeneous3DPoint c) {
        Homogeneous3DPoint move = Homogeneous3DPoint.subtract(c, center);
        points.forEach(p -> p.translate(move));
        center = c;
    }

    /** @return the relative origin of this {@link Face} */
    public Homogeneous3DPoint getCenter() {
        return center;
    }

}
