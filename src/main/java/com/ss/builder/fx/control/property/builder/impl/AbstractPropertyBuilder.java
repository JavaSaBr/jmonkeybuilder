package com.ss.builder.ui.control.property.builder.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.ui.css.CssClasses;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FxUtils;
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
     * The type of change consumer,
     */
    @NotNull
    private final Class<? extends C> type;

    protected AbstractPropertyBuilder(@NotNull Class<? extends C> type) {
        this.type = type;
    }

    @Override
    @FxThread
    public void buildFor(
            @NotNull Object object,
            @Nullable Object parent,
            @NotNull VBox container,
            @NotNull ChangeConsumer changeConsumer
    ) {
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
    @FxThread
    protected void buildForImpl(
            @NotNull Object object,
            @Nullable Object parent,
            @NotNull VBox container,
            @NotNull C changeConsumer
    ) {
    }

    /**
     * Create a split pane.
     *
     * @param pane the container of the line.
     */
    @FxThread
    protected void buildSplitLine(@NotNull Pane pane) {

        var line = new HBox();
        var container = new VBox(line);

        FxUtils.addClass(line, CssClasses.DEF_HBOX)
                .addClass(container, CssClasses.PROPERTY_EDITOR_CONTAINER_SPLIT_LINE);

        FxUtils.addChild(pane, container);
    }
}
