package com.ss.editor.ui.control.filter.property.control;

import static com.ss.editor.ui.control.filter.property.control.FilterPropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.model.tree.dialog.NodeSelectorDialog;
import com.ss.editor.ui.control.property.impl.AbstractElementPropertyControl;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractElementPropertyControl} to edit an elements from scene.
 *
 * @author JavaSaBr
 */
public abstract class AbstractElementFilterPropertyControl<D, T> extends AbstractElementPropertyControl<SceneChangeConsumer, D, T> {

    public AbstractElementFilterPropertyControl(@NotNull final Class<T> type, @Nullable final T propertyValue,
                                                @NotNull final String propertyName,
                                                @NotNull final SceneChangeConsumer changeConsumer) {
        super(type, propertyValue, propertyName, changeConsumer, newChangeHandler());
    }

    @Override
    protected void processAdd() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final NodeSelectorDialog<T> dialog = createNodeSelectorDialog();
        dialog.show(scene.getWindow());
    }

    @NotNull
    protected NodeSelectorDialog<T> createNodeSelectorDialog() {
        final SceneChangeConsumer changeConsumer = getChangeConsumer();
        return new NodeSelectorDialog<>(changeConsumer.getCurrentModel(), type, this::processAdd);
    }

    protected void processAdd(@NotNull final T newElement) {
        changed(newElement, getPropertyValue());
    }
}
