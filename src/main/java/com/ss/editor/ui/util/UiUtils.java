package com.ss.editor.ui.util;

import static com.ss.editor.util.EditorUtil.getFxScene;
import static com.ss.rlib.util.ClassUtils.unsafeCast;
import static com.ss.rlib.util.ReflectionUtils.getStaticField;
import static java.lang.Math.min;
import com.jme3.math.ColorRGBA;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.UObject;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.dialog.asset.file.AssetEditorDialog;
import com.ss.editor.ui.dialog.asset.file.FileAssetEditorDialog;
import com.ss.editor.ui.dialog.asset.file.FolderAssetEditorDialog;
import com.ss.editor.ui.dialog.asset.virtual.StringVirtualAssetEditorDialog;
import com.ss.editor.ui.dialog.save.SaveAsEditorDialog;
import com.ss.rlib.util.ClassUtils;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactfx.util.TriConsumer;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The utility class with utility UI methods.
 *
 * @author JavaSaBr
 */
public abstract class UiUtils {

    @NotNull
    private static final PseudoClass FOCUSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("focused");

    /**
     * Add binding pseudo focus of the pane to focus state of the controls.
     *
     * @param pane   the pane.
     * @param controls  the controls.
     */
    @FxThread
    public static void addFocusBinding(@NotNull final Pane pane, @NotNull final Control... controls) {

        final BooleanProperty focused = new BooleanPropertyBase(false) {

            @Override
            public void invalidated() {
                pane.pseudoClassStateChanged(FOCUSED_PSEUDO_CLASS, get());
            }

            @Override
            public Object getBean() {
                return pane;
            }

            @Override
            public String getName() {
                return "focused";
            }
        };

        final ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> {

            boolean result = newValue;

            if (!result) {
                for (final Control control : controls) {
                    result = control.isFocused();
                    if (result) break;
                }
            }

            focused.setValue(result);
        };

        for (final Control control : controls) {
            control.focusedProperty().addListener(listener);
        }
    }

    /**
     * Clear children of a pane.
     *
     * @param pane the pane.
     */
    @FxThread
    public static void clear(@NotNull final Pane pane) {
        final ObservableList<Node> children = pane.getChildren();
        children.forEach(UiUtils::unbind);
        children.clear();
    }

    /**
     * Unbind a node.
     *
     * @param node the node.
     */
    @FxThread
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
     *
     * @param container the container
     * @param node      the node
     */
    @FxThread
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
     *
     * @param <T>  the type parameter
     * @param node the node
     * @param type the type
     * @return the array
     */
    @FxThread
    public static @NotNull <T extends Node> Array<T> fillComponents(@NotNull final Node node, @NotNull final Class<T> type) {
        final Array<T> container = ArrayFactory.newArray(type);
        fillComponents(container, node, type);
        return container;
    }

    /**
     * Find all components for a type.
     *
     * @param <T>       the type parameter
     * @param container the container
     * @param node      the node
     * @param type      the type
     */
    @FxThread
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
     *
     * @param menuBar the menu bar
     * @return the all items
     */
    @FxThread
    public static @NotNull Array<MenuItem> getAllItems(@NotNull final MenuBar menuBar) {

        final Array<MenuItem> container = ArrayFactory.newArray(MenuItem.class);

        final ObservableList<Menu> menus = menuBar.getMenus();
        menus.forEach(menu -> getAllItems(container, menu));

        return container;
    }

    /**
     * Collect all elements of a menu.
     */
    @FxThread
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
     *
     * @param container  the container
     * @param menuButton the menu button
     */
    @FxThread
    public static void getAllItems(@NotNull final Array<MenuItem> container, @NotNull final MenuButton menuButton) {
        final ObservableList<MenuItem> items = menuButton.getItems();
        items.forEach(subMenuItem -> getAllItems(container, subMenuItem));
    }

    /**
     * Add to.
     *
     * @param item   the item
     * @param parent the parent
     */
    @FxThread
    public static void addTo(@NotNull final TreeItem<? super Object> item,
                             @NotNull final TreeItem<? super Object> parent) {
        final ObservableList<TreeItem<Object>> children = parent.getChildren();
        children.add(item);
    }

    /**
     * Override tooltip timeout.
     *
     * @param openDelayInMillis       the open delay in millis
     * @param visibleDurationInMillis the visible duration in millis
     * @param closeDelayInMillis      the close delay in millis
     */
    public static void overrideTooltipBehavior(int openDelayInMillis, int visibleDurationInMillis,
                                               int closeDelayInMillis) {
        try {

            // fix auto hiding tooltips
            final Field xOffsetField = getStaticField(Tooltip.class, "TOOLTIP_XOFFSET");
            xOffsetField.setAccessible(true);
            xOffsetField.set(null, 14);

            final Field yOffsetField = getStaticField(Tooltip.class, "TOOLTIP_YOFFSET");
            yOffsetField.setAccessible(true);
            yOffsetField.set(null, 14);

            final Class<?> tooltipBehaviourClass = Arrays.stream(Tooltip.class.getDeclaredClasses())
                    .filter(type -> type.getCanonicalName().equals(Tooltip.class.getName() + ".TooltipBehavior"))
                    .findAny().orElse(null);

            if (tooltipBehaviourClass == null) {
                return;
            }

            final Constructor<?> constructor = tooltipBehaviourClass.
                    getDeclaredConstructor(Duration.class, Duration.class, Duration.class, boolean.class);
            constructor.setAccessible(true);

            final Object tooltipBehaviour = ClassUtils.newInstance(constructor, new Duration(openDelayInMillis),
                    new Duration(visibleDurationInMillis), new Duration(closeDelayInMillis), false);

            final Field field = getStaticField(Tooltip.class, "BEHAVIOR");
            field.setAccessible(true);
            field.get(Tooltip.class);
            field.set(Tooltip.class, tooltipBehaviour);

        } catch (final Exception e) {
            System.out.println("Aborted setup due to error:" + e.getMessage());
        }
    }

    /**
     * Find a tree item by object id.
     *
     * @param <T>      the type parameter
     * @param treeView the tree.
     * @param objectId the object id.
     * @return the tree item or null.
     */
    @FxThread
    public static @Nullable <T> TreeItem<T> findItem(@NotNull final TreeView<T> treeView, final long objectId) {
        return findItem(treeView.getRoot(), objectId);
    }

    /**
     * Find a tree item by object id.
     *
     * @param <T>      the type parameter
     * @param root     the root item.
     * @param objectId the object id.
     * @return the tree item or null.
     */
    @FxThread
    public static @Nullable <T> TreeItem<T> findItem(@NotNull final TreeItem<T> root, final long objectId) {

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
     * @param <T>      the type parameter
     * @param treeView the tree view.
     * @param object   the value.
     * @return the tree item or null.
     */
    @FxThread
    public static @Nullable <T> TreeItem<T> findItemForValue(@NotNull final TreeView<T> treeView, @Nullable final Object object) {
        return findItemForValue(treeView.getRoot(), object);
    }

    /**
     * Find a tree item by its value.
     *
     * @param <T>    the type parameter
     * @param root   the root item.
     * @param object the value.
     * @return the tree item or null.
     */
    @FxThread
    public @Nullable static <T> TreeItem<T> findItemForValue(@NotNull final TreeItem<T> root, @Nullable final Object object) {

        if (object == null) {
            return null;
        } else if (Objects.equals(root.getValue(), object)) {
            return root;
        }

        final ObservableList<TreeItem<T>> children = root.getChildren();

        if (!children.isEmpty()) {
            for (final TreeItem<T> treeItem : children) {
                final TreeItem<T> result = findItemForValue(treeItem, object);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    /**
     * Get all elements of a tree view.
     *
     * @param <T>      the type parameter
     * @param treeView the tree view.
     * @return the list of all items.
     */
    @FxThread
    public static @NotNull <T> Array<TreeItem<T>> getAllItems(@NotNull final TreeView<T> treeView) {

        final Array<TreeItem<T>> container = ArrayFactory.newArray(TreeItem.class);

        final TreeItem<T> root = treeView.getRoot();
        final ObservableList<TreeItem<T>> children = root.getChildren();

        for (final TreeItem<T> child : children) {
            collectAllItems(container, child);
        }

        return container;
    }

    /**
     * Visit all items.
     *
     * @param <T>     the type parameter.
     * @param item    the tree item.
     * @param visitor the visitor.
     */
    @FxThread
    public static <T> void visit(@NotNull final TreeItem<T> item, @NotNull final Consumer<TreeItem<T>> visitor) {
        visitor.accept(item);

        final ObservableList<TreeItem<T>> children = item.getChildren();
        if (children.isEmpty()) return;

        for (final TreeItem<T> child : children) {
            visit(child, visitor);
        }
    }

    /**
     * Visit all items.
     *
     * @param <T>     the type parameter.
     * @param item    the tree item.
     * @param visitor the visitor.
     * @return true of we can visit child elements.
     */
    @FxThread
    public static <T> boolean visitUntil(@NotNull final TreeItem<T> item,
                                         @NotNull final Predicate<TreeItem<T>> visitor) {

        if (!visitor.test(item)) return false;

        final ObservableList<TreeItem<T>> children = item.getChildren();
        if (children.isEmpty()) return true;

        for (final TreeItem<T> child : children) {
            if (!visitUntil(child, visitor)) return false;
        }

        return true;
    }

    /**
     * Collect all elements of tree items.
     *
     * @param <T>  the type parameter
     * @param root the tree item.
     * @return the list with all items.
     */
    @FxThread
    public static <T> Array<TreeItem<T>> getAllItems(@NotNull final TreeItem<T> root) {
        final Array<TreeItem<T>> container = ArrayFactory.newArray(TreeItem.class);
        collectAllItems(container, root);
        return container;
    }

    /**
     * Collect all elements of tree items.
     *
     * @param <T>       the type parameter
     * @param container the container.
     * @param root      the tree item.
     */
    @FxThread
    public static <T> void collectAllItems(@NotNull final Array<TreeItem<T>> container, @NotNull final TreeItem<T> root) {

        container.add(root);

        final ObservableList<TreeItem<T>> children = root.getChildren();

        for (final TreeItem<T> child : children) {
            collectAllItems(container, child);
        }
    }

    /**
     * Convert a color from {@link Color} to {@link ColorRGBA}.
     *
     * @param color the color
     * @return the jme color
     */
    @FxThread
    public static @Nullable ColorRGBA from(@Nullable final Color color) {
        if (color == null) return null;
        return new ColorRGBA((float) color.getRed(), (float) color.getGreen(),
                (float) color.getBlue(), (float) color.getOpacity());
    }

    /**
     * Convert a color from {@link ColorRGBA} to {@link Color}.
     *
     * @param color the color
     * @return the FX color
     */
    @FxThread
    public static @Nullable Color from(@Nullable final ColorRGBA color) {
        if (color == null) return null;

        final float red = min(color.getRed(), 1F);
        final float green = min(color.getGreen(), 1F);
        final float blue = min(color.getBlue(), 1F);
        final float alpha = min(color.getAlpha(), 1F);

        return new Color(red, green, blue, alpha);
    }

    /**
     * @param event the event.
     * @return true if the event is not hotkey.
     */
    @FxThread
    public static boolean isNotHotKey(@Nullable final KeyEvent event) {
        if (event == null) return false;

        final String text = event.getText();
        if (text.isEmpty()) return false;

        final KeyCode code = event.getCode();
        final EventTarget target = event.getTarget();

        if (code == KeyCode.TAB && !(target instanceof TextInputControl)) {
            return false;
        }

        if (event.isControlDown()) {
            return false;
        } else return !event.isShiftDown();
    }

    /**
     * Consume an event if the condition returns true.
     *
     * @param event     the event.
     * @param condition the condition.
     */
    @FxThread
    public static void consumeIf(@NotNull final KeyEvent event, @NotNull final Predicate<KeyEvent> condition) {
        if (condition.test(event)) {
            event.consume();
        }
    }

    /**
     * Consume an event if the event is not hotkey.
     *
     * @param event the event.
     */
    @FxThread
    public static void consumeIfIsNotHotKey(@Nullable final KeyEvent event) {

        if (event == null) {
            return;
        }

        final KeyCode code = event.getCode();
        if (code == KeyCode.ESCAPE || code == KeyCode.ENTER) {
            return;
        }

        if (isNotHotKey(event)) {
            event.consume();
        }
    }

    /**
     * Update an edited cell.
     *
     * @param cell the edited cell.
     */
    @FxThread
    public static void updateEditedCell(final Labeled cell) {

        final javafx.scene.Node graphic = cell.getGraphic();

        if (graphic instanceof HBox) {
            final HBox hbox = (HBox) graphic;
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setMinHeight(cell.getMinHeight());
        }
    }

    /**
     * Open an resource asset dialog.
     *
     * @param handler   the result handler.
     * @param resources the resources.
     */
    @FxThread
    public static void openResourceAssetDialog(@NotNull final Consumer<String> handler,
                                               @NotNull final Array<String> resources) {
        openResourceAssetDialog(handler, null, resources);
    }

    /**
     * Open an resource asset dialog.
     *
     * @param handler   the result handler.
     * @param validator the validator.
     * @param resources the resources.
     */
    @FxThread
    public static void openResourceAssetDialog(@NotNull final Consumer<String> handler,
                                               @Nullable final Function<String, String> validator,
                                               @NotNull final Array<String> resources) {
        final StringVirtualAssetEditorDialog dialog = new StringVirtualAssetEditorDialog(handler, validator, resources);
        dialog.show();
    }

    /**
     * Open an asset dialog.
     *
     * @param handler      the result handler.
     * @param extensions   the extensions list.
     * @param actionTester the action tester.
     */
    @FxThread
    public static void openFileAssetDialog(@NotNull final Consumer<Path> handler,
                                           @NotNull final Array<String> extensions,
                                           @Nullable final Predicate<Class<?>> actionTester) {

        final AssetEditorDialog<Path> dialog = new FileAssetEditorDialog(handler);
        dialog.setExtensionFilter(extensions);
        dialog.setActionTester(actionTester);
        dialog.show();
    }

    /**
     * Open an asset dialog.
     *
     * @param handler      the result handler.
     * @param actionTester the action tester.
     */
    @FxThread
    public static void openFolderAssetDialog(@NotNull final Consumer<Path> handler,
                                             @Nullable final Predicate<Class<?>> actionTester) {

        final AssetEditorDialog<Path> dialog = new FolderAssetEditorDialog(handler);
        dialog.setActionTester(actionTester);
        dialog.show();
    }

    /**
     * Open a save as dialog.
     *
     * @param handler      the result handler.
     * @param extension    the file extension.
     * @param actionTester the action tester.
     */
    @FxThread
    public static void openSaveAsDialog(@NotNull final Consumer<@NotNull Path> handler, @NotNull final String extension,
                                        @Nullable final Predicate<@NotNull Class<?>> actionTester) {

        final SaveAsEditorDialog dialog = new SaveAsEditorDialog(handler);
        dialog.setExtension(extension);

        if (actionTester != null) {
            dialog.setActionTester(actionTester);
        }

        dialog.show();
    }

    /**
     * Accept a drag event if it has a file with required extensions.
     *
     * @param dragEvent  the drag event.
     * @param extensions the extensions.
     */
    @FxThread
    public static void acceptIfHasFile(@NotNull final DragEvent dragEvent, @NotNull final Array<String> extensions) {

        final Dragboard dragboard = dragEvent.getDragboard();
        if (!isHasFile(dragboard, extensions)) return;

        final Set<TransferMode> transferModes = dragboard.getTransferModes();
        final boolean isCopy = transferModes.contains(TransferMode.COPY);

        dragEvent.acceptTransferModes(isCopy ? TransferMode.COPY : TransferMode.MOVE);
        dragEvent.consume();
    }

    /**
     * Accept a drag event if it has a file with required extension.
     *
     * @param dragEvent       the drag event.
     * @param targetExtension the extension.
     */
    @FxThread
    public static void acceptIfHasFile(@NotNull final DragEvent dragEvent, @NotNull final String targetExtension) {

        final Dragboard dragboard = dragEvent.getDragboard();
        if (!isHasFile(dragboard, targetExtension)) return;

        final Set<TransferMode> transferModes = dragboard.getTransferModes();
        final boolean isCopy = transferModes.contains(TransferMode.COPY);

        dragEvent.acceptTransferModes(isCopy ? TransferMode.COPY : TransferMode.MOVE);
        dragEvent.consume();
    }


    /**
     * Check the dragboard.
     *
     * @param dragboard  the dragboard.
     * @param extensions the extensions.
     * @return true if there are required file.
     */
    @FxThread
    public static boolean isHasFile(@NotNull final Dragboard dragboard, @NotNull final Array<String> extensions) {

        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return false;
        }

        final File file = files.get(0);
        final String extension = FileUtils.getExtension(file.getName(), true);

        return extensions.contains(extension);
    }

    /**
     * Check the dragboard.
     *
     * @param dragboard       the dragboard.
     * @param targetExtension the target extension.
     * @return true if there are required file.
     */
    @FxThread
    public static boolean isHasFile(@NotNull final Dragboard dragboard, @NotNull final String targetExtension) {

        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return false;
        }

        final File file = files.get(0);
        final String extension = FileUtils.getExtension(file.getName(), true);

        return targetExtension.equalsIgnoreCase(extension);
    }

    /**
     * Handle a first dropped file if it has required extensions.
     *
     * @param dragEvent  the drag event.
     * @param extensions the extensions.
     * @param handler    the handler.
     */
    @FxThread
    public static void handleDroppedFile(@NotNull final DragEvent dragEvent, @NotNull final Array<String> extensions,
                                         @NotNull final Consumer<Path> handler) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return;
        }

        final File file = files.get(0);
        final String extension = FileUtils.getExtension(file.getName(), true);

        if (!extensions.contains(extension)) {
            return;
        }

        handler.accept(file.toPath());
    }

    /**
     * Handle a first dropped file if it has required extensions.
     *
     * @param <F>        the type parameter
     * @param dragEvent  the drag event.
     * @param extensions the extensions.
     * @param firstArg   the first argument.
     * @param handler    the handler.
     */
    @FxThread
    public static <F> void handleDroppedFile(@NotNull final DragEvent dragEvent,
                                             @NotNull final Array<String> extensions, @NotNull final F firstArg,
                                             @NotNull final BiConsumer<F, Path> handler) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return;
        }

        final File file = files.get(0);
        final String extension = FileUtils.getExtension(file.getName(), true);

        if (!extensions.contains(extension)) {
            return;
        }

        handler.accept(firstArg, file.toPath());
    }

    /**
     * Handle a first dropped file if it has required extensions.
     *
     * @param <F>             the type parameter
     * @param <S>             the type parameter
     * @param dragEvent       the drag event.
     * @param targetExtension the extension.
     * @param firstArg        the first argument.
     * @param secondArg       the second argument.
     * @param handler         the handler.
     */
    @FxThread
    public static <F, S> void handleDroppedFile(@NotNull final DragEvent dragEvent,
                                                @NotNull final String targetExtension, @NotNull final F firstArg,
                                                @NotNull final S secondArg,
                                                @NotNull final TriConsumer<F, S, Path> handler) {

        handleDroppedFile(dragEvent.getDragboard(), targetExtension, firstArg, secondArg, handler);
    }

    /**
     * Handle a first dropped file if it has required extensions.
     *
     * @param <F>             the type parameter
     * @param <S>             the type parameter
     * @param dragboard       the dragboard.
     * @param targetExtension the extension.
     * @param firstArg        the first argument.
     * @param secondArg       the second argument.
     * @param handler         the handler.
     */
    @FxThread
    public static <F, S> void handleDroppedFile(@NotNull final Dragboard dragboard,
                                                @NotNull final String targetExtension, @NotNull final F firstArg,
                                                @NotNull final S secondArg,
                                                @NotNull final TriConsumer<F, S, Path> handler) {

        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return;
        }

        final File file = files.get(0);
        final String extension = FileUtils.getExtension(file.getName(), false);

        if (!targetExtension.equalsIgnoreCase(extension)) {
            return;
        }

        handler.accept(firstArg, secondArg, file.toPath());
    }

    /**
     * Handle a first dropped file if it has required extensions.
     *
     * @param <F>        the type parameter
     * @param <S>        the type parameter
     * @param dragEvent  the drag event.
     * @param extensions the extensions.
     * @param firstArg   the first argument.
     * @param secondArg  the second argument.
     * @param handler    the handler.
     */
    @FxThread
    public static <F, S> void handleDroppedFile(@NotNull final DragEvent dragEvent,
                                                @NotNull final Array<String> extensions, @NotNull final F firstArg,
                                                @NotNull final S secondArg,
                                                @NotNull final TriConsumer<F, S, Path> handler) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return;
        }

        final File file = files.get(0);
        final String extension = FileUtils.getExtension(file.getName(), true);

        if (!extensions.contains(extension)) {
            return;
        }

        handler.accept(firstArg, secondArg, file.toPath());
    }


    /**
     * Convert the color to hex presentation to use in web.
     *
     * @param color the color.
     * @return the web presentation.
     */
    @FromAnyThread
    public static @NotNull String toWeb(@NotNull final Color color) {
        final int red = (int) (color.getRed() * 255);
        final int green = (int) (color.getGreen() * 255);
        final int blue = (int) (color.getBlue() * 255);
        return "#" + Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);
    }

    /**
     * Increment the loading counter.
     */
    @FxThread
    public static void incrementLoading() {
        getFxScene().incrementLoading();
    }

    /**
     * Decrement the loading counter.
     */
    @FxThread
    public static void decrementLoading() {
        getFxScene().decrementLoading();
    }

    /**
     * Create a dialog for showing the exception.
     */
    @FxThread
    public static @NotNull Alert createErrorAlert(@NotNull final Exception e, @Nullable final String localizedMessage,
                                                  @Nullable final String stackTrace) {

        final TextArea textArea = new TextArea(stackTrace);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        VBox.setMargin(textArea, new Insets(2, 5, 2, 5));

        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(StringUtils.isEmpty(localizedMessage) ? e.getClass().getSimpleName() : localizedMessage);

        final DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setExpandableContent(new VBox(textArea));
        dialogPane.expandedProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == Boolean.TRUE) {
                alert.setWidth(800);
                alert.setHeight(400);
            } else {
                alert.setWidth(500);
                alert.setHeight(220);
            }
        });

        return alert;
    }

    /**
     * Check of existing a file in clipboard.
     *
     * @return true if you have a file in your system clipboard.
     */
    @FxThread
    public static boolean hasFileInClipboard() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard == null) return false;
        final List<File> files = unsafeCast(clipboard.getContent(DataFormat.FILES));
        return !(files == null || files.isEmpty());
    }

    private UiUtils() {
        throw new RuntimeException();
    }
}
