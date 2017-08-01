package com.ss.editor.ui.css;

import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

/**
 * The list of color themes.
 *
 * @author JavaSaBr
 */
public enum CssColorTheme {
    LIGHT("/ui/css/light-color.css", "White", Color.BLACK),
    SHADOW("/ui/css/shadow-color.css", "Shadow", Color.web("#c8d2e1")),
    DARK("/ui/css/dark-color.css", "Dark", Color.web("#c8dae2")),;

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
     * The css file.
     */
    @NotNull
    private final String cssFile;

    /**
     * The name of this theme.
     */
    @NotNull
    private final String name;

    CssColorTheme(@NotNull final String cssFile, @NotNull final String name, @NotNull final Color iconColor) {
        this.cssFile = cssFile;
        this.name = name;
        this.iconColor = iconColor;
    }

    /**
     * @return the name of this theme.
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * @return the css file.
     */
    @NotNull
    public String getCssFile() {
        return cssFile;
    }

    /**
     * @return the icon color.
     */
    @NotNull
    public Color getIconColor() {
        return iconColor;
    }

    /**
     * @return true if this theme is dark.
     */
    public boolean needRepaintIcons() {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
