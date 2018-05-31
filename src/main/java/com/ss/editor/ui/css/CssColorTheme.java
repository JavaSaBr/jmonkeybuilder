package com.ss.editor.ui.css;

import com.ss.editor.annotation.FromAnyThread;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

/**
 * The list of color themes.
 *
 * @author JavaSaBr
 */
public enum CssColorTheme {
    LIGHT("/ui/css/light-color.css", "White", Color.web("#5d626e"), Color.web("#ffffff")),
    SHADOW("/ui/css/shadow-color.css", "Shadow", Color.web("#c8d2e1"), Color.web("#404552")),
    DARK("/ui/css/dark-color.css", "Dark", Color.web("#c8dae2"), Color.web("#3c3f41")),;

    @NotNull
    public static final CssColorTheme[] VALUES = values();

    @NotNull
    public static CssColorTheme valueOf(final int index) {
        return VALUES[index];
    }

    /**
     * The icon color.
     */
    @NotNull
    private final Color iconColor;

    /**
     * The background color.
     */
    @NotNull
    private final Color backgroundColor;

    /**
     * The css file.
     */
    @NotNull
    private final String cssFile;

    /**
     * The name of this theme.
     */
    @NotNull
    private final String name;

    CssColorTheme(
            @NotNull String cssFile,
            @NotNull String name,
            @NotNull Color iconColor,
            @NotNull Color backgroundColor
    ) {
        this.cssFile = cssFile;
        this.name = name;
        this.iconColor = iconColor;
        this.backgroundColor = backgroundColor;
    }

    /**
     * Get the name of this theme.
     *
     * @return the name of this theme.
     */
    @FromAnyThread
    public @NotNull String getName() {
        return name;
    }

    /**
     * Get the css file.
     *
     * @return the css file.
     */
    @FromAnyThread
    public @NotNull String getCssFile() {
        return cssFile;
    }

    /**
     * Get the icon color.
     *
     * @return the icon color.
     */
    @FromAnyThread
    public @NotNull Color getIconColor() {
        return iconColor;
    }

    /**
     * Get the background color.
     *
     * @return the background color.
     */
    @FromAnyThread
    public @NotNull Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Return true if this theme is dark.
     *
     * @return true if this theme is dark.
     */
    @FromAnyThread
    public boolean needRepaintIcons() {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
