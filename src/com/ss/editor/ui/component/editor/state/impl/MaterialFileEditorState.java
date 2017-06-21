package com.ss.editor.ui.component.editor.state.impl;

import com.jme3.renderer.queue.RenderQueue;
import com.ss.editor.state.editor.impl.material.MaterialEditorAppState;
import com.ss.editor.ui.component.editor.impl.material.MaterialFileEditor;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of a state container for the {@link MaterialFileEditor}.
 *
 * @author JavaSaBr
 */
public class MaterialFileEditorState extends AbstractEditorState {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 3;

    private static transient final RenderQueue.Bucket[] BUCKETS = RenderQueue.Bucket.values();

    /**
     * The selected model type.
     */
    private volatile int modelType;

    /**
     * The selected bucket type.
     */
    private volatile int bucketTypeId;

    /**
     * Opened editor tool.
     */
    private volatile int openedTool;

    /**
     * Is light enable.
     */
    private volatile boolean lightEnable;

    /**
     * Instantiates a new Material file editor state.
     */
    public MaterialFileEditorState() {
        modelType = MaterialEditorAppState.ModelType.BOX.ordinal();
        bucketTypeId = RenderQueue.Bucket.Inherit.ordinal();
        openedTool = 0;
        lightEnable = EDITOR_CONFIG.isDefaultEditorCameraEnabled();
    }

    /**
     * Gets bucket type.
     *
     * @return the bucket type.
     */
    public RenderQueue.Bucket getBucketType() {
        return BUCKETS[bucketTypeId];
    }

    /**
     * Sets bucket type.
     *
     * @param bucketType the bucket type.
     */
    public void setBucketType(@NotNull final RenderQueue.Bucket bucketType) {
        final boolean changed = getBucketTypeId() != bucketType.ordinal();
        this.bucketTypeId = bucketType.ordinal();
        final Runnable changeHandler = getChangeHandler();
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * Gets bucket type id.
     *
     * @return the bucket type ordinal.
     */
    public int getBucketTypeId() {
        return bucketTypeId;
    }

    /**
     * Gets opened tool.
     *
     * @return the opened tool.
     */
    public int getOpenedTool() {
        return openedTool;
    }

    /**
     * Sets opened tool.
     *
     * @param openedTool the opened tool.
     */
    public void setOpenedTool(final int openedTool) {
        final boolean changed = getOpenedTool() != openedTool;
        this.openedTool = openedTool;
        final Runnable changeHandler = getChangeHandler();
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * Gets model type.
     *
     * @return the model type.
     */
    public int getModelType() {
        return modelType;
    }

    /**
     * Sets model type.
     *
     * @param modelType the model type.
     */
    public void setModelType(@NotNull final MaterialEditorAppState.ModelType modelType) {
        final boolean changed = getModelType() != modelType.ordinal();
        this.modelType = modelType.ordinal();
        final Runnable changeHandler = getChangeHandler();
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * Is light enable boolean.
     *
     * @return true if the light is enabled.
     */
    public boolean isLightEnable() {
        return lightEnable;
    }

    /**
     * Sets light enable.
     *
     * @param lightEnable true if the light is enabled.
     */
    public void setLightEnable(final boolean lightEnable) {
        final boolean changed = isLightEnable() != lightEnable;
        this.lightEnable = lightEnable;
        final Runnable changeHandler = getChangeHandler();
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    @Override
    public String toString() {
        return "MaterialFileEditorState{" +
                "modelType=" + modelType +
                ", bucketTypeId=" + bucketTypeId +
                ", openedTool=" + openedTool +
                ", lightEnable=" + lightEnable +
                "} " + super.toString();
    }
}
