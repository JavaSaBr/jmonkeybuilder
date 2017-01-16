package com.ss.editor.ui.component.editor.state.impl;

import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;

/**
 * The implementation of a state container for the {@link ModelFileEditor}.
 *
 * @author JavaSaBr
 */
public class ModelFileEditorState extends AbstractModelFileEditorState {

    public static final long serialVersionUID = 3;

    /**
     * The sky type.
     */
    private volatile int skyType;

    /**
     * Is enabled light.
     */
    private volatile boolean enableLight;

    public ModelFileEditorState() {
        this.skyType = 0;
        this.enableLight = EDITOR_CONFIG.isDefaultEditorCameraEnabled();
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

    @Override
    public String toString() {
        return "ModelFileEditorState{" +
                "skyType=" + skyType +
                ", enableLight=" + enableLight +
                "} " + super.toString();
    }
}
