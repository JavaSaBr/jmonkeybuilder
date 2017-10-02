package com.ss.editor.ui.component.virtual.tree.resource;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.virtual.tree.VirtualResourceTree;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

/**
 * The factory to create a virtual resource elements.
 *
 * @author JavaSaBr
 */
public class VirtualResourceElementFactory {

    /**
     * Build the root virtual resource elements for the resources.
     *
     * @param resources the resources.
     * @return the root element.
     */
    @FromAnyThread
    public static <T> @NotNull RootVirtualResourceElement build(@NotNull final Array<T> resources,
                                                                @NotNull final VirtualResourceTree<T> resourceTree) {

        final ObjectDictionary<String, Array<String>> parentToChildren = DictionaryFactory.newObjectDictionary();
        final ObjectDictionary<String, T> pathToResource = DictionaryFactory.newObjectDictionary();
        resources.forEach(element -> pathToResource.put(resourceTree.getPath(element), element));

        pathToResource.forEach((key, resource) -> {

            String path = key;
            String parent = FileUtils.getParent(path, '/');

            while (!StringUtils.equals(path, parent)) {

                final Array<String> children = parentToChildren.get(parent, () -> ArrayFactory.newArray(String.class));
                if (!children.contains(path)) {
                    children.add(path);
                }

                path = parent;
                parent = FileUtils.getParent(path, '/');
            }

            final Array<String> children = parentToChildren.get("/", () -> ArrayFactory.newArray(String.class));
            if (!children.contains(path)) {
                children.add(path);
            }
        });

        final ObjectDictionary<String, VirtualResourceElement<?>> pathToResult = DictionaryFactory.newObjectDictionary();

        parentToChildren.forEach((path, children) -> {

            final T resource = pathToResource.get(path);

            if (resource == null) {
                pathToResult.put(path, new FolderVirtualResourceElement(resourceTree, path));
            } else {
                pathToResult.put(path, new ObjectVirtualResourceElement<>(resourceTree, resource));
            }
        });

        parentToChildren.forEach((path, children) -> {

            final VirtualResourceElement<?> parent = pathToResult.get(path);
            if (parent == null) return;

            for (final String child : children) {

                final T resource = pathToResource.get(child);
                VirtualResourceElement<?> element = pathToResult.get(child);

                if (element == null && resource != null) {
                    element = new ObjectVirtualResourceElement<>(resourceTree, resource);
                    pathToResult.put(child, element);
                }

                if (element != null) {
                    parent.addChild(element);
                }
            }
        });

        final RootVirtualResourceElement root = new RootVirtualResourceElement(resourceTree);
        final Array<String> rootPaths = parentToChildren.get("/");

        if (rootPaths == null) {
            return root;
        }

        rootPaths.forEach(path -> {

            final VirtualResourceElement<?> element = pathToResult.get(path);

            if (element == null) {
                return;
            }

            root.addChild(element);
        });

        return root;
    }
}
