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

/**
 * Базовая реализация операции по смене текстуры материала.
 *
 * @author Ronn
 */
public class TextureMaterialParamOperation extends AbstractEditorOperation<MaterialChangeConsumer> {

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    /**
     * Название изменяемого параметра.
     */
    private final String paramName;

    /**
     * Новый ключ текстуры..
     */
    private final TextureKey newTextureKey;

    /**
     * Режим врапинга.
     */
    private final Texture.WrapMode newWrapMode;

    /**
     * Старое ключ текстуры.
     */
    private final TextureKey oldTextureKey;

    /**
     * Старый режим врапинга.
     */
    private final Texture.WrapMode oldWrapModel;

    public TextureMaterialParamOperation(final String paramName, final TextureKey newTextureKey, final Texture.WrapMode newWrapMode, final TextureKey oldTextureKey, final Texture.WrapMode oldWrapModel) {
        this.paramName = paramName;
        this.newTextureKey = newTextureKey;
        this.newWrapMode = newWrapMode;
        this.oldTextureKey = oldTextureKey;
        this.oldWrapModel = oldWrapModel;
    }

    /**
     * @return название изменяемого параметра.
     */
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
