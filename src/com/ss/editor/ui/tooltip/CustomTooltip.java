package com.ss.editor.ui.tooltip;

import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;

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
        this.root.setStyle("-fx-background-color: -var-menu-background-color;");

        final Scene scene = getScene();
        scene.setRoot(root);

        createContent(root);

        FXUtils.addClassTo(root, CSSClasses.CUSTOM_TOOLTIP);
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
