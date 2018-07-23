package com.ss.editor.config;

import static com.ss.editor.config.DefaultSettingsProvider.Defaults.*;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.*;
import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.TextureKey;
import com.jme3.jfx.injfx.JmeToJfxIntegrator;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.ss.editor.JmeApplication;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.TimeTracker;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.ObjectUtils;
import com.ss.rlib.common.util.Utils;
import com.ss.rlib.common.util.dictionary.ConcurrentObjectDictionary;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * The user configuration of this editor.
 *
 * @author JavaSaBr
 */
public final class EditorConfig implements AssetEventListener {

    private static final Logger LOGGER = LoggerManager.getLogger(EditorConfig.class);

    private static final String SCREEN_ALIAS = "screen";
    private static final String ASSET_ALIAS = "asset";
    private static final String OTHER_ALIAS = "other";

    private static final String PREF_SCREEN_WIDTH = SCREEN_ALIAS + "." + "width";
    private static final String PREF_SCREEN_HEIGHT = SCREEN_ALIAS + "." + "height";
    private static final String PREF_SCREEN_MAXIMIZED = SCREEN_ALIAS + "." + "maximized";

    private static final String PREF_ASSET_CURRENT_ASSET = ASSET_ALIAS + "." + "current";
    private static final String PREF_ASSET_LAST_OPENED_ASSETS = ASSET_ALIAS + "." + "lastOpened";

    private static final String PREF_OTHER_ANALYTICS_QUESTION = OTHER_ALIAS + "." + "analyticsQuestion" + Config.STRING_VERSION;

    private static final String PREF_OTHER_GLOBAL_LEFT_TOOL_WIDTH = OTHER_ALIAS + "." + "global.leftTool.width";
    private static final String PREF_OTHER_GLOBAL_LEFT_TOOL_COLLAPSED = OTHER_ALIAS + "." + "global.leftTool.collapsed";
    private static final String PREF_OTHER_GLOBAL_BOTTOM_TOOL_WIDTH = OTHER_ALIAS + "." + "global.bottomTool.height";
    private static final String PREF_OTHER_GLOBAL_BOTTOM_TOOL_COLLAPSED = OTHER_ALIAS + "." + "global.bottomTool.collapsed";

    @Nullable
    private static volatile EditorConfig instance;

    @FromAnyThread
    public static @NotNull EditorConfig getInstance() {
        if (instance == null) {
            synchronized (EditorConfig.class) {
                if (instance == null) {
                    instance = new EditorConfig();
                }
            }
        }
        return instance;
    }

    /**
     * The list of last opened asset folders.
     */
    @NotNull
    private final List<String> lastOpenedAssets;

    /**
     * The all settings.
     */
    @NotNull
    private final ConcurrentObjectDictionary<String, Object> settings;

    /**
     * The current asset folder.
     */
    @Nullable
    private volatile Path currentAsset;

    /**
     * The width of this screen.
     */
    private volatile int screenWidth;

    /**
     * The height of this screen.
     */
    private volatile int screenHeight;

    /**
     * The global left tool width.
     */
    private volatile int globalLeftToolWidth;

    /**
     * The global bottom tool width.
     */
    private volatile int globalBottomToolHeight;

    /**
     * Flag is for collapsing the global left tool.
     */
    private volatile boolean globalLeftToolCollapsed;

    /**
     * Flag is for collapsing the global bottom tool.
     */
    private volatile boolean globalBottomToolCollapsed;

    /**
     * Flag is for maximizing a window.
     */
    private volatile boolean maximized;

    /**
     * Flag is of showing analytics question.
     */
    private volatile boolean analyticsQuestion;

    public EditorConfig() {
        this.lastOpenedAssets = new ArrayList<>();
        this.settings = DictionaryFactory.newConcurrentAtomicObjectDictionary();
        init();
    }

    /**
     * Get the generic method to get generic values from settings.
     *
     * @param id   the setting id.
     * @param type the type.
     * @param def  the default value.
     * @param <T>  the setting's type.
     * @return the setting's value or default.
     */
    @FromAnyThread
    private synchronized <T> @Nullable T get(@NotNull String id, @NotNull Class<T> type, @Nullable T def) {

        var value = settings.getInReadLock(id, ObjectDictionary::get);

        if (value == null) {
            return def;
        } else if (type.isInstance(value)) {
            return ClassUtils.unsafeCast(value);
        }

        T result = null;

        if (type == Boolean.class) {

            if (value instanceof String) {
                result = ClassUtils.unsafeCast(Boolean.valueOf(value.toString()));
            }

        } else if (type == Integer.class) {

            if (value instanceof String) {
                result = ClassUtils.unsafeCast(Integer.valueOf(value.toString()));
            }

        } else if (type == Vector3f.class) {

            if (value instanceof String) {
                var values = value.toString().split(",");
                var x = Float.parseFloat(values[0]);
                var y = Float.parseFloat(values[1]);
                var z = Float.parseFloat(values[2]);
                result = ClassUtils.unsafeCast(new Vector3f(x, y, z));
            }

        } else if (Enum.class.isAssignableFrom(type)) {

            Class<Enum> enumType = ClassUtils.unsafeCast(type);

            if (value instanceof String) {
                var enumValue = Enum.valueOf(enumType, value.toString());
                result = ClassUtils.unsafeCast(enumValue);
            }

        } else if (Path.class.isAssignableFrom(type)) {
            if (value instanceof String) {
                var uri = Utils.get(value.toString(), URI::new);
                result = ClassUtils.unsafeCast(Paths.get(uri));
            }
        }

        if (result != null) {
            set(id, result);
            return result;
        }

        throw new IllegalArgumentException("Can't convert the value " + value + " to the type " + type);
    }

    /**
     * Set the new value of the setting by the id.
     *
     * @param id    the setting's id.
     * @param value the setting's value.
     */
    @FromAnyThread
    public synchronized void set(@NotNull String id, @Nullable Object value) {
        var stamp = settings.writeLock();
        try {

            if (value == null) {
                settings.remove(id);
            } else {
                settings.put(id, value);
            }

        } finally {
            settings.writeUnlock(stamp);
        }
    }

    @FromAnyThread
    public boolean getBoolean(@NotNull String id, boolean def) {
        return Boolean.TRUE.equals(get(id, Boolean.class, def));
    }

    @FromAnyThread
    public @Nullable Boolean getBoolean(@NotNull String id) {
        return get(id, Boolean.class, null);
    }

    @FromAnyThread
    public int getInteger(@NotNull String id, int def) {
        return ObjectUtils.notNull(get(id, Integer.class, def));
    }

    @FromAnyThread
    public @Nullable Integer getInteger(@NotNull String id) {
        return get(id, Integer.class, null);
    }

    @FromAnyThread
    public <T extends Enum<T>> @Nullable T getEnum(@NotNull String id, @NotNull Class<T> type) {
        return get(id, type, null);
    }

    @FromAnyThread
    public <T extends Enum<T>> @NotNull T getEnum(@NotNull String id, @NotNull T def) {
        return ObjectUtils.notNull(get(id, ClassUtils.unsafeCast(def.getClass()), def));
    }

    @FromAnyThread
    public @NotNull Vector3f getVector3f(@NotNull String id, @NotNull Vector3f def) {
        return ObjectUtils.notNull(get(id, Vector3f.class, def));
    }

    @FromAnyThread
    public @Nullable Path getFile(@NotNull String id) {
        return get(id, Path.class, null);
    }

    @FromAnyThread
    public @Nullable String getString(@NotNull String id) {
        return get(id, String.class, null);
    }

    /**
     * Get the last opened assets.
     *
     * @return The list of last opened asset folders.
     */
    @FromAnyThread
    public synchronized @NotNull List<String> getLastOpenedAssets() {
        return lastOpenedAssets;
    }

    /**
     * Add the new last opened asset folder.
     *
     * @param currentAsset the current asset
     */
    @FromAnyThread
    public synchronized void addOpenedAsset(@NotNull Path currentAsset) {

        var filePath = currentAsset.toString();
        var lastOpenedAssets = getLastOpenedAssets();
        lastOpenedAssets.remove(filePath);
        lastOpenedAssets.add(0, filePath);

        if (lastOpenedAssets.size() > 10) {
            lastOpenedAssets.remove(lastOpenedAssets.size() - 1);
        }
    }

    @Override
    public void assetDependencyNotFound(@Nullable AssetKey parentKey, @Nullable AssetKey dependentAssetKey) {
    }

    @Override
    public void assetLoaded(@NotNull AssetKey key) {
    }

    @Override
    public void assetRequested(@NotNull AssetKey key) {
        if (key instanceof TextureKey) {
            ((TextureKey) key).setAnisotropy(getInteger(PREF_ANISOTROPY, PREF_DEFAULT_ANISOTROPY));
        }
    }

    /**
     * Get the current asset folder.
     *
     * @return the current asset folder.
     */
    @FromAnyThread
    public @Nullable Path getCurrentAsset() {
        return currentAsset;
    }

    /**
     * Get an optional of the current asset folder.
     *
     * @return the optional of the current asset folder.
     */
    @FromAnyThread
    public @NotNull Optional<Path> getCurrentAssetOpt() {
        return Optional.ofNullable(currentAsset);
    }

    /**
     * Get the current asset folder with checking not null.
     *
     * @return the current asset folder.
     * @throws NullPointerException id the current asset is null.
     */
    @FromAnyThread
    public @NotNull Path requiredCurrentAsset() {
        return ObjectUtils.notNull(currentAsset);
    }

    /**
     * Set the current asset folder.
     *
     * @param currentAsset the current asset folder.
     */
    @FromAnyThread
    public void setCurrentAsset(@Nullable Path currentAsset) {
        this.currentAsset = currentAsset;
    }

    /**
     * Set the screen height.
     *
     * @param screenHeight the height of this screen.
     */
    @FromAnyThread
    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    /**
     * Set the screen width.
     *
     * @param screenWidth the width of this screen.
     */
    @FromAnyThread
    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    /**
     * Get the screen height.
     *
     * @return the height of this screen.
     */
    @FromAnyThread
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * Get the screen width.
     *
     * @return the width of this screen.
     */
    @FromAnyThread
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * Return the maximized state of editor's window.
     *
     * @return true is the editor's window is maximized.
     */
    @FromAnyThread
    public boolean isMaximized() {
        return maximized;
    }

    /**
     * Set the maximized state of editor's window.
     *
     * @param maximized true is the editor's window is maximized.
     */
    @FromAnyThread
    public void setMaximized(boolean maximized) {
        this.maximized = maximized;
    }

    /**
     * Get the global left tool width.
     *
     * @return the global left tool width.
     */
    @FromAnyThread
    public int getGlobalLeftToolWidth() {
        return globalLeftToolWidth;
    }

    /**
     * Get the global bottom tool height.
     *
     * @return the global bottom tool height.
     */
    @FromAnyThread
    public int getGlobalBottomToolHeight() {
        return globalBottomToolHeight;
    }

    /**
     * Set the global left tool width.
     *
     * @param globalLeftToolWidth the global left tool width.
     */
    @FromAnyThread
    public void setGlobalLeftToolWidth(int globalLeftToolWidth) {
        this.globalLeftToolWidth = globalLeftToolWidth;
    }

    /**
     * Set the global bottom tool height.
     *
     * @param globalBottomToolHeight the global bottom tool height.
     */
    @FromAnyThread
    public void setGlobalBottomToolHeight(int globalBottomToolHeight) {
        this.globalBottomToolHeight = globalBottomToolHeight;
    }

    /**
     * Set the global left tool collapsed.
     *
     * @param globalLeftToolCollapsed flag is for collapsing the global left tool.
     */
    @FromAnyThread
    public void setGlobalLeftToolCollapsed(boolean globalLeftToolCollapsed) {
        this.globalLeftToolCollapsed = globalLeftToolCollapsed;
    }

    /**
     * Set the global bottom tool collapsed.
     *
     * @param globalBottomToolCollapsed flag is for collapsing the global bottom tool.
     */
    @FromAnyThread
    public void setGlobalBottomToolCollapsed(boolean globalBottomToolCollapsed) {
        this.globalBottomToolCollapsed = globalBottomToolCollapsed;
    }

    /**
     * Is global left tool collapsed boolean.
     *
     * @return true if the global left tool is collapsed.
     */
    @FromAnyThread
    public boolean isGlobalLeftToolCollapsed() {
        return globalLeftToolCollapsed;
    }

    /**
     * Is global bottom tool collapsed boolean.
     *
     * @return true if the global bottom tool is collapsed.
     */
    @FromAnyThread
    public boolean isGlobalBottomToolCollapsed() {
        return globalBottomToolCollapsed;
    }

    /**
     * Is analytics question boolean.
     *
     * @return true if the question was showed.
     */
    public boolean isAnalyticsQuestion() {
        return analyticsQuestion;
    }

    /**
     * Sets analytics question.
     *
     * @param analyticsQuestion true if the question was showed.
     */
    public void setAnalyticsQuestion(boolean analyticsQuestion) {
        this.analyticsQuestion = analyticsQuestion;
    }

    /**
     * Get the jME settings.
     *
     * @return the the jME settings.
     */
    @FromAnyThread
    public AppSettings getSettings() {

        var graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        var device = graphicsEnvironment.getDefaultScreenDevice();
        var displayMode = device.getDisplayMode();

        var settings = new AppSettings(true);
        settings.setFrequency(displayMode.getRefreshRate());
        settings.setGammaCorrection(getBoolean(PREF_GAMMA_CORRECTION, PREF_DEFAULT_GAMMA_CORRECTION));
        settings.setResizable(true);
        // settings.putBoolean("GraphicsDebug", true);

        JmeToJfxIntegrator.prepareSettings(settings, getInteger(PREF_FRAME_RATE, PREF_DEFAULT_FRAME_RATE));

        return settings;
    }

    /**
     * Load user settings.
     */
    private void init() {

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_3)
                .start();

        var prefs = Preferences.userNodeForPackage(JmeApplication.class);
        var stamp = settings.writeLock();
        try {

            for (var key : prefs.keys()) {
                settings.put(key, prefs.get(key, null));
            }

        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        } finally {
            settings.writeUnlock(stamp);
        }

        this.maximized = prefs.getBoolean(PREF_SCREEN_MAXIMIZED, false);
        this.screenHeight = prefs.getInt(PREF_SCREEN_HEIGHT, 800);
        this.screenWidth = prefs.getInt(PREF_SCREEN_WIDTH, 1200);
        this.globalLeftToolWidth = prefs.getInt(PREF_OTHER_GLOBAL_LEFT_TOOL_WIDTH, 300);
        this.globalLeftToolCollapsed = prefs.getBoolean(PREF_OTHER_GLOBAL_LEFT_TOOL_COLLAPSED, false);
        this.globalBottomToolHeight = prefs.getInt(PREF_OTHER_GLOBAL_BOTTOM_TOOL_WIDTH, 300);
        this.globalBottomToolCollapsed = prefs.getBoolean(PREF_OTHER_GLOBAL_BOTTOM_TOOL_COLLAPSED, true);
        this.analyticsQuestion = prefs.getBoolean(PREF_OTHER_ANALYTICS_QUESTION, false);

        var currentAssetUri = prefs.get(PREF_ASSET_CURRENT_ASSET, null);

        if (currentAssetUri != null) {
            this.currentAsset = Utils.get(currentAssetUri, uri -> Paths.get(new URI(uri)));
        }

        if (currentAsset != null && !Files.exists(currentAsset)) {
            this.currentAsset = null;
        }

        var cameraAngle = getInteger(PREF_CAMERA_ANGLE, PREF_DEFAULT_CAMERA_ANGLE);

        System.setProperty("jfx.frame.transfer.camera.angle", String.valueOf(cameraAngle));

        var byteArray = prefs.getByteArray(PREF_ASSET_LAST_OPENED_ASSETS, null);
        if (byteArray == null) {
            return;
        }

        var lastOpenedAssets = getLastOpenedAssets();
        try {

            lastOpenedAssets.addAll(EditorUtil.deserialize(byteArray));

            for (var iterator = lastOpenedAssets.iterator(); iterator.hasNext(); ) {

                var assetUrl = iterator.next();
                var assetPath = Utils.get(assetUrl, uri -> Paths.get(uri));

                if (!Files.exists(assetPath)) {
                    iterator.remove();
                }
            }

        } catch (RuntimeException e) {
            LOGGER.warning(e);
        }

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_3)
                .finish(() -> "loading of the editor config");
    }

    /**
     * Save these settings.
     */
    @FromAnyThread
    public synchronized void save() {

        var prefs = Preferences.userNodeForPackage(JmeApplication.class);

        var stamp = settings.readLock();
        try {

            settings.forEach((key, value) -> {
                if (value instanceof Boolean) {
                    prefs.putBoolean(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    prefs.putInt(key, (Integer) value);
                }  else if (value instanceof Enum<?>) {
                    prefs.put(key, (((Enum) value).name()));
                } else if (value instanceof Path) {
                    prefs.put(key, ((Path) value).toUri().toString());
                } else if (value instanceof Vector3f) {
                    final Vector3f vector = (Vector3f) value;
                    prefs.put(key, vector.getX() + "," + vector.getY() + "," + vector.getZ());
                } else {
                    prefs.put(key, value.toString());
                }
            });

        } finally {
            settings.readUnlock(stamp);
        }

        prefs.putInt(PREF_SCREEN_HEIGHT, getScreenHeight());
        prefs.putInt(PREF_SCREEN_WIDTH, getScreenWidth());
        prefs.putBoolean(PREF_SCREEN_MAXIMIZED, isMaximized());
        prefs.putInt(PREF_OTHER_GLOBAL_LEFT_TOOL_WIDTH, getGlobalLeftToolWidth());
        prefs.putBoolean(PREF_OTHER_GLOBAL_LEFT_TOOL_COLLAPSED, isGlobalLeftToolCollapsed());
        prefs.putInt(PREF_OTHER_GLOBAL_BOTTOM_TOOL_WIDTH, getGlobalBottomToolHeight());
        prefs.putBoolean(PREF_OTHER_GLOBAL_BOTTOM_TOOL_COLLAPSED, isGlobalBottomToolCollapsed());
        prefs.putBoolean(PREF_OTHER_ANALYTICS_QUESTION, isAnalyticsQuestion());

        if (currentAsset != null && !Files.exists(currentAsset)) {
            currentAsset = null;
        }

        if (currentAsset != null) {
            prefs.put(PREF_ASSET_CURRENT_ASSET, currentAsset.toUri().toString());
        } else {
            prefs.remove(PREF_ASSET_CURRENT_ASSET);
        }

        prefs.putByteArray(PREF_ASSET_LAST_OPENED_ASSETS,
                EditorUtil.serialize((Serializable) getLastOpenedAssets()));

        try {
            prefs.flush();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }
}
