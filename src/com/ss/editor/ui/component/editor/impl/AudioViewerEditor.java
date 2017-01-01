package com.ss.editor.ui.component.editor.impl;

import static com.jme3.audio.AudioSource.Status.Playing;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioSource;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.state.editor.impl.audio.AudioViewerAppState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link Editor} for viewing an audio.
 *
 * @author JavaSaBr
 */
public class AudioViewerEditor extends AbstractFileEditor<VBox> {

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(AudioViewerEditor::new);
        DESCRIPTION.setEditorName("Audio View");
        DESCRIPTION.setEditorId(AudioViewerEditor.class.getSimpleName());
        DESCRIPTION.addExtension(FileExtensions.AUDIO_MP3);
        DESCRIPTION.addExtension(FileExtensions.AUDIO_OGG);
        DESCRIPTION.addExtension(FileExtensions.AUDIO_WAV);
    }

    /**
     * The editor app state.
     */
    @NotNull
    private final AudioViewerAppState editorAppState;

    /**
     * The play button.
     */
    private Button playButton;

    /**
     * The stop button.
     */
    private Button stopButton;

    /**
     * The duration field.
     */
    private TextField durationField;

    /**
     * The bits per sample field.
     */
    private TextField bitsPerSampleField;

    /**
     * The channels field.
     */
    private TextField channelsField;

    /**
     * The data type field.
     */
    private TextField dataTypeField;

    /**
     * The sample rate field.
     */
    private TextField sampleRateField;

    public AudioViewerEditor() {
        this.editorAppState = new AudioViewerAppState(this);
        addEditorState(editorAppState);
    }

    @NotNull
    @Override
    protected VBox createRoot() {
        return new VBox();
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        root.setAlignment(Pos.CENTER);

        final Label durationLabel = new Label("Duration" + ":");
        durationLabel.setId(CSSIds.AUDIO_VIEWER_EDITOR_PARAM_LABEL);

        final Label bitsPerSampleLabel = new Label("Bits per sample" + ":");
        bitsPerSampleLabel.setId(CSSIds.AUDIO_VIEWER_EDITOR_PARAM_LABEL);

        final Label channelsLabel = new Label("Channels" + ":");
        channelsLabel.setId(CSSIds.AUDIO_VIEWER_EDITOR_PARAM_LABEL);

        final Label dataTypeLabel = new Label("Data type" + ":");
        dataTypeLabel.setId(CSSIds.AUDIO_VIEWER_EDITOR_PARAM_LABEL);

        final Label sampleRateLabel = new Label("Sample rate" + ":");
        sampleRateLabel.setId(CSSIds.AUDIO_VIEWER_EDITOR_PARAM_LABEL);

        durationField = new TextField();
        durationField.setId(CSSIds.AUDIO_VIEWER_EDITOR_PARAM_VALUE);
        durationField.setEditable(false);

        bitsPerSampleField = new TextField();
        bitsPerSampleField.setId(CSSIds.AUDIO_VIEWER_EDITOR_PARAM_VALUE);
        bitsPerSampleField.setEditable(false);

        channelsField = new TextField();
        channelsField.setId(CSSIds.AUDIO_VIEWER_EDITOR_PARAM_VALUE);
        channelsField.setEditable(false);

        dataTypeField = new TextField();
        dataTypeField.setId(CSSIds.AUDIO_VIEWER_EDITOR_PARAM_VALUE);
        dataTypeField.setEditable(false);

        sampleRateField = new TextField();
        sampleRateField.setId(CSSIds.AUDIO_VIEWER_EDITOR_PARAM_VALUE);
        sampleRateField.setEditable(false);

        final GridPane gridPane = new GridPane();
        gridPane.setId(CSSIds.AUDIO_VIEWER_EDITOR_PARAM_CONTAINER);
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

        final HBox container = new HBox();
        container.setId(CSSIds.AUDIO_VIEWER_EDITOR_BUTTON_CONTAINER);

        playButton = new Button();
        playButton.setId(CSSIds.AUDIO_VIEWER_EDITOR_BUTTON);
        playButton.setGraphic(new ImageView(Icons.PLAY_128));
        playButton.setOnAction(event -> processPlay());

        stopButton = new Button();
        stopButton.setId(CSSIds.AUDIO_VIEWER_EDITOR_BUTTON);
        stopButton.setGraphic(new ImageView(Icons.STOP_128));
        stopButton.setOnAction(event -> processStop());
        stopButton.setDisable(true);

        FXUtils.addToPane(gridPane, container);
        FXUtils.addToPane(playButton, container);
        FXUtils.addToPane(stopButton, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(durationLabel, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(durationField, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(bitsPerSampleLabel, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(bitsPerSampleField, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(channelsLabel, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(channelsField, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(dataTypeLabel, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(dataTypeField, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(sampleRateLabel, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(sampleRateField, CSSClasses.SPECIAL_FONT_16);
    }

    /**
     * Stop of plying.
     */
    private void processStop() {
        getEditorAppState().stop();
    }

    /**
     * Play the audio.
     */
    private void processPlay() {
        final AudioViewerAppState appState = getEditorAppState();
        if (appState.getPrevStatus() == Playing) {
            appState.pause();
        } else {
            appState.play();
        }
    }

    @Override
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        final Path assetFile = Objects.requireNonNull(EditorUtil.getAssetFile(file));
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final AudioKey audioKey = new AudioKey(assetPath);
        final AssetManager assetManager = EDITOR.getAssetManager();
        final AudioData audioData = assetManager.loadAudio(audioKey);

        final float duration = audioData.getDuration();
        final int bitsPerSample = audioData.getBitsPerSample();
        final int channels = audioData.getChannels();
        final AudioData.DataType dataType = audioData.getDataType();
        final int sampleRate = audioData.getSampleRate();

        final AudioViewerAppState editorAppState = getEditorAppState();
        editorAppState.load(audioData, audioKey);

        getChannelsField().setText(String.valueOf(channels));
        getDurationField().setText(String.valueOf(duration));
        getDataTypeField().setText(String.valueOf(dataType));
        getSampleRateField().setText(String.valueOf(sampleRate));
        getBitsPerSampleField().setText(String.valueOf(bitsPerSample));
    }

    @NotNull
    @Override
    public EditorDescription getDescription() {
        return DESCRIPTION;
    }

    /**
     * @return the editor app state.
     */
    @NotNull
    @FromAnyThread
    public AudioViewerAppState getEditorAppState() {
        return editorAppState;
    }

    /**
     * Notify about changing a status of playing audio.
     *
     * @param status the new status.
     */
    @FXThread
    public void notifyChangedStatus(final AudioSource.Status status) {

        final Button playButton = getPlayButton();
        final Button stopButton = getStopButton();

        switch (status) {
            case Playing: {
                final ImageView graphic = (ImageView) playButton.getGraphic();
                graphic.setImage(Icons.PAUSE_128);
                stopButton.setDisable(false);
                break;
            }
            case Paused: {
                final ImageView graphic = (ImageView) playButton.getGraphic();
                graphic.setImage(Icons.PLAY_128);
                stopButton.setDisable(false);
                break;
            }
            case Stopped: {
                final ImageView graphic = (ImageView) playButton.getGraphic();
                graphic.setImage(Icons.PLAY_128);
                stopButton.setDisable(true);
            }
        }
    }

    /**
     * @return the play button.
     */
    @NotNull
    private Button getPlayButton() {
        return Objects.requireNonNull(playButton);
    }

    /**
     * @return the stop button.
     */
    @NotNull
    private Button getStopButton() {
        return Objects.requireNonNull(stopButton);
    }

    /**
     * @return the channels field.
     */
    @NotNull
    private TextField getChannelsField() {
        return Objects.requireNonNull(channelsField);
    }

    /**
     * @return the duration field.
     */
    @NotNull
    private TextField getDurationField() {
        return Objects.requireNonNull(durationField);
    }

    /**
     * @return the data type field.
     */
    @NotNull
    private TextField getDataTypeField() {
        return Objects.requireNonNull(dataTypeField);
    }

    /**
     * @return the sample rate field.
     */
    @NotNull
    private TextField getSampleRateField() {
        return Objects.requireNonNull(sampleRateField);
    }

    /**
     * @return the bits per sample field.
     */
    @NotNull
    private TextField getBitsPerSampleField() {
        return Objects.requireNonNull(bitsPerSampleField);
    }

    @Override
    public String toString() {
        return "AudioViewerEditor{" +
                "editorAppState=" + editorAppState +
                "} " + super.toString();
    }
}
