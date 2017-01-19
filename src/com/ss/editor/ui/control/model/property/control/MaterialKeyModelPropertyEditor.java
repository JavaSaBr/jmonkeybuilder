package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.getRealFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;

import com.jme3.asset.MaterialKey;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.dialog.asset.FileAssetEditorDialog;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import javafx.scene.control.Label;
import rlib.util.StringUtils;

/**
 * The implementation of the {@link ModelPropertyControl} to edit the {@link MaterialKey}.
 *
 * @author JavaSaBr
 */
public class MaterialKeyModelPropertyEditor<T extends Spatial> extends MaterialModelPropertyEditor<T, MaterialKey> {

    public MaterialKeyModelPropertyEditor(@Nullable final MaterialKey element, @NotNull final String paramName,
                                          @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void processChange() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final AssetEditorDialog dialog = new FileAssetEditorDialog(this::addMaterial);
        dialog.setExtensionFilter(MATERIAL_EXTENSIONS);
        dialog.show(scene.getWindow());
    }

    /**
     * Add the mew material.
     */
    private void addMaterial(@NotNull final Path file) {

        final Path assetFile = Objects.requireNonNull(getAssetFile(file));
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
        final Path realFile = Objects.requireNonNull(getRealFile(assetFile));
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
