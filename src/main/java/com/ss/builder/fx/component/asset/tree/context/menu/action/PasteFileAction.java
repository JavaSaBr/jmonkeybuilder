package com.ss.builder.fx.component.asset.tree.context.menu.action;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.component.asset.tree.resource.ResourceElement;
import com.ss.builder.fx.event.FxEventManager;
import com.ss.builder.fx.event.impl.MovedFileEvent;
import com.ss.builder.fx.event.impl.RequestSelectFileEvent;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.component.asset.tree.resource.ResourceElement;
import com.ss.builder.fx.event.FxEventManager;
import com.ss.builder.fx.event.impl.MovedFileEvent;
import com.ss.builder.fx.event.impl.RequestSelectFileEvent;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.array.Array;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * The action to paste a file.
 *
 * @author JavaSaBr
 */
public class PasteFileAction extends FileAction {

    @FxThread
    public static void applyFor(@NotNull ResourceElement element) {
        new PasteFileAction(element).getOnAction().handle(null);
    }

    public PasteFileAction(@NotNull ResourceElement element) {
        super(element);
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_PASTE_FILE;
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.PASTE_16;
    }

    @Override
    @FxThread
    protected void execute(@Nullable ActionEvent event) {
        super.execute(event);

        var clipboard = Clipboard.getSystemClipboard();
        if (clipboard == null) {
            return;
        }

        var files = ClassUtils.<List<File>>unsafeCast(clipboard.getContent(DataFormat.FILES));
        if (files == null || files.isEmpty()) {
            return;
        }

        var currentFile = getElement().getFile();
        var isCut = "cut".equals(clipboard.getContent(EditorUtils.JAVA_PARAM));

        if (isCut) {
            files.forEach(file -> moveFile(currentFile, file.toPath()));
        } else {
            files.forEach(file -> copyFile(currentFile, file.toPath()));
        }

        clipboard.clear();
    }

    private void copyFile(@NotNull Path currentFile, @NotNull Path file) {
        if (Files.isDirectory(currentFile)) {
            processCopy(currentFile, file);
        } else {
            processCopy(currentFile.getParent(), file);
        }
    }

    private void moveFile(@NotNull Path currentFile, @NotNull Path file) {
        if (Files.isDirectory(currentFile)) {
            processMove(currentFile, file);
        } else {
            processMove(currentFile.getParent(), file);
        }
    }

    /**
     * Process of moving.
     */
    private void processMove(@NotNull Path targetFolder, @NotNull Path file) {

        var newFile = targetFolder.resolve(file.getFileName());

        try {
            Files.move(file, newFile);
        } catch (IOException e) {
            EditorUtils.handleException(LOGGER, this, e);
            return;
        }

        FxEventManager.getInstance()
                .notify(new MovedFileEvent(file, newFile));
    }

    /**
     * Process of copying.
     */
    private void processCopy(@NotNull Path targetFolder, @NotNull Path file) {

        var toCopy = Array.ofType(Path.class);
        var copied = Array.ofType(Path.class);

        if (Files.isDirectory(file)) {
            toCopy.addAll(FileUtils.getFiles(file, true));
            toCopy.sort(FileUtils.FILE_PATH_LENGTH_COMPARATOR);
            toCopy.slowRemove(file);
        }

        var freeName = FileUtils.getFirstFreeName(targetFolder, file);
        var newFile = targetFolder.resolve(freeName);

        try {
            processCopy(file, toCopy, copied, newFile);
        } catch (IOException e) {
            EditorUtils.handleException(LOGGER, this, e);
            return;
        }

        FxEventManager.getInstance()
                .notify(new RequestSelectFileEvent(newFile));
    }

    /**
     * Process of copying.
     */
    private void processCopy(
            @NotNull Path file,
            @NotNull Array<Path> toCopy,
            @NotNull Array<Path> copied,
            @NotNull Path newFile
    ) throws IOException {

        Files.copy(file, newFile);

        copied.add(newFile);
        toCopy.forEach(path -> {

            var relativeFile = file.relativize(path);
            var targetFile = newFile.resolve(relativeFile);

            try {
                Files.copy(path, targetFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            var needAddToCopied = true;

            for (var copiedFile : copied) {

                if (!Files.isDirectory(copiedFile)) {
                    continue;
                }

                if (targetFile.startsWith(copiedFile)) {
                    needAddToCopied = false;
                    break;
                }
            }

            if (needAddToCopied) {
                copied.add(targetFile);
            }
        });
    }
}
