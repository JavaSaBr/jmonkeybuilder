package com.ss.editor.ui.control.app.state.property.builder.impl;

import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.model.property.builder.impl.EditableModelObjectPropertyBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The property builder to build property controls of editable scene app states.
 *
 * @author JavaSaBr
 */
public class AppStatePropertyBuilder extends EditableModelObjectPropertyBuilder {

    @NotNull
    private static final AppStatePropertyBuilder INSTANCE = new AppStatePropertyBuilder();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static AppStatePropertyBuilder getInstance() {
        return INSTANCE;
    }

    /**
     * Instantiates a new App state property builder.
     */
    protected AppStatePropertyBuilder() {
        super(SceneChangeConsumer.class);
    }

    @Override
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull final Object object) {
        if (!(object instanceof EditableSceneAppState)) return null;
        final EditableSceneAppState state = (EditableSceneAppState) object;
        return state.getEditableProperties();
    }
}
