package com.ss.editor.ui.control.model.property.particle.influencer.color;

import com.ss.editor.ui.util.UIUtils;

import org.jetbrains.annotations.NotNull;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import tonegod.emitter.influencers.ColorInfluencer;

/**
 * The control for editing colors in the {@link ColorInfluencer}.
 *
 * @author JavaSaBr
 */
public class ColorInfluencerControl extends ScrollPane {

    /**
     * The color influencer.
     */
    private final ColorInfluencer influencer;

    /**
     * The root of this control.
     */
    private VBox root;

    public ColorInfluencerControl(@NotNull final ColorInfluencer influencer) {
        this.influencer = influencer;
        setContent(createControls());
    }

    private Node createControls() {

        root = new VBox();

        return root;
    }

    /**
     * @return the color influencer.
     */
    protected ColorInfluencer getInfluencer() {
        return influencer;
    }

    /**
     * @return the root of this control.
     */
    @NotNull
    protected VBox getRoot() {
        return root;
    }

    public void reload() {

        final ColorInfluencer influencer = getInfluencer();
        final VBox root = getRoot();

        UIUtils.clear(root);

    }
}
