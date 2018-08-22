package com.ss.builder.fx.control.property.impl;

import com.jme3.asset.MaterialKey;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.util.EditorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The implementation of the {@link MaterialPropertyControl} to edit the {@link MaterialKey}.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class MaterialKeyPropertyControl<C extends ChangeConsumer, T> extends MaterialPropertyControl<C, T, MaterialKey> {

    public MaterialKeyPropertyControl(
            @Nullable MaterialKey element,
            @NotNull String paramName,
            @NotNull C changeConsumer
    ) {
        super(element, paramName, changeConsumer);
    }

    @Override
    @FxThread
    protected void chooseKey() {
        UiUtils.openFileAssetDialog(this::applyNewKey, MATERIAL_EXTENSIONS, DEFAULT_ACTION_TESTER);
        super.chooseKey();
    }

    @Override
    @FxThread
    protected void applyNewKey(@NotNull Path file) {
        changed(EditorUtils.realFileToKey(file, MaterialKey::new), getPropertyValue());
        super.applyNewKey(file);
    }

    @Override
    @FxThread
    protected void openKey() {
        EditorUtils.openInEditor(getPropertyValue());
        super.openKey();
    }

    @Override
    @FxThread
    protected void reloadImpl() {
        keyLabel.setText(EditorUtils.ifEmpty(getPropertyValue(), NO_MATERIAL));
        super.reloadImpl();
    }
}
