package com.ss.editor.ui.control.property.builder.impl;

import com.ss.editor.Editor;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of the {@link PropertyBuilder}.
 *
 * @param <C> the type of a {@link ChangeConsumer}
 * @author JavaSaBr
 */
public abstract class AbstractPropertyBuilder<C extends ChangeConsumer> implements PropertyBuilder {

    /**
     * The jME part of the editor.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * The type of change consumer,
     */
    @NotNull
    private final Class<? extends C> type;

    protected AbstractPropertyBuilder(@NotNull final Class<? extends C> type) {
        this.type = type;
    }

    @Override
    @FXThread
    public void buildFor(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                         @NotNull final ChangeConsumer changeConsumer) {

        if (type.isInstance(changeConsumer)) {
            buildForImpl(object, parent, container, type.cast(changeConsumer));
        }
    }

    /**
     * Build properties for the object.
     *
     * @param object         the object.
     * @param parent         the parent.
     * @param container      the container.
     * @param changeConsumer the change consumer.
     */
    @FXThread
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent,
                                @NotNull final VBox container, @NotNull final C changeConsumer) {
    }

    /**
     * Create a split pane.
     *
     * @param pane the container of the line.
     */
    @FXThread
    protected void buildSplitLine(@NotNull final Pane pane) {
        final HBox line = new HBox();
        final VBox container = new VBox(line);
        FXUtils.addClassTo(line, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(container, CSSClasses.ABSTRACT_PARAM_CONTROL_CONTAINER_SPLIT_LINE);
        FXUtils.addToPane(container, pane);
    }
}
