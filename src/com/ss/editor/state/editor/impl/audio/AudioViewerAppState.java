package com.ss.editor.state.editor.impl.audio;

import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource.Status;
import com.jme3.scene.Node;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.state.editor.impl.AbstractEditorAppState;
import com.ss.editor.ui.component.editor.impl.AudioViewerEditor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The implementation of an editor app state for the {@link AudioViewerEditor}.
 *
 * @author JavaSaBr
 */
public class AudioViewerAppState extends AbstractEditorAppState<AudioViewerEditor> {

    /**
     * The previous status.
     */
    @Nullable
    private volatile Status prevStatus;

    /**
     * The audio node.
     */
    @Nullable
    private AudioNode audioNode;

    /**
     * The audio data.
     */
    @Nullable
    private AudioData audioData;

    /**
     * The audio key.
     */
    @Nullable
    private AudioKey audioKey;

    /**
     * Create a file editor app state.
     *
     * @param fileEditor the editor.
     */
    public AudioViewerAppState(@NotNull final AudioViewerEditor fileEditor) {
        super(fileEditor);
    }

    /**
     * Load the audio data.
     *
     * @param audioData the audio data.
     * @param audioKey  the audio key.
     */
    @FromAnyThread
    public void load(@NotNull final AudioData audioData, @NotNull final AudioKey audioKey) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> loadImpl(audioData, audioKey));
    }

    /**
     * Load the audio data.
     *
     * @param audioData the audio data.
     * @param audioKey  the audio key.
     */
    private void loadImpl(@NotNull final AudioData audioData, @NotNull final AudioKey audioKey) {
        removeAudioNode();
        setAudioData(audioData);
        setAudioKey(audioKey);

        final Node stateNode = getStateNode();

        final AudioNode audioNode = new AudioNode(audioData, audioKey);
        audioNode.setPositional(false);
        stateNode.attachChild(audioNode);

        setAudioNode(audioNode);
    }

    /**
     * Remove the current audio node.
     */
    private void removeAudioNode() {

        final AudioNode currentAudioNode = getAudioNode();
        if (currentAudioNode == null) return;

        final Node stateNode = getStateNode();
        final Status status = currentAudioNode.getStatus();

        if (status == Status.Playing || status == Status.Paused) {
            currentAudioNode.stop();
        }

        stateNode.detachChild(currentAudioNode);

        setAudioNode(null);
        setPrevStatus(null);
    }

    /**
     * Play the current audio.
     */
    @FromAnyThread
    public void play() {
        EXECUTOR_MANAGER.addEditorThreadTask(this::playImpl);
    }

    /**
     * Play the current audio.
     */
    private void playImpl() {

        final AudioNode currentAudioNode = getAudioNode();

        if (currentAudioNode != null) {

            final Status status = currentAudioNode.getStatus();

            if (status == Status.Paused) {
                currentAudioNode.play();
                return;
            } else if (status == Status.Playing) {
                return;
            }
        }

        loadImpl(getAudioData(), getAudioKey());

        final AudioNode audioNode = getAudioNode();
        audioNode.play();
    }

    @Override
    public void cleanup() {
        stopImpl();
        super.cleanup();
    }

    /**
     * Pause the current audio.
     */
    @FromAnyThread
    public void pause() {
        EXECUTOR_MANAGER.addEditorThreadTask(this::pauseImpl);
    }

    /**
     * Pause the current audio.
     */
    private void pauseImpl() {
        final AudioNode currentAudioNode = getAudioNode();
        if (currentAudioNode == null) return;
        currentAudioNode.pause();
    }

    /**
     * Stop the current audio.
     */
    @FromAnyThread
    public void stop() {
        EXECUTOR_MANAGER.addEditorThreadTask(this::stopImpl);
    }

    /**
     * Stop the current audio.
     */
    private void stopImpl() {
        removeAudioNode();
        EXECUTOR_MANAGER.addFXTask(() -> getFileEditor().notifyChangedStatus(Status.Stopped));
    }

    @Override
    public void update(final float tpf) {
        super.update(tpf);

        final AudioNode audioNode = getAudioNode();
        if (audioNode == null) return;

        final Status status = audioNode.getStatus();
        if (status != getPrevStatus()) {
            EXECUTOR_MANAGER.addFXTask(() -> getFileEditor().notifyChangedStatus(status));
            setPrevStatus(status);
        }
    }

    /**
     * @return the audio node.
     */
    @Nullable
    private AudioNode getAudioNode() {
        return audioNode;
    }

    /**
     * @param audioNode the audio node.
     */
    private void setAudioNode(@Nullable final AudioNode audioNode) {
        this.audioNode = audioNode;
    }

    /**
     * @return the audio data.
     */
    @NotNull
    private AudioData getAudioData() {
        return Objects.requireNonNull(audioData);
    }

    /**
     * @param audioData the audio data.
     */
    private void setAudioData(@NotNull final AudioData audioData) {
        this.audioData = audioData;
    }

    /**
     * @return the audio key.
     */
    @NotNull
    private AudioKey getAudioKey() {
        return Objects.requireNonNull(audioKey);
    }

    /**
     * @param audioKey the audio key.
     */
    private void setAudioKey(@NotNull final AudioKey audioKey) {
        this.audioKey = audioKey;
    }

    /**
     * Gets prev status.
     *
     * @return the previous status.
     */
    @Nullable
    @FromAnyThread
    public Status getPrevStatus() {
        return prevStatus;
    }

    /**
     * @param prevStatus the previous status.
     */
    private void setPrevStatus(@Nullable final Status prevStatus) {
        this.prevStatus = prevStatus;
    }

    @Override
    public String toString() {
        return "AudioViewerAppState{" +
                "prevStatus=" + prevStatus +
                ", audioNode=" + audioNode +
                ", audioData=" + audioData +
                ", audioKey=" + audioKey +
                "} " + super.toString();
    }
}
