package com.ss.editor.ui.component.editor.impl;

import static com.jme3.audio.AudioSource.Status.Playing;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioSource;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.state.editor.impl.audio.AudioViewerAppState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The implementation of the {@link Editor} to view audio files.
 *
 * @author JavaSaBr
 */
public class AudioViewerEditor extends AbstractFileEditor<VBox> {

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(AudioViewerEditor::new);
        DESCRIPTION.setEditorName(Messages.AUDIO_VIEWER_EDITOR_NAME);
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
    @Nullable
    private Button playButton;

    /**
     * The stop button.
     */
    @Nullable
    private Button stopButton;

    /**
     * The duration field.
     */
    @Nullable
    private TextField durationField;

    /**
     * The bits per sample field.
     */
    @Nullable
    private TextField bitsPerSampleField;

    /**
     * The channels field.
     */
    @Nullable
    private TextField channelsField;

    /**
     * The data type field.
     */
    @Nullable
    private TextField dataTypeField;

    /**
     * The sample rate field.
     */
    @Nullable
    private TextField sampleRateField;

    private AudioViewerEditor() {
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

        final Label durationLabel = new Label(Messages.AUDIO_VIEWER_EDITOR_DURATION_LABEL + ":");
        final Label bitsPerSampleLabel = new Label(Messages.AUDIO_VIEWER_EDITOR_BITS_PER_SAMPLE_LABEL + ":");
        final Label channelsLabel = new Label(Messages.AUDIO_VIEWER_EDITOR_CHANNELS_LABEL + ":");
        final Label dataTypeLabel = new Label(Messages.AUDIO_VIEWER_EDITOR_DATA_TYPE_LABEL + ":");
        final Label sampleRateLabel = new Label(Messages.AUDIO_VIEWER_EDITOR_SAMPLE_RATE_LABEL + ":");

        durationField = new TextField();
        durationField.setEditable(false);

        bitsPerSampleField = new TextField();
        bitsPerSampleField.setEditable(false);

        channelsField = new TextField();
        channelsField.setEditable(false);

        dataTypeField = new TextField();
        dataTypeField.setEditable(false);

        sampleRateField = new TextField();
        sampleRateField.setEditable(false);

        final GridPane gridPane = new GridPane();
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

        playButton = new Button();
        playButton.setGraphic(new ImageView(Icons.PLAY_128));
        playButton.setOnAction(event -> processPlay());

        stopButton = new Button();
        stopButton.setGraphic(new ImageView(Icons.STOP_128));
        stopButton.setOnAction(event -> processStop());
        stopButton.setDisable(true);

        final HBox container = new HBox();

        FXUtils.addToPane(gridPane, container);
        FXUtils.addToPane(playButton, container);
        FXUtils.addToPane(stopButton, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(playButton, CSSClasses.BUTTON_WITHOUT_RIGHT_BORDER);
        FXUtils.addClassTo(stopButton, CSSClasses.BUTTON_WITHOUT_LEFT_BORDER);
        FXUtils.addClassTo(container, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(gridPane, CSSClasses.DEF_GRID_PANE);
        FXUtils.addClassesTo(root, CSSClasses.DEF_VBOX, CSSClasses.AUDIO_VIEW_EDITOR_CONTAINER);

        DynamicIconSupport.addSupport(playButton, stopButton);
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

    @FXThread
    @Override
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        final Path assetFile = notNull(EditorUtil.getAssetFile(file));
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
    private AudioViewerAppState getEditorAppState() {
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
        return notNull(playButton);
    }

    /**
     * @return the stop button.
     */
    @NotNull
    private Button getStopButton() {
        return notNull(stopButton);
    }

    /**
     * @return the channels field.
     */
    @NotNull
    private TextField getChannelsField() {
        return notNull(channelsField);
    }

    /**
     * @return the duration field.
     */
    @NotNull
    private TextField getDurationField() {
        return notNull(durationField);
    }

    /**
     * @return the data type field.
     */
    @NotNull
    private TextField getDataTypeField() {
        return notNull(dataTypeField);
    }

    /**
     * @return the sample rate field.
     */
    @NotNull
    private TextField getSampleRateField() {
        return notNull(sampleRateField);
    }

    /**
     * @return the bits per sample field.
     */
    @NotNull
    private TextField getBitsPerSampleField() {
        return notNull(bitsPerSampleField);
    }

    @Override
    public String toString() {
        return "AudioViewerEditor{" +
                "editorAppState=" + editorAppState +
                "} " + super.toString();
    }
}
