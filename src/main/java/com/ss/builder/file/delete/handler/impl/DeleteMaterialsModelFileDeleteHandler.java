package com.ss.editor.file.delete.handler.impl;

import static com.ss.editor.FileExtensions.JME_OBJECT;
import static com.ss.editor.util.EditorUtils.getAssetFile;
import static com.ss.editor.util.EditorUtils.toAssetPath;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.dialog.ConfirmDialog;
import com.ss.editor.util.EditorUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The implementation of delete handler to delete related materials.
 *
 * @author JavaSaBr
 */
public class DeleteMaterialsModelFileDeleteHandler extends AbstractFileDeleteHandler {

    /**
     * The list of used materials.
     */
    @NotNull
    private final Array<String> assetKeys;

    public DeleteMaterialsModelFileDeleteHandler() {
        this.assetKeys = ArrayFactory.newArray(String.class);
    }

    @Override
    public void preDelete(@NotNull final Path file) {
        super.preDelete(file);

        final AssetManager assetManager = EditorUtils.getAssetManager();
        final Path assetFile = notNull(getAssetFile(file));
        final String assetPath = toAssetPath(assetFile);

        final Spatial model;

        try {
            model = assetManager.loadModel(assetPath);
        } catch (final Exception e) {
            LOGGER.warning(this, e);
            return;
        }

        NodeUtils.visitGeometry(model, geometry -> {

            final Material material = geometry.getMaterial();
            final String assetName = material.getAssetName();

            if (!StringUtils.isEmpty(assetName)) {
                assetKeys.add(assetName);
            }
        });
    }

    /**
     * Get the list of used materials.
     *
     * @return the list of used materials.
     */
    private @NotNull Array<String> getAssetKeys() {
        return assetKeys;
    }

    @Override
    public void postDelete(@NotNull final Path file) {
        super.postDelete(file);

        final Array<String> assetKeys = getAssetKeys();
        if (assetKeys.isEmpty()) {
            return;
        }

        String question = Messages.FILE_DELETE_HANDLER_DELETE_MATERIALS;
        question = question.replace("%file_name%", file.getFileName().toString());

        final ConfirmDialog confirmDialog = new ConfirmDialog(this::handle, question);
        confirmDialog.show();
    }

    private void handle(@Nullable final Boolean result) {

        if (!Boolean.TRUE.equals(result)) {
            return;
        }

        getAssetKeys().stream().map(EditorUtils::getRealFile)
                .filter(Files::exists)
                .forEach(FileUtils::delete);
    }

    @Override
    public boolean isNeedHandle(@NotNull final Path file) {
        final String extension = FileUtils.getExtension(file);
        return JME_OBJECT.equals(extension);
    }
}
