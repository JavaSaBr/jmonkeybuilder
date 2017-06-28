package com.ss.editor.ui.tooltip;

import org.jetbrains.annotations.NotNull;

import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;

/**
 * The base implementation of custom tooltip.
 *
 * @param <R> the type parameter
 * @author JavaSaBr
 */
public abstract class CustomTooltip<R extends Region> extends Tooltip {

    /**
     * The root container.
     */
    @NotNull
    private final R root;

    /**
     * Instantiates a new Custom tooltip.
     */
    CustomTooltip() {
        this.root = createRoot();

        final Scene scene = getScene();
        scene.setRoot(root);

        createContent(root);
    }

    /**
     * Gets root.
     *
     * @return the root container.
     */
    @NotNull
    protected R getRoot() {
        return root;
    }

    /**
     * Create root r.
     *
     * @return the r
     */
    @NotNull
    protected abstract R createRoot();

    /**
     * Create content of this tooltip.
     *
     * @param root the root
     */
    protected void createContent(@NotNull final R root) {
    }
}
