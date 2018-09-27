package com.ss.builder.editor.impl.audio;

import static com.jme3.audio.AudioSource.Status.Playing;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioSource;
import com.ss.builder.FileExtensions;
import com.ss.builder.JmeApplication;
import com.ss.builder.Messages;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.editor.EditorDescriptor;
import com.ss.builder.editor.impl.AbstractFileEditor;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.editor.layout.impl.VBoxEditorLayout;
import com.ss.builder.fx.editor.part.ui.AudioViewerUiPart;
import com.ss.builder.fx.util.DynamicIconSupport;
import com.ss.builder.jme.editor.part3d.impl.audio.AudioViewer3dPart;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The implementation of the {@link JmeApplication} to view audio files.
 *
 * @author JavaSaBr
 */
public class AudioViewerEditor extends AbstractFileEditor<VBoxEditorLayout> {

    public static final EditorDescriptor DESCRIPTOR = new EditorDescriptor(
            AudioViewerEditor::new,
            Messages.AUDIO_VIEWER_EDITOR_NAME,
            AudioViewerEditor.class.getSimpleName(),
            FileExtensions.AUDIO_EXTENSIONS
    );

    /**
     * The editor's 3d part.
     */
    @NotNull
    private final AudioViewer3dPart editor3dPart;

    private AudioViewerEditor() {
        this.editor3dPart = new AudioViewer3dPart(this);
        addEditor3dPart(editor3dPart);
        addEditorUiPart(new AudioViewerUiPart(this));
    }

    @Override
    @BackgroundThread
    protected @NotNull VBoxEditorLayout createLayout() {
        return new VBoxEditorLayout();
    }

    /**
     * Stop of plying.
     */
    @FxThread
    private void processStop() {
        getEditor3dPart().stop();
    }

    /**
     * Play the audio.
     */
    @FxThread
    private void processPlay() {

        var editor3dPart = getEditor3dPart();

        if (editor3dPart.getPrevStatus() == Playing) {
            editor3dPart.pause();
        } else {
            editor3dPart.play();
        }
    }

    @Override
    @BackgroundThread
    public void openFile(@NotNull Path file) {
        super.openFile(file);

        var assetFile = notNull(EditorUtils.getAssetFile(file));
        var assetPath = EditorUtils.toAssetPath(assetFile);

        var audioKey = new AudioKey(assetPath);
        var audioData = EditorUtils.getAssetManager()
                .loadAudio(audioKey);

        getEditor3dPart()
                .load(audioData, audioKey);

        var executorManager = ExecutorManager.getInstance();
        executorManager.addFxTask(() -> {

            var duration = audioData.getDuration();
            var bitsPerSample = audioData.getBitsPerSample();
            var channels = audioData.getChannels();
            var dataType = audioData.getDataType();
            var sampleRate = audioData.getSampleRate();

            channelsField.setText(String.valueOf(channels));
            durationField.setText(String.valueOf(duration));
            dataTypeField.setText(String.valueOf(dataType));
            sampleRateField.setText(String.valueOf(sampleRate));
            bitsPerSampleField.setText(String.valueOf(bitsPerSample));
        });
    }

    @Override
    @FromAnyThread
    public @NotNull EditorDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    /**
     * Get the editor's 3d part.
     *
     * @return the editor's 3d part.
     */
    @FromAnyThread
    private @NotNull AudioViewer3dPart getEditor3dPart() {
        return editor3dPart;
    }

    /**
     * Notify about changing a status of playing audio.
     *
     * @param status the new status.
     */
    @FxThread
    public void notifyChangedStatus(@NotNull AudioSource.Status status) {

        switch (status) {
            case Playing: {
                var graphic = (ImageView) playButton.getGraphic();
                graphic.setImage(Icons.PAUSE_128);
                stopButton.setDisable(false);
                break;
            }
            case Paused: {
                var graphic = (ImageView) playButton.getGraphic();
                graphic.setImage(Icons.PLAY_128);
                stopButton.setDisable(false);
                break;
            }
            case Stopped: {
                var graphic = (ImageView) playButton.getGraphic();
                graphic.setImage(Icons.PLAY_128);
                stopButton.setDisable(true);
            }
        }
    }

    @Override
    public String toString() {
        return "AudioViewerEditor{" +
                "editor3dPart=" + editor3dPart +
                "} " + super.toString();
    }
}
