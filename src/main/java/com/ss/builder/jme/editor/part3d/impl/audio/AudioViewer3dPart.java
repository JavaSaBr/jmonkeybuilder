package com.ss.builder.jme.editor.part3d.impl.audio;

import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource.Status;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.editor.impl.AudioViewerEditor;
import com.ss.builder.jme.editor.part3d.impl.AbstractExtendableEditor3dPart;
import com.ss.builder.manager.ExecutorManager;
import com.ss.rlib.common.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The implementation of an editor app state for the {@link AudioViewerEditor}.
 *
 * @author JavaSaBr
 */
public class AudioViewer3dPart extends AbstractExtendableEditor3dPart<AudioViewerEditor> {

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

    @JmeThread
    private @NotNull Optional<AudioNode> getAudioNodeOpt() {
        return Optional.ofNullable(audioNode);
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
                .addJmeTask(() -> loadInJme(audioData, audioKey));
    }

    /**
     * Load the audio data in jME thread.
     *
     * @param audioData the audio data.
     * @param audioKey  the audio key.
     */
    @JmeThread
    private @NotNull AudioNode loadInJme(@NotNull AudioData audioData, @NotNull AudioKey audioKey) {
        removeAudioNode();

        this.audioData = audioData;
        this.audioKey = audioKey;

        audioNode = new AudioNode(audioData, audioKey);
        audioNode.setPositional(false);

        stateNode.attachChild(audioNode);

        return audioNode;
    }

    /**
     * Remove the current audio node.
     */
    @JmeThread
    private void removeAudioNode() {

        if (audioNode == null) {
            return;
        }

        var status = audioNode.getStatus();

        if (status == Status.Playing || status == Status.Paused) {
            audioNode.stop();
        }

        stateNode.detachChild(audioNode);

        this.audioNode = null;
        this.prevStatus = null;
    }

    /**
     * Play the current audio.
     */
    @FromAnyThread
    public void play() {
        ExecutorManager.getInstance()
                .addJmeTask(this::playInJme);
    }

    /**
     * Play the current audio.
     */
    @JmeThread
    private void playInJme() {

        if (audioNode != null) {

            var status = audioNode.getStatus();

            if (status == Status.Paused) {
                audioNode.play();
                return;
            } else if (status == Status.Playing) {
                return;
            }
        }

        var audioData = ObjectUtils.notNull(this.audioData, "audio data can't be null.");
        var audioKey = ObjectUtils.notNull(this.audioKey, "audio key can't be null.");

        var audioNode = loadInJme(audioData, audioKey);
        audioNode.play();
    }

    @Override
    @JmeThread
    public void cleanup() {
        stopInJme();
        super.cleanup();
    }

    /**
     * Pause the current audio.
     */
    @FromAnyThread
    public void pause() {
        ExecutorManager.getInstance()
                .addJmeTask(this::pauseInJme);
    }

    /**
     * Pause the current audio in jME.
     */
    @JmeThread
    private void pauseInJme() {
        getAudioNodeOpt().ifPresent(AudioNode::pause);
    }

    /**
     * Stop the current audio.
     */
    @FromAnyThread
    public void stop() {
        ExecutorManager.getInstance()
                .addJmeTask(this::stopInJme);
    }

    /**
     * Stop the current audio in jME thread.
     */
    @JmeThread
    private void stopInJme() {
        removeAudioNode();
        notifyChangeStatus(Status.Stopped);
    }

    @Override
    @JmeThread
    public void update(float tpf) {
        super.update(tpf);

        var status = getAudioNodeOpt()
                .map(AudioNode::getStatus)
                .orElse(null);

        if (status != null && status != prevStatus) {
            notifyChangeStatus(status);
            prevStatus = status;
        }
    }

    /**
     * Get a previous status of playing audio.
     *
     * @return the previous status of playing audio.
     */
    public @Nullable Status getPrevStatus() {
        return prevStatus;
    }

    @FromAnyThread
    private void notifyChangeStatus(@NotNull Status status) {
        ExecutorManager.getInstance()
                .addFxTask(() -> fileEditor.notifyChangedStatus(status));
    }

    @Override
    public String toString() {
        return "AudioViewer3dPart{" +
                "prevStatus=" + prevStatus +
                ", audioNode=" + audioNode +
                ", audioData=" + audioData +
                ", audioKey=" + audioKey +
                "} " + super.toString();
    }
}
