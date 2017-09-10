package com.ss.editor.ui.control.model.property.control.particle;

import static com.ss.editor.util.EditorUtil.getRealFile;
import com.jme3.asset.AssetKey;
import com.jme3.material.Material;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.MaterialPropertyControl;
import com.ss.editor.ui.dialog.asset.ParticlesAssetEditorDialog;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import com.ss.rlib.util.StringUtils;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.material.ParticlesMaterial;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The implementation of the {@link MaterialPropertyControl} to edit the {@link Material} of the {@link
 * ParticleEmitterNode}*.
 *
 * @author JavaSaBr
 */
public class MaterialEmitterPropertyControl extends
        MaterialPropertyControl<ModelChangeConsumer, ParticleEmitterNode, ParticlesMaterial> {

    public MaterialEmitterPropertyControl(@NotNull final ParticlesMaterial element, @NotNull final String paramName,
                                          @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    /**
     * Show dialog for choosing another material.
     */
    @FXThread
    protected void processChange() {
        final ParticlesAssetEditorDialog dialog = new ParticlesAssetEditorDialog(this::addMaterial);
        dialog.setExtensionFilter(MATERIAL_EXTENSIONS);
        dialog.show();
    }

    /**
     * Add the mew material.
     */
    private void addMaterial(@NotNull final ParticlesMaterial particlesMaterial) {
        changed(particlesMaterial, getPropertyValue());
    }

    @FXThread
    @Override
    protected void processEdit() {

        final ParticlesMaterial element = getPropertyValue();
        if (element == null) return;

        final Material material = element.getMaterial();
        final AssetKey<?> key = material.getKey();
        if (key == null) return;

        final Path assetFile = Paths.get(key.getName());
        final Path realFile = getRealFile(assetFile);
        if (realFile == null || !Files.exists(realFile)) return;

        final RequestedOpenFileEvent event = new RequestedOpenFileEvent();
        event.setFile(realFile);

        FX_EVENT_MANAGER.notify(event);
    }

    @FXThread
    @Override
    protected void reload() {

        final ParticlesMaterial element = getPropertyValue();
        if (element == null) return;

        final Material material = element.getMaterial();
        final AssetKey<?> key = material.getKey();

        final Label materialLabel = getMaterialLabel();
        materialLabel.setText(key == null || StringUtils.isEmpty(key.getName()) ? NO_MATERIAL : key.getName());
    }
}
