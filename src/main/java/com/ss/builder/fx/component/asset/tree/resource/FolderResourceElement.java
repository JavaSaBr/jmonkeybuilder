package com.ss.builder.ui.component.asset.tree.resource;

import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The presentation of a folder.
 *
 * @author JavaSaBr
 */
public class FolderResourceElement extends ResourceElement {

    public FolderResourceElement(@NotNull Path file) {
        super(file);
    }

    @Override
    @FromAnyThread
    public @Nullable Array<ResourceElement> getChildren(
            @NotNull Array<String> extensionFilter,
            boolean onlyFolders
    ) {

        if (!Files.isDirectory(file)) {
            return null;
        }

        final Array<ResourceElement> elements = ArrayFactory.newArray(ResourceElement.class);

        try (var stream = Files.newDirectoryStream(file)) {
            for (Path child : stream) {

                var fileName = child.getFileName().toString();

                if (fileName.startsWith(".")) {
                    continue;
                } else if (Files.isDirectory(child)) {
                    elements.add(ResourceElementFactory.createFor(child));
                    continue;
                }

                if (onlyFolders) {
                    continue;
                }

                var extension = FileUtils.getExtension(child);

                if (extensionFilter.isEmpty() || extensionFilter.contains(extension)) {
                    elements.add(ResourceElementFactory.createFor(child));
                }
            }

        } catch (IOException e) {
            LOGGER.warning(this, e);
        }

        return elements;
    }

    @Override
    @FromAnyThread
    public boolean hasChildren(@NotNull Array<String> extensionFilter, boolean onlyFolders) {

        if (!Files.isDirectory(file)) {
            return false;
        }

        try (var stream = Files.newDirectoryStream(file)) {
            for (var path : stream) {

                var fileName = path.getFileName().toString();

                if (fileName.startsWith(".")) {
                    continue;
                } else if (Files.isDirectory(path)) {
                    return true;
                }

                if (onlyFolders) {
                    continue;
                }

                var extension = FileUtils.getExtension(path);

                if (extensionFilter.isEmpty() || extensionFilter.contains(extension)) {
                    return true;
                }
            }

        } catch (AccessDeniedException e) {
            return false;
        } catch (IOException e) {
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
