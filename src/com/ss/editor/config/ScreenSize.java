package com.ss.editor.config;

import java.awt.*;

import rlib.util.array.Array;
import rlib.util.array.ArrayComparator;
import rlib.util.array.ArrayFactory;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

import static java.lang.Math.max;

/**
 * Перечисление возможных разрешений экрана.
 *
 * @author Ronn
 */
public class ScreenSize {

    public static final int SCREEN_SIZE_MAX_WIDTH = 1920;
    public static final int SCREEN_SIZE_MIN_HEIGHT = 700;
    public static final int SCREEN_SIZE_MIN_WIDTH = 1244;

    /**
     * Сортировщик размеров экрана.
     */
    private static final ArrayComparator<ScreenSize> COMPARATOR = (first, second) -> {

        final int firstTotal = first.getHeight() * first.getWidth();
        final int secondTotal = second.getHeight() * second.getWidth();

        return -(firstTotal - secondTotal);
    };

    /**
     * Таблица доступных расширений экрана.
     */
    private static final ObjectDictionary<String, ScreenSize> SCREEN_SIZE_TABLE = DictionaryFactory.newObjectDictionary();

    /**
     * Список доступных разрешений.
     */
    private static ScreenSize[] values;

    /**
     * Строковый вид.
     */
    private final String size;

    /**
     * Ширина экрана.
     */
    private final int width;

    /**
     * Высота кэрана.
     */
    private final int height;

    /**
     * Поддерживается ли полный экран.
     */
    private final boolean fullscreenSupported;

    private ScreenSize(final int width, final int height, final boolean fullscreenSupported) {
        this.width = width;
        this.height = height;
        this.size = width + "x" + height;
        this.fullscreenSupported = fullscreenSupported;
    }

    /**
     * Инициализация списка доступных разрешений экрана.
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
            } else if (mode.getWidth() > SCREEN_SIZE_MAX_WIDTH) {
                continue;
            }

            container.add(new ScreenSize(mode.getWidth(), mode.getHeight(), device.isFullScreenSupported()));
        }

        container.add(new ScreenSize(SCREEN_SIZE_MIN_WIDTH, SCREEN_SIZE_MIN_HEIGHT, false));

        if (maxWidth >= 1600 && maxHeight >= 900) {
            container.add(new ScreenSize(1600, 900, false));
        }

        if (maxWidth >= 1366 && maxHeight >= 768) {
            container.add(new ScreenSize(1366, 768, false));
        }

        container.sort(COMPARATOR);

        for (final ScreenSize screenSize : container) {
            SCREEN_SIZE_TABLE.put(screenSize.size, screenSize);
        }

        values = container.toArray(new ScreenSize[container.size()]);
    }

    /**
     * @param size строкое представление разрешения.
     * @return ссылка на инстанс размера.
     */
    public static ScreenSize sizeOf(final String size) {
        final ScreenSize screenSize = SCREEN_SIZE_TABLE.get(size);
        return screenSize == null ? new ScreenSize(SCREEN_SIZE_MIN_WIDTH, SCREEN_SIZE_MIN_HEIGHT, false) : screenSize;
    }

    /**
     * @return список доступных разрешений.
     */
    public static ScreenSize[] values() {
        return values;
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
     * @return высота.
     */
    public final int getHeight() {
        return height;
    }

    /**
     * @return ширина.
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
     * @return поддерживается ли полноэкранный режим.
     */
    public boolean isFullscreenSupported() {
        return fullscreenSupported;
    }

    @Override
    public String toString() {
        return size;
    }
}
