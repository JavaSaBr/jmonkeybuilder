package com.ss.builder.fx.editor.toolbar;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.editor.FileEditor;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.util.DynamicIconSupport;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The base toolbar action for {@link FileEditor}.
 *
 * @author JavaSaBr
 */
public abstract class ToolbarAction<T extends FileEditor> extends Button {

    /**
     * The file editor.
     */
    @NotNull
    protected final T fileEditor;

    public ToolbarAction(@NotNull T fileEditor) {
        this.fileEditor = fileEditor;

        setGraphic(new ImageView(getIcon()));
        setOnAction(event -> activate());
        getToolTipText().ifPresent(text -> setTooltip(new Tooltip(text)));

        FxUtils.addClass(this,
                CssClasses.FLAT_BUTTON, CssClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(this);
    }

    /**
     * Get the action's icon.
     *
     * @return the action's icon.
     */
    @FromAnyThread
    protected abstract @NotNull Image getIcon();

    /**
     * Get the tooltip text for this action.
     *
     * @return the tooltip text for this action.
     */
    @FromAnyThread
    protected @NotNull Optional<String> getToolTipText() {
        return Optional.empty();
    }

    /**
     * Activate this action.
     */
    @FxThread
    protected abstract void activate();
}
