package com.ss.editor.ui.cursor;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.InputManager;
import com.jme3x.jfx.cursor.CursorDisplayProvider;
import com.ss.client.executor.impl.GameThreadExecutor;
import com.ss.client.game.task.impl.ChangeCursorTask;
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

    private static final ObjectDictionary<CursorType, String> CURSOR_TEXTURE_MAPPING = DictionaryFactory.newObjectDictionary();

    static {
        CURSOR_TEXTURE_MAPPING.put(CursorType.CLOSED_HAND, "ui/fx/cursor/ubuntu/Ubuntu link.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.CROSSHAIR, "ui/fx/cursor/ubuntu/Ubuntu Precision.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.DEFAULT, "ui/fx/cursor/ubuntu/Ubuntu Normal Select.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.E_RESIZE, "ui/fx/cursor/ubuntu/Ubuntu horozontal.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.H_RESIZE, "ui/fx/cursor/ubuntu/Ubuntu horozontal.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.HAND, "ui/fx/cursor/ubuntu/Ubuntu link.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.MOVE, "ui/fx/cursor/ubuntu/The Real Ubuntu Move.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.N_RESIZE, "ui/fx/cursor/ubuntu/Ubuntu vertical.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.NE_RESIZE, "ui/fx/cursor/ubuntu/Ubuntu diagonal 2.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.NW_RESIZE, "ui/fx/cursor/ubuntu/Ubuntu diagonal 1.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.OPEN_HAND, "ui/fx/cursor/ubuntu/Ubuntu link.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.S_RESIZE, "ui/fx/cursor/ubuntu/Ubuntu vertical.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.SE_RESIZE, "ui/fx/cursor/ubuntu/Ubuntu diagonal 1.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.SW_RESIZE, "ui/fx/cursor/ubuntu/Ubuntu diagonal 2.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.TEXT, "ui/fx/cursor/ubuntu/Ubuntu Text.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.V_RESIZE, "ui/fx/cursor/ubuntu/Ubuntu vertical.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.W_RESIZE, "ui/fx/cursor/ubuntu/Ubuntu horozontal.cur");
        CURSOR_TEXTURE_MAPPING.put(CursorType.WAIT, "ui/fx/cursor/ubuntu/Ubuntu Busy.ani");
    }

    /**
     * Кеш текстур курсоров.
     */
    private final ObjectDictionary<CursorType, JmeCursor> cache = DictionaryFactory.newObjectDictionary();

    private final AssetManager assetManager;
    private final InputManager inputManager;
    private final Application app;

    public UbuntuCursorProvider(final Application app, final AssetManager assetManager, final InputManager inputManager) {
        this.assetManager = assetManager;
        this.inputManager = inputManager;
        this.app = app;
        assetManager.registerLocator("", ClasspathLocator.class);
    }

    public Application getApp() {
        return app;
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

    public InputManager getInputManager() {
        return inputManager;
    }

    @Override
    public void setup(final CursorType cursorType) {

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
            LOGGER.warning("Unkown Cursor! " + cursorType);
        }

        final JmeCursor cursor = cache.get(cursorType);

        if (cursor == null) {
            return;
        }

        final GameThreadExecutor executor = GameThreadExecutor.getInstance();
        executor.addToExecute(ChangeCursorTask.getInstance(cursor));
    }
}
