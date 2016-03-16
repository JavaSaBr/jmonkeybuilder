package com.ss.editor.config;

import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.TextureKey;
import com.jme3.system.AppSettings;
import com.ss.editor.Editor;
import com.ss.editor.EditorContext;
import com.ss.editor.util.EditorUtil;

import java.awt.*;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;

/**
 * Набор настроек, изменяемых пользователем.
 *
 * @author Ronn
 */
public final class EditorConfig implements AssetEventListener {

    private static final Logger LOGGER = LoggerManager.getLogger(EditorConfig.class);

    public static final String GRAPHICS_ALIAS = "Graphics";
    public static final String ASSET_ALIAS = "ASSET";

    public static final String PREF_GRAPHIC_SCREEN_SIZE = GRAPHICS_ALIAS + "." + "screenSize";
    public static final String PREF_GRAPHIC_ANISOTROPY = GRAPHICS_ALIAS + "." + "anisotropy";
    public static final String PREF_GRAPHIC_FXAA = GRAPHICS_ALIAS + "." + "fxaa";
    public static final String PREF_GRAPHIC_FULLSCREEN = GRAPHICS_ALIAS + "." + "fullscreen";

    public static final String PREF_CURRENT_ASSET = ASSET_ALIAS + "." + "currentAsset";
    public static final String PREF_LAST_OPENED_ASSETS = ASSET_ALIAS + "." + "lastOpenedAssets";

    private static EditorConfig instance;

    public static EditorConfig getInstance() {

        if (instance == null) {

            final EditorConfig config = new EditorConfig();
            config.init();

            instance = config;
        }

        return instance;
    }

    /**
     * Список последних открываемых asset.
     */
    private final List<String> lastOpenedAssets;

    /**
     * Используемое разрешение экрана.
     */
    private volatile ScreenSize screenSize;

    /**
     * Уровень анизатропной фильтрации.
     */
    private volatile int anisotropy;

    /**
     * Включено ли FXAA.
     */
    private volatile boolean fxaa;

    /**
     * Включен ли полноэкранный режим.
     */
    private volatile boolean fullscreen;

    /**
     * Текущий выбранный Asset.
     */
    private volatile Path currentAsset;

    public EditorConfig() {
        this.lastOpenedAssets = new ArrayList<>();
    }

    /**
     * @return список последних открываемых asset.
     */
    public List<String> getLastOpenedAssets() {
        return lastOpenedAssets;
    }

    /**
     * Запоминание открытияуказанного Asset.
     */
    public void addOpenedAsset(final Path currentAsset) {

        final String filePath = currentAsset.toString();

        final List<String> lastOpenedAssets = getLastOpenedAssets();
        lastOpenedAssets.remove(filePath);
        lastOpenedAssets.add(0, filePath);

        if (lastOpenedAssets.size() > 10) {
            lastOpenedAssets.remove(lastOpenedAssets.size() - 1);
        }
    }

    @Override
    public void assetDependencyNotFound(final AssetKey parentKey, final AssetKey dependentAssetKey) {
    }

    @Override
    public void assetLoaded(final AssetKey key) {
    }

    @Override
    public void assetRequested(final AssetKey key) {
        if (key instanceof TextureKey) {
            ((TextureKey) key).setAnisotropy(getAnisotropy());
        }
    }

    /**
     * @param anisotropy уровень анизатропной фильтрации.
     */
    public void setAnisotropy(int anisotropy) {
        this.anisotropy = anisotropy;
    }

    /**
     * @return уровень анизатропной фильтрации.
     */
    public int getAnisotropy() {
        return anisotropy;
    }

    /**
     * @param fxaa включено ли FXAA.
     */
    public void setFXAA(boolean fxaa) {
        this.fxaa = fxaa;
    }

    /**
     * @return включено ли FXAA.
     */
    public boolean isFXAA() {
        return fxaa;
    }

    /**
     * @return используемое разрешение экрана.
     */
    public ScreenSize getScreenSize() {
        return screenSize;
    }

    /**
     * @param screenSize используемое разрешение экрана.
     */
    public void setScreenSize(final ScreenSize screenSize) {
        this.screenSize = screenSize;
    }

    /**
     * @return текущий выбранный Asset.
     */
    public Path getCurrentAsset() {
        return currentAsset;
    }

    /**
     * @param currentAsset текущий выбранный Asset.
     */
    public void setCurrentAsset(final Path currentAsset) {
        this.currentAsset = currentAsset;
    }

    /**
     * @param fullscreen включен ли полноэкранный режим.
     */
    public void setFullscreen(final boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    /**
     * @return включен ли полноэкранный режим.
     */
    public boolean isFullscreen() {
        return fullscreen;
    }

    /**
     * @return настройки движка.
     */
    public AppSettings getSettings() {

        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice device = graphicsEnvironment.getDefaultScreenDevice();
        final DisplayMode displayMode = device.getDisplayMode();

        final AppSettings settings = new AppSettings(true);
        settings.setRenderer("CUSTOM" + EditorContext.class.getName());
        settings.setTitle(Config.TITLE + " " + Config.VERSION);
        settings.setFullscreen(isFullscreen() && screenSize.isFullscreenSupported());
        settings.setResolution(screenSize.getWidth(), screenSize.getHeight());
        settings.setFrequency(displayMode.getRefreshRate());
        settings.setFrameRate(60);
        //settings.setResizable(true);
        // settings.putBoolean("GraphicsDebug", true);

        return settings;
    }

    /**
     * Инициализация.
     */
    private void init() {

        final Preferences prefs = Preferences.userNodeForPackage(Editor.class);

        this.screenSize = ScreenSize.sizeOf(prefs.get(PREF_GRAPHIC_SCREEN_SIZE, "1244x700"));
        this.anisotropy = prefs.getInt(PREF_GRAPHIC_ANISOTROPY, 0);
        this.fxaa = prefs.getBoolean(PREF_GRAPHIC_FXAA, false);
        this.fullscreen = prefs.getBoolean(PREF_GRAPHIC_FULLSCREEN, false);

        final String currentAssetURI = prefs.get(PREF_CURRENT_ASSET, null);

        if (currentAssetURI != null) {
            try {
                this.currentAsset = Paths.get(new URI(currentAssetURI));
            } catch (URISyntaxException e) {
                LOGGER.error(e);
            }
        }

        final List<String> deserializeLastOpened = EditorUtil.deserialize(prefs.getByteArray(PREF_LAST_OPENED_ASSETS, null));

        if (deserializeLastOpened != null) {
            getLastOpenedAssets().addAll(deserializeLastOpened);
        }
    }

    /**
     * Сохранения настроек.
     */
    public void save() {

        final Preferences prefs = Preferences.userNodeForPackage(Editor.class);

        prefs.put(PREF_GRAPHIC_SCREEN_SIZE, getScreenSize().toString());
        prefs.putInt(PREF_GRAPHIC_ANISOTROPY, getAnisotropy());
        prefs.putBoolean(PREF_GRAPHIC_FXAA, isFXAA());
        prefs.putBoolean(PREF_GRAPHIC_FULLSCREEN, isFullscreen());

        if (currentAsset != null) {
            prefs.put(PREF_CURRENT_ASSET, currentAsset.toUri().toString());
        } else {
            prefs.remove(PREF_CURRENT_ASSET);
        }

        if (currentAsset != null && !Files.exists(currentAsset)) {
            currentAsset = null;
        }

        final List<String> lastOpenedAssets = getLastOpenedAssets();

        prefs.putByteArray(PREF_LAST_OPENED_ASSETS, EditorUtil.serialize((Serializable) lastOpenedAssets));

        try {
            prefs.flush();
        } catch (final BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }
}
