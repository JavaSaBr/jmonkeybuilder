package com.ss.builder.ui.component.asset.tree.resource;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayCollectors;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;
import static com.ss.rlib.common.util.array.ArrayCollectors.toArray;

/**
 * The presentation of list of folders.
 *
 * @author JavaSaBr
 */
public class FoldersResourceElement extends ResourceElement {

    /**
     * The list of folders.
     */
    @NotNull
    private final Array<Path> folders;

    public FoldersResourceElement(@NotNull final Array<Path> folders) {
        super(folders.first());
        this.folders = folders;
    }

    @Override
    @FromAnyThread
    public @Nullable Array<ResourceElement> getChildren(@NotNull final Array<String> extensionFilter, final boolean onlyFolders) {
        return folders.stream().map(ResourceElementFactory::createFor)
                .collect(toArray(ResourceElement.class));
    }

    @Override
    @FromAnyThread
    public boolean hasChildren(@NotNull final Array<String> extensionFilter, final boolean onlyFolders) {
        return !folders.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FoldersResourceElement that = (FoldersResourceElement) o;
        return Objects.equals(folders, that.folders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), folders);
    }

    @Override
    public String toString() {
        return "FoldersResourceElement";
    }
}
