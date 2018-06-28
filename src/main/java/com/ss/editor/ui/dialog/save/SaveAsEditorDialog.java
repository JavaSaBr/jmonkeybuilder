package com.ss.editor.ui.dialog.save;

import static com.ss.editor.Messages.SAVE_AS_EDITOR_DIALOG_FIELD_FILENAME;
import static com.ss.editor.Messages.SAVE_AS_EDITOR_DIALOG_TITLE;
import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;
import static com.ss.editor.ui.util.UiUtils.findItemForValue;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.dialog.EditorDialog;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.CreatedFileEvent;
import com.ss.editor.ui.event.impl.DeletedFileEvent;
import com.ss.editor.ui.event.impl.RequestSelectFileEvent;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The implementation of the {@link EditorDialog} to choose how to save an object.
 *
 * @author JavaSaBr
 */
public class SaveAsEditorDialog extends AbstractSimpleEditorDialog {

    protected static final Point DIALOG_SIZE = new Point(900, -1);

    @NotNull
    private final EventHandler<CreatedFileEvent> createdFileHandler = this::processEvent;

    @NotNull
    private final EventHandler<RequestSelectFileEvent> selectFileHandle = this::processEvent;

    @NotNull
    private final EventHandler<DeletedFileEvent> deletedFileHandler = this::processEvent;

    /**
     * The list of waited files to select.
     */
    @NotNull
    private final Array<Path> waitedFilesToSelect;

    /**
     * The function for handling the choose.
     */
    @NotNull
    protected final Consumer<Path> consumer;

    /**
     * The tree with all resources.
     */
    @NotNull
    private ResourceTree resourceTree;

    /**
     * The filename field.
     */
    @NotNull
    private TextField fileNameField;

    /**
     * The file extension.
     */
    @Nullable
    private String extension;

    public SaveAsEditorDialog(@NotNull Consumer<Path> consumer) {
        this.waitedFilesToSelect = Array.ofType(Path.class);
        this.consumer = consumer;
        this.resourceTree = new ResourceTree(null, true);
        this.fileNameField = new TextField();
    }

    /**
     * Get the title of filename field.
     *
     * @return the title of filename field.
     */
    @FxThread
    protected @NotNull String getFileNameLabelText() {
        return SAVE_AS_EDITOR_DIALOG_FIELD_FILENAME;
    }

    /**
     * Set the target file extension.
     *
     * @param extension the target file extension.
     */
    @FxThread
    public void setExtension(@NotNull String extension) {
        this.extension = extension;
        getResourceTree().setExtensionFilter(ArrayFactory.asArray(extension));
    }

    /**
     * Get the target file extension.
     *
     * @return the target file extension.
     */
    @FxThread
    private @NotNull String getExtension() {
        return extension == null ? "" : extension;
    }

    /**
     * Sets action tester.
     *
     * @param actionTester the action tester.
     */
    @FxThread
    public void setActionTester(@NotNull Predicate<Class<?>> actionTester) {
        getResourceTree().setActionTester(actionTester);
    }

    @Override
    @FxThread
    protected void createContent(@NotNull VBox root) {
        super.createContent(root);

        var container = new HBox();
        container.prefWidthProperty()
                .bind(widthProperty());

        var settingsContainer = new GridPane();
        settingsContainer.prefWidthProperty()
                .bind(container.widthProperty().multiply(0.5));

        settingsContainer.prefHeightProperty()
                .bind(container.heightProperty());

        resourceTree.prefWidthProperty()
                .bind(container.widthProperty().multiply(0.5));

        FxControlUtils.onSelectedItemChange(resourceTree, this::processSelection);

        createSettings(settingsContainer);

        FxUtils.addClass(root, CssClasses.SAVE_AS_DIALOG)
                .addClass(container, CssClasses.DEF_HBOX)
                .addClass(settingsContainer, CssClasses.DEF_GRID_PANE);

        FxUtils.addChild(container, resourceTree, settingsContainer)
                .addChild(root, container);
    }

    /**
     * Create settings of the creating file.
     *
     * @param root the root
     */
    @FxThread
    protected void createSettings(@NotNull GridPane root) {

        var fileNameLabel = new Label(getFileNameLabelText() + ":");
        fileNameLabel.prefWidthProperty()
                .bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        fileNameField.prefWidthProperty()
                .bind(root.widthProperty());

        fileNameField.prefWidthProperty()
                .bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FxControlUtils.onTextChange(fileNameField, this::validateFileName);

        root.add(fileNameLabel, 0, 0);
        root.add(fileNameField, 1, 0);

        FxUtils.addClass(fileNameLabel, CssClasses.DIALOG_DYNAMIC_LABEL)
                .addClass(fileNameField, CssClasses.DIALOG_FIELD);
    }

    /**
     * Handle the new selected item.
     *
     * @param newValue the new selected item.
     */
    @FxThread
    protected void processSelection(@Nullable TreeItem<ResourceElement> newValue) {

        if (newValue != null) {

            var file = newValue.getValue()
                    .getFile();

            if (!Files.isDirectory(file)) {
                getFileNameField().setText(FileUtils.getNameWithoutExtension(file));
            }
        }

        validateFileName();
    }

    /**
     * Validate the inputted name.
     */
    @FxThread
    protected void validateFileName() {

        var okButton = getOkButton();
        if (okButton == null) {
            return;
        }

        var fileToCreate = getFileToSave();

        if (fileToCreate == null) {
            okButton.setDisable(true);
            return;
        }

        okButton.setDisable(false);
    }

    /**
     * Get the file to create.
     *
     * @return the file to creating.
     */
    @FromAnyThread
    protected @Nullable Path getFileToSave() {

        var filename = getFileNameField().getText();
        var selectedFile = getSelectedFile();

        if (StringUtils.isEmpty(filename) || selectedFile == null) {
            return null;
        }

        var fileExtension = getExtension();
        var directory = Files.isDirectory(selectedFile) ?
                selectedFile : selectedFile.getParent();

        return StringUtils.isEmpty(fileExtension) ? directory.resolve(filename) :
                directory.resolve(filename + "." + fileExtension);
    }

    /**
     * Get the selected file in the resources tree.
     *
     * @return the selected file in the resources tree.
     */
    @FromAnyThread
    private @Nullable Path getSelectedFile() {

        var selectedItem = getResourceTree()
                .getSelectionModel()
                .getSelectedItem();

        if (selectedItem == null) {
            return null;
        }

        return selectedItem.getValue()
                .getFile();
    }

    /**
     * Get the filename field.
     *
     * @return the filename field.
     */
    @FxThread
    protected @NotNull TextField getFileNameField() {
        return notNull(fileNameField);
    }

    @Override
    @FxThread
    public void show(@NotNull Window owner) {
        super.show(owner);

        var currentAsset = EditorConfig.getInstance()
                .requiredCurrentAsset();

        getResourceTree()
                .setOnLoadHandler(finished -> expand(currentAsset, finished))
                .fill(currentAsset);

        FxEventManager.getInstance()
                .addEventHandler(CreatedFileEvent.EVENT_TYPE, createdFileHandler)
                .addEventHandler(RequestSelectFileEvent.EVENT_TYPE, selectFileHandle)
                .addEventHandler(DeletedFileEvent.EVENT_TYPE, deletedFileHandler);

        validateFileName();

        ExecutorManager.getInstance()
                .addFxTask(getFileNameField()::requestFocus);
    }

    @FxThread
    private void expand(@NotNull Path file, @NotNull Boolean finished) {
        if (finished) {
            getResourceTree().expandTo(file, true);
        }
    }

    /**
     * Handle creating file event.
     */
    @FxThread
    private void processEvent(@NotNull CreatedFileEvent event) {

        var file = event.getFile();

        var waitedFilesToSelect = getWaitedFilesToSelect();
        var waitedSelect = waitedFilesToSelect.contains(file);

        var resourceTree = getResourceTree();
        resourceTree.notifyCreated(file);

        if (waitedSelect) {
            waitedFilesToSelect.fastRemove(file);
        }

        if (waitedSelect || event.isNeedSelect()) {
            resourceTree.expandTo(file, true);
        }
    }

    /**
     * Handle deleting file event.
     */
    @FxThread
    private void processEvent(@NotNull final DeletedFileEvent event) {

        final Path file = event.getFile();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyDeleted(file);
    }

    /**
     * Handle selecting file event.
     */
    @FxThread
    private void processEvent(@NotNull final RequestSelectFileEvent event) {

        final Path file = event.getFile();

        final ResourceTree resourceTree = getResourceTree();
        final ResourceElement element = createFor(file);
        final TreeItem<ResourceElement> treeItem = findItemForValue(resourceTree.getRoot(), element);

        if (treeItem == null) {
            getWaitedFilesToSelect().add(file);
            return;
        }

        resourceTree.expandTo(treeItem, true);
    }

    /**
     * @return the list of waited files to select.
     */
    @FxThread
    private @NotNull Array<Path> getWaitedFilesToSelect() {
        return waitedFilesToSelect;
    }

    @Override
    @FxThread
    public void hide() {

        FxEventManager.getInstance()
                .removeEventHandler(CreatedFileEvent.EVENT_TYPE, createdFileHandler)
                .removeEventHandler(RequestSelectFileEvent.EVENT_TYPE, selectFileHandle)
                .removeEventHandler(DeletedFileEvent.EVENT_TYPE, deletedFileHandler);

        super.hide();
    }

    /**
     * Get the function for handling the choose.
     *
     * @return the function for handling the choose.
     */
    @FxThread
    protected @NotNull Consumer<@NotNull Path> getConsumer() {
        return consumer;
    }

    /**
     * Get the tree with all resources.
     *
     * @return the tree with all resources.
     */
    @FxThread
    private @NotNull ResourceTree getResourceTree() {
        return notNull(resourceTree);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return SAVE_AS_EDITOR_DIALOG_TITLE;
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_SAVE;
    }

    @Override
    @FxThread
    protected void processOk() {
        super.processOk();
        consumer.accept(notNull(getFileToSave()));
    }
}
