package com.ss.editor.ui.css;

import org.jetbrains.annotations.NotNull;

/**
 * The list of color themes.
 *
 * @author JavaSaBr
 */
public enum CssColorTheme {
    LIGHT("/ui/css/light-color.css", "White") {
        @Override
        public boolean isDark() {
            return false;
        }
    },

    SHADOW("/ui/css/shadow-color.css", "Shadow"),
    DARK("/ui/css/dark-color.css", "Dark"),;

    @NotNull
    public static final CssColorTheme[] VALUES = values();

    @NotNull
    public static CssColorTheme valueOf(final int index) {
        return VALUES[index];
    }

    /**
     * The css file.
     */
    @NotNull
    private String cssFile;

    /**
     * The name of this theme.
     */
    @NotNull
    private String name;

    CssColorTheme(@NotNull final String cssFile, @NotNull final String name) {
        this.cssFile = cssFile;
        this.name = name;
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
     * @return true if this theme is dark.
     */
    public boolean isDark() {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
