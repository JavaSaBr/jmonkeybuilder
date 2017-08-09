package com.ss.editor.ui.component.creator.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.creator.FileCreator;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestSelectFileEvent;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.Utils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The base implementation of a file creator.
 *
 * @author JavaSaBr
 */
public abstract class AbstractFileCreator extends AbstractSimpleEditorDialog implements FileCreator {

    /**
     * The constant LOGGER.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(FileCreator.class);

    /**
     * The constant DIALOG_SIZE.
     */
    @NotNull
    protected static final Point DIALOG_SIZE = new Point(900, -1);

    /**
     * The constant EXECUTOR_MANAGER.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

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
     * The resources tree.
     */
    @Nullable
    private ResourceTree resourceTree;

    /**
     * The preview container.
     */
    @Nullable
    private BorderPane previewContainer;

    /**
     * The filed with new file name.
     */
    @Nullable
    private TextField fileNameField;

    /**
     * The init file.
     */
    @Nullable
    private Path initFile;

    @Override
    public void start(@NotNull final Path file) {
        this.initFile = file;

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = notNull(editorConfig.getCurrentAsset());

        show(JFX_APPLICATION.getLastWindow());

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.setOnLoadHandler(finished -> expand(file, resourceTree, finished));
        resourceTree.fill(currentAsset);

        EXECUTOR_MANAGER.addFXTask(getFileNameField()::requestFocus);

        validateFileName();
    }

    private void expand(@NotNull final Path file, @NotNull final ResourceTree resourceTree,
                        @NotNull final Boolean finished) {
        if (finished) resourceTree.expandTo(file, true);
    }

    /**
     * @return the resources tree.
     */
    @NotNull
    private ResourceTree getResourceTree() {
        return notNull(resourceTree);
    }

    /**
     * @param initFile the init file.
     */
    private void setInitFile(@NotNull final Path initFile) {
        this.initFile = initFile;
    }

    /**
     * @return the init file.
     */
    @NotNull
    private Path getInitFile() {
        return notNull(initFile);
    }

    @NotNull
    @Override
    protected String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_CREATE;
    }

    /**
     * @return the selected file in the resources tree.
     */
    @NotNull
    private Path getSelectedFile() {

        final ResourceTree resourceTree = getResourceTree();
        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = resourceTree.getSelectionModel();
        final TreeItem<ResourceElement> selectedItem = selectionModel.getSelectedItem();
        if (selectedItem == null) return getInitFile();

        final ResourceElement element = selectedItem.getValue();
        return element.getFile();
    }

    /**
     * Gets file to create.
     *
     * @return the file to creating.
     */
    @Nullable
    protected Path getFileToCreate() {

        final TextField fileNameField = getFileNameField();
        final String filename = fileNameField.getText();
        if (StringUtils.isEmpty(filename)) return null;

        final String fileExtension = getFileExtension();

        final Path selectedFile = getSelectedFile();
        final Path directory = Files.isDirectory(selectedFile) ? selectedFile : selectedFile.getParent();

        return StringUtils.isEmpty(fileExtension) ? directory.resolve(filename) :
                directory.resolve(filename + "." + fileExtension);
    }

    /**
     * Gets file extension.
     *
     * @return the file extension.
     */
    @NotNull
    protected String getFileExtension() {
        return StringUtils.EMPTY;
    }

    @Override
    @FXThread
    protected void processOk() {
        super.processOk();

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        scene.incrementLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> {

            final Path tempFile;
            try {
                tempFile = Files.createTempFile("SSEditor", "fileCreator");
            } catch (final IOException e) {
                EditorUtil.handleException(LOGGER, this, e);
                EXECUTOR_MANAGER.addFXTask(scene::decrementLoading);
                return;
            }

            final Path fileToCreate = notNull(getFileToCreate());
            try {

                writeData(tempFile);

                try {
                    Files.move(tempFile, fileToCreate, REPLACE_EXISTING, ATOMIC_MOVE);
                } catch (final AtomicMoveNotSupportedException ex) {
                    Files.move(tempFile, fileToCreate, REPLACE_EXISTING);
                }

                notifyFileCreated(fileToCreate, true);

            } catch (final IOException e) {
                Utils.run(tempFile, Files::delete);
                EditorUtil.handleException(LOGGER, this, e);
            }

            EXECUTOR_MANAGER.addFXTask(scene::decrementLoading);
        });
    }

    /**
     * Write created data to the created file.
     *
     * @param resultFile the result file.
     */
    @BackgroundThread
    protected void writeData(@NotNull final Path resultFile) {
    }

    /**
     * Notify about the file created.
     *
     * @param createdFile the created file
     * @param needSelect  the need select
     */
    @FromAnyThread
    protected void notifyFileCreated(@NotNull final Path createdFile, final boolean needSelect) {
        if (!needSelect) return;

        final RequestSelectFileEvent event = new RequestSelectFileEvent();
        event.setFile(createdFile);

        FX_EVENT_MANAGER.notify(event);
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        final HBox container = new HBox();
        container.prefWidthProperty().bind(widthProperty());

        final GridPane settingsContainer = new GridPane();
        settingsContainer.prefHeightProperty().bind(container.heightProperty());
        settingsContainer.prefWidthProperty().bind(container.widthProperty().multiply(0.5));

        resourceTree = new ResourceTree(null, true);
        resourceTree.prefWidthProperty().bind(container.widthProperty().multiply(0.5));

        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = resourceTree.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> validateFileName());

        createSettings(settingsContainer);

        FXUtils.addToPane(resourceTree, container);

        if (needPreview()) {

            final VBox wrapper = new VBox();

            previewContainer = new BorderPane();
            previewContainer.prefWidthProperty().bind(wrapper.widthProperty());
            previewContainer.prefHeightProperty().bind(wrapper.widthProperty());

            createPreview(previewContainer);

            FXUtils.addToPane(settingsContainer, wrapper);
            FXUtils.addToPane(previewContainer, wrapper);
            FXUtils.addToPane(wrapper, container);

            FXUtils.addClassTo(wrapper, CSSClasses.DEF_VBOX);

        } else {
            FXUtils.addToPane(settingsContainer, container);
        }

        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(root, CSSClasses.FILE_CREATOR_DIALOG);
        FXUtils.addClassTo(container, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(settingsContainer, CSSClasses.DEF_GRID_PANE);
    }

    /**
     * If return true the creator will create {@link #previewContainer}.
     *
     * @return true if need to create preview container.
     */
    protected boolean needPreview() {
        return false;
    }

    /**
     * @return the preview container.
     */
    @Nullable
    protected BorderPane getPreviewContainer() {
        return previewContainer;
    }

    /**
     * Create preview.
     *
     * @param container the preview container.
     */
    protected void createPreview(@NotNull final BorderPane container) {
    }

    /**
     * @return the filed with new file name.
     */
    @NotNull
    protected TextField getFileNameField() {
        return notNull(fileNameField);
    }

    /**
     * Create settings of the creating file.
     *
     * @param root the root
     */
    protected void createSettings(@NotNull final GridPane root) {

        final Label fileNameLabel = new Label(getFileNameLabelText() + ":");
        fileNameLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        fileNameField = new TextField();
        fileNameField.prefWidthProperty().bind(root.widthProperty());
        fileNameField.textProperty().addListener((observable, oldValue, newValue) -> validateFileName());
        fileNameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        root.add(fileNameLabel, 0, 0);
        root.add(fileNameField, 1, 0);

        FXUtils.addClassTo(fileNameLabel, CSSClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(fileNameField, CSSClasses.DIALOG_FIELD);
    }

    /**
     * Gets file name label text.
     *
     * @return the label text "file name".
     */
    @NotNull
    protected String getFileNameLabelText() {
        return Messages.FILE_CREATOR_FILE_NAME_LABEL;
    }

    /**
     * Validate the inputted name.
     */
    protected void validateFileName() {

        final Path fileToCreate = getFileToCreate();
        final Button okButton = getOkButton();

        if (fileToCreate == null || Files.exists(fileToCreate)) {
            okButton.setDisable(true);
            return;
        }

        okButton.setDisable(false);
    }

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
