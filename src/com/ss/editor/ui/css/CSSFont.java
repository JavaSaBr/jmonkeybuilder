package com.ss.editor.ui.css;

/**
 * Перечисление используемых шрифтов используемых в UI FX.
 *
 * @author Ronn
 */
public enum CSSFont {
    ROBOTO_BLACK("Roboto Black", "/ui/fonts/Roboto-Black.ttf", 0);

    public static final CSSFont[] FONTS = values();

    /**
     * Название шрифта.
     */
    private final String name;

    /**
     * Путь для загрузки шрифта.
     */
    private final String path;

    /**
     * Размер шрифта.
     */
    private final float size;

    private CSSFont(final String name, final String path, final float size) {
        this.name = name;
        this.path = path;
        this.size = size;
    }

    /**
     * @return название шрифта.
     */
    public String getName() {
        return name;
    }

    /**
     * @return путь для загрузки шрифта.
     */
    public String getPath() {
        return path;
    }

    /**
     * @return размер шрифта.
     */
    public float getSize() {
        return size;
    }
}
