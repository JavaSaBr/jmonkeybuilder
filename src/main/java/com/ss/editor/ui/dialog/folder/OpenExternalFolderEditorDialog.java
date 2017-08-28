package com.ss.editor.ui.dialog.folder;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Editor;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.dialog.EditorDialog;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * The implementation of the {@link EditorDialog} to choose a folder on machine.
 *
 * @author JavaSaBr
 */
public class OpenExternalFolderEditorDialog extends AbstractSimpleEditorDialog {

    /**
     * The constant DIALOG_SIZE.
     */
    @NotNull
    protected static final Point DIALOG_SIZE = new Point(-1, -1);

    /**
     * The executing manager.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The editor.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

    /**
     * The function to handle a selected folder.
     */
    @NotNull
    protected final Consumer<@NotNull Path> consumer;

    /**
     * The tree with all resources.
     */
    @Nullable
    private ResourceTree resourceTree;

    /**
     * Init directory.
     */
    @Nullable
    private Path initDirectory;

    public OpenExternalFolderEditorDialog(@NotNull final Consumer<@NotNull Path> consumer) {
        this.consumer = consumer;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {

        resourceTree = new ResourceTree(this::processOpen, false);
        resourceTree.prefHeightProperty().bind(heightProperty());
        resourceTree.prefWidthProperty().bind(widthProperty());
        resourceTree.setLazyMode(true);
        resourceTree.setOnlyFolders(true);
        resourceTree.setShowRoot(false);
        resourceTree.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> processSelected(newValue));

        FXUtils.addToPane(resourceTree, root);
        FXUtils.addClassTo(root, CSSClasses.OPEN_EXTERNAL_FOLDER_EDITOR_DIALOG);
    }

    @FXThread
    private void processOpen(@NotNull final ResourceElement element) {
        final Button okButton = getOkButton();
        if (okButton.isDisabled()) return;
        hide();
        consumer.accept(element.getFile());
    }

    @Override
    @FXThread
    protected void processOk() {
        super.processOk();

        final ResourceElement element = getResourceTree()
                .getSelectionModel()
                .getSelectedItem()
                .getValue();

        consumer.accept(element.getFile());
    }

    @Override
    public void show(@NotNull final Window owner) {
        super.show(owner);

        Path initDirectory = getInitDirectory();
        if (initDirectory == null) {
            initDirectory = Paths.get(System.getProperty("user.home"));
        }

        final Path toExpand = initDirectory;
        final Path root = initDirectory.getRoot();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.fill(root);
        resourceTree.setOnLoadHandler(finished -> {
            if (finished) {
                resourceTree.expandTo(toExpand, true);
            }
        });

        EXECUTOR_MANAGER.addFXTask(resourceTree::requestFocus);
    }

    /**
     * Handle selected element in the tree.
     */
    private void processSelected(@Nullable final TreeItem<ResourceElement> newValue) {
        final ResourceElement element = newValue == null ? null : newValue.getValue();
        final Path file = element == null ? null : element.getFile();
        final Button okButton = notNull(getOkButton());
        okButton.setDisable(file == null || !Files.isWritable(file));
    }

    /**
     * Gets consumer.
     *
     * @return the function for handling the choose.
     */
    protected @NotNull Consumer<@NotNull Path> getConsumer() {
        return consumer;
    }

    /**
     * Set the init directory.
     *
     * @param initDirectory the init directory.
     */
    public void setInitDirectory(@Nullable final Path initDirectory) {
        this.initDirectory = initDirectory;
    }

    /**
     * Get the init directory.
     *
     * @return the init directory.
     */
    public @Nullable Path getInitDirectory() {
        return initDirectory;
    }

    /**
     * @return the tree with all resources.
     */
    private @NotNull ResourceTree getResourceTree() {
        return notNull(resourceTree);
    }

    @Override
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }

    @Override
    protected @NotNull String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_SELECT;
    }
}
