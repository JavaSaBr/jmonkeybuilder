package com.ss.editor.ui.control.property.builder.impl;

import com.ss.editor.Editor;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import com.ss.rlib.ui.util.FXUtils;

/**
 * The base implementation of the {@link PropertyBuilder}.
 *
 * @param <C> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractPropertyBuilder<C extends ChangeConsumer> implements PropertyBuilder {

    /**
     * The constant SPLIT_LINE_OFFSET.
     */
    protected static final Insets SPLIT_LINE_OFFSET = new Insets(6, 0, 6, 0);

    /**
     * The constant EDITOR.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

    @NotNull
    private final Class<C> type;

    /**
     * Instantiates a new Abstract property builder.
     *
     * @param type the type
     */
    protected AbstractPropertyBuilder(final @NotNull Class<C> type) {
        this.type = type;
    }

    /**
     * Create a new line for splitting properties.
     *
     * @param container the container
     * @return the line
     */
    @NotNull
    protected Line createSplitLine(@NotNull final VBox container) {

        final Line line = new Line();
        line.setId(CSSIds.ABSTRACT_PARAM_CONTROL_SPLIT_LINE);
        line.setStartX(0);
        line.endXProperty().bind(container.widthProperty().subtract(50));

        return line;
    }

    /**
     * Create a new line for splitting properties and add this to the container.
     *
     * @param container the container
     */
    protected void addSplitLine(final @NotNull VBox container) {
        final Line splitLine = createSplitLine(container);
        VBox.setMargin(splitLine, SPLIT_LINE_OFFSET);
        FXUtils.addToPane(splitLine, container);
    }

    @Override
    public void buildFor(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                         @NotNull final ChangeConsumer changeConsumer) {

        if (type.isInstance(changeConsumer)) {
            buildForImpl(object, parent, container, type.cast(changeConsumer));
        }
    }

    /**
     * Build for.
     *
     * @param object         the object
     * @param parent         the parent
     * @param container      the container
     * @param changeConsumer the change consumer
     */
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final C changeConsumer) {
    }
}
