package com.ss.editor.plugin.api.extension;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The class to present an extension point.
 *
 * @author JavaSaBr
 */
public class ExtensionPoint<T> {

    /**
     * The list of extensions.
     */
    @NotNull
    private final Array<T> extensions;

    /**
     * The reference to a read only list.
     */
    @NotNull
    private final AtomicReference<List<T>> readOnlyList;

    public ExtensionPoint() {
        this.extensions = ArrayFactory.newCopyOnModifyArray(Object.class);
        this.readOnlyList = new AtomicReference<>(Collections.emptyList());
    }

    /**
     * Register a new extension.
     *
     * @param extension the new extension.
     */
    @FromAnyThread
    public void register(@NotNull T extension) {
        this.extensions.add(extension);

        var currentList = readOnlyList.get();
        var newList = List.of(extensions.array());

        while (!readOnlyList.compareAndSet(currentList, newList)) {
            currentList = readOnlyList.get();
            newList = List.of(extensions.array());
        }
    }

    /**
     * Get all registered extensions.
     *
     * @return the all registered extensions.
     */
    @FromAnyThread
    public @NotNull List<T> getExtensions() {
        return readOnlyList.get();
    }
}
