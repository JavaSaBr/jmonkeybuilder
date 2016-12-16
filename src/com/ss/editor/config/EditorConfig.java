package com.ss.editor.config;

import static rlib.util.Util.safeGet;

import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.TextureKey;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3x.jfx.injfx.JmeToJFXIntegrator;
import com.ss.editor.Editor;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;

/**
 * The user configuration of this editor.
 *
 * @author JavaSaBr
 */
public final class EditorConfig implements AssetEventListener {

    private static final Logger LOGGER = LoggerManager.getLogger(EditorConfig.class);

    public static final String GRAPHICS_ALIAS = "Graphics";
    public static final String SCREEN_ALIAS = "Screen";
    public static final String ASSET_ALIAS = "ASSET";
    public static final String ASSET_OTHER = "Other";

    public static final String PREF_SCREEN_WIDTH = SCREEN_ALIAS + "." + "screenWidth";
    public static final String PREF_SCREEN_HEIGHT = SCREEN_ALIAS + "." + "screenHeight";
    public static final String PREF_SCREEN_MAXIMIZED = SCREEN_ALIAS + "." + "screenMaximized";

    public static final String PREF_GRAPHIC_ANISOTROPY = GRAPHICS_ALIAS + "." + "anisotropy";
    public static final String PREF_GRAPHIC_FXAA = GRAPHICS_ALIAS + "." + "fxaa";
    public static final String PREF_GRAPHIC_GAMA_CORRECTION = GRAPHICS_ALIAS + "." + "gammaCorrection";
    public static final String PREF_GRAPHIC_TONEMAP_FILTER = GRAPHICS_ALIAS + "." + "toneMapFilter";
    public static final String PREF_GRAPHIC_TONEMAP_FILTER_WHITE_POINT = GRAPHICS_ALIAS + "." + "toneMapFilterWhitePoint";

    public static final String PREF_CURRENT_ASSET = ASSET_ALIAS + "." + "currentAsset";
    public static final String PREF_LAST_OPENED_ASSETS = ASSET_ALIAS + "." + "lastOpenedAssets";

    public static final String PREF_ADDITIONAL_CLASSPATH = ASSET_OTHER + "." + "additionalClasspath";
    public static final String PREF_GLOBAL_TOOL_WIDTH = ASSET_OTHER + "." + "globalToolWidth";
    public static final String PREF_GLOBAL_TOOL_COLLAPSED = ASSET_OTHER + "." + "globalToolCollapsed";

    private static volatile EditorConfig instance;

    public static EditorConfig getInstance() {

        if (instance == null) {

            final EditorConfig config = new EditorConfig();
            config.init();

            instance = config;
        }

        return instance;
    }

    /**
     * The list of last opened asset folders.
     */
    private final List<String> lastOpenedAssets;

    /**
     * The current white point for the tone map filter.
     */
    private volatile Vector3f toneMapFilterWhitePoint;

    /**
     * The current level of the anisotropy.
     */
    private volatile int anisotropy;

    /**
     * The width of this screen.
     */
    private volatile int screenWidth;

    /**
     * The height of this screen.
     */
    private volatile int screenHeight;

    /**
     * The global tool width.
     */
    private volatile int globalToolWidth;

    /**
     * Flag is for collapsing the global tool.
     */
    private volatile boolean globalToolCollapsed;

    /**
     * Flag is for enabling the FXAA.
     */
    private volatile boolean fxaa;

    /**
     * Flag is for enabling the gamma correction.
     */
    private volatile boolean gammaCorrection;

    /**
     * Flag is for enabling the tone map filter.
     */
    private volatile boolean toneMapFilter;

    /**
     * Flag is for maximizing a window.
     */
    private volatile boolean maximized;

    /**
     * The current asset folder.
     */
    private volatile Path currentAsset;

    /**
     * The path to the folder with additional classpath.
     */
    private volatile Path additionalClasspath;

    public EditorConfig() {
        this.lastOpenedAssets = new ArrayList<>();
    }

    /**
     * @return The list of last opened asset folders.
     */
    @NotNull
    public List<String> getLastOpenedAssets() {
        return lastOpenedAssets;
    }

    /**
     * Add the new last opened asset folder.
     */
    public void addOpenedAsset(@NotNull final Path currentAsset) {

        final String filePath = currentAsset.toString();

        final List<String> lastOpenedAssets = getLastOpenedAssets();
        lastOpenedAssets.remove(filePath);
        lastOpenedAssets.add(0, filePath);

        if (lastOpenedAssets.size() > 10) lastOpenedAssets.remove(lastOpenedAssets.size() - 1);
    }

    @Override
    public void assetDependencyNotFound(@Nullable final AssetKey parentKey, @Nullable final AssetKey dependentAssetKey) {
    }

    @Override
    public void assetLoaded(@NotNull final AssetKey key) {
    }

    @Override
    public void assetRequested(@NotNull final AssetKey key) {
        if (key instanceof TextureKey) {
            ((TextureKey) key).setAnisotropy(getAnisotropy());
        }
    }

    /**
     * @param anisotropy the new level of the anisotropy.
     */
    public void setAnisotropy(final int anisotropy) {
        this.anisotropy = anisotropy;
    }

    /**
     * @return the current level of the anisotropy.
     */
    public int getAnisotropy() {
        return anisotropy;
    }

    /**
     * @param fxaa flag is for enabling the FXAA.
     */
    public void setFXAA(final boolean fxaa) {
        this.fxaa = fxaa;
    }

    /**
     * @return flag is for enabling the FXAA.
     */
    public boolean isFXAA() {
        return fxaa;
    }

    /**
     * @return the current asset folder.
     */
    @Nullable
    public Path getCurrentAsset() {
        return currentAsset;
    }

    /**
     * @param currentAsset the new current asset folder.
     */
    public void setCurrentAsset(@Nullable final Path currentAsset) {
        this.currentAsset = currentAsset;
    }

    /**
     * @return путь к папке с дополнительным classpath.
     */
    @Nullable
    public Path getAdditionalClasspath() {
        return additionalClasspath;
    }

    /**
     * @param additionalClasspath путь к папке с дополнительным classpath.
     */
    public void setAdditionalClasspath(@Nullable final Path additionalClasspath) {
        this.additionalClasspath = additionalClasspath;
    }

    /**
     * @return flag is for enabling the gamma correction.
     */
    public boolean isGammaCorrection() {
        return gammaCorrection;
    }

    /**
     * @param gammaCorrection flag is for enabling the gamma correction.
     */
    public void setGammaCorrection(final boolean gammaCorrection) {
        this.gammaCorrection = gammaCorrection;
    }

    /**
     * @return flag is for enabling the tone map filter.
     */
    public boolean isToneMapFilter() {
        return toneMapFilter;
    }

    /**
     * @param toneMapFilter flag is for enabling the tone map filter.
     */
    public void setToneMapFilter(final boolean toneMapFilter) {
        this.toneMapFilter = toneMapFilter;
    }

    /**
     * @return the current white point for the tone map filter.
     */
    @NotNull
    public Vector3f getToneMapFilterWhitePoint() {
        return toneMapFilterWhitePoint;
    }

    /**
     * @param toneMapFilterWhitePoint the new white point for the tone map filter.
     */
    public void setToneMapFilterWhitePoint(@NotNull final Vector3f toneMapFilterWhitePoint) {
        this.toneMapFilterWhitePoint = toneMapFilterWhitePoint;
    }

    /**
     * @param screenHeight the height of this screen.
     */
    public void setScreenHeight(final int screenHeight) {
        this.screenHeight = screenHeight;
    }

    /**
     * @param screenWidth the width of this screen.
     */
    public void setScreenWidth(final int screenWidth) {
        this.screenWidth = screenWidth;
    }

    /**
     * @return the height of this screen.
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * @return the width of this screen.
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * @return true is a window is maximized.
     */
    public boolean isMaximized() {
        return maximized;
    }

    /**
     * @param maximized flag is for maximizing a window.
     */
    public void setMaximized(final boolean maximized) {
        this.maximized = maximized;
    }

    /**
     * @return the global tool width.
     */
    public int getGlobalToolWidth() {
        return globalToolWidth;
    }

    /**
     * @param globalToolWidth the global tool width.
     */
    public void setGlobalToolWidth(final int globalToolWidth) {
        this.globalToolWidth = globalToolWidth;
    }

    /**
     * @param globalToolCollapsed flag is for collapsing the global tool.
     */
    public void setGlobalToolCollapsed(final boolean globalToolCollapsed) {
        this.globalToolCollapsed = globalToolCollapsed;
    }

    /**
     * @return true if the global tool is collapsed.
     */
    public boolean isGlobalToolCollapsed() {
        return globalToolCollapsed;
    }

    /**
     * @return the settings for JME.
     */
    public AppSettings getSettings() {

        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice device = graphicsEnvironment.getDefaultScreenDevice();
        final DisplayMode displayMode = device.getDisplayMode();

        final AppSettings settings = new AppSettings(true);
        settings.setFrequency(displayMode.getRefreshRate());
        settings.setGammaCorrection(isGammaCorrection());
        settings.setResizable(true);
        // settings.putBoolean("GraphicsDebug", true);

        try {

            final BufferedImage[] icons = new BufferedImage[5];
            icons[0] = ImageIO.read(EditorUtil.getInputStream("/ui/icons/app/SSEd256.png"));
            icons[1] = ImageIO.read(EditorUtil.getInputStream("/ui/icons/app/SSEd128.png"));
            icons[2] = ImageIO.read(EditorUtil.getInputStream("/ui/icons/app/SSEd64.png"));
            icons[3] = ImageIO.read(EditorUtil.getInputStream("/ui/icons/app/SSEd32.png"));
            icons[4] = ImageIO.read(EditorUtil.getInputStream("/ui/icons/app/SSEd16.png"));

            settings.setIcons(icons);
        } catch (final IOException e) {
            LOGGER.warning(e);
        }

        JmeToJFXIntegrator.prepareSettings(settings, displayMode.getRefreshRate());

        return settings;
    }

    /**
     * Load user settings.
     */
    private void init() {

        final Preferences prefs = Preferences.userNodeForPackage(Editor.class);

        this.anisotropy = prefs.getInt(PREF_GRAPHIC_ANISOTROPY, 0);
        this.fxaa = prefs.getBoolean(PREF_GRAPHIC_FXAA, false);
        this.gammaCorrection = prefs.getBoolean(PREF_GRAPHIC_GAMA_CORRECTION, false);
        this.toneMapFilter = prefs.getBoolean(PREF_GRAPHIC_TONEMAP_FILTER, false);
        this.maximized = prefs.getBoolean(PREF_SCREEN_MAXIMIZED, false);
        this.screenHeight = prefs.getInt(PREF_SCREEN_HEIGHT, 800);
        this.screenWidth = prefs.getInt(PREF_SCREEN_WIDTH, 1200);
        this.globalToolWidth = prefs.getInt(PREF_GLOBAL_TOOL_WIDTH, 300);
        this.globalToolCollapsed = prefs.getBoolean(PREF_GLOBAL_TOOL_COLLAPSED, false);

        final String currentAssetURI = prefs.get(PREF_CURRENT_ASSET, null);

        if (currentAssetURI != null) {
            this.currentAsset = safeGet(currentAssetURI, uri -> Paths.get(new URI(uri)));
        }

        final String classpathURI = prefs.get(PREF_ADDITIONAL_CLASSPATH, null);

        if (classpathURI != null) {
            this.additionalClasspath = safeGet(classpathURI, uri -> Paths.get(new URI(uri)));
        }

        this.toneMapFilterWhitePoint = new Vector3f(11, 11, 11);

        final String whitePoint = prefs.get(PREF_GRAPHIC_TONEMAP_FILTER_WHITE_POINT, null);
        final String[] coords = whitePoint == null ? null : whitePoint.split(",", 3);

        if (coords != null && coords.length > 2) {
            try {
                toneMapFilterWhitePoint.setX(Float.parseFloat(coords[0]));
                toneMapFilterWhitePoint.setY(Float.parseFloat(coords[1]));
                toneMapFilterWhitePoint.setZ(Float.parseFloat(coords[2]));
            } catch (NumberFormatException e) {
                LOGGER.error(e);
            }
        }

        final byte[] byteArray = prefs.getByteArray(PREF_LAST_OPENED_ASSETS, null);
        if (byteArray == null) return;

        final List<String> lastOpenedAssets = getLastOpenedAssets();
        try {
            lastOpenedAssets.addAll(EditorUtil.deserialize(byteArray));
        } catch (final RuntimeException e) {
            LOGGER.warning(e);
        }
    }

    /**
     * Save these settings.
     */
    public void save() {

        final Preferences prefs = Preferences.userNodeForPackage(Editor.class);
        prefs.putInt(PREF_GRAPHIC_ANISOTROPY, getAnisotropy());
        prefs.putBoolean(PREF_GRAPHIC_FXAA, isFXAA());
        prefs.putBoolean(PREF_GRAPHIC_GAMA_CORRECTION, isGammaCorrection());
        prefs.putBoolean(PREF_GRAPHIC_TONEMAP_FILTER, isToneMapFilter());
        prefs.putInt(PREF_SCREEN_HEIGHT, getScreenHeight());
        prefs.putInt(PREF_SCREEN_WIDTH, getScreenWidth());
        prefs.putBoolean(PREF_SCREEN_MAXIMIZED, isMaximized());
        prefs.putInt(PREF_GLOBAL_TOOL_WIDTH, getGlobalToolWidth());
        prefs.putBoolean(PREF_GLOBAL_TOOL_COLLAPSED, isGlobalToolCollapsed());

        final Vector3f whitePoint = getToneMapFilterWhitePoint();

        prefs.put(PREF_GRAPHIC_TONEMAP_FILTER_WHITE_POINT, whitePoint.getX() + "," + whitePoint.getY() + "," + whitePoint.getZ());

        if (currentAsset != null && !Files.exists(currentAsset)) currentAsset = null;
        if (additionalClasspath != null && !Files.exists(additionalClasspath))
            additionalClasspath = null;

        if (currentAsset != null) {
            prefs.put(PREF_CURRENT_ASSET, currentAsset.toUri().toString());
        } else {
            prefs.remove(PREF_CURRENT_ASSET);
        }

        if (additionalClasspath != null) {
            prefs.put(PREF_ADDITIONAL_CLASSPATH, additionalClasspath.toUri().toString());
        } else {
            prefs.remove(PREF_ADDITIONAL_CLASSPATH);
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
