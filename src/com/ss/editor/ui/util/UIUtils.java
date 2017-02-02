package com.ss.editor.ui.util;

import com.jme3.math.ColorRGBA;
import com.ss.editor.JFXApplication;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.UObject;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.dialog.asset.FileAssetEditorDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.util.ClassUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The utility class with utility UI methods.
 *
 * @author JavaSaBr
 */
public class UIUtils {

    /**
     * Clear children of a pane.
     *
     * @param pane the pane.
     */
    @FXThread
    public static void clear(@NotNull final Pane pane) {
        final ObservableList<Node> children = pane.getChildren();
        children.forEach(UIUtils::unbind);
        children.clear();
    }

    /**
     * Unbind a node.
     *
     * @param node the node.
     */
    @FXThread
    private static void unbind(@NotNull final Node node) {
        if (node instanceof Control) {
            final Control control = (Control) node;
            control.prefWidthProperty().unbind();
            control.prefHeightProperty().unbind();
        } else if (node instanceof Pane) {
            final Pane pane = (Pane) node;
            pane.prefHeightProperty().unbind();
            pane.prefWidthProperty().unbind();
        }
    }

    /**
     * Fill a list of components.
     */
    @FXThread
    public static void fillComponents(@NotNull final Array<ScreenComponent> container, @NotNull final Node node) {

        if (node instanceof ScreenComponent) {
            container.add((ScreenComponent) node);
        }

        if (node instanceof SplitPane) {
            final ObservableList<Node> items = ((SplitPane) node).getItems();
            items.forEach(child -> fillComponents(container, child));
        } else if (node instanceof TabPane) {
            final ObservableList<Tab> tabs = ((TabPane) node).getTabs();
            tabs.forEach(tab -> fillComponents(container, tab.getContent()));
        }

        if (!(node instanceof Parent)) {
            return;
        }

        final ObservableList<Node> nodes = ((Parent) node).getChildrenUnmodifiable();
        nodes.forEach(child -> fillComponents(container, child));
    }

    /**
     * Find all components for a type.
     */
    @NotNull
    @FXThread
    public static <T extends Node> Array<T> fillComponents(@NotNull final Node node, @NotNull final Class<T> type) {
        final Array<T> container = ArrayFactory.newArray(type);
        fillComponents(container, node, type);
        return container;
    }

    /**
     * Find all components for a type.
     */
    @FXThread
    public static <T extends Node> void fillComponents(@NotNull final Array<T> container, @NotNull final Node node,
                                                       @NotNull final Class<T> type) {

        if (type.isInstance(container)) {
            container.add(type.cast(node));
        }

        if (!(node instanceof Parent)) {
            return;
        }

        final ObservableList<Node> nodes = ((Parent) node).getChildrenUnmodifiable();
        nodes.forEach(child -> fillComponents(container, child, type));
    }

    /**
     * Get all elements of a menu.
     */
    @NotNull
    @FXThread
    public static Array<MenuItem> getAllItems(@NotNull final MenuBar menuBar) {

        final Array<MenuItem> container = ArrayFactory.newArray(MenuItem.class);

        final ObservableList<Menu> menus = menuBar.getMenus();
        menus.forEach(menu -> getAllItems(container, menu));

        return container;
    }

    /**
     * Collect all elements of a menu.
     */
    @FXThread
    private static void getAllItems(@NotNull final Array<MenuItem> container, @NotNull final MenuItem menuItem) {

        container.add(menuItem);

        if (!(menuItem instanceof Menu)) {
            return;
        }

        final ObservableList<MenuItem> items = ((Menu) menuItem).getItems();
        items.forEach(subMenuItem -> getAllItems(container, subMenuItem));
    }

    /**
     * Collect all items.
     */
    @FXThread
    public static void getAllItems(@NotNull final Array<MenuItem> container, @NotNull final MenuButton menuButton) {
        final ObservableList<MenuItem> items = menuButton.getItems();
        items.forEach(subMenuItem -> getAllItems(container, subMenuItem));
    }

    @FXThread
    public static void addTo(@NotNull final TreeItem<? super Object> item,
                             @NotNull final TreeItem<? super Object> parent) {
        final ObservableList<TreeItem<Object>> children = parent.getChildren();
        children.add(item);
    }

    /**
     * Override tooltip timeout.
     */
    public static void overrideTooltipBehavior(int openDelayInMillis, int visibleDurationInMillis,
                                               int closeDelayInMillis) {

        try {

            Class<?> tooltipBehaviourClass = null;
            Class<?>[] declaredClasses = Tooltip.class.getDeclaredClasses();

            for (Class<?> declaredClass : declaredClasses) {
                if (declaredClass.getCanonicalName().equals("javafx.scene.control.Tooltip.TooltipBehavior")) {
                    tooltipBehaviourClass = declaredClass;
                    break;
                }
            }

            if (tooltipBehaviourClass == null) {
                return;
            }

            Constructor<?> constructor = tooltipBehaviourClass.getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);

            if (constructor == null) {
                return;
            }

            constructor.setAccessible(true);

            Object tooltipBehaviour = ClassUtils.newInstance(constructor, new Duration(openDelayInMillis), new Duration(visibleDurationInMillis), new Duration(closeDelayInMillis), false);
            Field field = Tooltip.class.getDeclaredField("BEHAVIOR");

            if (field == null) {
                return;
            }

            field.setAccessible(true);

            // Cache the default behavior if needed.
            field.get(Tooltip.class);
            field.set(Tooltip.class, tooltipBehaviour);

        } catch (final Exception e) {
            System.out.println("Aborted setup due to error:" + e.getMessage());
        }
    }

    /**
     * Find a tree item by object id.
     *
     * @param treeView the tree.
     * @param objectId the object id.
     * @return the tree item or null.
     */
    @Nullable
    @FXThread
    public static <T> TreeItem<T> findItem(@NotNull final TreeView<T> treeView, final long objectId) {

        final TreeItem<T> root = treeView.getRoot();
        final T value = root.getValue();

        if (value instanceof UObject && ((UObject) value).getObjectId() == objectId) {
            return root;
        }

        final ObservableList<TreeItem<T>> children = root.getChildren();

        if (!children.isEmpty()) {
            for (final TreeItem<T> treeItem : children) {
                final TreeItem<T> result = findItem(treeItem, objectId);
                if (result != null) return result;
            }
        }

        return null;
    }

    /**
     * Find a tree item by object id.
     *
     * @param root     the root item.
     * @param objectId the object id.
     * @return the tree item or null.
     */
    @Nullable
    @FXThread
    public static <T> TreeItem<T> findItem(@NotNull final TreeItem<T> root, final long objectId) {

        final T value = root.getValue();

        if (value instanceof UObject && ((UObject) value).getObjectId() == objectId) {
            return root;
        }

        final ObservableList<TreeItem<T>> children = root.getChildren();

        if (!children.isEmpty()) {
            for (final TreeItem<T> treeItem : children) {
                final TreeItem<T> result = findItem(treeItem, objectId);
                if (result != null) return result;
            }
        }

        return null;
    }

    /**
     * Find a tree item by its value.
     *
     * @param treeView the tree view.
     * @param object   the value.
     * @return the tree item or null.
     */
    @Nullable
    @FXThread
    public static <T> TreeItem<T> findItemForValue(@NotNull final TreeView<T> treeView, @Nullable final Object object) {
        if (object == null) return null;

        final TreeItem<T> root = treeView.getRoot();
        if (root.getValue().equals(object)) return root;

        final ObservableList<TreeItem<T>> children = root.getChildren();

        if (!children.isEmpty()) {
            for (final TreeItem<T> treeItem : children) {
                final TreeItem<T> result = findItemForValue(treeItem, object);
                if (result != null) return result;
            }
        }

        return null;
    }

    /**
     * Find a tree item by its value.
     *
     * @param root   the root item.
     * @param object the value.
     * @return the tree item or null.
     */
    @Nullable
    @FXThread
    public static <T> TreeItem<T> findItemForValue(@NotNull final TreeItem<T> root, @Nullable final Object object) {

        if (Objects.equals(root.getValue(), object)) {
            return root;
        }

        final ObservableList<TreeItem<T>> children = root.getChildren();

        if (!children.isEmpty()) {
            for (final TreeItem<T> treeItem : children) {
                final TreeItem<T> result = findItemForValue(treeItem, object);
                if (result != null) return result;
            }
        }

        return null;
    }

    /**
     * Get all elements of a tree view.
     *
     * @param treeView the tree view.
     * @return the list of all items.
     */
    @NotNull
    @FXThread
    public static <T> Array<TreeItem<T>> getAllItems(@NotNull final TreeView<T> treeView) {

        final Array<TreeItem<T>> container = ArrayFactory.newArray(TreeItem.class);

        final TreeItem<T> root = treeView.getRoot();
        final ObservableList<TreeItem<T>> children = root.getChildren();

        for (final TreeItem<T> child : children) {
            getAllItems(container, child);
        }

        return container;
    }

    /**
     * Collect all elements of a tree item.
     *
     * @param container the container.
     * @param root      the tree item.
     */
    @FXThread
    public static <T> void getAllItems(final Array<TreeItem<T>> container, final TreeItem<T> root) {

        container.add(root);

        final ObservableList<TreeItem<T>> children = root.getChildren();

        for (final TreeItem<T> child : children) {
            getAllItems(container, child);
        }
    }

    /**
     * Convert a color from {@link Color} to {@link ColorRGBA}.
     */
    @NotNull
    @FXThread
    public static ColorRGBA convertColor(@NotNull final Color newValue) {
        return new ColorRGBA((float) newValue.getRed(), (float) newValue.getGreen(), (float) newValue.getBlue(), (float) newValue.getOpacity());
    }

    /**
     * Consume an event if the event is not hotkey.
     *
     * @param event the event.
     */
    @FXThread
    public static void consumeIfIsNotHotKey(@Nullable final KeyEvent event) {
        if (event == null || event.isControlDown() || event.isShiftDown()) {
            return;
        }
        event.consume();
    }

    /**
     * Update an edited cell.
     *
     * @param cell the edited cell.
     */
    @FXThread
    public static void updateEditedCell(final Labeled cell) {

        final javafx.scene.Node graphic = cell.getGraphic();

        if (graphic instanceof HBox) {
            final HBox hbox = (HBox) graphic;
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setMinHeight(cell.getMinHeight());
        } else if (graphic instanceof Control) {
            ((Control) graphic).setMinHeight(cell.getMinHeight());
        }
    }

    /**
     * Open an asset dialog.
     *
     * @param handler      the result handler.
     * @param extensions   the extensions list.
     * @param actionTester the action tester.
     */
    @FXThread
    public static void openAssetDialog(@NotNull final Consumer<Path> handler, @NotNull final Array<String> extensions,
                                       @Nullable final Predicate<Class<?>> actionTester) {

        final JFXApplication jfxApplication = JFXApplication.getInstance();
        final EditorFXScene scene = jfxApplication.getScene();

        final AssetEditorDialog dialog = new FileAssetEditorDialog(handler);
        dialog.setExtensionFilter(extensions);
        dialog.setActionTester(actionTester);
        dialog.show(scene.getWindow());
    }

    private UIUtils() {
        throw new RuntimeException();
    }
}
