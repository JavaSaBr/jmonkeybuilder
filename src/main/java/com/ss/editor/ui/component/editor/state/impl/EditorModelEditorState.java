package com.ss.editor.ui.component.editor.state.impl;

import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;

/**
 * The implementation of a state container for the {@link ModelFileEditor}.
 *
 * @author JavaSaBr
 */
public class EditorModelEditorState extends BaseEditorSceneEditorState {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 4;

    /**
     * The sky type.
     */
    private volatile int skyType;

    /**
     * Is enabled light.
     */
    private volatile boolean enableLight;

    /**
     * Instantiates a new Model file editor state.
     */
    public EditorModelEditorState() {
        this.skyType = 0;
        this.enableLight = EDITOR_CONFIG.isDefaultEditorCameraEnabled();
    }

    /**
     * Gets sky type.
     *
     * @return the sky type.
     */
    public int getSkyType() {
        return skyType;
    }

    /**
     * Sets sky type.
     *
     * @param skyType the sky type.
     */
    public void setSkyType(final int skyType) {
        final boolean changed = getSkyType() != skyType;
        this.skyType = skyType;
        if (changed) notifyChange();
    }

    /**
     * Sets enable light.
     *
     * @param enableLight true if the light is enabled.
     */
    public void setEnableLight(final boolean enableLight) {
        final boolean changed = isEnableLight() != enableLight;
        this.enableLight = enableLight;
        if (changed) notifyChange();
    }

    /**
     * Is enable light boolean.
     *
     * @return true if the light is enabled.
     */
    public boolean isEnableLight() {
        return enableLight;
    }

    @Override
    public String toString() {
        return "EditorModelEditorState{" +
                "skyType=" + skyType +
                ", enableLight=" + enableLight +
                "} " + super.toString();
    }
}
