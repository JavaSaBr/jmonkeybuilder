package com.ss.editor.ui.component.painting.spawn;

import com.jme3.math.Vector3f;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.control.painting.spawn.SpawnToolControl.SpawnMethod;
import com.ss.editor.ui.component.painting.impl.AbstractPaintingStateWithEditorTool;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * The state of spawn painting component.
 *
 * @author JavaSaBr
 */
public class SpawnPaintingStateWithEditorTool extends AbstractPaintingStateWithEditorTool {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 4;

    /**
     * The selected models.
     */
    @NotNull
    private String[] selectedModels;

    /**
     * The models min scale.
     */
    @NotNull
    private Vector3f minScale;

    /**
     * The models max scale.
     */
    @NotNull
    private Vector3f maxScale;

    /**
     * The models padding.
     */
    @NotNull
    private Vector3f padding;

    /**
     * The spawn method.
     */
    private int method;

    public SpawnPaintingStateWithEditorTool() {
        this.method = SpawnMethod.BATCH.ordinal();
        this.selectedModels = new String[SpawnPaintingComponent.AVAILABLE_MODELS];
        this.minScale = Vector3f.UNIT_XYZ.clone();
        this.maxScale = Vector3f.UNIT_XYZ.clone();
        this.padding = Vector3f.ZERO.clone();
    }

    /**
     * Set the spawn method.
     *
     * @param method the spawn method.
     */
    @FxThread
    public void setMethod(int method) {
        final boolean changed = getMethod() != method;
        this.method = method;
        if (changed) notifyChange();
    }

    /**
     * Get the spawn method.
     *
     * @return the spawn method.
     */
    @FxThread
    public int getMethod() {
        return method;
    }

    /**
     * Set the models min scale.
     *
     * @param minScale the models min scale.
     */
    @FxThread
    public void setMinScale(@NotNull Vector3f minScale) {
        final boolean changed = !minScale.equals(getMinScale());
        this.minScale = minScale;
        if (changed) notifyChange();
    }

    /**
     * Set the models max scale.
     *
     * @param maxScale the models max scale.
     */
    @FxThread
    public void setMaxScale(@NotNull Vector3f maxScale) {
        final boolean changed = !maxScale.equals(getMaxScale());
        this.maxScale = maxScale;
        if (changed) notifyChange();
    }

    /**
     * Set the models padding.
     *
     * @param padding the models padding.
     */
    @FxThread
    public void setPadding(@NotNull Vector3f padding) {
        final boolean changed = !padding.equals(getPadding());
        this.padding = padding;
        if (changed) notifyChange();
    }

    /**
     * Get the models min scale.
     *
     * @return the models min scale.
     */
    @FxThread
    public @NotNull Vector3f getMinScale() {
        return minScale;
    }

    /**
     * Get the models max scale.
     *
     * @return the models max scale.
     */
    @FxThread
    public @NotNull Vector3f getMaxScale() {
        return maxScale;
    }

    /**
     * Get the models padding.
     *
     * @return the models padding.
     */
    @FxThread
    public @NotNull Vector3f getPadding() {
        return padding;
    }

    /**
     * Set the selected models.
     *
     * @param selectedModels the selected models.
     */
    @FxThread
    public void setSelectedModels(@NotNull String[] selectedModels) {
        final boolean changed = !Arrays.equals(getSelectedModels(), selectedModels);
        this.selectedModels = selectedModels;
        if (changed) notifyChange();
    }

    /**
     * Get the selected models.
     *
     * @return the selected models.
     */
    @FxThread
    public @NotNull String[] getSelectedModels() {
        return selectedModels;
    }
}
