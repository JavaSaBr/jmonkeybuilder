package com.ss.editor.ui.control.property.builder;

import com.ss.editor.annotation.FxThread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to filter builder for some objects.
 *
 * @author JavaSaBr
 */
@FunctionalInterface
public interface PropertyBuilderFilter {

    /**
     * Check the property builder and the object.
     *
     * @param builder the property builder.
     * @param object  the object.
     * @param parent  the parent.
     * @return true of we should skip the builder for the object.
     */
    @FxThread
    boolean skip(@NotNull PropertyBuilder builder, @NotNull Object object, @Nullable Object parent);
}
