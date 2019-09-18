package main;

import model.Face;
import renderers.PreviewRenderer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import static main.Main.PROPS;

/**
 * Represents a selector for a {@link Face}.
 * By requirements, there would be three instances of this class,
 * showing the three {@link Face}s to interpolate.
 *
 * @author 150009974
 * @version 1.4
 */
public class FaceSelector extends JPanel {

    /** The maximum height of a text field, as a % of this panel's height. */
    private static final double FIELD_MAX_HEIGHT =
            PROPS.getDouble("face selector field max height");

    /** The face scaling factor for the preview. */
    private static final double FACE_SCALING =
            PROPS.getDouble("face selector face scaling");

    /** The prefix before a shape filename. */
    private static final String SHAPE_PREFIX =
            PROPS.getString("shape prefix");

    /** The prefix before a texture filename. */
    private static final String TEXTURE_PREFIX =
            PROPS.getString("texture prefix");

    /** The suffix after a filename (extension). */
    private static final String FILENAME_SUFFIX =
            PROPS.getString("filename suffix");

    /** The initial weight of a {@link Face}. */
    private static final double INITIAL_WEIGHT =
            PROPS.getDouble("face selector initial weight");

    /** The weight of this {@link Face} for synthesising. */
    private JTextField weight = new JTextField();

    /** The panel where a preview will be shown. */
    private PreviewRenderer preview = new PreviewRenderer();

    /** The {@link JTextField} to enter a file name. */
    private JTextField faceIndex = new JTextField();

    /** Creates a {@link FaceSelector} with a vertical {@link BoxLayout}. */
    public FaceSelector(final String initialFaceIndex) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setWeight(INITIAL_WEIGHT);
        faceIndex.setText(initialFaceIndex);
    }

    /** Configures all the components. */
    public void configureComponents() {
        configureWeight();
        this.add(preview);
        configureFaceIndex();
    }

    /** Configures the {@link #weight} component. */
    private void configureWeight() {
        int h = (int) (getHeight() * FIELD_MAX_HEIGHT);
        weight.setMaximumSize(new Dimension(getWidth(), h));
        weight.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(weight);
    }

    /** Configures the {@link #faceIndex} component. */
    private void configureFaceIndex() {
        int h = (int) (getHeight() * FIELD_MAX_HEIGHT);
        faceIndex.setMaximumSize(new Dimension(getWidth(), h));
        faceIndex.setHorizontalAlignment(SwingConstants.CENTER);
        faceIndex.addActionListener(e -> updatePreview());
        this.add(faceIndex);
    }

    /**
     * Updates the preview face.
     * Parses the {@link Face} identified by the index in {@link #faceIndex}.
     * Then inverts it (account for upside-down by default)
     * and scales it by {@link #FACE_SCALING}.
     * Finally, passes it to {@link #preview} to do a quick render.
     */
    public void updatePreview() {
        String index = faceIndex.getText();
        String shapeFile = SHAPE_PREFIX + index + FILENAME_SUFFIX;
        String textureFile = TEXTURE_PREFIX + index + FILENAME_SUFFIX;
        int weightIndex = Integer.parseInt(index) - 1;
        Face current = Face.parse(shapeFile, textureFile, weightIndex);
        current.scale(-FACE_SCALING);
        preview.setFace(current);
        this.validate();
        this.repaint();
    }

    /**
     * Undoes all transformation done on the {@link Face} when it was previewed,
     * returns it and forgets about it.
     *
     * @return the originally parsed {@link Face}
     */
    public Face restoreFace() {
        Face f = preview.restoreFace();
        f.scale(-1 / FACE_SCALING);
        return f;
    }

    /**
     * Creates and returns a {@link String} describing the contribution
     * of this {@link FaceSelector} to the synthesised {@link Face}.
     * This is intended to be used as a title.
     *
     * @return the contribution of this {@link FaceSelector} to the synthesis
     */
    public String getContribution() {
        return String.format("%.2f", getWeight()) + "*" + faceIndex.getText();
    }

    /** @return the selected weight of the {@link Face} in the synthetic one */
    public double getWeight() {
        return Double.parseDouble((weight.getText()));
    }

    /** @param w the weight to use when synthesising a {@link Face} */
    public void setWeight(final double w) {
        weight.setText(String.format("%.3f", w));
    }

    /**
     * Adds the given {@link ActionListener} to this {@link FaceSelector}'s
     * {@link #weight} field.
     * The primary use of this method is to connect the weights
     * of the different {@link FaceSelector}s.
     *
     * @param listener the listener to add
     */
    public void addWeightActionListener(final ActionListener listener) {
        weight.addActionListener(listener);
    }

}
