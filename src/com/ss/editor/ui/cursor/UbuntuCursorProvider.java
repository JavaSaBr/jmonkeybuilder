package com.ss.editor.ui.cursor;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.InputManager;
import com.jme3x.jfx.cursor.CursorDisplayProvider;
import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.cursor.CursorType;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * @author Ronn
 */
public class UbuntuCursorProvider implements CursorDisplayProvider {

    private static final Logger LOGGER = LoggerManager.getLogger(UbuntuCursorProvider.class);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    private static final ObjectDictionary<CursorType, String> CURSOR_TEXTURE_MAPPING = DictionaryFactory.newObjectDictionary();

    static {
        CURSOR_TEXTURE_MAPPING.put(CursorType.CLOSED_HAND, "ui/cursor/ubuntu/Ubuntu link.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.CROSSHAIR, "ui/cursor/ubuntu/Ubuntu Precision.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.DEFAULT, "ui/cursor/ubuntu/Ubuntu Normal Select.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.E_RESIZE, "ui/cursor/ubuntu/Ubuntu horozontal.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.H_RESIZE, "ui/cursor/ubuntu/Ubuntu horozontal.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.HAND, "ui/cursor/ubuntu/Ubuntu link.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.MOVE, "ui/cursor/ubuntu/The Real Ubuntu Move.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.N_RESIZE, "ui/cursor/ubuntu/Ubuntu vertical.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.NE_RESIZE, "ui/cursor/ubuntu/Ubuntu diagonal 2.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.NW_RESIZE, "ui/cursor/ubuntu/Ubuntu diagonal 1.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.OPEN_HAND, "ui/cursor/ubuntu/Ubuntu link.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.S_RESIZE, "ui/cursor/ubuntu/Ubuntu vertical.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.SE_RESIZE, "ui/cursor/ubuntu/Ubuntu diagonal 1.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.SW_RESIZE, "ui/cursor/ubuntu/Ubuntu diagonal 2.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.TEXT, "ui/cursor/ubuntu/Ubuntu Text.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.V_RESIZE, "ui/cursor/ubuntu/Ubuntu vertical.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.W_RESIZE, "ui/cursor/ubuntu/Ubuntu horozontal.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.WAIT, "ui/cursor/ubuntu/Ubuntu Busy.ani");
    }

    /**
     * Кеш текстур курсоров.
     */
    private final ObjectDictionary<CursorType, JmeCursor> cache = DictionaryFactory.newObjectDictionary();

    private final AssetManager assetManager;

    public UbuntuCursorProvider(final Application app, final AssetManager assetManager, final InputManager inputManager) {
        this.assetManager = assetManager;
        assetManager.registerLocator("", ClasspathLocator.class);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * @return кеш текстур курсоров.
     */
    private ObjectDictionary<CursorType, JmeCursor> getCache() {
        return cache;
    }

    @Override
    public void setupCursor(final CursorType cursorType) {

        final ObjectDictionary<CursorType, JmeCursor> cache = getCache();
        final String path = CURSOR_TEXTURE_MAPPING.get(cursorType);

        if (path == null) {
            cache.put(cursorType, null);
            return;
        }

        final AssetManager assetManager = getAssetManager();
        final JmeCursor cursor = (JmeCursor) assetManager.loadAsset(path);

        cache.put(cursorType, cursor);
    }

    @Override
    public void showCursor(final CursorFrame cursorFrame) {

        final ObjectDictionary<CursorType, JmeCursor> cache = getCache();

        CursorType cursorType = cursorFrame.getCursorType();

        if (!cache.containsKey(cursorType)) {
            cursorType = CursorType.DEFAULT;
            LOGGER.warning("Unknown Cursor! " + cursorType);
        }

        final JmeCursor cursor = cache.get(cursorType);

        if (cursor == null) {
            return;
        }

        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            final Editor editor = Editor.getInstance();
            final InputManager inputManager = editor.getInputManager();
            inputManager.setMouseCursor(cursor);
        });
    }
}
