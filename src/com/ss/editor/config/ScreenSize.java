package com.ss.editor.config;

import static java.lang.Math.max;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import rlib.util.array.Array;
import rlib.util.array.ArrayComparator;
import rlib.util.array.ArrayFactory;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * The class for presentations a screen size.
 *
 * @author JavaSaBr.
 */
public class ScreenSize {

    public static final int SCREEN_SIZE_MIN_HEIGHT = 700;
    public static final int SCREEN_SIZE_MIN_WIDTH = 1244;

    /**
     * The table with available screen sizes.
     */
    private static final ObjectDictionary<String, ScreenSize> SCREEN_SIZE_TABLE = DictionaryFactory.newObjectDictionary();

    /**
     * The array with available screen sizes.
     */
    private static ScreenSize[] values;

    /**
     * Init the available screen sizes.
     */
    public static void init() {

        final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        final DisplayMode[] modes = device.getDisplayModes();

        final Array<ScreenSize> container = ArrayFactory.newArraySet(ScreenSize.class);

        int maxWidth = 0;
        int maxHeight = 0;

        for (final DisplayMode mode : modes) {

            maxWidth = max(mode.getWidth(), maxWidth);
            maxHeight = max(mode.getHeight(), maxHeight);

            if (mode.getWidth() < SCREEN_SIZE_MIN_WIDTH || mode.getHeight() < SCREEN_SIZE_MIN_HEIGHT) {
                continue;
            }

            container.add(new ScreenSize(mode.getWidth(), mode.getHeight(), device.isFullScreenSupported()));
        }

        container.add(new ScreenSize(SCREEN_SIZE_MIN_WIDTH, SCREEN_SIZE_MIN_HEIGHT, false));

        if (maxWidth >= 1600 && maxHeight >= 900) container.add(new ScreenSize(1600, 900, false));
        if (maxWidth >= 1850 && maxHeight >= 1000) container.add(new ScreenSize(1850, 1000, false));
        if (maxWidth >= 1366 && maxHeight >= 768) container.add(new ScreenSize(1366, 768, false));

        container.sort(COMPARATOR);
        container.forEach(size -> SCREEN_SIZE_TABLE.put(size.size, size));

        values = container.toArray(new ScreenSize[container.size()]);
    }

    /**
     * @param size the string presentation of the screen size.
     * @return the screen size.
     */
    public static ScreenSize sizeOf(final String size) {
        final ScreenSize screenSize = SCREEN_SIZE_TABLE.get(size);
        return screenSize == null ? new ScreenSize(SCREEN_SIZE_MIN_WIDTH, SCREEN_SIZE_MIN_HEIGHT, false) : screenSize;
    }

    /**
     * @return the array with available screen sizes.
     */
    public static ScreenSize[] values() {
        return values;
    }

    /**
     * The string presentation.
     */
    private final String size;

    /**
     * The width of the screen.
     */
    private final int width;

    /**
     * The height of the screen.
     */
    private final int height;

    /**
     * The comparator for sorting by screen size.
     */
    private static final ArrayComparator<ScreenSize> COMPARATOR = (first, second) -> {

        final int firstTotal = first.getHeight() * first.getWidth();
        final int secondTotal = second.getHeight() * second.getWidth();

        return -(firstTotal - secondTotal);
    };

    /**
     * Is support fullscreen.
     */
    private final boolean fullscreenSupported;

    private ScreenSize(final int width, final int height, final boolean fullscreenSupported) {
        this.width = width;
        this.height = height;
        this.size = width + "x" + height;
        this.fullscreenSupported = fullscreenSupported;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }

        final ScreenSize other = (ScreenSize) obj;

        if (height != other.height) {
            return false;
        } else if (width != other.width) {
            return false;
        }

        return true;
    }

    /**
     * @return the height of the screen.
     */
    public final int getHeight() {
        return height;
    }

    /**
     * @return the width of the screen.
     */
    public final int getWidth() {
        return width;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + width;
        return result;
    }

    /**
     * @return is support fullscreen.
     */
    public boolean isFullscreenSupported() {
        return fullscreenSupported;
    }

    @Override
    public String toString() {
        return size;
    }
}
