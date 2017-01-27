package com.ss.editor.ui.tooltip;

import org.jetbrains.annotations.NotNull;

import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;

/**
 * The base implementation of custom tooltip.
 *
 * @author JavaSaBr
 */
public abstract class CustomTooltip<R extends Region> extends Tooltip {

    /**
     * The root container.
     */
    @NotNull
    private final R root;

    public CustomTooltip() {
        this.root = createRoot();

        final Scene scene = getScene();
        scene.setRoot(root);

        createContent(root);
    }

    /**
     * @return the root container.
     */
    @NotNull
    protected R getRoot() {
        return root;
    }

    @NotNull
    protected abstract R createRoot();

    /**
     * Create content of this tooltip.
     */
    protected void createContent(@NotNull final R root) {
    }
}
