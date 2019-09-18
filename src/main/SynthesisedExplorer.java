package main;

import model.Face;
import renderers.SynthesisedRenderer;

import javax.swing.JDialog;
import javax.swing.WindowConstants;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

/**
 * Provides a way to interact with a Synthesised Face.
 *
 * @author 150009974
 * @version 1.0
 */
public class SynthesisedExplorer extends JDialog {

    /** The renderer for synthesised faces. */
    private SynthesisedRenderer renderer = new SynthesisedRenderer();

    public SynthesisedExplorer(final Face synthesised) {
        configureBasic();
        configureRenderer();
        renderer.setFace(synthesised);
    }

    /** Sets the basic configuration. */
    private void configureBasic() {
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLayout(null);
        this.setUndecorated(true);
        Rectangle appBounds = GraphicsEnvironment
                .getLocalGraphicsEnvironment().getMaximumWindowBounds();
        this.setBounds(appBounds);
    }

    /** Configures the {@link #renderer}. */
    private void configureRenderer() {
        renderer.setLocation(0, 0);
        renderer.setSize(this.getSize());
        renderer.configureIllumination();
        this.add(renderer);
    }

}
