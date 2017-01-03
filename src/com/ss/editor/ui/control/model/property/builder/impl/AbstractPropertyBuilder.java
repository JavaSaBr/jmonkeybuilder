package com.ss.editor.ui.control.model.property.builder.impl;

import com.ss.editor.Editor;
import com.ss.editor.ui.control.model.property.builder.PropertyBuilder;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import rlib.ui.util.FXUtils;

/**
 * The base implementation of the {@link PropertyBuilder}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractPropertyBuilder implements PropertyBuilder {

    protected static final Insets SPLIT_LINE_OFFSET = new Insets(6, 0, 6, 0);

    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * Create a new line for splitting properties.
     */
    @NotNull
    protected Line createSplitLine(@NotNull final VBox container) {

        final Line line = new Line();
        line.setId(CSSIds.MODEL_PARAM_CONTROL_SPLIT_LINE);
        line.setStartX(0);
        line.endXProperty().bind(container.widthProperty().subtract(50));

        return line;
    }

    /**
     * Create a new line for splitting properties and add this to the container.
     */
    protected void addSplitLine(final @NotNull VBox container) {
        final Line splitLine = createSplitLine(container);
        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
        FXUtils.addToPane(splitLine, container);
    }
}
