package com.ss.editor.ui.control.property.impl;

import static com.ss.editor.FileExtensions.AUDIO_EXTENSIONS;
import static com.ss.editor.util.EditorUtil.*;
import static com.ss.rlib.util.ObjectUtils.notNull;
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
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
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

    public AudioKeyPropertyControl(@Nullable final AudioKey element, @NotNull final String paramName,
                                   @NotNull final C changeConsumer) {
        super(element, paramName, changeConsumer);
        setOnDragOver(this::handleDragOverEvent);
        setOnDragDropped(this::handleDragDroppedEvent);
        setOnDragExited(this::handleDragExitedEvent);
    }

    /**
     * Handle grad exited events.
     *
     * @param dragEvent the drag exited event.
     */
    private void handleDragExitedEvent(@NotNull final DragEvent dragEvent) {

    }

    /**
     * Handle dropped event.
     *
     * @param dragEvent the dropped event.
     */
    private void handleDragDroppedEvent(@NotNull final DragEvent dragEvent) {
        UIUtils.handleDroppedFile(dragEvent, AUDIO_EXTENSIONS, this, AudioKeyPropertyControl::addAudioData);
    }

    /**
     * Handle drag over events.
     *
     * @param dragEvent the drag over events.
     */
    private void handleDragOverEvent(@NotNull final DragEvent dragEvent) {
        UIUtils.acceptIfHasFile(dragEvent, AUDIO_EXTENSIONS);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        audioKeyLabel = new Label(NO_AUDIO);

        final Button changeButton = new Button();
        changeButton.setGraphic(new ImageView(Icons.ADD_16));
        changeButton.setOnAction(event -> processChange());

        final Button openButton = new Button();
        openButton.setGraphic(new ImageView(Icons.EDIT_16));
        openButton.disableProperty().bind(audioKeyLabel.textProperty().isEqualTo(NO_AUDIO));
        openButton.setOnAction(event -> processOpen());

        audioKeyLabel.prefWidthProperty().bind(widthProperty()
                .subtract(changeButton.widthProperty())
                .subtract(openButton.widthProperty()));

        FXUtils.addToPane(audioKeyLabel, container);
        FXUtils.addToPane(changeButton, container);
        FXUtils.addToPane(openButton, container);

        FXUtils.addClassesTo(container, CssClasses.TEXT_INPUT_CONTAINER,
                CssClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER);
        FXUtils.addClassTo(audioKeyLabel, CssClasses.ABSTRACT_PARAM_CONTROL_ELEMENT_LABEL);
        FXUtils.addClassesTo(changeButton, openButton, CssClasses.FLAT_BUTTON,
                CssClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(changeButton, openButton);
    }

    /**
     * Show dialog for choosing another audio key.
     */
    @FxThread
    protected void processChange() {
        UIUtils.openFileAssetDialog(this::addAudioData, AUDIO_EXTENSIONS, DEFAULT_ACTION_TESTER);
    }

    /**
     * Add the new audio data.
     *
     * @param file the audio file.
     */
    @FxThread
    private void addAudioData(@NotNull final Path file) {

        final Path assetFile = notNull(getAssetFile(file));
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
    @FxThread
    protected void processOpen() {

        final AudioKey element = getPropertyValue();
        if (element == null) return;

        final String assetPath = element.getName();
        if (StringUtils.isEmpty(assetPath)) return;

        final Path assetFile = Paths.get(assetPath);
        final Path realFile = notNull(getRealFile(assetFile));
        if (!Files.exists(realFile)) return;

        final RequestedOpenFileEvent event = new RequestedOpenFileEvent();
        event.setFile(realFile);

        FX_EVENT_MANAGER.notify(event);
    }

    /**
     * Gets audio key label.
     *
     * @return the label with name of the audio key.
     */
    @FxThread
    private @NotNull Label getAudioKeyLabel() {
        return notNull(audioKeyLabel);
    }

    @Override
    @FxThread
    protected void reload() {
        final AudioKey element = getPropertyValue();
        final Label audioKeyLabel = getAudioKeyLabel();
        audioKeyLabel.setText(element == null || StringUtils.isEmpty(element.getName()) ? NO_AUDIO : element.getName());
    }
}
