package com.ss.builder.ui.control.property.impl;

import com.jme3.post.Filter;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.dialog.scene.selector.FilterSceneSelectorDialog;
import com.ss.editor.ui.dialog.scene.selector.SceneSelectorDialog;
import com.ss.rlib.common.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link SceneElementPropertyControl} to edit light from a scene.
 *
 * @param <D> the edited object's type.
 * @author JavaSaBr
 */
public class FilterElementModelPropertyControl<D> extends SceneElementPropertyControl<D, Filter> {

    public FilterElementModelPropertyControl(
            @Nullable Filter propertyValue,
            @NotNull String propertyName,
            @NotNull SceneChangeConsumer changeConsumer
    ) {
        super(Filter.class, propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected @NotNull SceneSelectorDialog<Filter> createSceneSelectorDialog() {
        return new FilterSceneSelectorDialog(getChangeConsumer().getCurrentModel(), this::addElement);
    }

    @Override
    @FxThread
    protected @NotNull String getElementText() {
        return getPropertyValueOpt()
                .map(Filter::getName)
                .filter(StringUtils::isNotEmpty)
                .or(() -> getPropertyValueOpt()
                        .map(Filter::getClass)
                        .map(Class::getSimpleName))
                .orElse(NO_ELEMENT);
    }
}
