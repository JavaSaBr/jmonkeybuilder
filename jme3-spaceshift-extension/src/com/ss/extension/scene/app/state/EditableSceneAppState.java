package com.ss.extension.scene.app.state;

import com.ss.extension.property.EditableProperty;
import com.ss.extension.scene.filter.SceneFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The interface to implement am editable scene app state.
 *
 * @author JavaSaBr
 */
public interface EditableSceneAppState extends SceneAppState {

    Array<EditableProperty<?, ?>> EMPTY_PROPERTIES = ArrayFactory.newArray(EditableProperty.class);

    /**
     * Get list of editable properties.
     *
     * @return the list of editable properties.
     */
    @NotNull
    default Array<EditableProperty<?, ?>> getEditableProperties() {
        return EMPTY_PROPERTIES;
    }

    /**
     * Check state dependencies.
     *
     * @param exists the current exists states.
     * @return null of can create or message with description.
     */
    @Nullable
    default String checkStates(@NotNull final Array<SceneAppState> exists) {
        return null;
    }

    /**
     * Check filter dependencies.
     *
     * @param exists the current exists filters.
     * @return null of can create or message with description.
     */
    @Nullable
    default String checkFilters(@NotNull final Array<SceneFilter<?>> exists) {
        return null;
    }
}

