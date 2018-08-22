package com.ss.builder.ui.control.property.impl;

import com.jme3.asset.AssetKey;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.EditorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The implementation of the {@link PropertyControl} to edit {@link com.jme3.asset.AssetKey}.
 *
 * @param <C> the change consumer's type.
 * @param <E> the edited object's type.
 * @param <T> the asset key's generic type.
 * @param <A> the asset key's type.
 * @author JavaSaBr
 */
public abstract class AssetKeyPropertyControl<C extends ChangeConsumer, E, T, A extends AssetKey<T>>
        extends KeyPropertyControl<C, E, A> {


    public AssetKeyPropertyControl(
            @Nullable A element,
            @NotNull String paramName,
            @NotNull C changeConsumer
    ) {
        super(element, paramName, changeConsumer);
        setOnDragOver(this::handleDragOverEvent);
        setOnDragDropped(this::handleDragDroppedEvent);
        setOnDragExited(this::handleDragExitedEvent);
    }

    @Override
    @FxThread
    protected void chooseKey() {
        UiUtils.openFileAssetDialog(this::applyNewKey, getExtensions(), DEFAULT_ACTION_TESTER);
        super.chooseKey();
    }

    @Override
    @FxThread
    protected void applyNewKey(@NotNull Path file) {
        super.applyNewKey(file);
        reload();
    }

    @Override
    @FxThread
    protected void openKey() {
        getPropertyValueOpt().ifPresent(EditorUtils::openInEditor);
        super.openKey();
    }

    @Override
    @FxThread
    protected void reloadImpl() {
        keyLabel.setText(EditorUtils.ifEmpty(getPropertyValue(), getNoKeyLabel()));
        super.reloadImpl();
    }
}
