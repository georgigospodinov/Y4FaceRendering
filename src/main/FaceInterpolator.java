package main;

import model.Face;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.WindowConstants;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import static main.Main.PROPS;

/**
 * Represents an interpolator between Faces.
 *
 * @author 150009974
 * @version 1.4
 */
public class FaceInterpolator extends JDialog {

    /** Proportionate width of a {@link FaceSelector}. */
    private static final double SELECTOR_WIDTH =
            PROPS.getDouble("face interpolator selector width");

    /** Proportionate height of a {@link FaceSelector}. */
    private static final double SELECTOR_HEIGHT =
            PROPS.getDouble("face interpolator selector height");

    /** Proportionate width of the {@link #synthesise} button. */
    private static final double SYNTHESISE_BUTTON_WIDTH =
            PROPS.getDouble("synthesise button width");

    /** Proportionate height of the {@link #synthesise} button. */
    private static final double SYNTHESISE_BUTTON_HEIGTH =
            PROPS.getDouble("synthesise button height");

    /** The {@link FaceSelector} at the top of the triangle. */
    private FaceSelector top;

    /** The {@link FaceSelector} at the left of the triangle. */
    private FaceSelector left;

    /** The {@link FaceSelector} at the right of the triangle. */
    private FaceSelector right;

    /** The Pane containing the triangle through which the user sets the weights. */
    private TriangleSelector trianglePane = new TriangleSelector();

    /** The button that causes a new Face to be synthesised and displayed. */
    private JButton synthesise = new JButton("Synthesise");

    /** Creates a {@link FaceInterpolator} with default configuration. */
    public FaceInterpolator() {
        configGeneral();
        configureTopFace();
        configureBottomLeftFace();
        configureBottomRightFace();
        configureTriangle();
        configureSynthesise();
    }

    /** Updates all the previews. */
    public void updatePreviews() {
        top.updatePreview();
        left.updatePreview();
        right.updatePreview();
    }

    /** Applies the default configuration of this {@link FaceInterpolator}. */
    private void configGeneral() {
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLayout(null);
        this.setTitle("Face Interpolation");
        this.setUndecorated(true);
        Rectangle appBounds = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getMaximumWindowBounds();
        this.setBounds(appBounds);
    }

    /** Configures the {@link #top} {@link FaceSelector}. */
    private void configureTopFace() {
        top = new FaceSelector("001");
        int w = (int) (this.getWidth() * SELECTOR_WIDTH);
        int h = (int) (this.getHeight() * SELECTOR_HEIGHT);
        int x = (this.getWidth() - w) / 2;
        int y = 0;
        top.setLocation(x, y);
        top.setSize(w, h);
        top.configureComponents();
        top.addWeightActionListener(e -> adjustWeightsTopAnchor());
        this.add(top);
    }

    /** Configures the {@link #left} {@link FaceSelector}. */
    private void configureBottomLeftFace() {
        left = new FaceSelector("002");
        int w = (int) (this.getWidth() * SELECTOR_WIDTH);
        int h = (int) (this.getHeight() * SELECTOR_HEIGHT);
        int x = 0;
        int y = this.getHeight() - h * 2;
        left.setLocation(x, y);
        left.setSize(w, h);
        left.configureComponents();
        left.addWeightActionListener(e -> adjustWeightsLeftAnchor());
        this.add(left);
    }

    /** Configures the {@link #right} {@link FaceSelector}. */
    private void configureBottomRightFace() {
        right = new FaceSelector("003");
        int w = (int) (this.getWidth() * SELECTOR_WIDTH);
        int h = (int) (this.getHeight() * SELECTOR_HEIGHT);
        int x = this.getWidth() - w;
        int y = this.getHeight() - h * 2;
        right.setLocation(x, y);
        right.setSize(w, h);
        right.configureComponents();
        double weight = 1 - top.getWeight() - left.getWeight();
        right.setWeight(weight);
        right.addWeightActionListener(e -> adjustWeightsRightAnchor());
        this.add(right);
    }

    /** Configures the selection triangle. */
    private void configureTriangle() {
        // Position triangle between the three FaceSelectors
        int x = left.getX() + left.getWidth();
        int y = top.getY() + top.getHeight();
        int w = right.getX() - x;
        int h = right.getY() + right.getHeight() - y;
        trianglePane.setLocation(x, y);
        trianglePane.setSize(w, h);
        trianglePane.configureComponents();
        trianglePane.setOnClick(this::determineWeights);
        this.add(trianglePane);
    }

    /** Configures the {@link #synthesise} button. */
    private void configureSynthesise() {
        // Top-right corner
        int w = (int) (this.getWidth() * SYNTHESISE_BUTTON_WIDTH);
        int h = (int) (this.getHeight() * SYNTHESISE_BUTTON_HEIGTH);
        int x = this.getWidth() - w;
        int y = 0;
        synthesise.setLocation(x, y);
        synthesise.setSize(w, h);
        synthesise.addActionListener(e -> {
            Face[] faces = {
                    top.restoreFace(), left.restoreFace(), right.restoreFace()};
            double[] weights = {
                    top.getWeight(), left.getWeight(), right.getWeight()};
            Face synthesised = Face.synthesise(faces, weights);
            SynthesisedExplorer explorer = new SynthesisedExplorer(synthesised);
            String title = "Synthesis of " + top.getContribution() + " + "
                    + left.getContribution() + " + " + right.getContribution();
            explorer.setTitle(title);
            explorer.setVisible(true);
            top.updatePreview();
            left.updatePreview();
            right.updatePreview();
        });
        this.add(synthesise);
    }

    /** Adjusts the weights of the faces, anchoring the top weight. */
    private void adjustWeightsTopAnchor() {
        double tw = top.getWeight();
        double lw = left.getWeight();
        double rw = right.getWeight();

        if (tw > 1) {
            top.setWeight(1);
        } else if (tw < 0) {
            top.setWeight(0);
        }
        tw = top.getWeight();

        if (tw + lw > 1) {
            left.setWeight(1 - tw);
        }
        lw = left.getWeight();

        if (rw != 1 - tw - lw) {
            // weights must sum up to one.
            right.setWeight(1 - tw - lw);
        }
        trianglePane.changeSelection(left.getWeight(), right.getWeight());
    }

    /** Adjusts the weights of the faces, anchoring the left weight. */
    private void adjustWeightsLeftAnchor() {
        double tw = top.getWeight();
        double lw = left.getWeight();
        double rw = right.getWeight();

        if (lw > 1) {
            left.setWeight(1);
        } else if (lw < 0) {
            left.setWeight(0);
        }
        lw = left.getWeight();

        if (lw + rw > 1) {
            right.setWeight(1 - lw);
        }
        rw = right.getWeight();

        if (tw != 1 - lw - rw) {
            // weights must sum up to one.
            top.setWeight(1 - lw - rw);
        }
        trianglePane.changeSelection(left.getWeight(), right.getWeight());
    }

    /** Adjusts the weights of the faces, anchoring the right weight. */
    private void adjustWeightsRightAnchor() {
        double tw = top.getWeight();
        double lw = left.getWeight();
        double rw = right.getWeight();

        if (rw > 1) {
            right.setWeight(1);
        } else if (rw < 0) {
            right.setWeight(0);
        }
        rw = right.getWeight();

        if (rw + tw > 1) {
            top.setWeight(1 - rw);
        }
        tw = top.getWeight();

        if (lw != 1 - rw - tw) {
            // weights must sum up to one.
            left.setWeight(1 - rw - tw);
        }
        trianglePane.changeSelection(left.getWeight(), right.getWeight());
    }

    /** Determines the weights given the alpha and beta 2D interpolation. */
    private void determineWeights(final Double alpha, final Double beta) {
        if (alpha >= 0 && beta >= 0 && alpha + beta <= 1) {
            top.setWeight(1 - alpha - beta);
            left.setWeight(alpha);
            right.setWeight(beta);
        }
    }

}
