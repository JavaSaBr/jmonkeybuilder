package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.dialog.RenameDialog;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RenamedFileEvent;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;

/**
 * The action to rename a file.
 *
 * @author JavaSaBr
 */
public class RenameFileAction extends MenuItem {

    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The action element.
     */
    private final ResourceElement element;

    /**
     * Instantiates a new Rename file action.
     *
     * @param element the element
     */
    public RenameFileAction(final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_RENAME_FILE);
        setOnAction(event -> processRename());
        setGraphic(new ImageView(Icons.EDIT_16));
    }

    /**
     * The process of this action.
     */
    private void processRename() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();

        final Path file = element.getFile();

        final RenameDialog renameDialog = new RenameDialog();
        renameDialog.setValidator(this::checkName);
        renameDialog.setHandler(this::processRename);
        renameDialog.setInitName(FileUtils.getNameWithoutExtension(file));
        renameDialog.show(scene.getWindow());
    }

    /**
     * The checking of the new file name.
     */
    private Boolean checkName(final String newFileName) {
        if (!FileUtils.isValidName(newFileName)) return false;

        final Path file = element.getFile();
        final String extension = FileUtils.getExtension(file);

        final Path parent = file.getParent();
        final Path targetFile = parent.resolve(StringUtils.isEmpty(extension) ? newFileName : newFileName + "." + extension);

        return !Files.exists(targetFile);
    }

    /**
     * The process of renaming.
     */
    private void processRename(final String newFileName) {

        final Path file = element.getFile();

        final String extension = FileUtils.getExtension(file);
        final String resultName = StringUtils.isEmpty(extension) ? newFileName : newFileName + "." + extension;

        final Path newFile = file.resolveSibling(resultName);

        try {
            Files.move(file, newFile);
        } catch (final IOException e) {
            EditorUtil.handleException(null, this, e);
            return;
        }

        final RenamedFileEvent event = new RenamedFileEvent();
        event.setNewFile(newFile);
        event.setPrevFile(file);

        FX_EVENT_MANAGER.notify(event);
    }
}
