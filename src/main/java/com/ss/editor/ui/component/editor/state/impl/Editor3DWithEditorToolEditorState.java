package com.ss.editor.ui.component.editor.state.impl;

import static java.lang.Math.abs;
import com.ss.editor.ui.component.editor.state.EditorToolConfig;

/**
 * The base implementation of a state container for an 3D editor with editor tool.
 *
 * @author JavaSaBr
 */
public class Editor3DWithEditorToolEditorState extends Editor3DEditorState implements EditorToolConfig {

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
     * Instantiates a new Abstract editor state.
     */
    public Editor3DWithEditorToolEditorState() {
        this.toolWidth = 250;
        this.toolCollapsed = false;
    }

    @Override
    public int getToolWidth() {
        return toolWidth;
    }

    @Override
    public void setToolWidth(final int toolWidth) {
        final boolean changed = abs(getToolWidth() - toolWidth) > 3;
        this.toolWidth = toolWidth;
        if (changed) notifyChange();
    }

    @Override
    public boolean isToolCollapsed() {
        return toolCollapsed;
    }

    @Override
    public void setToolCollapsed(final boolean toolCollapsed) {
        final boolean changed = isToolCollapsed() != toolCollapsed;
        this.toolCollapsed = toolCollapsed;
        if (changed) notifyChange();
    }

    @Override
    public String toString() {
        return "Editor3DWithEditorToolEditorState{" + "toolWidth=" + toolWidth + ", toolCollapsed=" + toolCollapsed +
                ", cameraLocation=" + cameraLocation + ", cameraVRotation=" + cameraVRotation + ", cameraHRotation=" +
                cameraHRotation + ", cameraSpeed=" + cameraSpeed + ", cameraTDistance=" + cameraTDistance + '}';
    }
}
