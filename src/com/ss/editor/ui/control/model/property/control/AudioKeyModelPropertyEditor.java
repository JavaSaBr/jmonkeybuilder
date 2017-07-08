package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.util.EditorUtil.*;
import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import com.ss.editor.ui.util.UIUtils;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * The implementation of the {@link ModelPropertyControl} for editing the {@link AudioData}.
 *
 * @author JavaSaBr
 */
public class AudioKeyModelPropertyEditor extends ModelPropertyControl<AudioNode, AudioKey> {

    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class ||
            type == RenameFileAction.class;

    private static final String NO_AUDIO = Messages.AUDIO_KEY_PROPERTY_CONTROL_NO_AUDIO;
    private static final Insets BUTTON_OFFSET = new Insets(0, 0, 0, 3);

    /**
     * The constant FX_EVENT_MANAGER.
     */
    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    /**
     * The constant JFX_APPLICATION.
     */
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    /**
     * The constant EDITOR.
     */
    protected static final Editor EDITOR = Editor.getInstance();

    private static final Array<String> AUDIO_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        AUDIO_EXTENSIONS.add(FileExtensions.AUDIO_MP3);
        AUDIO_EXTENSIONS.add(FileExtensions.AUDIO_WAV);
        AUDIO_EXTENSIONS.add(FileExtensions.AUDIO_OGG);
    }

    /**
     * The label with name of the audio key.
     */
    private Label audioKeyLabel;

    /**
     * Instantiates a new Audio key model property editor.
     *
     * @param element        the element
     * @param paramName      the param name
     * @param changeConsumer the change consumer
     */
    public AudioKeyModelPropertyEditor(@Nullable final AudioKey element, @NotNull final String paramName,
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

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return;
        }

        final File file = files.get(0);
        final String extension = FileUtils.getExtension(file.getName());

        if (!AUDIO_EXTENSIONS.contains(extension)) {
            return;
        }

        addAudioData(file.toPath());
    }

    /**
     * Handle drag over.
     */
    private void dragOver(@NotNull final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return;
        }

        final File file = files.get(0);
        final String extension = FileUtils.getExtension(file.getName());

        if (!AUDIO_EXTENSIONS.contains(extension)) {
            return;
        }

        final Set<TransferMode> transferModes = dragboard.getTransferModes();
        final boolean isCopy = transferModes.contains(TransferMode.COPY);

        dragEvent.acceptTransferModes(isCopy ? TransferMode.COPY : TransferMode.MOVE);
        dragEvent.consume();
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        audioKeyLabel = new Label(NO_AUDIO);
        audioKeyLabel.setId(CSSIds.ABSTRACT_PARAM_CONTROL_ELEMENT_LABEL);

        final Button changeButton = new Button();
        changeButton.setId(CSSIds.ABSTRACT_PARAM_CONTROL_ELEMENT_BUTTON);
        changeButton.setGraphic(new ImageView(Icons.ADD_16));
        changeButton.setOnAction(event -> processChange());

        final Button openButton = new Button();
        openButton.setId(CSSIds.ABSTRACT_PARAM_CONTROL_ELEMENT_BUTTON);
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
        UIUtils.openAssetDialog(this::addAudioData, AUDIO_EXTENSIONS, ACTION_TESTER);
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
     * Gets audio key label.
     *
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
