package com.ss.editor.ui.util;

import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.ui.css.CssColorTheme;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

/**
 * The class to add supporting dynamic icons.
 *
 * @author JavaSaBr
 */
public class DynamicIconSupport {

    @NotNull
    private static final FileIconManager FILE_ICON_MANAGER = FileIconManager.getInstance();

    @NotNull
    private static final Object NOT_SELECTED_IMAGE = new Object();

    @NotNull
    private static final Object SELECTED_IMAGE = new Object();

    @NotNull
    private static final Object NOT_SELECTED_IMAGE_2 = new Object();

    @NotNull
    private static final Object SELECTED_IMAGE_2 = new Object();

    @NotNull
    private static final Object SELECTED_IMAGE_LISTENER = new Object();

    @NotNull
    private static final Object SELECTED_IMAGE_LISTENER_2 = new Object();

    @NotNull
    private static final Object NOT_PRESSED_IMAGE = new Object();

    @NotNull
    private static final Object PRESSED_IMAGE = new Object();

    /**
     * Adds support changing icons by selection.
     *
     * @param buttons the buttons.
     */
    public static void addSupport(@NotNull final ToggleButton... buttons) {
        for (final ToggleButton button : buttons) {
            addSupport(button);
        }
    }

    /**
     * Adds support changing icons by pressed.
     *
     * @param buttons the buttons.
     */
    public static void addSupport(@NotNull final ButtonBase... buttons) {
        for (final ButtonBase button : buttons) {
            addSupport(button);
        }
    }

    /**
     * Adds support changing icons by selection.
     *
     * @param button the button.
     */
    public static void addSupport(@NotNull final ToggleButton button) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final CssColorTheme theme = editorConfig.getTheme();

        if (theme.isDark()) {
            return;
        }

        final ImageView graphic = (ImageView) button.getGraphic();
        final Image image = graphic.getImage();
        final Image original = FILE_ICON_MANAGER.getOriginal(image);

        final ObservableMap<Object, Object> properties = button.getProperties();
        properties.put(NOT_SELECTED_IMAGE, image);
        properties.put(SELECTED_IMAGE, original);

        button.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                graphic.setImage((Image) properties.get(SELECTED_IMAGE));
            } else {
                graphic.setImage((Image) properties.get(NOT_SELECTED_IMAGE));
            }
        });

        if (button.isSelected()) {
            graphic.setImage(original);
        } else {
            graphic.setImage(image);
        }
    }

    /**
     * Adds support changing icons by pressed.
     *
     * @param button the button.
     */
    public static void addSupport(@NotNull final ButtonBase button) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final CssColorTheme theme = editorConfig.getTheme();

        if (theme.isDark()) {
            return;
        }

        final ImageView graphic = (ImageView) button.getGraphic();
        final Image image = graphic.getImage();
        final Image original = FILE_ICON_MANAGER.getOriginal(image);

        final ObservableMap<Object, Object> properties = button.getProperties();
        properties.put(NOT_PRESSED_IMAGE, image);
        properties.put(PRESSED_IMAGE, original);

        button.pressedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                graphic.setImage((Image) properties.get(PRESSED_IMAGE));
            } else {
                graphic.setImage((Image) properties.get(NOT_PRESSED_IMAGE));
            }
        });

        if (button.isPressed()) {
            graphic.setImage(original);
        } else {
            graphic.setImage(image);
        }
    }

    /**
     * Updated listener of changing icons.
     *
     * @param node      the node.
     * @param imageView the image view.
     * @param condition the condition of changing.
     */
    public static void updateListener(@NotNull final Node node, @NotNull final ImageView imageView,
                                      @NotNull final ReadOnlyBooleanProperty condition) {
        updateListener(node, imageView, condition, SELECTED_IMAGE_LISTENER, NOT_SELECTED_IMAGE, SELECTED_IMAGE);
    }

    /**
     * Updated listener of changing icons.
     *
     * @param node      the node.
     * @param imageView the image view.
     * @param condition the condition of changing.
     */
    public static void updateListener2(@NotNull final Node node, @NotNull final ImageView imageView,
                                       @NotNull final ReadOnlyBooleanProperty condition) {
        updateListener(node, imageView, condition, SELECTED_IMAGE_LISTENER_2, NOT_SELECTED_IMAGE_2, SELECTED_IMAGE_2);
    }

    private static void updateListener(@NotNull final Node node, @NotNull final ImageView imageView,
                                       @NotNull final ReadOnlyBooleanProperty condition,
                                       @NotNull final Object listenerKey, @NotNull final Object notSelectedKey,
                                       @NotNull final Object selectedKey) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final CssColorTheme theme = editorConfig.getTheme();

        if (theme.isDark()) {
            return;
        }

        final ObservableMap<Object, Object> properties = node.getProperties();
        final Image newImage = imageView.getImage();

        if (newImage == null) {
            properties.remove(listenerKey);
            return;
        }

        final Image original = FILE_ICON_MANAGER.getOriginal(newImage);

        properties.put(notSelectedKey, newImage);
        properties.put(selectedKey, original);

        final ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> {
            if (newValue) {
                imageView.setImage((Image) properties.get(selectedKey));
            } else {
                imageView.setImage((Image) properties.get(notSelectedKey));
            }
        };

        condition.addListener(listener);

        properties.put(listenerKey, listener);

        if (condition.get()) {
            imageView.setImage(original);
        } else {
            imageView.setImage(newImage);
        }
    }
}
