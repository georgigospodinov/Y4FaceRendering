package renderers;

import model.Face;
import model.Homogeneous3DPoint;
import model.Homogeneous3DPolygon;
import org.jblas.DoubleMatrix;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;

import static main.Main.PROPS;

/**
 * Represents a Renderer for synthesised faces.
 *
 * @author 150009974
 * @version 1.2
 */
public class SynthesisedRenderer extends JPanel {

    /** The face scaling factor for fitting on scree */
    private static final double SCREEN_FIT_FACTOR =
            PROPS.getDouble("synthesised screen fit factor");

    /** The lighting color used at all times. */
    private static final Color LIGHT = PROPS.getColor("lighting");

    /** The point light source that a {@link SynthesisedRenderer} can switch to. */
    private static final PointLightSource PRESET_POINT;

    static {
        double x = PROPS.getDouble("preset point light source x");
        double y = PROPS.getDouble("preset point light source y");
        double z = PROPS.getDouble("preset point light source z");
        PRESET_POINT = new PointLightSource(x, y, z, LIGHT);
    }

    //<editor-fold desc="Transformation Matrices">
    /** The change in angle, when rotating around. */
    private static final double ROTATION_STEP = PROPS.getDouble("rotation step");

    /** Matrix for rotation of {@link Homogeneous3DPoint}s down around the X axis. */
    private static final DoubleMatrix RX_UP = DoubleMatrix.zeros(4, 4);

    /** Matrix for rotation of {@link Homogeneous3DPoint}s up around the X axis. */
    private static final DoubleMatrix RX_DOWN = DoubleMatrix.zeros(4, 4);

    /** Matrix for rotation of {@link Homogeneous3DPoint}s left around the Y axis. */
    private static final DoubleMatrix RY_LEFT = DoubleMatrix.zeros(4, 4);

    /** Matrix for rotation of {@link Homogeneous3DPoint}s right around the Y axis. */
    private static final DoubleMatrix RY_RIGHT = DoubleMatrix.zeros(4, 4);

    /** Matrix for rotation of {@link Homogeneous3DPoint}s clockwise around the Z axis. */
    private static final DoubleMatrix RZ_CLOCK = DoubleMatrix.zeros(4, 4);

    /** Matrix for rotation of {@link Homogeneous3DPoint}s counter clockwise around the Z axis. */
    private static final DoubleMatrix RZ_COUNTER = DoubleMatrix.zeros(4, 4);

    /** Matrix for enlarging size. */
    private static final DoubleMatrix ENLARGE = DoubleMatrix.zeros(4, 4);

    /** Matrix for reducing size. */
    private static final DoubleMatrix REDUCE = DoubleMatrix.zeros(4, 4);

    static {
        double cosR = Math.cos(ROTATION_STEP);
        double sinR = Math.sin(ROTATION_STEP);

        RX_DOWN.put(0, 0, 1);
        RX_DOWN.put(1, 1, cosR);
        RX_DOWN.put(1, 2, -sinR);
        RX_DOWN.put(2, 1, sinR);
        RX_DOWN.put(2, 2, cosR);
        RX_DOWN.put(3, 3, 1);
        RX_UP.put(0, 0, 1);
        RX_UP.put(1, 1, cosR);
        RX_UP.put(1, 2, sinR);
        RX_UP.put(2, 1, -sinR);
        RX_UP.put(2, 2, cosR);
        RX_UP.put(3, 3, 1);

        RY_LEFT.put(0, 0, cosR);
        RY_LEFT.put(0, 2, sinR);
        RY_LEFT.put(1, 1, 1);
        RY_LEFT.put(2, 0, -sinR);
        RY_LEFT.put(2, 2, cosR);
        RY_LEFT.put(3, 3, 1);
        RY_RIGHT.put(0, 0, cosR);
        RY_RIGHT.put(0, 2, -sinR);
        RY_RIGHT.put(1, 1, 1);
        RY_RIGHT.put(2, 0, sinR);
        RY_RIGHT.put(2, 2, cosR);
        RY_RIGHT.put(3, 3, 1);

        RZ_CLOCK.put(0, 0, cosR);
        RZ_CLOCK.put(0, 1, -sinR);
        RZ_CLOCK.put(1, 0, sinR);
        RZ_CLOCK.put(1, 1, cosR);
        RZ_CLOCK.put(2, 2, 1);
        RZ_CLOCK.put(3, 3, 1);
        RZ_COUNTER.put(0, 0, cosR);
        RZ_COUNTER.put(0, 1, sinR);
        RZ_COUNTER.put(1, 0, -sinR);
        RZ_COUNTER.put(1, 1, cosR);
        RZ_COUNTER.put(2, 2, 1);
        RZ_COUNTER.put(3, 3, 1);

        double enlargeFactor = 1.1;
        ENLARGE.put(0, 0, enlargeFactor);
        ENLARGE.put(1, 1, enlargeFactor);
        ENLARGE.put(2, 2, enlargeFactor);
        ENLARGE.put(3, 3, 1);
        double reduceFactor = 0.9;
        REDUCE.put(0, 0, reduceFactor);
        REDUCE.put(1, 1, reduceFactor);
        REDUCE.put(2, 2, reduceFactor);
        REDUCE.put(3, 3, 1);
    }
    //</editor-fold>

    /** The projector that transforms 3D into 2D. */
    private Projector projector = new OrthographicProjector();

    /** The main source of light. */
    private LightSource source;

    /** The {@link IlluminationModel} that calculates the colors. */
    private IlluminationModel illuminationModel;

    /** The {@link Face} being rendered. */
    private Face current;

    /** Matrix for translating the {@link #current} {@link Face} to the origin. */
    private DoubleMatrix toOrigin;

    /** Matrix for translating the {@link #current} {@link Face} to its center. */
    private DoubleMatrix toCenter;

    /**
     * Creates a {@link SynthesisedRenderer} with default configuration.
     * This includes a single directional light source, aligned with the viewing direction.
     * Lambert's Illumination Model to determine the light source's affect.
     * Flat shading applied to all triangles.
     * Orthographic projection.
     */
    public SynthesisedRenderer() {
        setLayout(null);
        setFocusable(true);
        setBackground(Color.WHITE);

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(final KeyEvent e) {
                int code = e.getKeyCode();
                checkRotation(code);
                checkProjectorChange(code);
                checkZoomLevelChange(code);
                checkLightSourceChange(code);
                repaint();
            }
        });
    }

    /** Configures the {@link #illuminationModel}. */
    public void configureIllumination() {
        double x = getWidth() / 2.0;
        double y = getHeight() / 2.0;
        Homogeneous3DPoint view = new Homogeneous3DPoint(x, y, Integer.MIN_VALUE);
        illuminationModel = new LambertianIlluminationModel(1, view);
        source = new DirectionalLightSource(x, y, Integer.MIN_VALUE, LIGHT);
        illuminationModel.addSource(source);
    }

    /**
     * If the given key code corresponds to a light source change,
     * the if the {@link #source} is not of the pressed class,
     * it is removed from the {@link #illuminationModel},
     * the re-instantiated, and the re-added to the {@link #illuminationModel}.
     *
     * @param code the {@link KeyEvent#getKeyCode()} raised by a key press
     */
    private void checkLightSourceChange(final int code) {
        if (code == KeyEvent.VK_D) {
            if (!(source instanceof DirectionalLightSource)) {
                illuminationModel.removeSource(source);
                double x = getWidth() / 2.0;
                double y = getHeight() / 2.0;
                source = new DirectionalLightSource(x, y, Integer.MIN_VALUE, LIGHT);
                illuminationModel.addSource(source);
            }
        } else if (code == KeyEvent.VK_F) {
            if (!(source instanceof PointLightSource)) {
                illuminationModel.removeSource(source);
                source = PRESET_POINT;
                illuminationModel.addSource(source);
            }
        }
    }

    /**
     * If the given key code corresponds to a zoom level change,
     * then if the {@link #projector} is a {@link PerspectiveProjector},
     * it is re-instantiated with a new target screen F.
     * If the projector is an {@link OrthographicProjector},
     * then the {@link #current} {@link Face} is scaled respectively.
     *
     * @param code the {@link KeyEvent#getKeyCode()} raised by a key press
     */
    private void checkZoomLevelChange(final int code) {
        if (code == KeyEvent.VK_S) {
            if (projector instanceof PerspectiveProjector) {
                double f = ((PerspectiveProjector) projector).getF();
                projector = new PerspectiveProjector(f - 100);
            } else if (projector instanceof OrthographicProjector) {
                transform(REDUCE);
            }
        } else if (code == KeyEvent.VK_W) {
            if (projector instanceof PerspectiveProjector) {
                double f = ((PerspectiveProjector) projector).getF();
                projector = new PerspectiveProjector(f + 100);
            } else if (projector instanceof OrthographicProjector) {
                transform(ENLARGE);
            }
        }
    }

    /**
     * If the given key code corresponds to a {@link Projector} change,
     * then if the {@link #projector} is not of the pressed class,
     * it is re-instantiated.
     *
     * @param code the {@link KeyEvent#getKeyCode()} raised by a key press
     */
    private void checkProjectorChange(final int code) {
        if (code == KeyEvent.VK_P) {
            if (!(projector instanceof PerspectiveProjector)) {
                projector = new PerspectiveProjector(-400);
            }
        } else if (code == KeyEvent.VK_O) {
            if (!(projector instanceof OrthographicProjector)) {
                projector = new OrthographicProjector();
            }
        }
    }

    /**
     * If the given key code corresponds to a mapping for a rotation,
     * the respective rotation is performed.
     *
     * @param code the {@link KeyEvent#getKeyCode()} raised by a key press
     */
    private void checkRotation(int code) {
        if (code == KeyEvent.VK_UP) {
            transform(RX_UP);
        } else if (code == KeyEvent.VK_DOWN) {
            transform(RX_DOWN);
        } else if (code == KeyEvent.VK_LEFT) {
            transform(RY_LEFT);
        } else if (code == KeyEvent.VK_RIGHT) {
            transform(RY_RIGHT);
        } else if (code == KeyEvent.VK_PAGE_DOWN) {
            transform(RZ_CLOCK);
        } else if (code == KeyEvent.VK_PAGE_UP) {
            transform(RZ_COUNTER);
        }
    }

    /**
     * Transforms the {@link #current} {@link Face}
     * via the given transformation matrix.
     * Transformation happens by first translating towards the origin,
     * then multiplying the rotation matrix,
     * and finally translating back to the center.
     *
     * @param transformation the matrix to transform by
     *
     * @see #toOrigin
     * @see #toCenter
     */
    private void transform(final DoubleMatrix transformation) {
        for (Homogeneous3DPoint point : current.getPoints()) {
            // Move towards origin, transform relative to origin, return back
            DoubleMatrix m = point.getMatrix();
            m.data = toOrigin.mmul(m).data;
            m.data = transformation.mmul(m).data;
            m.data = toCenter.mmul(m).data;
        }
    }

    /**
     * Creates and stores a transformation matrix that will
     * translate a point to the origin (0, 0, 0).
     */
    private void storeToOriginTranslationMatrix() {
        Homogeneous3DPoint center = current.getCenter();
        toOrigin = DoubleMatrix.eye(4);
        toOrigin.put(0, 3, -center.getX());
        toOrigin.put(1, 3, -center.getY());
        toOrigin.put(2, 3, -center.getZ());
    }

    /**
     * Creates and stores a transformation matrix that will
     * translate a point to the {@link #current} {@link Face}'s center.
     */
    private void storeToCenterTranslationMatrix() {
        Homogeneous3DPoint center = current.getCenter();
        toCenter = DoubleMatrix.eye(4);
        toCenter.put(0, 3, center.getX());
        toCenter.put(1, 3, center.getY());
        toCenter.put(2, 3, center.getZ());
    }

    /** @param f the {@link Face} to draw */
    public void setFace(final Face f) {
        current = f;
        current.scale(-SCREEN_FIT_FACTOR);
        int x = this.getWidth() / 2;
        int y = this.getHeight() / 2;
        Homogeneous3DPoint c = new Homogeneous3DPoint(x, y, 0);
        current.setCenter(c);
        storeToOriginTranslationMatrix();
        storeToCenterTranslationMatrix();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        renderFace((Graphics2D) g);
    }

    /**
     * Renders the current {@link Face} using the given {@link Graphics2D} instance.
     *
     * @param g the {@link Graphics2D} to use for rendering
     */
    public void renderFace(final Graphics2D g) {
        if (current == null) {
            return;
        }
        ArrayList<Homogeneous3DPolygon> polygons = current.getPolygons();
        Collections.sort(polygons);
        for (Homogeneous3DPolygon polygon : polygons) {
            illuminationModel.configure(g, polygon);
            Shape projection = projector.project(polygon);
            g.fill(projection);
        }
    }

}
