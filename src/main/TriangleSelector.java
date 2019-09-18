package main;

import org.jblas.DoubleMatrix;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;

import static main.Main.PROPS;

/**
 * Represents a Triangle-style Selector.
 * The user clicks within a triangle to set weights
 * for the face synthesiser.
 *
 * @author 150009974
 * @version 1.0
 */
public class TriangleSelector extends JPanel {

    /** The radius of the selected point as a proportion of screen dimensions. */
    private static final double SELECTION_RADIUS =
            PROPS.getDouble("triangle selection radius");

    /** The user-selected {@link Point}. */
    private Point selection;

    /** The radius of the {@link #selection} to draw. */
    private int selectionRadius;

    /** The top {@link Point} of the triangle. Corresponds to top face. */
    private Point top;

    /** The left {@link Point} of the triangle. Corresponds to left face. */
    private Point left;

    /** The right {@link Point} of the triangle. Corresponds to right face. */
    private Point right;

    /** The inverse Matrix of M. @see {@link #configureComponents()}. */
    private DoubleMatrix M_1 = new DoubleMatrix(2, 2);

    /** The drawable triangle. */
    private Polygon triangle = new Polygon();

    /** The method used to configure weights when the user selects a point. */
    private BiConsumer<Double, Double> onClick;

    /** Creates a {@link TriangleSelector} with default configuration. */
    public TriangleSelector() {
        setLayout(null);
        setFocusable(true);
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(final MouseEvent e) {
                DoubleMatrix Q = new DoubleMatrix(2, 1);
                Q.put(0, 0, e.getX() - top.getX());
                Q.put(1, 0, e.getY() - top.getY());
                DoubleMatrix alphaBeta = M_1.mmul(Q);
                double alpha = alphaBeta.get(0, 0);
                double beta = alphaBeta.get(1, 0);
                selection = e.getPoint();
                if (onClick != null) {
                    onClick.accept(alpha, beta);
                }
                repaint();
            }
        });
    }

    /** Sets the triangle's {@link #top}, {@link #left}, and {@link #right} {@link Point}s. */
    public void configureComponents() {
        configurePoints();
        configureTriangle();
        configureM1();
    }

    /** Configures the points of the triangle. */
    private void configurePoints() {
        int y = this.getHeight() * 3 / 4;
        int width = this.getWidth();
        int mid = width / 2;
        left = new Point(0, y);
        top = new Point(mid, 0);
        right = new Point(width, y);
        double geometricMean = Math.sqrt(width * getHeight());
        selectionRadius = (int) (geometricMean * SELECTION_RADIUS);
    }

    /** Configures the {@link #triangle}. */
    private void configureTriangle() {
        triangle = new Polygon();
        triangle.addPoint(left.x, left.y);
        triangle.addPoint(top.x, top.y);
        triangle.addPoint(right.x, right.y);
    }

    /** Configures the matrix used for determining weights. */
    private void configureM1() {
        double a = left.getX() - top.getX();
        double b = right.getX() - top.getX();
        double c = left.getY() - top.getY();
        double d = right.getY() - top.getY();
        double determinant = 1 / (a * d - b * c);
        M_1.put(0, 0, d * determinant);
        M_1.put(0, 1, -b * determinant);
        M_1.put(1, 0, -c * determinant);
        M_1.put(1, 1, a * determinant);
    }

    /** @param onClick the method to execute when new weights are selected */
    public void setOnClick(final BiConsumer<Double, Double> onClick) {
        this.onClick = onClick;
    }

    /**
     * Changes the selection, given values for alpha and beta interpolations.
     *
     * @param alpha the weight of the left face
     * @param beta  the weight of the right face
     */
    public void changeSelection(final double alpha, final double beta) {
        double tx = top.getX();
        double ty = top.getY();
        double x = tx + alpha * (left.getX() - tx) + beta * (right.getX() - tx);
        double y = ty + alpha * (left.getY() - ty) + beta * (right.getY() - ty);
        selection = new Point((int) x, (int) y);
        repaint();
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        paintTriangle(g);
        paintSelection(g);
    }

    /**
     * Draws the selection triangle on the screen
     * using the given {@link Graphics} instance.
     *
     * @param g the {@link Graphics} instance used to draw
     */
    private void paintTriangle(final Graphics g) {
        if (triangle != null) {
            g.setColor(Color.BLACK);
            g.drawPolygon(triangle);
        }
    }

    /**
     * Draws the current {@link #selection} on the screen
     * using the given {@link Graphics} instance.
     *
     * @param g the {@link Graphics} instance used to draw
     */
    private void paintSelection(final Graphics g) {
        if (selection == null) {
            return;
        }
        g.setColor(Color.RED);
        int x = selection.x - selectionRadius;
        int y = selection.y - selectionRadius;
        int d = 2 * selectionRadius;
        g.fillOval(x, y, d, d);
    }

}
