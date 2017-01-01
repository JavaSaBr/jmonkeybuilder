package com.ss.editor.ui.control.model.property;

import com.jme3.audio.AudioData;
import com.jme3.scene.Spatial;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.JFXApplication;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link ModelPropertyControl} for editing the {@link AudioData}.
 *
 * @author JavaSaBr
 */
public class AudioDataModelPropertyEditor<T extends Spatial, V> extends ModelPropertyControl<T, V> {

    public static final String NO_AUDIO = "No audio";
    public static final Insets BUTTON_OFFSET = new Insets(0, 0, 0, 3);

    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    protected static final Array<String> MATERIAL_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        MATERIAL_EXTENSIONS.add(FileExtensions.JME_MATERIAL);
    }

    /**
     * The label with name of the audio data.
     */
    private Label audioLabel;

    /**
     * The button for choosing other audio data.
     */
    private Button changeButton;

    /**
     * The button for opening the audio.
     */
    private Button openButton;

    public AudioDataModelPropertyEditor(@Nullable final V element, @NotNull final String paramName,
                                        @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        audioLabel = new Label(NO_AUDIO);
        audioLabel.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_LABEL);

        changeButton = new Button();
        changeButton.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_BUTTON);
        changeButton.setGraphic(new ImageView(Icons.ADD_24));
        changeButton.setOnAction(event -> processChange());

        openButton = new Button();
        openButton.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_BUTTON);
        openButton.setGraphic(new ImageView(Icons.EDIT_16));
        openButton.disableProperty().bind(audioLabel.textProperty().isEqualTo(NO_AUDIO));
        openButton.setOnAction(event -> processEdit());

        audioLabel.prefWidthProperty().bind(widthProperty()
                .subtract(changeButton.widthProperty())
                .subtract(openButton.widthProperty())
                .subtract(BUTTON_OFFSET.getLeft() * 2));

        FXUtils.addToPane(audioLabel, container);
        FXUtils.addToPane(changeButton, container);
        FXUtils.addToPane(openButton, container);

        HBox.setMargin(changeButton, BUTTON_OFFSET);
        HBox.setMargin(openButton, BUTTON_OFFSET);

        FXUtils.addClassTo(audioLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(changeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(changeButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(openButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(openButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
    }

    /**
     * Show dialog for choosing another material.
     */
    protected void processChange() {
    }

    /**
     * Open this material in the material editor.
     */
    protected void processEdit() {
    }

    /**
     * @return the label with name of the material.
     */
    protected Label getAudioLabel() {
        return audioLabel;
    }
}
