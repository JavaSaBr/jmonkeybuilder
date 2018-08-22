package com.ss.builder.fx.tooltip;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.css.CssClasses;
import com.ss.rlib.fx.util.FXUtils;
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

    public CustomTooltip() {
        this.root = createRoot();
        this.root.setStyle("-fx-background-color: -var-menu-background-color;");

        final Scene scene = getScene();
        scene.setRoot(root);

        createContent(root);

        FXUtils.addClassTo(root, CssClasses.CUSTOM_TOOLTIP);
    }

    /**
     * Get the root container.
     *
     * @return the root container.
     */
    @FxThread
    protected @NotNull R getRoot() {
        return root;
    }

    /**
     * Create the root container.
     *
     * @return the root container.
     */
    @FxThread
    protected abstract @NotNull R createRoot();

    /**
     * Create content of this tooltip.
     *
     * @param root the root container.
     */
    @FxThread
    protected void createContent(@NotNull final R root) {
    }
}
