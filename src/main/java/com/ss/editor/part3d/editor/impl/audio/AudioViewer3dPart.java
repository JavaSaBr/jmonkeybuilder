package com.ss.editor.part3d.editor.impl.audio;

import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource.Status;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.part3d.editor.impl.AbstractExtendableEditor3dPart;
import com.ss.editor.ui.component.editor.impl.AudioViewerEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private void loadInJme(@NotNull AudioData audioData, @NotNull AudioKey audioKey) {
        removeAudioNode();

        this.audioData = audioData;
        this.audioKey = audioKey;

        audioNode = new AudioNode(audioData, audioKey);
        audioNode.setPositional(false);

        stateNode.attachChild(audioNode);
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

        loadInJme(getAudioData(), getAudioKey());

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
        if (audioNode != null) {
            audioNode.pause();
        }
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

        ExecutorManager.getInstance()
                .addFxTask(() -> fileEditor.notifyChangedStatus(Status.Stopped));
    }

    @Override
    @JmeThread
    public void update(float tpf) {
        super.update(tpf);

        if (audioNode == null) {
            return;
        }

        var status = audioNode.getStatus();

        if (status != getPrevStatus()) {

            ExecutorManager.getInstance()
                    .addFxTask(() -> fileEditor.notifyChangedStatus(status));

            setPrevStatus(status);
        }
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
