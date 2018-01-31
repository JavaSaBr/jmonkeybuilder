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
    public static final long serialVersionUID = 3;

    /**
     * The selected models.
     */
    @NotNull
    private String[] selectedModels;

    /**
     * The models scale.
     */
    @NotNull
    private Vector3f scale;

    /**
     * The spawn method.
     */
    private int method;

    public SpawnPaintingStateWithEditorTool() {
        this.method = SpawnMethod.BATCH.ordinal();
        this.selectedModels = new String[SpawnPaintingComponent.AVAILABLE_MODELS];
        this.scale = new Vector3f(Vector3f.UNIT_XYZ);
    }

    /**
     * Set the spawn method.
     *
     * @param method the spawn method.
     */
    @FxThread
    public void setMethod(final int method) {
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
     * Get the models scale.
     *
     * @param scale the models scale.
     */
    @FxThread
    public void setScale(@NotNull final Vector3f scale) {
        this.scale = scale;
    }

    /**
     * Set the models scale.
     *
     * @return the models scale.
     */
    @FxThread
    public @NotNull Vector3f getScale() {
        return scale;
    }

    /**
     * Set the selected models.
     *
     * @param selectedModels the selected models.
     */
    @FxThread
    public void setSelectedModels(@NotNull final String[] selectedModels) {
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
