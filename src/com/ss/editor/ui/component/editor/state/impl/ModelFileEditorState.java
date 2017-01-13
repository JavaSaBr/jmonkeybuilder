package com.ss.editor.ui.component.editor.state.impl;

import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;

/**
 * The implementation of a state container for the {@link ModelFileEditor}.
 *
 * @author JavaSaBr
 */
public class ModelFileEditorState extends AbstractEditorState {

    public static final long serialVersionUID = 2;

    /**
     * The sky type.
     */
    private volatile int skyType;

    /**
     * The transformation type.
     */
    private volatile int transformationType;

    /**
     * Is enabled light.
     */
    private volatile boolean enableLight;

    /**
     * Is enabled grid.
     */
    private volatile boolean enableGrid;

    /**
     * Is enabled selection.
     */
    private volatile boolean enableSelection;

    public ModelFileEditorState() {
        this.skyType = 0;
        this.enableLight = EDITOR_CONFIG.isDefaultEditorCameraEnabled();
        this.enableGrid = true;
        this.enableSelection = true;
    }

    /**
     * @return the sky type.
     */
    public int getSkyType() {
        return skyType;
    }

    /**
     * @param skyType the sky type.
     */
    public void setSkyType(final int skyType) {
        final boolean changed = getSkyType() != skyType;
        this.skyType = skyType;
        final Runnable changeHandler = getChangeHandler();
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * @param enableLight true if the light is enabled.
     */
    public void setEnableLight(final boolean enableLight) {
        final boolean changed = isEnableLight() != enableLight;
        this.enableLight = enableLight;
        final Runnable changeHandler = getChangeHandler();
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * @return true if the light is enabled.
     */
    public boolean isEnableLight() {
        return enableLight;
    }

    /**
     * @param enableGrid true is the grid is enabled.
     */
    public void setEnableGrid(final boolean enableGrid) {
        final boolean changed = isEnableGrid() != enableGrid;
        this.enableGrid = enableGrid;
        final Runnable changeHandler = getChangeHandler();
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * @return true is the grid is enabled.
     */
    public boolean isEnableGrid() {
        return enableGrid;
    }

    /**
     * @param enableSelection true if the selection is enabled.
     */
    public void setEnableSelection(final boolean enableSelection) {
        final boolean changed = isEnableSelection() != enableSelection;
        this.enableSelection = enableSelection;
        final Runnable changeHandler = getChangeHandler();
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * @return true if the selection is enabled.
     */
    public boolean isEnableSelection() {
        return enableSelection;
    }

    /**
     * @return the transformation type.
     */
    public int getTransformationType() {
        return transformationType;
    }

    /**
     * @param transformationType the transformation type.
     */
    public void setTransformationType(final int transformationType) {
        final boolean changed = getTransformationType() != transformationType;
        this.transformationType = transformationType;
        final Runnable changeHandler = getChangeHandler();
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    @Override
    public String toString() {
        return "ModelFileEditorState{" +
                "skyType=" + skyType +
                ", transformationType=" + transformationType +
                ", enableLight=" + enableLight +
                ", enableGrid=" + enableGrid +
                ", enableSelection=" + enableSelection +
                "} " + super.toString();
    }
}
