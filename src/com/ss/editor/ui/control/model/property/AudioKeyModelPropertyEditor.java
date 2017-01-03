package com.ss.editor.ui.control.model.property;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.getRealFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;

import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.JFXApplication;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.dialog.asset.FileAssetEditorDialog;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link ModelPropertyControl} for editing the {@link AudioData}.
 *
 * @author JavaSaBr
 */
public class AudioKeyModelPropertyEditor extends ModelPropertyControl<AudioNode, AudioKey> {

    public static final String NO_AUDIO = "No audio";
    public static final Insets BUTTON_OFFSET = new Insets(0, 0, 0, 3);

    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    protected static final Array<String> AUDIO_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        AUDIO_EXTENSIONS.add(FileExtensions.AUDIO_MP3);
        AUDIO_EXTENSIONS.add(FileExtensions.AUDIO_WAV);
        AUDIO_EXTENSIONS.add(FileExtensions.AUDIO_OGG);
    }

    /**
     * The label with name of the audio key.
     */
    private Label audioKeyLabel;

    public AudioKeyModelPropertyEditor(@Nullable final AudioKey element, @NotNull final String paramName,
                                       @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        audioKeyLabel = new Label(NO_AUDIO);
        audioKeyLabel.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_LABEL);

        final Button changeButton = new Button();
        changeButton.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_BUTTON);
        changeButton.setGraphic(new ImageView(Icons.ADD_24));
        changeButton.setOnAction(event -> processChange());

        final Button openButton = new Button();
        openButton.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_BUTTON);
        openButton.setGraphic(new ImageView(Icons.EDIT_16));
        openButton.disableProperty().bind(audioKeyLabel.textProperty().isEqualTo(NO_AUDIO));
        openButton.setOnAction(event -> processOpen());

        audioKeyLabel.prefWidthProperty().bind(widthProperty()
                .subtract(changeButton.widthProperty())
                .subtract(openButton.widthProperty())
                .subtract(BUTTON_OFFSET.getLeft() * 2));

        FXUtils.addToPane(audioKeyLabel, container);
        FXUtils.addToPane(changeButton, container);
        FXUtils.addToPane(openButton, container);

        HBox.setMargin(changeButton, BUTTON_OFFSET);
        HBox.setMargin(openButton, BUTTON_OFFSET);

        FXUtils.addClassTo(audioKeyLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(changeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(changeButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(openButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(openButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
    }

    /**
     * Show dialog for choosing another audio key.
     */
    protected void processChange() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final AssetEditorDialog dialog = new FileAssetEditorDialog(this::addAudioData);
        dialog.setExtensionFilter(AUDIO_EXTENSIONS);
        dialog.show(scene.getWindow());
    }

    private void addAudioData(@NotNull final Path file) {

        final Path assetFile = Objects.requireNonNull(getAssetFile(file));
        final AudioKey audioKey = new AudioKey(toAssetPath(assetFile));

        changed(audioKey, getPropertyValue());
        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }
    }

    /**
     * Open this audio data in the audio viewer.
     */
    protected void processOpen() {

        final AudioKey element = getPropertyValue();
        if (element == null) return;

        final String assetPath = element.getName();
        if (StringUtils.isEmpty(assetPath)) return;

        final Path assetFile = Paths.get(assetPath);
        final Path realFile = Objects.requireNonNull(getRealFile(assetFile));
        if (!Files.exists(realFile)) return;

        final RequestedOpenFileEvent event = new RequestedOpenFileEvent();
        event.setFile(realFile);

        FX_EVENT_MANAGER.notify(event);
    }

    /**
     * @return the label with name of the audio key.
     */
    protected Label getAudioKeyLabel() {
        return audioKeyLabel;
    }

    @Override
    protected void reload() {
        final AudioKey element = getPropertyValue();
        final Label audioKeyLabel = getAudioKeyLabel();
        audioKeyLabel.setText(element == null || StringUtils.isEmpty(element.getName()) ? NO_AUDIO : element.getName());
    }
}
