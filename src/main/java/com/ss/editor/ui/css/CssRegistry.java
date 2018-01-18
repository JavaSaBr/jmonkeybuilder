package com.ss.editor.ui.css;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

/**
 * The registry of available css files.
 *
 * @author JavaSaBr
 */
public class CssRegistry {

    @NotNull
    private static final CssRegistry INSTANCE = new CssRegistry();

    @FromAnyThread
    public static @NotNull CssRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The list of available css files.
     */
    @NotNull
    private final Array<String> availableCssFiles;

    private CssRegistry() {
        this.availableCssFiles = ArrayFactory.newArray(String.class);
    }

    /**
     * Add the CSS file to this registry.
     *
     * @param cssFile the URL to the CSS file.
     */
    @FromAnyThread
    public void register(@NotNull final URL cssFile) {
        availableCssFiles.add(cssFile.toExternalForm());
    }

    /**
     * Add the CSS file to this registry.
     *
     * @param cssFile     the path to CSS file.
     * @param classLoader the class loader which can load this path.
     */
    @FromAnyThread
    public void register(@NotNull final String cssFile, @NotNull final ClassLoader classLoader) {
        register(notNull(classLoader.getResource(cssFile)));
    }

    /**
     * Get a list of available CSS files.
     *
     * @return the list of available css files.
     */
    @FromAnyThread
    public @NotNull Array<String> getAvailableCssFiles() {
        return availableCssFiles;
    }
}
