package com.ss.editor.ui.component.creator.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.creator.FileCreator;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.RequestSelectFileEvent;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.Utils;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    protected static final Logger LOGGER = LoggerManager.getLogger(FileCreator.class);

    protected static final Point DIALOG_SIZE = new Point(900, -1);

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final FxEventManager FX_EVENT_MANAGER = FxEventManager.getInstance();

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
    public void start(@NotNull Path file) {
        this.initFile = file;

        var editorConfig = EditorConfig.getInstance();
        var currentAsset = notNull(editorConfig.getCurrentAsset());

        show();

        var resourceTree = getResourceTree();
        resourceTree.setOnLoadHandler(finished -> expand(file, resourceTree, finished));
        resourceTree.fill(currentAsset);

        EXECUTOR_MANAGER.addFxTask(getFileNameField()::requestFocus);

        validateFileName();
    }

    @FxThread
    private void expand(@NotNull Path file, @NotNull ResourceTree resourceTree, @NotNull Boolean finished) {
        if (finished) {
            resourceTree.expandTo(file, true);
        }
    }

    /**
     * Get the resources tree.
     *
     * @return the resources tree.
     */
    @FromAnyThread
    private @NotNull ResourceTree getResourceTree() {
        return notNull(resourceTree);
    }

    /**
     * Set the init file.
     *
     * @param initFile the init file.
     */
    @FromAnyThread
    private void setInitFile(@NotNull Path initFile) {
        this.initFile = initFile;
    }

    /**
     * Get the init file.
     *
     * @return the init file.
     */
    @FromAnyThread
    private @NotNull Path getInitFile() {
        return notNull(initFile);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_CREATE;
    }

    /**
     * Get the selected file in the resources tree.
     *
     * @return the selected file in the resources tree.
     */
    @FromAnyThread
    private @NotNull Path getSelectedFile() {

        var selectedItem = getResourceTree()
                .getSelectionModel()
                .getSelectedItem();

        if (selectedItem == null) {
            return getInitFile();
        }

        return selectedItem.getValue()
                .getFile();
    }

    /**
     * Gets file to create.
     *
     * @return the file to creating.
     */
    @FromAnyThread
    protected @Nullable Path getFileToCreate() {

        var fileNameField = getFileNameField();
        var filename = fileNameField.getText();

        if (StringUtils.isEmpty(filename)) {
            return null;
        }

        var fileExtension = getFileExtension();

        var selectedFile = getSelectedFile();
        var directory = Files.isDirectory(selectedFile) ? selectedFile : selectedFile.getParent();

        return StringUtils.isEmpty(fileExtension) ? directory.resolve(filename) :
                directory.resolve(filename + "." + fileExtension);
    }

    /**
     * Gets file extension.
     *
     * @return the file extension.
     */
    @FromAnyThread
    protected @NotNull String getFileExtension() {
        return StringUtils.EMPTY;
    }

    @Override
    @FxThread
    protected void processOk() {
        super.processOk();

        UiUtils.incrementLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> {

            Path tempFile;
            try {
                tempFile = Files.createTempFile("SSEditor", "fileCreator");
            } catch (final IOException e) {
                EditorUtil.handleException(LOGGER, this, e);
                EXECUTOR_MANAGER.addFxTask(UiUtils::decrementLoading);
                return;
            }

            Path fileToCreate = notNull(getFileToCreate());
            try {

                writeData(tempFile);
                try {
                    Files.move(tempFile, fileToCreate, REPLACE_EXISTING, ATOMIC_MOVE);
                } catch (final AtomicMoveNotSupportedException ex) {
                    Files.move(tempFile, fileToCreate, REPLACE_EXISTING);
                }

                notifyFileCreated(fileToCreate, true);

            } catch (Exception e) {
                Utils.run(tempFile, Files::delete);
                EditorUtil.handleException(LOGGER, this, e);
            }

            EXECUTOR_MANAGER.addFxTask(UiUtils::decrementLoading);
        });
    }

    /**
     * Write created data to the created file.
     *
     * @param resultFile the result file.
     * @throws IOException if was some problem with writing to the result file.
     */
    @BackgroundThread
    protected void writeData(@NotNull Path resultFile) throws IOException {
    }

    @Override
    @FxThread
    protected void createContent(@NotNull VBox root) {
        super.createContent(root);

        var container = new HBox();
        container.prefWidthProperty().bind(widthProperty());

        var settingsContainer = new GridPane();
        settingsContainer.prefWidthProperty()
                .bind(container.widthProperty().multiply(0.5));

        resourceTree = new ResourceTree(null, true);
        resourceTree.prefWidthProperty()
                .bind(container.widthProperty().multiply(0.5));

        FxControlUtils.onSelectedItemChange(resourceTree, this::validateFileName);

        createSettings(settingsContainer);

        FxUtils.addChild(container, resourceTree);

        if (needPreview()) {

            var wrapper = new VBox();

            previewContainer = new BorderPane();

            settingsContainer.prefHeightProperty()
                    .bind(container.heightProperty().subtract(previewContainer.heightProperty()));

            createPreview(previewContainer);

            FXUtils.bindFixedWidth(previewContainer, wrapper.widthProperty());
            FXUtils.bindFixedHeight(previewContainer, wrapper.widthProperty());

            FxUtils.addClass(wrapper, CssClasses.DEF_VBOX)
                    .addClass(previewContainer, CssClasses.DEF_BORDER_PANE);

            FxUtils.addChild(wrapper, settingsContainer, previewContainer)
                    .addChild(container, wrapper);

        } else {

            settingsContainer.prefHeightProperty()
                    .bind(container.heightProperty());

            FxUtils.addChild(container, settingsContainer);
        }

        FxUtils.addClass(root, CssClasses.FILE_CREATOR_DIALOG)
                .addClass(container, CssClasses.DEF_HBOX)
                .addClass(settingsContainer, CssClasses.DEF_GRID_PANE);

        FxUtils.addChild(root, container);
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

    /**
     * If return true the creator will create {@link #previewContainer}.
     *
     * @return true if need to create preview container.
     */
    @FromAnyThread
    protected boolean needPreview() {
        return false;
    }

    /**
     * @return the preview container.
     */
    @FromAnyThread
    protected @Nullable BorderPane getPreviewContainer() {
        return previewContainer;
    }

    /**
     * Create preview.
     *
     * @param container the preview container.
     */
    @FxThread
    protected void createPreview(@NotNull final BorderPane container) {
    }

    /**
     * @return the filed with new file name.
     */
    @FromAnyThread
    protected @NotNull TextField getFileNameField() {
        return notNull(fileNameField);
    }

    /**
     * Create settings of the creating file.
     *
     * @param root the root
     */
    @FxThread
    protected void createSettings(@NotNull final GridPane root) {

        final Label fileNameLabel = new Label(getFileNameLabelText() + ":");
        fileNameLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        fileNameField = new TextField();
        fileNameField.prefWidthProperty().bind(root.widthProperty());
        fileNameField.textProperty().addListener((observable, oldValue, newValue) -> validateFileName());
        fileNameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        root.add(fileNameLabel, 0, 0);
        root.add(fileNameField, 1, 0);

        FXUtils.addClassTo(fileNameLabel, CssClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(fileNameField, CssClasses.DIALOG_FIELD);
    }

    /**
     * Gets file name label text.
     *
     * @return the label text "file name".
     */
    @FromAnyThread
    protected @NotNull String getFileNameLabelText() {
        return Messages.FILE_CREATOR_FILE_NAME_LABEL;
    }

    /**
     * Validate the inputted name.
     */
    @FxThread
    protected void validateFileName() {

        final Button okButton = getOkButton();
        if (okButton == null) return;

        final Path fileToCreate = getFileToCreate();

        if (fileToCreate == null || Files.exists(fileToCreate)) {
            okButton.setDisable(true);
            return;
        }

        okButton.setDisable(false);
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }
}
