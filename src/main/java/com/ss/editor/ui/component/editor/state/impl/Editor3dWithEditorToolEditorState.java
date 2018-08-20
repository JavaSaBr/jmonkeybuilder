package com.ss.editor.ui.component.editor.state.impl;

import static java.lang.Math.abs;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.editor.state.EditorToolConfig;

/**
 * The base implementation of a state container for an 3D editor with editor tool.
 *
 * @author JavaSaBr
 */
public class Editor3dWithEditorToolEditorState extends Editor3dEditorState implements EditorToolConfig {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 1;

    /**
     * The width of tool split panel.
     */
    protected volatile int toolWidth;

    /**
     * The flag of collapsing split panel.
     */
    protected volatile boolean toolCollapsed;

    /**
     * Opened editor tool.
     */
    private volatile int openedTool;

    /**
     * Instantiates a new Abstract editor state.
     */
    public Editor3dWithEditorToolEditorState() {
        this.toolWidth = 250;
        this.toolCollapsed = false;
        openedTool = 0;
    }

    @Override
    @FxThread
    public int getToolWidth() {
        return toolWidth;
    }

    @Override
    @FxThread
    public void setToolWidth(final int toolWidth) {
        final boolean changed = abs(getToolWidth() - toolWidth) > 3;
        this.toolWidth = toolWidth;
        if (changed) notifyChange();
    }

    @Override
    @FxThread
    public boolean isToolCollapsed() {
        return toolCollapsed;
    }

    @Override
    @FxThread
    public void setToolCollapsed(final boolean toolCollapsed) {
        final boolean changed = isToolCollapsed() != toolCollapsed;
        this.toolCollapsed = toolCollapsed;
        if (changed) notifyChange();
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

    @Override
    public String toString() {
        return "Editor3DWithEditorToolEditorState{" + "toolWidth=" + toolWidth + ", toolCollapsed=" + toolCollapsed +
                ", cameraLocation=" + cameraLocation + ", cameraVRotation=" + cameraVRotation + ", cameraHRotation=" +
                cameraHRotation + ", cameraFlySpeed=" + cameraFlySpeed + ", cameraTargetDistance=" + cameraTargetDistance + '}';
    }
}
