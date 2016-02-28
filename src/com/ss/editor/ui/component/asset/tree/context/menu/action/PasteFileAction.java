package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.CreatedFileEvent;
import com.ss.editor.ui.event.impl.DeletedFileEvent;
import com.ss.editor.util.EditorUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.FileUtils;

import static com.ss.editor.Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_PASTE_FILE;

/**
 * Реализация действия по вставке файла.
 *
 * @author Ronn
 */
public class PasteFileAction extends MenuItem {

    private static final Logger LOGGER = LoggerManager.getLogger(PasteFileAction.class);

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * Элемент действия.
     */
    private final ResourceElement element;

    public PasteFileAction(final ResourceElement element) {
        this.element = element;
        setText(ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_PASTE_FILE);
        setOnAction(event -> processPaste());
    }

    /**
     * Процесс вставки файла.
     */
    private void processPaste() {

        final Clipboard clipboard = Clipboard.getSystemClipboard();

        if (clipboard == null) {
            return;
        }

        final List<File> files = (List<File>) clipboard.getContent(DataFormat.FILES);

        if (files == null || files.isEmpty()) {
            return;
        }

        final Path currentFile = element.getFile();
        final Path file = files.get(0).toPath();

        final boolean isCut = "cut".equals(clipboard.getContent(EditorUtil.JAVA_PARAM));

        if (Files.isDirectory(currentFile)) {

            final String fileName = FileUtils.getFirstFreeName(currentFile, file);
            final Path newFile = currentFile.resolve(fileName);

            try {
                Files.copy(file, newFile);
            } catch (IOException e) {
                LOGGER.warning(e);
            }

            final CreatedFileEvent createdFileEvent = new CreatedFileEvent();
            createdFileEvent.setFile(newFile);

            FX_EVENT_MANAGER.notify(createdFileEvent);

            if (isCut) {

                FileUtils.delete(file);

                final DeletedFileEvent deletedFileEvent = new DeletedFileEvent();
                deletedFileEvent.setFile(file);

                FX_EVENT_MANAGER.notify(deletedFileEvent);
            }

            clipboard.clear();
            return;
        }

        final Path parent = currentFile.getParent();
        final String fileName = FileUtils.getFirstFreeName(parent, file);
        final Path newFile = parent.resolve(fileName);

        try {
            Files.copy(file, newFile);
        } catch (IOException e) {
            LOGGER.warning(e);
        }

        final CreatedFileEvent createdFileEvent = new CreatedFileEvent();
        createdFileEvent.setFile(newFile);

        FX_EVENT_MANAGER.notify(createdFileEvent);

        if (isCut) {

            FileUtils.delete(file);

            final DeletedFileEvent deletedFileEvent = new DeletedFileEvent();
            deletedFileEvent.setFile(file);

            FX_EVENT_MANAGER.notify(deletedFileEvent);
        }

        clipboard.clear();
    }
}
