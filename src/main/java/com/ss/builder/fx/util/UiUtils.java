package com.ss.builder.ui.util;

import static com.ss.editor.util.EditorUtils.getFxScene;
import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import static java.lang.Math.min;
import com.jme3.math.ColorRGBA;
import com.ss.builder.ui.dialog.save.SaveAsEditorDialog;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.UObject;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.dialog.asset.BaseAssetEditorDialog;
import com.ss.editor.ui.dialog.asset.file.AssetEditorDialog;
import com.ss.editor.ui.dialog.asset.file.FileAssetEditorDialog;
import com.ss.editor.ui.dialog.asset.file.FolderAssetEditorDialog;
import com.ss.editor.ui.dialog.asset.virtual.StringVirtualAssetEditorDialog;
import com.ss.editor.ui.dialog.save.SaveAsEditorDialog;
import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.application.Platform;
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
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The utility class with utility UI methods.
 *
 * @author JavaSaBr
 */
public abstract class UiUtils {

    private static final PseudoClass FOCUSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("focused");

    private static final Duration TOOLTIP_HIDE_DELAY = new Duration(100);
    private static final Duration TOOLTIP_SHOW_DELAY = new Duration(1000);
    private static final Duration TOOLTIP_SHOW_DURATION = new Duration(5000);

    /**
     * Add binding pseudo focus of the pane to focus state of the controls.
     *
     * @param pane   the pane.
     * @param controls  the controls.
     */
    @FxThread
    public static @NotNull BooleanProperty addFocusBinding(@NotNull Pane pane, @NotNull Control... controls) {

        var focused = new BooleanPropertyBase(true) {

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

        ChangeListener<Boolean> listener = (observable, oldValue, newValue) ->
                focused.setValue(newValue || ArrayUtils.anyMatch(controls, Node::isFocused));

        for (var control : controls) {
            control.focusedProperty().addListener(listener);
            control.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> control.requestFocus());
        }

        focused.setValue(ArrayUtils.anyMatch(controls, Node::isFocused));

        return focused;
    }

    /**
     * Clear children of the pane.
     *
     * @param pane the pane.
     */
    @FxThread
    public static void clear(@NotNull Pane pane) {
        var children = pane.getChildren();
        children.forEach(UiUtils::unbind);
        children.clear();
    }

    /**
     * Unbind a node.
     *
     * @param node the node.
     */
    @FxThread
    private static void unbind(@NotNull Node node) {
        if (node instanceof Control) {
            var control = (Control) node;
            control.prefWidthProperty().unbind();
            control.prefHeightProperty().unbind();
        } else if (node instanceof Pane) {
            var pane = (Pane) node;
            pane.prefHeightProperty().unbind();
            pane.prefWidthProperty().unbind();
        }
    }

    /**
     * Fill a list of components.
     *
     * @param container the container.
     * @param node      the node.
     */
    @FromAnyThread
    public static @NotNull Array<ScreenComponent> fillComponents(
            @NotNull Array<ScreenComponent> container,
            @NotNull Node node
    ) {

        if (node instanceof ScreenComponent) {
            container.add((ScreenComponent) node);
        }

        if (node instanceof SplitPane) {
            var items = ((SplitPane) node).getItems();
            items.forEach(child -> fillComponents(container, child));
        } else if (node instanceof TabPane) {
            var tabs = ((TabPane) node).getTabs();
            tabs.forEach(tab -> fillComponents(container, tab.getContent()));
        }

        if (!(node instanceof Parent)) {
            return container;
        }

        var nodes = ((Parent) node).getChildrenUnmodifiable();
        nodes.forEach(child -> fillComponents(container, child));

        return container;
    }

    /**
     * Get all components of the type.
     *
     * @param <T>  the component's type.
     * @param node the node.
     * @param type the type.
     * @return the found components.
     */
    @FxThread
    public static @NotNull <T extends Node> Array<T> getComponents(@NotNull Node node, @NotNull Class<T> type) {
        var container = Array.ofType(type);
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
    public static <T extends Node> void fillComponents(
            @NotNull Array<T> container,
            @NotNull Node node,
            @NotNull Class<T> type
    ) {

        if (type.isInstance(container)) {
            container.add(type.cast(node));
        }

        if (!(node instanceof Parent)) {
            return;
        }

        ((Parent) node).getChildrenUnmodifiable()
                .forEach(child -> fillComponents(container, child, type));
    }

    /**
     * Get all elements of the menu.
     *
     * @param menuBar the menu bar.
     * @return the all items.
     */
    @FxThread
    public static @NotNull Array<MenuItem> getAllItems(@NotNull MenuBar menuBar) {

        var container = Array.ofType(MenuItem.class);

        menuBar.getMenus()
                .forEach(menu -> getAllItems(container, menu));

        return container;
    }

    /**
     * Collect all elements of a menu.
     */
    @FxThread
    private static void getAllItems(@NotNull Array<MenuItem> container, @NotNull MenuItem menuItem) {

        container.add(menuItem);

        if (!(menuItem instanceof Menu)) {
            return;
        }

        ((Menu) menuItem).getItems()
                .forEach(subMenuItem -> getAllItems(container, subMenuItem));
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
     * Update behaviour of the tooltip.
     *
     * @param tooltip the tooltip.
     * @return the updated tooltip.
     */
    @FxThread
    public static <T extends Tooltip> T updateTooltip(final T tooltip) {
        tooltip.setHideDelay(TOOLTIP_HIDE_DELAY);
        tooltip.setShowDelay(TOOLTIP_SHOW_DELAY);
        tooltip.setShowDuration(TOOLTIP_SHOW_DURATION);
        return tooltip;
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
    public static <T> Array<TreeItem<T>> getAllItems(@NotNull TreeItem<T> root) {
        final Array<TreeItem<T>> container = ArrayFactory.newArray(TreeItem.class);
        collectAllItems(container, root);
        return container;
    }

    /**
     * Collect all elements of tree items.
     *
     * @param <T>  the type parameter
     * @param root the tree item.
     * @return the list with all items.
     */
    @FxThread
    public static <T> Stream<TreeItem<T>> allItems(@NotNull TreeItem<T> root) {
        Array<TreeItem<T>> container = ArrayFactory.newArray(TreeItem.class);
        collectAllItems(container, root);
        return container.stream();
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
    public static @Nullable ColorRGBA from(@Nullable Color color) {

        if (color == null) {
            return null;
        }

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
    public static void openResourceAssetDialog(
            @NotNull Consumer<String> handler,
            @Nullable BaseAssetEditorDialog.Validator<String> validator,
            @NotNull Array<String> resources
    ) {
        new StringVirtualAssetEditorDialog(handler, validator, resources).show();
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
     * Accept a transfer mode for the event.
     *
     * @param dragEvent the drag event.
     * @param dragboard the dragboard.
     */
    @FxThread
    private static void acceptTransferMode(@NotNull DragEvent dragEvent, @NotNull Dragboard dragboard) {

        var transferModes = dragboard.getTransferModes();
        var isCopy = transferModes.contains(TransferMode.COPY);

        dragEvent.acceptTransferModes(isCopy ? TransferMode.COPY : TransferMode.MOVE);
        dragEvent.consume();
    }

    /**
     * Get a file from the dragboard.
     *
     * @param dragboard the dragboard.
     * @return the file or null.
     */
    @FxThread
    private static @Nullable File getDragboardFile(@NotNull Dragboard dragboard) {

        var files = ClassUtils.<List<File>>unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return null;
        }

        return files.get(0);
    }

    /**
     * Get a file from the dragboard.
     *
     * @param dragboard the dragboard.
     * @return the optional value of the file.
     */
    private static @NotNull Optional<File> getDragboardFileOpt(@NotNull Dragboard dragboard) {
        return Optional.ofNullable(getDragboardFile(dragboard));
    }

    /**
     * Accept a drag event if it has a file with required extensions.
     *
     * @param dragEvent  the drag event.
     * @param extensions the extensions.
     */
    @FxThread
    public static void acceptIfHasFile(@NotNull DragEvent dragEvent, @NotNull Array<String> extensions) {

        var dragboard = dragEvent.getDragboard();

        if (isHasFile(dragboard, extensions)) {
            acceptTransferMode(dragEvent, dragboard);
        }
    }

    /**
     * Accept a drag event if it has a file with required extension.
     *
     * @param dragEvent       the drag event.
     * @param targetExtension the extension.
     */
    @FxThread
    public static void acceptIfHasFile(@NotNull DragEvent dragEvent, @NotNull String targetExtension) {

        var dragboard = dragEvent.getDragboard();

        if (isHasFile(dragboard, targetExtension)) {
            acceptTransferMode(dragEvent, dragboard);
        }
    }

    /**
     * Accept a drag event if it has a file with required extension.
     *
     * @param dragEvent the drag event.
     * @param checker   the checker.
     */
    @FxThread
    public static void acceptIfHasFile(@NotNull DragEvent dragEvent, @NotNull Function<File, Boolean> checker) {

        var dragboard = dragEvent.getDragboard();

        if (!isHasFile(dragboard, checker)) {
            acceptTransferMode(dragEvent, dragboard);
        }
    }

    /**
     * Return true if there are required file.
     *
     * @param dragboard  the dragboard.
     * @param extensions the extensions.
     * @return true if there are required file.
     */
    @FxThread
    public static boolean isHasFile(@NotNull Dragboard dragboard, @NotNull Array<String> extensions) {
        return getDragboardFileOpt(dragboard)
                .map(file -> FileUtils.getExtension(file.getName(), true))
                .map(extensions::contains)
                .orElse(false);
    }

    /**
     * Return true if there are required file.
     *
     * @param dragboard       the dragboard.
     * @param targetExtension the target extension.
     * @return true if there are required file.
     */
    @FxThread
    public static boolean isHasFile(@NotNull Dragboard dragboard, @NotNull String targetExtension) {
        return getDragboardFileOpt(dragboard)
                .map(file -> FileUtils.getExtension(file.getName(), true))
                .map(targetExtension::equalsIgnoreCase)
                .orElse(false);
    }

    /**
     * Return true if there are required file.
     *
     * @param dragboard the dragboard.
     * @param checker   the checker.
     * @return true if there are required file.
     */
    @FxThread
    public static boolean isHasFile(@NotNull Dragboard dragboard, @NotNull Function<File, Boolean> checker) {
        return getDragboardFileOpt(dragboard)
                .map(checker)
                .orElse(false);
    }

    /**
     * Handle a first dropped file if it has required extensions.
     *
     * @param dragEvent the drag event.
     * @param handler   the handler.
     */
    @FxThread
    public static void handleDroppedFile(
            @NotNull DragEvent dragEvent,
            @NotNull Consumer<Path> handler
    ) {
        getDragboardFileOpt(dragEvent.getDragboard())
                .map(File::toPath)
                .ifPresent(handler);
    }

    /**
     * Handle a first dropped file if it has required extensions.
     *
     * @param firstArg the first argument.
     * @param handler  the handler.
     * @param <F>      the first arg's type.
     */
    @FxThread
    public static <F> void handleDroppedFile(
            @NotNull DragEvent dragEvent,
            @NotNull F firstArg,
            @NotNull BiConsumer<F, Path> handler
    ) {
        getDragboardFileOpt(dragEvent.getDragboard())
                .map(File::toPath)
                .ifPresent(path -> handler.accept(firstArg, path));
    }

    /**
     * Handle a first dropped file if it has required extensions.
     *
     * @param dragEvent the drag event.
     * @param extension the extension.
     * @param firstArg  the first argument.
     * @param handler   the handler.
     * @param <F>       the first arg's type.
     */
    @FxThread
    public static <F> void handleDroppedFile(
            @NotNull DragEvent dragEvent,
            @NotNull String extension,
            @NotNull F firstArg,
            @NotNull BiConsumer<F, Path> handler
    ) {
        getDragboardFileOpt(dragEvent.getDragboard())
                .map(File::toPath)
                .filter(file -> extension.equalsIgnoreCase(FileUtils.getExtension(file)))
                .ifPresent(path -> handler.accept(firstArg, path));
    }


    /**
     * Handle a first dropped file if it has required extensions.
     *
     * @param dragEvent the drag event.
     * @param firstArg  the first argument.
     * @param secondArg the second argument.
     * @param handler   the handler.
     * @param <F>       the first arg's type.
     * @param <S>       the second arg's type.
     */
    @FxThread
    public static <F, S> void handleDroppedFile(
            @NotNull DragEvent dragEvent,
            @NotNull F firstArg,
            @NotNull S secondArg,
            @NotNull TriConsumer<F, S, Path> handler
    ) {
        handleDroppedFile(dragEvent.getDragboard(), firstArg, secondArg, handler);
    }

    /**
     * Handle a first dropped file if it has required extensions.
     *
     * @param dragEvent the drag event.
     * @param extension the extension.
     * @param firstArg  the first argument.
     * @param secondArg the second argument.
     * @param handler   the handler.
     * @param <F>       the first arg's type.
     * @param <S>       the second arg's type.
     */
    @FxThread
    public static <F, S> void handleDroppedFile(
            @NotNull DragEvent dragEvent,
            @NotNull F firstArg,
            @NotNull S secondArg,
            @NotNull String extension,
            @NotNull TriConsumer<F, S, Path> handler
    ) {
        getDragboardFileOpt(dragEvent.getDragboard())
                .map(File::toPath)
                .filter(file -> extension.equalsIgnoreCase(FileUtils.getExtension(file)))
                .ifPresent(path -> handler.accept(firstArg, secondArg, path));
    }

    /**
     * Handle a first dropped file if it has required extensions.
     *
     * @param <F>             the type parameter
     * @param <S>             the type parameter
     * @param dragboard       the dragboard.
     * @param firstArg        the first argument.
     * @param secondArg       the second argument.
     * @param handler         the handler.
     */
    @FxThread
    public static <F, S> void handleDroppedFile(
            @NotNull Dragboard dragboard,
            @NotNull F firstArg,
            @NotNull S secondArg,
            @NotNull TriConsumer<F, S, Path> handler
    ) {
        getDragboardFileOpt(dragboard)
                .map(File::toPath)
                .ifPresent(path -> handler.accept(firstArg, secondArg, path));
    }

    /**
     * Convert the color to hex presentation to use in web.
     *
     * @param color the color.
     * @return the web presentation.
     */
    @FromAnyThread
    public static @NotNull String toWeb(@NotNull final Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        return "#" + Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);
    }

    /**
     * Increment the loading counter.
     */
    @FromAnyThread
    public static void incrementLoading() {

        if (Platform.isFxApplicationThread()) {
            getFxScene().incrementLoading();
        } else {
            ExecutorManager.getInstance()
                    .addFxTask(UiUtils::incrementLoading);
        }
    }

    /**
     * Decrement the loading counter.
     */
    @FromAnyThread
    public static void decrementLoading() {

        if (Platform.isFxApplicationThread()) {
            getFxScene().decrementLoading();
        } else {
            ExecutorManager.getInstance()
                    .addFxTask(UiUtils::decrementLoading);
        }
    }

    /**
     * Create a dialog for showing the exception.
     */
    @FxThread
    public static @NotNull Alert createErrorAlert(
            @NotNull Throwable e,
            @Nullable String localizedMessage,
            @Nullable String stackTrace
    ) {

        var textArea = new TextArea(stackTrace);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        VBox.setMargin(textArea, new Insets(2, 5, 2, 5));

        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(StringUtils.isEmpty(localizedMessage) ? e.getClass().getSimpleName() : localizedMessage);

        var dialogPane = alert.getDialogPane();
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

    /**
     * Find a menu item by the item's type.
     *
     * @param items the item list.
     * @param type  the item's type.
     * @param <T>   the item's type.
     * @return the found item or null.
     */
    @FxThread
    public static <T extends MenuItem> @Nullable T findMenuItem(@NotNull final List<? extends MenuItem> items,
                                                                @NotNull final Class<T> type) {
        for (final MenuItem item : items) {

            if (type.isInstance(item)) {
                return type.cast(item);
            }

            if (item instanceof Menu) {
                final T result = findMenuItem(((Menu) item).getItems(), type);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    private UiUtils() {
        throw new RuntimeException();
    }
}
