package com.ss.editor.ui.control.property.impl;

import static com.ss.editor.util.EditorUtil.*;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.MaterialKey;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.util.StringUtils;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The implementation of the {@link MaterialPropertyControl} to edit the {@link MaterialKey}.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class MaterialKeyPropertyControl<C extends ChangeConsumer, T> extends MaterialPropertyControl<C, T, MaterialKey> {

    public MaterialKeyPropertyControl(@Nullable final MaterialKey element, @NotNull final String paramName,
                                      @NotNull final C changeConsumer) {
        super(element, paramName, changeConsumer);
    }

    @Override
    protected void processChange() {
        UIUtils.openFileAssetDialog(this::addMaterial, MATERIAL_EXTENSIONS, DEFAULT_ACTION_TESTER);
    }

    @Override
    protected void addMaterial(@NotNull final Path file) {

        final Path assetFile = notNull(getAssetFile(file));
        final MaterialKey materialKey = new MaterialKey(toAssetPath(assetFile));

        changed(materialKey, getPropertyValue());
        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }
    }

    @Override
    protected void processEdit() {

        final MaterialKey element = getPropertyValue();
        if (element == null) return;

        final String assetPath = element.getName();
        if (StringUtils.isEmpty(assetPath)) return;

        final Path assetFile = Paths.get(assetPath);
        final Path realFile = notNull(getRealFile(assetFile));
        if (!Files.exists(realFile)) return;

        final RequestedOpenFileEvent event = new RequestedOpenFileEvent();
        event.setFile(realFile);

        FX_EVENT_MANAGER.notify(event);
    }

    @Override
    protected void reload() {
        final MaterialKey element = getPropertyValue();
        final Label materialLabel = getMaterialLabel();
        materialLabel.setText(element == null || StringUtils.isEmpty(element.getName()) ? NO_MATERIAL : element.getName());
    }
}
