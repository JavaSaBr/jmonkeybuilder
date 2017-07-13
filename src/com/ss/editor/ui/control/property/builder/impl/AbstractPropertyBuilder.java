package com.ss.editor.ui.control.property.builder.impl;

import com.ss.editor.Editor;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of the {@link PropertyBuilder}.
 *
 * @param <C> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractPropertyBuilder<C extends ChangeConsumer> implements PropertyBuilder {

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
