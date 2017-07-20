package com.ss.editor.ui.css;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The registry of available css files.
 *
 * @author JavaSaBr
 */
public class CSSRegistry {

    @NotNull
    private static final CSSRegistry INSTANCE = new CSSRegistry();

    @NotNull
    public static CSSRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The list of available css files.
     */
    @NotNull
    private final Array<String> availableCssFiles;

    private CSSRegistry() {
        this.availableCssFiles = ArrayFactory.newArray(String.class);
    }

    /**
     * Add the CSS file to this registry.
     *
     * @param cssFile the CSS file.
     */
    @FromAnyThread
    public void addCssFile(@NotNull String cssFile) {
        availableCssFiles.add(cssFile);
    }

    /**
     * Get a list of available CSS files.
     *
     * @return the list of available css files.
     */
    @NotNull
    public Array<String> getAvailableCssFiles() {
        return availableCssFiles;
    }
}
