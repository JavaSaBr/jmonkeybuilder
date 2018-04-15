package com.ss.editor.ui.control.property.impl;

import static com.ss.editor.FileExtensions.AUDIO_EXTENSIONS;
import static com.ss.editor.util.EditorUtil.*;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.fx.util.FxUtils;
import com.ss.rlib.common.util.StringUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The implementation of the {@link PropertyControl} to edit the {@link AudioData}.
 *
 * @param <C> the type of a change consumer.
 * @author JavaSaBr
 */
public class AudioKeyPropertyControl<C extends ChangeConsumer> extends PropertyControl<C, AudioNode, AudioKey> {

    @NotNull
    private static final String NO_AUDIO = Messages.AUDIO_KEY_PROPERTY_CONTROL_NO_AUDIO;

    /**
     * The label with name of the audio key.
     */
    @Nullable
    private Label audioKeyLabel;

    public AudioKeyPropertyControl(
            @Nullable AudioKey element,
            @NotNull String paramName,
            @NotNull C changeConsumer
    ) {
        super(element, paramName, changeConsumer);
        setOnDragOver(this::handleDragOverEvent);
        setOnDragDropped(this::handleDragDroppedEvent);
        setOnDragExited(this::handleDragExitedEvent);
    }

    /**
     * Handle drag exited events.
     *
     * @param dragEvent the drag exited event.
     */
    private void handleDragExitedEvent(@NotNull DragEvent dragEvent) {
    }

    /**
     * Handle dropped events.
     *
     * @param dragEvent the dropped event.
     */
    private void handleDragDroppedEvent(@NotNull DragEvent dragEvent) {
        UiUtils.handleDroppedFile(dragEvent, AUDIO_EXTENSIONS, this, AudioKeyPropertyControl::addAudioData);
    }

    /**
     * Handle drag over events.
     *
     * @param dragEvent the drag over events.
     */
    private void handleDragOverEvent(@NotNull DragEvent dragEvent) {
        UiUtils.acceptIfHasFile(dragEvent, AUDIO_EXTENSIONS);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull HBox container) {
        super.createComponents(container);

        audioKeyLabel = new Label(NO_AUDIO);

        var changeButton = new Button();
        changeButton.setGraphic(new ImageView(Icons.ADD_16));
        changeButton.setOnAction(this::processChange);

        var openButton = new Button();
        openButton.setGraphic(new ImageView(Icons.EDIT_16));
        openButton.setOnAction(this::openAudio);
        openButton.disableProperty()
            .bind(audioKeyLabel.textProperty().isEqualTo(NO_AUDIO));

        audioKeyLabel.prefWidthProperty().bind(widthProperty()
                .subtract(changeButton.widthProperty())
                .subtract(openButton.widthProperty()));

        FxUtils.addChild(container, audioKeyLabel, changeButton, openButton);
        FxUtils.addClass(container, CssClasses.TEXT_INPUT_CONTAINER, CssClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER)
                .addClass(audioKeyLabel, CssClasses.ABSTRACT_PARAM_CONTROL_ELEMENT_LABEL)
                .addClass(changeButton, CssClasses.FLAT_BUTTON, CssClasses.INPUT_CONTROL_TOOLBAR_BUTTON)
                .addClass(openButton, CssClasses.FLAT_BUTTON, CssClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(changeButton, openButton);
    }

    /**
     * Show dialog for choosing another audio key.
     *
     * @param event the action event.
     */
    @FxThread
    protected void processChange(@Nullable ActionEvent event) {
        UiUtils.openFileAssetDialog(this::addAudioData, AUDIO_EXTENSIONS, DEFAULT_ACTION_TESTER);
    }

    /**
     * Add the new audio data.
     *
     * @param file the audio file.
     */
    @FxThread
    private void addAudioData(@NotNull Path file) {

        var assetFile = notNull(getAssetFile(file));
        var audioKey = new AudioKey(toAssetPath(assetFile));

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
     *
     * @param event the action event.
     */
    @FxThread
    protected void openAudio(@Nullable ActionEvent event) {

        var element = getPropertyValue();
        if (element == null) {
            return;
        }

        var assetPath = element.getName();
        if (StringUtils.isEmpty(assetPath)) {
            return;
        }

        var assetFile = Paths.get(assetPath);
        var realFile = notNull(getRealFile(assetFile));
        if (!Files.exists(realFile)) {
            return;
        }

        FX_EVENT_MANAGER.notify(new RequestedOpenFileEvent(realFile));
    }

    /**
     * Get the audio key label.
     *
     * @return the audio key label.
     */
    @FxThread
    private @NotNull Label getAudioKeyLabel() {
        return notNull(audioKeyLabel);
    }

    @Override
    @FxThread
    protected void reload() {
        getAudioKeyLabel().setText(getKeyLabel(getPropertyValue()));
    }

    private @NotNull String getKeyLabel(@Nullable AudioKey assetKey) {
        return EditorUtil.isEmpty(assetKey) ? NO_AUDIO : assetKey.getName();
    }
}
