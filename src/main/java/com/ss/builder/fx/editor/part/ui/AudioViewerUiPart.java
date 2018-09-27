package com.ss.builder.fx.editor.part.ui;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.editor.impl.audio.AudioViewerEditor;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.editor.layout.EditorLayout;
import com.ss.builder.fx.util.DynamicIconSupport;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of Ui part of {@link AudioViewerEditor}.
 *
 * @author JavaSaBr
 */
public class AudioViewerUiPart extends AbstractEditorUiPart<AudioViewerEditor> {

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

    public AudioViewerUiPart(@NotNull AudioViewerEditor fileEditor) {
        super(fileEditor);
        this.bitsPerSampleField = new TextField();
        this.channelsField = new TextField();
        this.durationField = new TextField();
        this.dataTypeField = new TextField();
        this.sampleRateField = new TextField();
        this.playButton = new Button();
        this.stopButton = new Button();
    }

    @FxThread
    @Override
    public void buildUi(@NotNull EditorLayout layout) {

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
        playButton.setOnAction(event -> fileEditor.processPlay());

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
}
