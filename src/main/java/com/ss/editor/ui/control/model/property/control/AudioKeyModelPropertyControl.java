package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.FileExtensions.AUDIO_EXTENSIONS;
import static com.ss.editor.util.EditorUtil.*;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.event.FXEventManager;
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
import java.util.function.Predicate;

/**
 * The implementation of the {@link ModelPropertyControl} to edit the {@link AudioData}.
 *
 * @author JavaSaBr
 */
public class AudioKeyModelPropertyControl extends ModelPropertyControl<AudioNode, AudioKey> {

    @NotNull
    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class ||
            type == RenameFileAction.class;

    @NotNull
    private static final String NO_AUDIO = Messages.AUDIO_KEY_PROPERTY_CONTROL_NO_AUDIO;

    /**
     * The constant FX_EVENT_MANAGER.
     */
    @NotNull
    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The constant JFX_APPLICATION.
     */
    @NotNull
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * The constant EDITOR.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * The label with name of the audio key.
     */
    @Nullable
    private Label audioKeyLabel;

    /**
     * Instantiates a new Audio key model property editor.
     *
     * @param element        the element
     * @param paramName      the param name
     * @param changeConsumer the change consumer
     */
    public AudioKeyModelPropertyControl(@Nullable final AudioKey element, @NotNull final String paramName,
                                        @NotNull final ModelChangeConsumer changeConsumer) {
        super(element, paramName, changeConsumer);
        setOnDragOver(this::dragOver);
        setOnDragDropped(this::dragDropped);
        setOnDragExited(this::dragExited);
    }

    /**
     * Handle grad exiting.
     */
    private void dragExited(@NotNull final DragEvent dragEvent) {

    }

    /**
     * Handle dropped files to editor.
     */
    private void dragDropped(@NotNull final DragEvent dragEvent) {
        UIUtils.handleDroppedFile(dragEvent, AUDIO_EXTENSIONS, this, AudioKeyModelPropertyControl::addAudioData);
    }

    /**
     * Handle drag over.
     */
    private void dragOver(@NotNull final DragEvent dragEvent) {
        UIUtils.acceptIfHasFile(dragEvent, AUDIO_EXTENSIONS);
    }

    @Override
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

        FXUtils.addClassesTo(container, CSSClasses.TEXT_INPUT_CONTAINER,
                CSSClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER);
        FXUtils.addClassTo(audioKeyLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_ELEMENT_LABEL);
        FXUtils.addClassesTo(changeButton, openButton, CSSClasses.FLAT_BUTTON,
                CSSClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(changeButton, openButton);
    }

    /**
     * Show dialog for choosing another audio key.
     */
    protected void processChange() {
        UIUtils.openAssetDialog(this::addAudioData, AUDIO_EXTENSIONS, ACTION_TESTER);
    }

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
    @NotNull
    private Label getAudioKeyLabel() {
        return notNull(audioKeyLabel);
    }

    @Override
    protected void reload() {
        final AudioKey element = getPropertyValue();
        final Label audioKeyLabel = getAudioKeyLabel();
        audioKeyLabel.setText(element == null || StringUtils.isEmpty(element.getName()) ? NO_AUDIO : element.getName());
    }
}
