package com.ss.editor.ui.control.material.operation;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;
import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.MaterialChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.EditorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The operation to change texture parameters.
 *
 * @author JavaSaBr
 */
public class TextureMaterialParamOperation extends AbstractEditorOperation<MaterialChangeConsumer> {

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    /**
     * The parameter name.
     */
    @NotNull
    private final String paramName;

    /**
     * The new texture key.
     */
    @Nullable
    private final TextureKey newTextureKey;

    /**
     * The new wrap mode.
     */
    @Nullable
    private final Texture.WrapMode newWrapMode;

    /**
     * The old texture key.
     */
    @Nullable
    private final TextureKey oldTextureKey;

    /**
     * The old wrap mode.
     */
    @Nullable
    private final Texture.WrapMode oldWrapModel;

    /**
     * Instantiates a new Texture material param operation.
     *
     * @param paramName     the param name
     * @param newTextureKey the new texture key
     * @param newWrapMode   the new wrap mode
     * @param oldTextureKey the old texture key
     * @param oldWrapModel  the old wrap model
     */
    public TextureMaterialParamOperation(@NotNull final String paramName, @Nullable final TextureKey newTextureKey,
                                         @Nullable final Texture.WrapMode newWrapMode,
                                         @Nullable final TextureKey oldTextureKey,
                                         @Nullable final Texture.WrapMode oldWrapModel) {
        this.paramName = paramName;
        this.newTextureKey = newTextureKey;
        this.newWrapMode = newWrapMode;
        this.oldTextureKey = oldTextureKey;
        this.oldWrapModel = oldWrapModel;
    }

    /**
     * Gets param name.
     *
     * @return The parameter name.
     */
    @NotNull
    protected String getParamName() {
        return paramName;
    }

    @Override
    protected void redoImpl(@NotNull final MaterialChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Material currentMaterial = editor.getCurrentMaterial();

            if (newTextureKey == null) {
                currentMaterial.clearParam(getParamName());
            } else {

                final AssetManager assetManager = EDITOR.getAssetManager();

                final Texture texture;
                try {
                    texture = assetManager.loadTexture(newTextureKey);
                } catch (final Exception e) {
                    EditorUtil.handleException(null, this, e);
                    return;
                }

                texture.setWrap(newWrapMode);

                currentMaterial.setTextureParam(getParamName(), VarType.Texture2D, texture);
            }

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeParam(getParamName()));
        });
    }

    @Override
    protected void undoImpl(@NotNull final MaterialChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Material currentMaterial = editor.getCurrentMaterial();

            if (oldTextureKey == null) {
                currentMaterial.clearParam(getParamName());
            } else {

                final AssetManager assetManager = EDITOR.getAssetManager();

                final Texture texture;

                try {
                    texture = assetManager.loadTexture(oldTextureKey);
                } catch (final Exception e) {
                    EditorUtil.handleException(null, this, e);
                    return;
                }

                texture.setWrap(oldWrapModel);

                currentMaterial.setTextureParam(getParamName(), VarType.Texture2D, texture);
            }

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeParam(getParamName()));
        });
    }
}
