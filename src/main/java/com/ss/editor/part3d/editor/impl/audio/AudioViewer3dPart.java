package com.ss.editor.part3d.editor.impl.audio;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource.Status;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.part3d.editor.impl.AbstractEditor3dPart;
import com.ss.editor.ui.component.editor.impl.AudioViewerEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of an editor app state for the {@link AudioViewerEditor}.
 *
 * @author JavaSaBr
 */
public class AudioViewer3dPart extends AbstractEditor3dPart<AudioViewerEditor> {

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

    public AudioViewer3dPart(@NotNull AudioViewerEditor fileEditor) {
        super(fileEditor);
    }

    /**
     * Load the audio data.
     *
     * @param audioData the audio data.
     * @param audioKey  the audio key.
     */
    @FromAnyThread
    public void load(@NotNull AudioData audioData, @NotNull AudioKey audioKey) {

        ExecutorManager.getInstance()
                .addJmeTask(() -> loadImpl(audioData, audioKey));
    }

    /**
     * Load the audio data.
     *
     * @param audioData the audio data.
     * @param audioKey  the audio key.
     */
    @JmeThread
    private void loadImpl(@NotNull AudioData audioData, @NotNull AudioKey audioKey) {

        removeAudioNode();

        setAudioData(audioData);
        setAudioKey(audioKey);

        var stateNode = getStateNode();
        var audioNode = new AudioNode(audioData, audioKey);
        audioNode.setPositional(false);
        stateNode.attachChild(audioNode);

        setAudioNode(audioNode);
    }

    /**
     * Remove the current audio node.
     */
    @JmeThread
    private void removeAudioNode() {

        var currentAudioNode = getAudioNode();
        if (currentAudioNode == null) {
            return;
        }

        var stateNode = getStateNode();
        var status = currentAudioNode.getStatus();

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
        ExecutorManager.getInstance()
                .addJmeTask(this::playImpl);
    }

    /**
     * Play the current audio.
     */
    @JmeThread
    private void playImpl() {

        var currentAudioNode = getAudioNode();

        if (currentAudioNode != null) {

            var status = currentAudioNode.getStatus();

            if (status == Status.Paused) {
                currentAudioNode.play();
                return;
            } else if (status == Status.Playing) {
                return;
            }
        }

        loadImpl(getAudioData(), getAudioKey());
        getAudioNode().play();
    }

    @Override
    @JmeThread
    public void cleanup() {
        stopImpl();
        super.cleanup();
    }

    /**
     * Pause the current audio.
     */
    @FromAnyThread
    public void pause() {
        ExecutorManager.getInstance()
                .addJmeTask(this::pauseImpl);
    }

    /**
     * Pause the current audio.
     */
    @JmeThread
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
        ExecutorManager.getInstance()
                .addJmeTask(this::stopImpl);
    }

    /**
     * Stop the current audio.
     */
    @JmeThread
    private void stopImpl() {

        removeAudioNode();

        ExecutorManager.getInstance()
                .addFxTask(() -> getFileEditor().notifyChangedStatus(Status.Stopped));
    }

    @Override
    @JmeThread
    public void update(float tpf) {
        super.update(tpf);

        var audioNode = getAudioNode();

        if (audioNode == null) {
            return;
        }

        var status = audioNode.getStatus();

        if (status != getPrevStatus()) {

            ExecutorManager.getInstance()
                    .addFxTask(() -> getFileEditor().notifyChangedStatus(status));

            setPrevStatus(status);
        }
    }

    /**
     * Get the audio node.
     *
     * @return the audio node.
     */
    @JmeThread
    private @Nullable AudioNode getAudioNode() {
        return audioNode;
    }

    /**
     * Set the audio node.
     *
     * @param audioNode the audio node.
     */
    @JmeThread
    private void setAudioNode(@Nullable AudioNode audioNode) {
        this.audioNode = audioNode;
    }

    /**
     * Get the audio data.
     *
     * @return the audio data.
     */
    @JmeThread
    private @NotNull AudioData getAudioData() {
        return notNull(audioData);
    }

    /**
     * Set the audio data.
     *
     * @param audioData the audio data.
     */
    @JmeThread
    private void setAudioData(@NotNull AudioData audioData) {
        this.audioData = audioData;
    }

    /**
     * Get the audio key.
     *
     * @return the audio key.
     */
    @JmeThread
    private @NotNull AudioKey getAudioKey() {
        return notNull(audioKey);
    }

    /**
     * Set the audio key.
     *
     * @param audioKey the audio key.
     */
    @JmeThread
    private void setAudioKey(@NotNull AudioKey audioKey) {
        this.audioKey = audioKey;
    }

    /**
     * Get previous prev status.
     *
     * @return the previous status.
     */
    @FromAnyThread
    public @Nullable Status getPrevStatus() {
        return prevStatus;
    }

    /**
     * Set previous prev status.
     *
     * @param prevStatus the previous status.
     */
    @JmeThread
    private void setPrevStatus(@Nullable Status prevStatus) {
        this.prevStatus = prevStatus;
    }

    @Override
    public String toString() {
        return "AudioViewer3DState{" +
                "prevStatus=" + prevStatus +
                ", audioNode=" + audioNode +
                ", audioData=" + audioData +
                ", audioKey=" + audioKey +
                "} " + super.toString();
    }
}
