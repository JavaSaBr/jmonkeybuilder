package com.ss.editor.ui.component.asset.tree.resource;

import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The presentation of a folder.
 *
 * @author JavaSaBr
 */
public class FolderResourceElement extends ResourceElement {

    public FolderResourceElement(@NotNull final Path file) {
        super(file);
    }

    @Override
    @FromAnyThread
    public @Nullable Array<ResourceElement> getChildren(@NotNull final Array<String> extensionFilter, final boolean onlyFolders) {
        if (!Files.isDirectory(file)) return null;

        final Array<ResourceElement> elements = ArrayFactory.newArray(ResourceElement.class);

        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(file)) {
            stream.forEach(child -> {

                final String fileName = child.getFileName().toString();

                if (fileName.startsWith(".")) {
                    return;
                } else if (Files.isDirectory(child)) {
                    elements.add(createFor(child));
                    return;
                }

                if (onlyFolders) return;

                final String extension = FileUtils.getExtension(child);

                if (extensionFilter.isEmpty() || extensionFilter.contains(extension)) {
                    elements.add(createFor(child));
                }
            });

        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        return elements;
    }

    @Override
    @FromAnyThread
    public boolean hasChildren(@NotNull final Array<String> extensionFilter, final boolean onlyFolders) {
        if (!Files.isDirectory(file)) return false;

        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(file)) {
            for (final Path path : stream) {

                final String fileName = path.getFileName().toString();

                if (fileName.startsWith(".")) {
                    continue;
                } else if (Files.isDirectory(path)) {
                    return true;
                }

                if (onlyFolders) continue;

                final String extension = FileUtils.getExtension(path);

                if (extensionFilter.isEmpty() || extensionFilter.contains(extension)) {
                    return true;
                }
            }

        } catch (final AccessDeniedException e) {
            return false;
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        return false;
    }

    @Override
    public String toString() {
        return "FolderResourceElement{" +
                "file=" + file +
                '}';
    }
}
