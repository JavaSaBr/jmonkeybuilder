package com.ss.editor.ui.component.editor.state.impl;

import com.ss.editor.ui.component.editor.impl.scene.AbstractSceneFileEditor;

/**
 * The base implementation of a state container for the {@link AbstractSceneFileEditor}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractModelFileEditorState extends AbstractEditorState {

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

    public AbstractModelFileEditorState() {
        this.enableGrid = true;
        this.enableSelection = true;
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

    /**
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
