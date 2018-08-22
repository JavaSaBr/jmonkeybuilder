package com.ss.builder.ui.control.choose;

import static com.ss.editor.util.EditorUtils.getAssetFile;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.builder.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.builder.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.builder.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.builder.ui.css.CssClasses;
import com.ss.builder.ui.util.DynamicIconSupport;
import com.ss.builder.ui.util.UiUtils;
import com.ss.builder.util.EditorUtils;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.common.util.StringUtils;
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

    public ChooseFolderControl() {
        createComponents();
        reload();
    }

    /**
     * Set the change handler.
     *
     * @param changeHandler the handler.
     */
    @FxThread
    public void setChangeHandler(@Nullable final Runnable changeHandler) {
        this.changeHandler = changeHandler;
    }

    /**
     * The handler.
     */
    @FxThread
    private @Nullable Runnable getChangeHandler() {
        return changeHandler;
    }

    /**
     * Create components.
     */
    @FxThread
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

        FXUtils.addClassesTo(this, CssClasses.TEXT_INPUT_CONTAINER, CssClasses.CHOOSE_RESOURCE_CONTROL);
        FXUtils.addClassesTo(addButton, removeButton, CssClasses.FLAT_BUTTON, CssClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(addButton, removeButton);

        removeButton.disableProperty()
                .bind(folderLabel.textProperty().isEmpty());
    }

    /**
     * Get the label of the path to a folder.
     *
     * @return the label of the path to a folder.
     */
    @FxThread
    private @NotNull Label getFolderLabel() {
        return notNull(folderLabel);
    }

    /**
     * Add a folder.
     */
    @FxThread
    private void processAdd() {
        UiUtils.openFolderAssetDialog(this::setFolder, ACTION_TESTER);
    }

    /**
     * Get the selected folder.
     *
     * @return the selected folder.
     */
    @FxThread
    public @Nullable Path getFolder() {
        return folder;
    }

    /**
     * Set the folder.
     *
     * @param folder the selected folder.
     */
    @FxThread
    public void setFolder(@Nullable final Path folder) {
        this.folder = folder;
        reload();
        final Runnable changeHandler = getChangeHandler();
        if (changeHandler != null) {
            changeHandler.run();
        }
    }

    /**
     * Remove the current folder.
     */
    @FxThread
    private void processRemove() {
        setFolder(null);
    }

    /**
     * Reload the current folder.
     */
    @FxThread
    protected void reload() {

        final Label folderLabel = getFolderLabel();
        final Path folder = getFolder();

        if (folder == null) {
            folderLabel.setText(Messages.CHOOSE_FOLDER_CONTROL_NO_FOLDER);
            return;
        }

        final Path assetFile = notNull(EditorUtils.getAssetFile(folder));
        folderLabel.setText(assetFile.toString());
    }
}
