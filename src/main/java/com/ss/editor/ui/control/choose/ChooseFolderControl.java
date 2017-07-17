package com.ss.editor.ui.control.choose;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.dialog.asset.FolderAssetEditorDialog;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The control to choose folder.
 *
 * @author JavaSaBr
 */
public class ChooseFolderControl extends HBox {

    @NotNull
    private static final Predicate<Class<?>> ACTION_TESTER = type -> type == NewFileAction.class ||
            type == DeleteFileAction.class ||
            type == RenameFileAction.class;

    /**
     * The label of the path to a folder.
     */
    @Nullable
    private Label folderLabel;

    /**
     * The selected folder.
     */
    @Nullable
    private Path folder;

    /**
     * The handler.
     */
    @Nullable
    private Runnable changeHandler;

    /**
     * Instantiates a new Choose folder control.
     */
    public ChooseFolderControl() {
        createComponents();
        reload();
    }

    /**
     * Sets change handler.
     *
     * @param changeHandler the handler.
     */
    public void setChangeHandler(@Nullable final Runnable changeHandler) {
        this.changeHandler = changeHandler;
    }

    /**
     * The handler.
     */
    @Nullable
    private Runnable getChangeHandler() {
        return changeHandler;
    }

    /**
     * Create components.
     */
    protected void createComponents() {

        folderLabel = new Label(StringUtils.EMPTY);
        folderLabel.prefWidthProperty().bind(widthProperty());

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> processAdd());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemove());

        FXUtils.addToPane(folderLabel, this);
        FXUtils.addToPane(addButton, this);
        FXUtils.addToPane(removeButton, this);

        FXUtils.addClassesTo(this, CSSClasses.TEXT_INPUT_CONTAINER, CSSClasses.CHOOSE_RESOURCE_CONTROL);
        FXUtils.addClassesTo(addButton, removeButton, CSSClasses.FLAT_BUTTON, CSSClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(addButton, removeButton);

        removeButton.disableProperty()
                .bind(folderLabel.textProperty().isEmpty());
    }

    /**
     * @return the label of the path to a folder.
     */
    @NotNull
    private Label getFolderLabel() {
        return notNull(folderLabel);
    }

    /**
     * Add a folder.
     */
    private void processAdd() {
        final AssetEditorDialog<Path> dialog = new FolderAssetEditorDialog(this::setFolder);
        dialog.setActionTester(ACTION_TESTER);
        dialog.show(this);
    }

    /**
     * Gets folder.
     *
     * @return the selected folder.
     */
    @Nullable
    public Path getFolder() {
        return folder;
    }

    /**
     * Sets folder.
     *
     * @param folder the selected folder.
     */
    public void setFolder(@Nullable final Path folder) {
        this.folder = folder;

        reload();

        final Runnable changeHandler = getChangeHandler();
        if (changeHandler != null) changeHandler.run();
    }

    /**
     * Remove the current folder.
     */
    private void processRemove() {
        setFolder(null);
    }

    /**
     * Reload the current folder.
     */
    protected void reload() {

        final Label folderLabel = getFolderLabel();
        final Path folder = getFolder();

        if (folder == null) {
            folderLabel.setText(Messages.CHOOSE_FOLDER_CONTROL_NO_FOLDER);
            return;
        }

        final Path assetFile = notNull(getAssetFile(folder));
        folderLabel.setText(assetFile.toString());
    }
}
