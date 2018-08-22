package com.ss.builder.ui.component.painting;

import com.ss.builder.annotation.FxThread;
import com.ss.editor.annotation.FxThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to create a painting component.
 *
 * @author JavaSaBr
 */
@FunctionalInterface
public interface ComponentConstructor {

    /**
     * Create a new painting component.
     *
     * @param container the container.
     * @return the new painting component.
     */
    @FxThread
    @NotNull PaintingComponent create(@NotNull PaintingComponentContainer container);
}
