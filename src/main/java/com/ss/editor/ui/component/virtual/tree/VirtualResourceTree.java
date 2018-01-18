package com.ss.editor.ui.component.virtual.tree;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.component.virtual.tree.resource.FolderVirtualResourceElement;
import com.ss.editor.ui.component.virtual.tree.resource.VirtualResourceElement;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayComparator;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * THe implementation of a tree with resources of an asset folder.
 *
 * @author JavaSaBr
 */
public class VirtualResourceTree<T> extends TreeView<VirtualResourceElement<?>> {

    @NotNull
    private static final ArrayComparator<VirtualResourceElement<?>> NAME_COMPARATOR = (first, second) -> {

        final int firstLevel = getLevel(first);
        final int secondLevel = getLevel(second);

        if (firstLevel != secondLevel) return firstLevel - secondLevel;

        final String firstName = notNull(first).getName();
        final String secondName = notNull(second).getName();

        return StringUtils.compareIgnoreCase(firstName, secondName);
    };

    private static int getLevel(@Nullable final VirtualResourceElement element) {
        if (element instanceof FolderVirtualResourceElement) return 1;
        return 2;
    }

    /**
     * The target objects type.
     */
    @NotNull
    private final Class<T> objectsType;

    /**
     * The open resource function.
     */
    @Nullable
    private Consumer<VirtualResourceElement<T>> openFunction;

    /**
     * The function to get an asset path of the resource element.
     */
    @Nullable
    private Function<T, String> pathFunction;

    /**
     * The function to get a name of the resource element.
     */
    @Nullable
    private Function<T, String> nameFunction;

    public VirtualResourceTree(@NotNull final Class<T> objectsType) {
        this.objectsType = objectsType;

        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setFixedCellSize(FXConstants.RESOURCE_TREE_CELL_HEIGHT);
        setCellFactory(param -> new VirtualResourceTreeCell());
        setShowRoot(false);
        setFocusTraversable(true);
    }

    /**
     * The type of target objects.
     *
     * @return the target type.
     */
    @FromAnyThread
    private @NotNull Class<T> getObjectsType() {
        return objectsType;
    }

    /**
     * Get the function to get an asset path of the resource element.
     *
     * @return the function to get an asset path of the resource element.
     */
    @FromAnyThread
    private @Nullable Function<T, String> getPathFunction() {
        return pathFunction;
    }

    /**
     * Set the function to get an asset path of the resource element.
     *
     * @param pathFunction the function to get an asset path of the resource element.
     */
    @FromAnyThread
    public void setPathFunction(final Function<T, String> pathFunction) {
        this.pathFunction = pathFunction;
    }

    /**
     * Get the function to get a name of the resource element.
     *
     * @return the function to get a name of the resource element.
     */
    @FromAnyThread
    private @Nullable Function<T, String> getNameFunction() {
        return nameFunction;
    }

    /**
     * Get the path of the object.
     *
     * @param object the object.
     * @return the path.
     */
    @FromAnyThread
    public @NotNull String getPath(@NotNull final Object object) {

        if (object instanceof String) {
            return object.toString();
        } else if (!getObjectsType().isInstance(object)) {
            throw new RuntimeException("Unknown type of the object " + object);
        }

        final Function<T, String> pathFunction = getPathFunction();

        if (pathFunction != null) {
            return pathFunction.apply(getObjectsType().cast(object));
        }

        throw new RuntimeException("Unknown type of the object " + object);
    }

    /**
     * Get the name of the object.
     *
     * @param object the object.
     * @return the name.
     */
    @FromAnyThread
    public @NotNull String getName(@NotNull final Object object) {

        if (object instanceof String) {
            final String path = object.toString();
            return FileUtils.getName(path, '/');
        } else if (!getObjectsType().isInstance(object)) {
            throw new RuntimeException("Unknown type of the object " + object);
        }

        final Function<T, String> nameFunction = getNameFunction();

        if (nameFunction != null) {
            return nameFunction.apply(getObjectsType().cast(object));
        }

        throw new RuntimeException("Unknown type of the object " + object);
    }

    /**
     * Fill the tree by the root element.
     *
     * @param element the root element.
     */
    @FxThread
    public void fill(@NotNull final VirtualResourceElement<?> element) {

        final TreeItem<VirtualResourceElement<?>> newRoot = new TreeItem<>(element);
        newRoot.setExpanded(true);

        fill(newRoot);
        setRoot(newRoot);
    }

    /**
     * Expand all nodes.
     */
    @FxThread
    public void expandAll() {
        UiUtils.visit(getRoot(), item -> item.setExpanded(true));
    }

    /**
     * Fill the tree item.
     *
     * @param item the tree item.
     */
    @FxThread
    private void fill(@NotNull final TreeItem<VirtualResourceElement<?>> item) {

        final VirtualResourceElement<?> element = item.getValue();
        if(!element.hasChildren()) {
            return;
        }

        final ObservableList<TreeItem<VirtualResourceElement<?>>> items = item.getChildren();

        final Array<VirtualResourceElement<?>> children = element.getChildren();
        children.sort(NAME_COMPARATOR);
        children.forEach(child -> items.add(new TreeItem<>(child)));

        items.forEach(this::fill);
    }

    /**
     * Get open function.
     *
     * @return the open resource function.
     */
    @FromAnyThread
    protected @Nullable Consumer<VirtualResourceElement<T>> getOpenFunction() {
        return openFunction;
    }

    /**
     * Set open function.
     *
     * @param openFunction the open resource function.
     */
    @FromAnyThread
    public void setOpenFunction(@Nullable final Consumer<VirtualResourceElement<T>> openFunction) {
        this.openFunction = openFunction;
    }
}
