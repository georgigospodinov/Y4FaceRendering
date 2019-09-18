package model;

import util.file.editing.WrappedReader;

import java.util.ArrayList;

import static main.Main.L;
import static main.Main.PROPS;

/**
 * This class stores the triangle definitions from the mesh.csv.
 *
 * @author 150009974
 * @version 1.1
 */
public final class Mesh {

    /** The triangle defined in the mesh file. */
    private static final ArrayList<TriangleDefinition> DEFINITIONS =
            new ArrayList<>();

    /**
     * Contains a tuple of three indexes.
     * They refer to points that need to be connected
     * to form a triangle.
     *
     * @author 150009974
     * @version 1.1
     */
    private static class TriangleDefinition {

        /** The index of the first vertex. */
        private int v1;

        /** The index of the second vertex. */
        private int v2;

        /** The index of the third vertex. */
        private int v3;

        /**
         * Parses the given {@link String}
         * and creates a {@link TriangleDefinition}.
         * The raw {@link String} must contain 3 comma-separated integers.
         * They represent the indexes of
         * the three vertexes of a triangle.
         *
         * @param raw the comma-separated indexes
         */
        TriangleDefinition(final String raw) {
            String[] rawIndexes = raw.split(",");
            // Account for indexes starting at 1.
            v1 = Integer.parseInt(rawIndexes[0].trim()) - 1;
            v2 = Integer.parseInt(rawIndexes[1].trim()) - 1;
            v3 = Integer.parseInt(rawIndexes[2].trim()) - 1;
        }

    }

    static {
        String meshFile = PROPS.getString("mesh file");
        WrappedReader reader = new WrappedReader(meshFile, L);
        reader.lines().forEach(line -> {
            TriangleDefinition def = new TriangleDefinition(line);
            DEFINITIONS.add(def);
        });
        reader.close();
    }

    /**
     * Creates {@link Homogeneous3DPolygon}s from the given points.
     * The {@link TriangleDefinition}s specify indexes in the given array.
     * The respective {@link Homogeneous3DPoint}s are obtained and used
     * to instantiate {@link Homogeneous3DPolygon}s.
     *
     * @param points the points to use
     *
     * @return the created {@link Homogeneous3DPolygon}s
     */
    public static ArrayList<Homogeneous3DPolygon> createTriangles(
            final ArrayList<Homogeneous3DPoint> points) {
        ArrayList<Homogeneous3DPolygon> created = new ArrayList<>(DEFINITIONS.size());
        for (TriangleDefinition definition : DEFINITIONS) {
            Homogeneous3DPoint p1 = points.get(definition.v1);
            Homogeneous3DPoint p2 = points.get(definition.v2);
            Homogeneous3DPoint p3 = points.get(definition.v3);
            created.add(new Homogeneous3DPolygon(p1, p2, p3));
        }
        return created;
    }

    /** Hides the constructor for this utility class. */
    private Mesh() {
    }

}
