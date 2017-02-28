package com.ss.editor.ui.control.choose;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static java.util.Objects.requireNonNull;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.dialog.asset.FolderAssetEditorDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * The control to choose folder.
 *
 * @author JavaSaBr
 */
public class ChooseFolderControl extends HBox {

    @NotNull
    private static final Insets ELEMENT_OFFSET = new Insets(0, 0, 0, 3);

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
        setAlignment(Pos.CENTER_LEFT);
        createComponents();
        reload();
    }

    /**
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

    protected void createComponents() {

        folderLabel = new Label(StringUtils.EMPTY);
        folderLabel.setId(CSSIds.CHOOSE_FOLDER_CONTROL_FOLDER_LABEL);

        final Button addButton = new Button();
        addButton.setId(CSSIds.CHOOSE_RESOURCE_CONTROL_BUTTON);
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> processAdd());

        final Button removeButton = new Button();
        removeButton.setId(CSSIds.CHOOSE_RESOURCE_CONTROL_BUTTON);
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemove());

        FXUtils.addToPane(folderLabel, this);
        FXUtils.addToPane(addButton, this);
        FXUtils.addToPane(removeButton, this);

        FXUtils.addClassTo(folderLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(addButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(removeButton, CSSClasses.TOOLBAR_BUTTON);

        HBox.setMargin(addButton, ELEMENT_OFFSET);
        HBox.setMargin(removeButton, ELEMENT_OFFSET);

        removeButton.disableProperty().bind(folderLabel.textProperty().isEmpty());
    }

    /**
     * @return the label of the path to a folder.
     */
    @NotNull
    private Label getFolderLabel() {
        return requireNonNull(folderLabel);
    }

    /**
     * Add a folder.
     */
    private void processAdd() {

        final JFXApplication jfxApplication = JFXApplication.getInstance();
        final EditorFXScene scene = jfxApplication.getScene();

        final AssetEditorDialog<Path> dialog = new FolderAssetEditorDialog(this::setFolder);
        dialog.setActionTester(ACTION_TESTER);
        dialog.show(scene.getWindow());
    }

    /**
     * @return the selected folder.
     */
    @Nullable
    public Path getFolder() {
        return folder;
    }

    /**
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

        final Path assetFile = requireNonNull(getAssetFile(folder));
        folderLabel.setText(assetFile.toString());
    }
}
