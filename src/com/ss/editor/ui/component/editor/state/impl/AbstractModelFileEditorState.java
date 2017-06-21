package com.ss.editor.ui.component.editor.state.impl;

import com.ss.editor.ui.component.editor.impl.scene.AbstractSceneFileEditor;

/**
 * The base implementation of a state container for the {@link AbstractSceneFileEditor}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractModelFileEditorState extends AbstractEditorState {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 2;

    /**
     * The transformation type.
     */
    private volatile int transformationType;

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

    /**
     * Instantiates a new Abstract model file editor state.
     */
    public AbstractModelFileEditorState() {
        this.enableGrid = true;
        this.enableSelection = true;
    }

    /**
     * Sets enable grid.
     *
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
     * Is enable grid boolean.
     *
     * @return true is the grid is enabled.
     */
    public boolean isEnableGrid() {
        return enableGrid;
    }

    /**
     * Sets enable selection.
     *
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
     * Is enable selection boolean.
     *
     * @return true if the selection is enabled.
     */
    public boolean isEnableSelection() {
        return enableSelection;
    }

    /**
     * Gets transformation type.
     *
     * @return the transformation type.
     */
    public int getTransformationType() {
        return transformationType;
    }

    /**
     * Sets transformation type.
     *
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

    /**
     * Sets show statistics.
     *
     * @param showStatistics true if the statistics is need to show.
     */
    public void setShowStatistics(final boolean showStatistics) {
        final boolean changed = isShowStatistics() != showStatistics;
        this.showStatistics = showStatistics;
        final Runnable changeHandler = getChangeHandler();
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * Is show statistics boolean.
     *
     * @return true if the statistics is need to show.
     */
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
