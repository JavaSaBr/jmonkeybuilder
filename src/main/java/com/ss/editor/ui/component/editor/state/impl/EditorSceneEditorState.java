package com.ss.editor.ui.component.editor.state.impl;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.component.editor.impl.scene.SceneFileEditor;

/**
 * The implementation of a state container for the {@link SceneFileEditor}.
 *
 * @author JavaSaBr
 */
public class EditorSceneEditorState extends BaseEditorSceneEditorState {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 3;

    /**
     * Is showed light.
     */
    private volatile boolean showedLight;

    /**
     * Is showed audio.
     */
    private volatile boolean showedAudio;

    public EditorSceneEditorState() {
        this.showedAudio = true;
        this.showedLight = true;
    }

    /**
     * Sets showed light.
     *
     * @param showedLight true if the light is showed.
     */
    @FXThread
    public void setShowedLight(final boolean showedLight) {
        final boolean changed = isShowedLight() != showedLight;
        this.showedLight = showedLight;
        if (changed) notifyChange();
    }

    /**
     * Is showed light boolean.
     *
     * @return true if the light is showed.
     */
    @FXThread
    public boolean isShowedLight() {
        return showedLight;
    }

    /**
     * Sets showed audio.
     *
     * @param showedAudio true if the audio is showed.
     */
    @FXThread
    public void setShowedAudio(final boolean showedAudio) {
        final boolean changed = isShowedAudio() != showedAudio;
        this.showedAudio = showedAudio;
        if (changed) notifyChange();
    }

    /**
     * Is showed audio boolean.
     *
     * @return true if the audio is showed.
     */
    @FXThread
    public boolean isShowedAudio() {
        return showedAudio;
    }

    @Override
    public String toString() {
        return "EditorSceneEditorState{" + "showedLight=" + showedLight + ", showedAudio=" + showedAudio + '}';
    }
}
