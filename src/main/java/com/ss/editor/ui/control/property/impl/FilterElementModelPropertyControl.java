package com.ss.editor.ui.control.property.impl;

import com.jme3.post.Filter;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.dialog.scene.selector.FilterSceneSelectorDialog;
import com.ss.editor.ui.dialog.scene.selector.SceneSelectorDialog;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link SceneElementPropertyControl} to edit light from a scene.
 *
 * @param <D> the edited object's type.
 * @author JavaSaBr
 */
public class FilterElementModelPropertyControl<D> extends SceneElementPropertyControl<D, Filter> {

    public FilterElementModelPropertyControl(@Nullable final Filter propertyValue, @NotNull final String propertyName,
                                             @NotNull final SceneChangeConsumer changeConsumer) {
        super(Filter.class, propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected @NotNull SceneSelectorDialog<Filter> createSceneSelectorDialog() {
        final SceneChangeConsumer changeConsumer = getChangeConsumer();
        return new FilterSceneSelectorDialog(changeConsumer.getCurrentModel(), this::processAdd);
    }

    @Override
    @FxThread
    protected void reload() {
        final Filter filter = getPropertyValue();
        final Label elementLabel = getElementLabel();
        String name = filter == null ? null : filter.getName();
        name = name == null && filter != null ? filter.getClass().getSimpleName() : name;
        elementLabel.setText(name == null ? NO_ELEMENT : name);
    }
}
