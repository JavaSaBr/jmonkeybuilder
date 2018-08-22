package com.ss.builder.editor.impl;

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
import com.ss.builder.jme.editor.part3d.impl.audio.AudioViewer3dPart;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.util.DynamicIconSupport;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.editor.EditorDescriptor;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The implementation of the {@link JmeApplication} to view audio files.
 *
 * @author JavaSaBr
 */
public class AudioViewerEditor extends AbstractFileEditor<VBox> {

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

    /**
     * The play button.
     */
    @NotNull
    private final Button playButton;

    /**
     * The stop button.
     */
    @NotNull
    private final Button stopButton;

    /**
     * The duration field.
     */
    @NotNull
    private final TextField durationField;

    /**
     * The bits per sample field.
     */
    @NotNull
    private final TextField bitsPerSampleField;

    /**
     * The channels field.
     */
    @NotNull
    private final TextField channelsField;

    /**
     * The data type field.
     */
    @NotNull
    private final TextField dataTypeField;

    /**
     * The sample rate field.
     */
    @NotNull
    private final TextField sampleRateField;

    private AudioViewerEditor() {
        this.editor3dPart = new AudioViewer3dPart(this);
        this.bitsPerSampleField = new TextField();
        this.channelsField = new TextField();
        this.durationField = new TextField();
        this.dataTypeField = new TextField();
        this.sampleRateField = new TextField();
        this.playButton = new Button();
        this.stopButton = new Button();
        addEditor3dPart(editor3dPart);
    }

    @Override
    @FxThread
    protected @NotNull VBox createRoot() {
        return new VBox();
    }

    @Override
    @FxThread
    protected void createContent(@NotNull VBox root) {

        var durationLabel = new Label(Messages.AUDIO_VIEWER_EDITOR_DURATION_LABEL + ":");
        var bitsPerSampleLabel = new Label(Messages.AUDIO_VIEWER_EDITOR_BITS_PER_SAMPLE_LABEL + ":");
        var channelsLabel = new Label(Messages.AUDIO_VIEWER_EDITOR_CHANNELS_LABEL + ":");
        var dataTypeLabel = new Label(Messages.AUDIO_VIEWER_EDITOR_DATA_TYPE_LABEL + ":");
        var sampleRateLabel = new Label(Messages.AUDIO_VIEWER_EDITOR_SAMPLE_RATE_LABEL + ":");

        durationField.setEditable(false);
        bitsPerSampleField.setEditable(false);
        channelsField.setEditable(false);
        dataTypeField.setEditable(false);
        sampleRateField.setEditable(false);

        var gridPane = new GridPane();
        gridPane.add(durationLabel, 0, 0);
        gridPane.add(bitsPerSampleLabel, 0, 1);
        gridPane.add(channelsLabel, 0, 2);
        gridPane.add(dataTypeLabel, 0, 3);
        gridPane.add(sampleRateLabel, 0, 4);
        gridPane.add(durationField, 1, 0);
        gridPane.add(bitsPerSampleField, 1, 1);
        gridPane.add(channelsField, 1, 2);
        gridPane.add(dataTypeField, 1, 3);
        gridPane.add(sampleRateField, 1, 4);

        playButton.setGraphic(new ImageView(Icons.PLAY_128));
        playButton.setOnAction(event -> processPlay());

        stopButton.setGraphic(new ImageView(Icons.STOP_128));
        stopButton.setOnAction(event -> processStop());
        stopButton.setDisable(true);

        var container = new HBox();

        FxUtils.addClass(playButton, CssClasses.BUTTON_WITHOUT_RIGHT_BORDER)
                .addClass(stopButton, CssClasses.BUTTON_WITHOUT_LEFT_BORDER)
                .addClass(container, CssClasses.DEF_HBOX)
                .addClass(gridPane, CssClasses.DEF_GRID_PANE)
                .addClass(root, CssClasses.DEF_VBOX, CssClasses.AUDIO_VIEW_EDITOR_CONTAINER);

        FxUtils.addChild(container, gridPane, playButton, stopButton)
                .addChild(root, container);

        DynamicIconSupport.addSupport(playButton, stopButton);
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
