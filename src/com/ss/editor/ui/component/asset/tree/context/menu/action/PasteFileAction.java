package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.CreatedFileEvent;
import com.ss.editor.ui.event.impl.MovedFileEvent;
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
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import static rlib.util.ClassUtils.unsafeCast;

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
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_PASTE_FILE);
        setOnAction(event -> processCopy());
    }

    /**
     * Процесс вставки файла.
     */
    private void processCopy() {

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard == null) return;

        final List<File> files = unsafeCast(clipboard.getContent(DataFormat.FILES));
        if (files == null || files.isEmpty()) return;

        final Path currentFile = element.getFile();
        final boolean isCut = "cut".equals(clipboard.getContent(EditorUtil.JAVA_PARAM));

        if (isCut) {
            files.forEach(file -> moveFile(clipboard, currentFile, file.toPath()));
        } else {
            files.forEach(file -> copyFile(clipboard, currentFile, file.toPath()));
        }
    }

    private void copyFile(final Clipboard clipboard, final Path currentFile, final Path file) {
        if (Files.isDirectory(currentFile)) {
            processCopy(clipboard, currentFile, file);
        } else {
            processCopy(clipboard, currentFile.getParent(), file);
        }
    }

    private void moveFile(final Clipboard clipboard, final Path currentFile, final Path file) {
        if (Files.isDirectory(currentFile)) {
            processMove(clipboard, currentFile, file);
        } else {
            processMove(clipboard, currentFile.getParent(), file);
        }
    }

    /**
     * Процесс перемещения файлов.
     */
    private void processMove(final Clipboard clipboard, final Path targetFolder, final Path file) {

        final Path newFile = targetFolder.resolve(file.getFileName());

        try {
            Files.move(file, newFile);
        } catch (IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        final MovedFileEvent event = new MovedFileEvent();
        event.setPrevFile(file);
        event.setNewFile(newFile);

        FX_EVENT_MANAGER.notify(event);
    }

    /**
     * Процесс копирования файлов.
     */
    private void processCopy(final Clipboard clipboard, final Path targetFolder, final Path file) {

        final Array<Path> toCopy = ArrayFactory.newArray(Path.class);
        final Array<Path> copied = ArrayFactory.newArray(Path.class);

        if (Files.isDirectory(file)) {
            toCopy.addAll(FileUtils.getFiles(file, true));
            toCopy.sort(FileUtils.FILE_PATH_LENGTH_COMPARATOR);
            toCopy.slowRemove(file);
        }

        final String freeName = FileUtils.getFirstFreeName(targetFolder, file);
        final Path newFile = targetFolder.resolve(freeName);

        try {
            processCopy(file, toCopy, copied, newFile);
        } catch (final IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
        }

        copied.forEach(path -> {

            final CreatedFileEvent createdFileEvent = new CreatedFileEvent();
            createdFileEvent.setFile(path);

            FX_EVENT_MANAGER.notify(createdFileEvent);
        });

        clipboard.clear();
    }

    /**
     * Процесс копирования файлов.
     */
    private void processCopy(final Path file, final Array<Path> toCopy, final Array<Path> copied, final Path newFile) throws IOException {

        Files.copy(file, newFile);

        copied.add(newFile);
        toCopy.forEach(path -> {

            final Path relativeFile = file.relativize(path);
            final Path targetFile = newFile.resolve(relativeFile);

            try {
                Files.copy(path, targetFile);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

            boolean needAddToCopied = true;

            for (final Path copiedFile : copied) {
                if (!Files.isDirectory(copiedFile)) continue;

                if (targetFile.startsWith(copiedFile)) {
                    needAddToCopied = false;
                    break;
                }
            }

            if (needAddToCopied) copied.add(targetFile);
        });
    }
}
