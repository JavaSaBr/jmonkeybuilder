package com.ss.editor.ui.component.creator.impl;

import com.ss.editor.Editor;
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
import com.ss.editor.ui.event.impl.CreatedFileEvent;
import com.ss.editor.ui.scene.EditorFXScene;

import java.awt.*;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;

import static com.ss.editor.Messages.FILE_CREATOR_BUTTON_CANCEL;
import static com.ss.editor.Messages.FILE_CREATOR_BUTTON_OK;
import static com.ss.editor.ui.css.CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER;
import static com.ss.editor.ui.css.CSSIds.EDITOR_DIALOG_BUTTON_CANCEL;
import static com.ss.editor.ui.css.CSSIds.EDITOR_DIALOG_BUTTON_OK;
import static javafx.geometry.Pos.CENTER_LEFT;
import static javafx.geometry.Pos.TOP_CENTER;

/**
 * Базовая реализация создателя файловс.
 *
 * @author Ronn
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
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * Дерево ресурсов.
     */
    private ResourceTree resourceTree;

    /**
     * Поле для ввода названия файла.
     */
    private TextField fileNameField;

    /**
     * Кнопка создания файла.
     */
    private Button okButton;

    /**
     * Файл на котором было вызвано создание.
     */
    private Path initFile;

    @Override
    public void start(final Path file) {
        setInitFile(file);

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        final EditorFXScene scene = EDITOR.getScene();
        show(scene.getWindow());

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.setOnLoadHandler(() -> resourceTree.expandTo(file, true));
        resourceTree.fill(currentAsset);

        EXECUTOR_MANAGER.addFXTask(getFileNameField()::requestFocus);

        validateFileName();
    }

    /**
     * @return дерево ресурсов.
     */
    protected ResourceTree getResourceTree() {
        return resourceTree;
    }

    /**
     * @param initFile файл на котором было вызвано создание.
     */
    private void setInitFile(final Path initFile) {
        this.initFile = initFile;
    }

    /**
     * @return файл на котором было вызвано создание.
     */
    private Path getInitFile() {
        return initFile;
    }

    @Override
    protected void createActions(final VBox root) {
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

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);
    }

    /**
     * @return выбранный файл в дереве.
     */
    protected Path getSelectedFile() {

        final ResourceTree resourceTree = getResourceTree();
        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = resourceTree.getSelectionModel();
        final TreeItem<ResourceElement> selectedItem = selectionModel.getSelectedItem();

        if (selectedItem == null) {
            return getInitFile();
        }

        final ResourceElement element = selectedItem.getValue();
        return element.getFile();
    }

    /**
     * Процесс созадния файла.
     */
    protected void processCreate() {
        hide();
    }

    /**
     * @return получение создаваемого файла.
     */
    protected Path getFileToCreate() {

        final TextField fileNameField = getFileNameField();
        final String filename = fileNameField.getText();

        if (StringUtils.isEmpty(filename)) {
            return null;
        }

        final String fileExtension = getFileExtension();

        final Path selectedFile = getSelectedFile();
        final Path directory = Files.isDirectory(selectedFile) ? selectedFile : selectedFile.getParent();
        final Path toCreate = StringUtils.isEmpty(fileExtension) ? directory.resolve(filename) : directory.resolve(filename + "." + fileExtension);

        return toCreate;
    }

    /**
     * @return расширение создаваемого файла.
     */
    protected String getFileExtension() {
        return StringUtils.EMPTY;
    }

    /**
     * Уведомление всех о создании файла.
     */
    protected void notifyFileCreated(final Path createdFile, boolean needSelect) {

        final CreatedFileEvent event = new CreatedFileEvent();
        event.setFile(createdFile);
        event.setNeedSelect(needSelect);

        FX_EVENT_MANAGER.notify(event);
    }

    @Override
    protected void createContent(final VBox root) {
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

        root.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                processCreate();
            }
        });
    }

    /**
     * @return поле для ввода названия файла.
     */
    protected TextField getFileNameField() {
        return fileNameField;
    }

    /**
     * Создание настроек по создаваемогу файлу.
     */
    protected void createSettings(final VBox root) {

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

        FXUtils.addClassTo(fileNameLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(fileNameField, CSSClasses.MAIN_FONT_13);

        VBox.setMargin(fileNameContainer, FILE_NAME_CONTAINER_OFFSET);
    }

    /**
     * @return текст "название файла"
     */
    protected String getFileNameLabelText() {
        return Messages.FILE_CREATOR_FILE_NAME_LABEL;
    }

    /**
     * @return кнопка создания файла.
     */
    public Button getOkButton() {
        return okButton;
    }

    /**
     * Валидация введенного имени файла.
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
