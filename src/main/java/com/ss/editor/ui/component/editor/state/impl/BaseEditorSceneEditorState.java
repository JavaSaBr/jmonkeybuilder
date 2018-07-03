package com.ss.editor.ui.component.editor.state.impl;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.control.transform.EditorTransformSupport.TransformType;
import com.ss.editor.control.transform.EditorTransformSupport.TransformationMode;
import com.ss.editor.ui.component.editor.impl.scene.AbstractSceneFileEditor;

/**
 * The base implementation of a state container for the {@link AbstractSceneFileEditor}.
 *
 * @author JavaSaBr
 */
public abstract class BaseEditorSceneEditorState extends Editor3dWithEditorToolEditorState {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 1;

    /**
     * The transformation type.
     */
    private volatile int transformationType;

    /**
     * The transformation mode.
     */
    private volatile int transformationMode;

    /**
     * Is enabled grid.
     */
    private volatile boolean enableGrid;

    /**
     * Is enabled selection.
     */
    private volatile boolean enableSelection;

    /**
     * Is enabled showing statistics.
     */
    private volatile boolean showStatistics;

    public BaseEditorSceneEditorState() {
        this.enableGrid = true;
        this.enableSelection = true;
        this.transformationMode = TransformationMode.GLOBAL.ordinal();
        this.transformationType = TransformType.MOVE_TOOL.ordinal();
    }

    /**
     * Sets enable grid.
     *
     * @param enableGrid true is the grid is enabled.
     */
    @FxThread
    public void setEnableGrid(final boolean enableGrid) {
        final boolean changed = isEnableGrid() != enableGrid;
        this.enableGrid = enableGrid;
        if (changed) notifyChange();
    }

    /**
     * Is enable grid boolean.
     *
     * @return true is the grid is enabled.
     */
    @FxThread
    public boolean isEnableGrid() {
        return enableGrid;
    }

    /**
     * Sets enable selection.
     *
     * @param enableSelection true if the selection is enabled.
     */
    @FxThread
    public void setEnableSelection(final boolean enableSelection) {
        final boolean changed = isEnableSelection() != enableSelection;
        this.enableSelection = enableSelection;
        if (changed) notifyChange();
    }

    /**
     * Is enable selection boolean.
     *
     * @return true if the selection is enabled.
     */
    @FxThread
    public boolean isEnableSelection() {
        return enableSelection;
    }

    /**
     * Gets transformation type.
     *
     * @return the transformation type.
     */
    @FxThread
    public int getTransformationType() {
        return transformationType;
    }

    /**
     * Sets transformation type.
     *
     * @param transformationType the transformation type.
     */
    @FxThread
    public void setTransformationType(final int transformationType) {
        final boolean changed = getTransformationType() != transformationType;
        this.transformationType = transformationType;
        if (changed) notifyChange();
    }

    /**
     * Gets transformation mode.
     *
     * @return the transformation mode.
     */
    @FxThread
    public int getTransformationMode() {
        return transformationMode;
    }

    /**
     * Sets transformation mode.
     *
     * @param transformationMode the transformation mode.
     */
    @FxThread
    public void setTransformationMode(final int transformationMode) {
        final boolean changed = getTransformationMode() != transformationMode;
        this.transformationMode = transformationMode;
        if (changed) notifyChange();
    }

    /**
     * Sets show statistics.
     *
     * @param showStatistics true if the statistics is need to show.
     */
    @FxThread
    public void setShowStatistics(final boolean showStatistics) {
        final boolean changed = isShowStatistics() != showStatistics;
        this.showStatistics = showStatistics;
        if (changed) notifyChange();
    }

    /**
     * Is show statistics boolean.
     *
     * @return true if the statistics is need to show.
     */
    @FxThread
    public boolean isShowStatistics() {
        return showStatistics;
    }

    @Override
    public String toString() {
        return "AbstractModelFileEditorState{" +
                ", transformationType=" + transformationType +
                ", enableGrid=" + enableGrid +
                ", enableSelection=" + enableSelection +
                "} " + super.toString();
    }

}
