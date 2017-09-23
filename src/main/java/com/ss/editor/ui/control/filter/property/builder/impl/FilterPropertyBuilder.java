package com.ss.editor.ui.control.filter.property.builder.impl;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.scene.filter.EditableSceneFilter;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.builder.impl.EditableModelObjectPropertyBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The property builder to build property controls of editable scene app states.
 *
 * @author JavaSaBr
 */
public class FilterPropertyBuilder extends EditableModelObjectPropertyBuilder {

    @NotNull
    private static final FilterPropertyBuilder INSTANCE = new FilterPropertyBuilder();

    /**
     * Get the single instance.
     *
     * @return the single instance
     */
    @FromAnyThread
    public static @NotNull FilterPropertyBuilder getInstance() {
        return INSTANCE;
    }

    protected FilterPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FXThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull final Object object) {
        if (!(object instanceof EditableSceneFilter)) return null;
        final EditableSceneFilter filter = (EditableSceneFilter) object;
        return filter.getEditableProperties();
    }
}