package main;

import model.Face;
import model.Homogeneous3DPoint;
import model.Homogeneous3DPolygon;
import renderers.DirectionalLightSource;
import renderers.IlluminationModel;
import renderers.LambertianIlluminationModel;
import renderers.LightSource;
import renderers.OrthographicProjector;
import renderers.Projector;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

import static main.Main.L;

/**
 * Represents a QuickVisualisation.
 *
 * @author 150009974
 * @version 2.0
 */
public final class QuickVisualisation extends JDialog {

    public static void main(final String[] args) {
        Face face = Face.parse("data/sh_001.csv", "data/tx_001.csv", 0);
        face.scale(-1);
        face.scale(0.003);
        QuickVisualisation quick = new QuickVisualisation();
        quick.setFace(face);
        quick.setModal(true);
        quick.setVisible(true);
        System.out.println("Closing logger.");
        L.close();
    }

    /** The main drawing area. */
    private JPanel canvas = new JPanel();

    /** The projector to use. */
    private Projector projector = new OrthographicProjector();

    /** The currently used {@link IlluminationModel}. */
    private IlluminationModel illuminationModel;

    /** The currently displayed face. */
    private Face current;

    /** Creates an application window with default configuration. */
    private QuickVisualisation() {
        config();
        configureCanvas();
        configureLightSource();
    }

    /** Applies the default configuration of the Application. */
    private void config() {
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLayout(null);
        this.setTitle("Quick Face Rendering");
        this.setUndecorated(true);
        Rectangle appBounds = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getMaximumWindowBounds();
        this.setBounds(appBounds);
    }

    /** Configures the {@link #illuminationModel} of light. */
    private void configureLightSource() {
        double x = this.getWidth() / 2.0;
        double y = this.getHeight() / 2.0;
        LightSource source;
        Color sunlight = new Color(255, 225, 200);
//        source = new PointLightSource(0, 0, -300, sunlight);
        source = new DirectionalLightSource(0, 0, Integer.MIN_VALUE, sunlight);
        Homogeneous3DPoint view = new Homogeneous3DPoint(x, y, Integer.MIN_VALUE);
        illuminationModel = new LambertianIlluminationModel(1.00, view);
        illuminationModel.addSource(source);
    }

    /** Configures the drawing canvas. */
    private void configureCanvas() {
        canvas.setLayout(null);
        canvas.setFocusable(true);

        // Full Screen
        canvas.setLocation(0, 0);
        canvas.setSize(this.getSize());

        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                int z = 0;
                Homogeneous3DPoint center = new Homogeneous3DPoint(x, y, z);
                current.setCenter(center);
                repaint();
            }
        });
        canvas.addMouseWheelListener(e -> {
            Homogeneous3DPoint center = current.getCenter();
            current.setCenter(new Homogeneous3DPoint(0, 0, 0));
            current.scale(1 - 0.1 * e.getPreciseWheelRotation());
            current.setCenter(center);
            repaint();
        });
        this.add(canvas);
    }

    /** @param f the face to draw */
    public void setFace(final Face f) {
        current = f;
        double x = this.getWidth() / 2.0;
        double y = this.getHeight() / 2.0;
        Homogeneous3DPoint center = new Homogeneous3DPoint(x, y, 0);
        current.setCenter(center);
        Collections.sort(current.getPolygons());
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        paintFace((Graphics2D) g);
    }

    /**
     * Draws the {@link #current} {@link Face} using the given
     * {@link Graphics2D} object.
     *
     * @param g the {@link Graphics2D} to use
     */
    public void paintFace(final Graphics2D g) {
        ArrayList<Homogeneous3DPolygon> polygons = current.getPolygons();
        for (Homogeneous3DPolygon polygon : polygons) {
            illuminationModel.configure(g, polygon);
            Shape projection = projector.project(polygon);
            g.fill(projection);
        }
    }

}
