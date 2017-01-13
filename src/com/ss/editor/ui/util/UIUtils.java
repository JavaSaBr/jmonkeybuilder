package com.ss.editor.ui.util;

import com.jme3.math.ColorRGBA;
import com.ss.editor.model.UObject;
import com.ss.editor.ui.component.ScreenComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Objects;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import rlib.util.ClassUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The utility class with utility UI methods.
 *
 * @author JavaSaBr
 */
public class UIUtils {

    public static void clear(final Pane node) {
        final ObservableList<Node> children = node.getChildren();
        children.forEach(UIUtils::unbind);
        children.clear();
    }

    public static void unbind(final Node node) {
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
     * Поиск всех компонентов экрана.
     */
    public static void fillComponents(final Array<ScreenComponent> container, final Node node) {

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
     * Поиск всех компонентов экрана.
     */
    public static <T extends Node> Array<T> fillComponents(final Node node, final Class<T> type) {
        final Array<T> container = ArrayFactory.newArray(type);
        fillComponents(container, node, type);
        return container;
    }

    /**
     * Поиск всех компонентов.
     */
    public static <T extends Node> void fillComponents(final Array<T> container, final Node node, final Class<T> type) {

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
     * Поиск всех элементов меню.
     */
    public static Array<MenuItem> getAllItems(final MenuBar menuBar) {

        final Array<MenuItem> container = ArrayFactory.newArray(MenuItem.class);

        final ObservableList<Menu> menus = menuBar.getMenus();
        menus.forEach(menu -> getAllItems(container, menu));

        return container;
    }

    /**
     * Поиск всех элементов меню.
     */
    public static void getAllItems(final Array<MenuItem> container, final MenuItem menuItem) {

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
    public static void getAllItems(final Array<MenuItem> container, final MenuButton menuButton) {
        final ObservableList<MenuItem> items = menuButton.getItems();
        items.forEach(subMenuItem -> getAllItems(container, subMenuItem));
    }


    public static void addTo(TreeItem<? super Object> item, TreeItem<? super Object> parent) {
        final ObservableList<TreeItem<Object>> children = parent.getChildren();
        children.add(item);
    }

    /**
     * Override tooltip timeout.
     */
    public static void overrideTooltipBehavior(int openDelayInMillis, int visibleDurationInMillis, int closeDelayInMillis) {

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

            if (tooltipBehaviour == null) {
                return;
            }

            Field field = Tooltip.class.getDeclaredField("BEHAVIOR");

            if (field == null) {
                return;
            }

            field.setAccessible(true);

            // Cache the default behavior if needed.
            field.get(Tooltip.class);
            field.set(Tooltip.class, tooltipBehaviour);

        } catch (Exception e) {
            System.out.println("Aborted setup due to error:" + e.getMessage());
        }
    }

    /**
     * Поиск элемента дерево по уникальному ид.
     */
    public static <T> TreeItem<T> findItem(final TreeView<T> treeView, final long objectId) {

        final TreeItem<T> root = treeView.getRoot();
        final T value = root.getValue();

        if (value instanceof UObject && ((UObject) value).getObjectId() == objectId) {
            return root;
        }

        final ObservableList<TreeItem<T>> children = root.getChildren();

        if (!children.isEmpty()) {

            for (TreeItem<T> treeItem : children) {

                final TreeItem<T> result = findItem(treeItem, objectId);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    /**
     * Поиск элемента.
     */
    public static <T> TreeItem<T> findItem(final TreeItem<T> root, final long objectId) {

        final T value = root.getValue();

        if (value instanceof UObject && ((UObject) value).getObjectId() == objectId) {
            return root;
        }

        final ObservableList<TreeItem<T>> children = root.getChildren();

        if (!children.isEmpty()) {

            for (TreeItem<T> treeItem : children) {

                final TreeItem<T> result = findItem(treeItem, objectId);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    /**
     * Поиск элемента дерева, содержашего указанное значение.
     */
    @Nullable
    public static <T> TreeItem<T> findItemForValue(@NotNull final TreeView<T> treeView, @NotNull final Object object) {

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
     * Поиск элемента дерева, содержашего указанное значение.
     */
    public static <T> TreeItem<T> findItemForValue(final TreeItem<T> root, final Object object) {

        if (Objects.equals(root.getValue(), object)) {
            return root;
        }

        final ObservableList<TreeItem<T>> children = root.getChildren();

        if (!children.isEmpty()) {

            for (TreeItem<T> treeItem : children) {

                final TreeItem<T> result = findItemForValue(treeItem, object);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    /**
     * Получение всех элементов дерева.
     */
    public static <T> Array<TreeItem<T>> getAllItems(final TreeView<T> treeView) {

        final Array<TreeItem<T>> container = ArrayFactory.newArray(TreeItem.class);

        final TreeItem<T> root = treeView.getRoot();
        final ObservableList<TreeItem<T>> children = root.getChildren();

        for (final TreeItem<T> child : children) {
            getAllItems(container, child);
        }

        return container;
    }

    public static <T> void getAllItems(final Array<TreeItem<T>> container, final TreeItem<T> root) {

        container.add(root);

        final ObservableList<TreeItem<T>> children = root.getChildren();

        for (final TreeItem<T> child : children) {
            getAllItems(container, child);
        }
    }

    /**
     * Конвертирование Color в ColorRGBA.
     */
    public static ColorRGBA convertColor(final Color newValue) {
        return new ColorRGBA((float) newValue.getRed(), (float) newValue.getGreen(), (float) newValue.getBlue(), (float) newValue.getOpacity());
    }

    private UIUtils() {
        throw new RuntimeException();
    }
}
