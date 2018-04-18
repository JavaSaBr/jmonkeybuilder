package com.ss.editor.ui.control.property.impl;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.asset.MaterialKey;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.util.StringUtils;
import javafx.event.ActionEvent;
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
    protected void change(@Nullable ActionEvent event) {
        UiUtils.openFileAssetDialog(this::addMaterial, MATERIAL_EXTENSIONS, DEFAULT_ACTION_TESTER);
    }

    @Override
    @FxThread
    protected void addMaterial(@NotNull Path file) {

        var assetFile = notNull(getAssetFile(file));
        var materialKey = new MaterialKey(toAssetPath(assetFile));

        changed(materialKey, getPropertyValue());
        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }
    }

    @Override
    @FxThread
    protected void openToEdit(@Nullable ActionEvent event) {
        EditorUtil.openInEditor(getPropertyValue());
    }

    @Override
    @FxThread
    protected void reload() {
        var element = getPropertyValue();
        getMaterialLabel().setText(element == null || StringUtils.isEmpty(element.getName()) ?
                NO_MATERIAL : element.getName());
    }
}
