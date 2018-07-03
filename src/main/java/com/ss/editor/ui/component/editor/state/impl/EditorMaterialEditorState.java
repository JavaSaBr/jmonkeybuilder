package com.ss.editor.ui.component.editor.state.impl;

import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_CAMERA_LAMP;
import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_CAMERA_LIGHT;
import com.jme3.renderer.queue.RenderQueue;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.editor.material.BaseMaterialEditor3DPart;
import com.ss.editor.ui.component.editor.impl.material.MaterialFileEditor;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of a state container for the {@link MaterialFileEditor}.
 *
 * @author JavaSaBr
 */
public class EditorMaterialEditorState extends Editor3dWithEditorToolEditorState {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 6;

    @NotNull
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
     * Is light enable.
     */
    private volatile boolean lightEnable;

    public EditorMaterialEditorState() {
        modelType = BaseMaterialEditor3DPart.ModelType.BOX.ordinal();
        bucketTypeId = RenderQueue.Bucket.Inherit.ordinal();
        lightEnable = EDITOR_CONFIG.getBoolean(PREF_CAMERA_LAMP, PREF_DEFAULT_CAMERA_LIGHT);
    }

    /**
     * Gets bucket type.
     *
     * @return the bucket type.
     */
    @FxThread
    public @NotNull RenderQueue.Bucket getBucketType() {
        return BUCKETS[bucketTypeId];
    }

    /**
     * Sets bucket type.
     *
     * @param bucketType the bucket type.
     */
    @FxThread
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
    @FxThread
    public int getBucketTypeId() {
        return bucketTypeId;
    }

    /**
     * Gets model type.
     *
     * @return the model type.
     */
    @FxThread
    public int getModelType() {
        return modelType;
    }

    /**
     * Sets model type.
     *
     * @param modelType the model type.
     */
    @FxThread
    public void setModelType(@NotNull final BaseMaterialEditor3DPart.ModelType modelType) {
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
    @FxThread
    public boolean isLightEnable() {
        return lightEnable;
    }

    /**
     * Sets light enable.
     *
     * @param lightEnable true if the light is enabled.
     */
    @FxThread
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
        return "EditorMaterialEditorState{" +
                "modelType=" + modelType +
                ", bucketTypeId=" + bucketTypeId +
                ", lightEnable=" + lightEnable +
                "} " + super.toString();
    }
}
