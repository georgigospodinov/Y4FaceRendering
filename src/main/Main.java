package main;

import util.log.Logger;
import util.properties.GraphicsProps;

import java.io.FileNotFoundException;

/**
 * Provides the main method to run the system.
 *
 * @author 150009974
 * @version 1.1
 */
public final class Main {

    /** A {@link Logger} to log errors. */
    public static final Logger L = new Logger("log.txt");

    /** The configuration properties. */
    public static final GraphicsProps PROPS;

    static {
        PROPS = new GraphicsProps();
        try {
            PROPS.load("settings.props");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) {
        FaceInterpolator interpolator = new FaceInterpolator();
        interpolator.setVisible(true);
        interpolator.updatePreviews();
        L.close();
    }

    /** Hides the constructor for this utility class. */
    private Main() {
    }

}
