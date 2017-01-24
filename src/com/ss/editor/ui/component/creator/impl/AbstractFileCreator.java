package com.ss.editor.ui.component.creator.impl;

import static com.ss.editor.Messages.FILE_CREATOR_BUTTON_CANCEL;
import static com.ss.editor.Messages.FILE_CREATOR_BUTTON_OK;
import static com.ss.editor.ui.css.CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER;
import static com.ss.editor.ui.css.CSSIds.EDITOR_DIALOG_BUTTON_CANCEL;
import static com.ss.editor.ui.css.CSSIds.EDITOR_DIALOG_BUTTON_OK;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.geometry.Pos.TOP_CENTER;

import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.creator.FileCreator;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.EditorDialog;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestSelectFileEvent;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Point;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;

/**
 * The base implementation of a file creator.
 *
 * @author JavaSaBr.
 */
public abstract class AbstractFileCreator extends EditorDialog implements FileCreator {

    protected static final Logger LOGGER = LoggerManager.getLogger(FileCreator.class);

    protected static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    protected static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);
    protected static final Insets FILE_NAME_CONTAINER_OFFSET = new Insets(15, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);
    protected static final Insets RESOURCE_TREE_OFFSET = new Insets(3, 0, 0, 0);

    protected static final Point DIALOG_SIZE = new Point(900, 400);

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * The resources tree.
     */
    private ResourceTree resourceTree;

    /**
     * The filed with new file name.
     */
    private TextField fileNameField;

    /**
     * The creation button.
     */
    private Button okButton;

    /**
     * The init file.
     */
    private Path initFile;

    @Override
    public void start(@NotNull final Path file) {
        setInitFile(file);

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        show(scene.getWindow());

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.setOnLoadHandler(finished -> expand(file, resourceTree, finished));
        resourceTree.fill(currentAsset);

        EXECUTOR_MANAGER.addFXTask(getFileNameField()::requestFocus);

        validateFileName();
    }

    protected void expand(@NotNull final Path file, @NotNull final ResourceTree resourceTree, @NotNull final Boolean finished) {
        if (finished) resourceTree.expandTo(file, true);
    }

    /**
     * @return the resources tree.
     */
    protected ResourceTree getResourceTree() {
        return resourceTree;
    }

    /**
     * @param initFile the init file.
     */
    private void setInitFile(final Path initFile) {
        this.initFile = initFile;
    }

    /**
     * @return the init file.
     */
    private Path getInitFile() {
        return initFile;
    }

    @Override
    protected void createActions(@NotNull final VBox root) {
        super.createActions(root);

        final HBox container = new HBox();
        container.setId(ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        okButton = new Button(FILE_CREATOR_BUTTON_OK);
        okButton.setId(EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processCreate());

        final Button cancelButton = new Button(FILE_CREATOR_BUTTON_CANCEL);
        cancelButton.setId(EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(okButton, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(cancelButton, CSSClasses.SPECIAL_FONT_16);

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);
    }

    /**
     * @return the selected file in the resources tree.
     */
    @NotNull
    protected Path getSelectedFile() {

        final ResourceTree resourceTree = getResourceTree();
        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = resourceTree.getSelectionModel();
        final TreeItem<ResourceElement> selectedItem = selectionModel.getSelectedItem();
        if (selectedItem == null) return getInitFile();

        final ResourceElement element = selectedItem.getValue();
        return element.getFile();
    }

    /**
     * The process of creation.
     */
    protected void processCreate() {
        hide();
    }

    /**
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

        return StringUtils.isEmpty(fileExtension) ? directory.resolve(filename) : directory.resolve(filename + "." + fileExtension);
    }

    /**
     * @return the file extension.
     */
    @NotNull
    protected String getFileExtension() {
        return StringUtils.EMPTY;
    }

    /**
     * Notify about the file created.
     */
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
        container.setAlignment(CENTER_LEFT);

        final VBox settingsContainer = new VBox();
        settingsContainer.setAlignment(TOP_CENTER);

        resourceTree = new ResourceTree(null, true);

        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = resourceTree.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> validateFileName());

        createSettings(settingsContainer);

        FXUtils.addToPane(resourceTree, container);
        FXUtils.addToPane(settingsContainer, container);
        FXUtils.addToPane(container, root);

        FXUtils.bindFixedWidth(resourceTree, root.widthProperty().divide(2));
        FXUtils.bindFixedWidth(settingsContainer, root.widthProperty().divide(2));

        HBox.setMargin(resourceTree, RESOURCE_TREE_OFFSET);

        root.setOnKeyReleased(this::processEnter);
    }

    protected void processEnter(@NotNull final KeyEvent event) {

        final Button okButton = getOkButton();

        if (event.getCode() == KeyCode.ENTER && !okButton.isDisable()) {
            processCreate();
        }
    }

    /**
     * @return the filed with new file name.
     */
    @NotNull
    protected TextField getFileNameField() {
        return fileNameField;
    }

    /**
     * Create settings of the creating file.
     */
    protected void createSettings(@NotNull final VBox root) {

        final HBox fileNameContainer = new HBox();
        fileNameContainer.setAlignment(Pos.CENTER_LEFT);

        final Label fileNameLabel = new Label(getFileNameLabelText() + ":");
        fileNameLabel.setId(CSSIds.FILE_CREATOR_LABEL);

        fileNameField = new TextField();
        fileNameField.setId(CSSIds.FILE_CREATOR_TEXT_FIELD);
        fileNameField.prefWidthProperty().bind(root.widthProperty());
        fileNameField.textProperty().addListener((observable, oldValue, newValue) -> validateFileName());

        FXUtils.addToPane(fileNameLabel, fileNameContainer);
        FXUtils.addToPane(fileNameField, fileNameContainer);
        FXUtils.addToPane(fileNameContainer, root);

        FXUtils.addClassTo(fileNameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(fileNameField, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(fileNameContainer, FILE_NAME_CONTAINER_OFFSET);
    }

    /**
     * @return the label text "file name".
     */
    @NotNull
    protected String getFileNameLabelText() {
        return Messages.FILE_CREATOR_FILE_NAME_LABEL;
    }

    /**
     * @return the creation button.
     */
    @NotNull
    public Button getOkButton() {
        return okButton;
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

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
