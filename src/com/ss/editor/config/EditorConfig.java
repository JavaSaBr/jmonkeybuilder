package com.ss.editor.config;

import static com.ss.editor.util.OpenGLVersion.GL_32;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static com.ss.rlib.util.Utils.get;
import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.TextureKey;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3x.jfx.injfx.JmeToJFXIntegrator;
import com.ss.editor.Editor;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.css.CssColorTheme;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.OpenGLVersion;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
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

/**
 * The user configuration of this editor.
 *
 * @author JavaSaBr
 */
public final class EditorConfig implements AssetEventListener {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(EditorConfig.class);

    private static final String GRAPHICS_ALIAS = "Graphics";
    private static final String SCREEN_ALIAS = "Screen";
    private static final String ASSET_ALIAS = "Asset";
    private static final String OTHER_ALIAS = "Other";
    private static final String EDITING_ALIAS = "Editing";

    private static final String PREF_SCREEN_WIDTH = SCREEN_ALIAS + "." + "screenWidth";
    private static final String PREF_SCREEN_HEIGHT = SCREEN_ALIAS + "." + "screenHeight";
    private static final String PREF_SCREEN_MAXIMIZED = SCREEN_ALIAS + "." + "screenMaximized";

    private static final String PREF_GRAPHIC_OPEN_GL = GRAPHICS_ALIAS + "." + "openGL";
    private static final String PREF_GRAPHIC_ANISOTROPY = GRAPHICS_ALIAS + "." + "anisotropy";
    private static final String PREF_GRAPHIC_FRAME_RATE = GRAPHICS_ALIAS + "." + "frameRate";
    private static final String PREF_GRAPHIC_CAMERA_ANGLE = GRAPHICS_ALIAS + "." + "cameraAngle";
    private static final String PREF_GRAPHIC_FXAA = GRAPHICS_ALIAS + "." + "fxaa";
    private static final String PREF_GRAPHIC_GAMA_CORRECTION = GRAPHICS_ALIAS + "." + "gammaCorrection";
    private static final String PREF_GRAPHIC_STOP_RENDER_ON_LOST_FOCUS = GRAPHICS_ALIAS + "." + "stopRenderOnLostFocus";
    private static final String PREF_GRAPHIC_TONEMAP_FILTER = GRAPHICS_ALIAS + "." + "toneMapFilter";
    private static final String PREF_GRAPHIC_TONEMAP_FILTER_WHITE_POINT = GRAPHICS_ALIAS + "." + "toneMapFilterWhitePoint";

    private static final String PREF_ASSET_CURRENT_ASSET = ASSET_ALIAS + "." + "currentAsset";
    private static final String PREF_ASSET_LAST_OPENED_ASSETS = ASSET_ALIAS + "." + "lastOpenedAssets";

    private static final String PREF_OTHER_ADDITIONAL_CLASSPATH = OTHER_ALIAS + "." + "additionalClasspath";
    private static final String PREF_OTHER_ADDITIONAL_ENVS = OTHER_ALIAS + "." + "additionalEnvs";
    private static final String PREF_OTHER_THEME = OTHER_ALIAS + "." + "theme";
    private static final String PREF_OTHER_ANALYTICS = OTHER_ALIAS + "." + "analytics";
    private static final String PREF_OTHER_ANALYTICS_QUESTION = OTHER_ALIAS + "." + "analyticsQuestion" + Config.STRING_VERSION;

    private static final String PREF_OTHER_GLOBAL_LEFT_TOOL_WIDTH = OTHER_ALIAS + "." + "globalLeftToolWidth";
    private static final String PREF_OTHER_GLOBAL_LEFT_TOOL_COLLAPSED = OTHER_ALIAS + "." + "globalLeftToolCollapsed";
    private static final String PREF_OTHER_GLOBAL_BOTTOM_TOOL_WIDTH = OTHER_ALIAS + "." + "globalBottomToolHeight";
    private static final String PREF_OTHER_GLOBAL_BOTTOM_TOOL_COLLAPSED = OTHER_ALIAS + "." + "globalBottomToolCollapsed";
    
    private static final String PREF_EDITING_AUTO_TANGENT_GENERATING = EDITING_ALIAS + "." + "autoTangentGenerating";
    private static final String PREF_EDITING_DEFAULT_USE_FLIPPED_TEXTURE = EDITING_ALIAS + "." + "defaultUseFlippedTexture";
    private static final String PREF_EDITING_CAMERA_LAMP_ENABLED = EDITING_ALIAS + "." + "defaultCameraLampEnabled";


    @Nullable
    private static volatile EditorConfig instance;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
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
    @NotNull
    private final List<String> lastOpenedAssets;

    /**
     * The current white point for the tone map filter.
     */
    @Nullable
    private volatile Vector3f toneMapFilterWhitePoint;

    /**
     * The current asset folder.
     */
    @Nullable
    private volatile Path currentAsset;

    /**
     * The current open GL version.
     */
    @Nullable
    private volatile OpenGLVersion openGLVersion;

    /**
     * The path to the folder with additional classpath.
     */
    @Nullable
    private volatile Path additionalClasspath;

    /**
     * The path to the folder with additional envs.
     */
    @Nullable
    private volatile Path additionalEnvs;

    /**
     * The current level of the anisotropy.
     */
    private volatile int anisotropy;

    /**
     * The current frame rate.
     */
    private volatile int frameRate;

    /**
     * The current camera angle.
     */
    private volatile int cameraAngle;

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
     * The current theme.
     */
    private volatile int theme;

    /**
     * Flag is for collapsing the global left tool.
     */
    private volatile boolean globalLeftToolCollapsed;

    /**
     * Flag is for collapsing the global bottom tool.
     */
    private volatile boolean globalBottomToolCollapsed;

    /**
     * Flag is for enabling the FXAA.
     */
    private volatile boolean fxaa;

    /**
     * Flag is for enabling the gamma correction.
     */
    private volatile boolean gammaCorrection;

    /**
     * Flag is for enabling stoping render on lost focus.
     */
    private volatile boolean stopRenderOnLostFocus;

    /**
     * Flag is for enabling the tone map filter.
     */
    private volatile boolean toneMapFilter;

    /**
     * Flag is for maximizing a window.
     */
    private volatile boolean maximized;

    /**
     * Flag is of enabling analytics.
     */
    private volatile boolean analytics;

    /**
     * Flag is of enabling auto tangent generating.
     */
    private volatile boolean autoTangentGenerating;

    /**
     * Flag is of enabling using flip textures by default.
     */
    private volatile boolean defaultUseFlippedTexture;

    /**
     * Flag is of enabling camera lamp in editors by default.
     */
    private volatile boolean defaultEditorCameraEnabled;

    /**
     * Flag is of showing analytics question.
     */
    private volatile boolean analyticsQuestion;

    /**
     * Instantiates a new Editor config.
     */
    public EditorConfig() {
        this.lastOpenedAssets = new ArrayList<>();
    }

    /**
     * Gets last opened assets.
     *
     * @return The list of last opened asset folders.
     */
    @NotNull
    @FromAnyThread
    public synchronized List<String> getLastOpenedAssets() {
        return lastOpenedAssets;
    }

    /**
     * Add the new last opened asset folder.
     *
     * @param currentAsset the current asset
     */
    @FromAnyThread
    public synchronized void addOpenedAsset(@NotNull final Path currentAsset) {

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
     * Sets anisotropy.
     *
     * @param anisotropy the new level of the anisotropy.
     */
    @FromAnyThread
    public void setAnisotropy(final int anisotropy) {
        this.anisotropy = anisotropy;
    }

    /**
     * Gets anisotropy.
     *
     * @return the current level of the anisotropy.
     */
    @FromAnyThread
    public int getAnisotropy() {
        return anisotropy;
    }

    /**
     * Sets fxaa.
     *
     * @param fxaa flag is for enabling the FXAA.
     */
    @FromAnyThread
    public void setFXAA(final boolean fxaa) {
        this.fxaa = fxaa;
    }

    /**
     * Is fxaa boolean.
     *
     * @return flag is for enabling the FXAA.
     */
    @FromAnyThread
    public boolean isFXAA() {
        return fxaa;
    }

    /**
     * Gets current asset.
     *
     * @return the current asset folder.
     */
    @Nullable
    @FromAnyThread
    public Path getCurrentAsset() {
        return currentAsset;
    }

    /**
     * Sets current asset.
     *
     * @param currentAsset the new current asset folder.
     */
    @FromAnyThread
    public void setCurrentAsset(@Nullable final Path currentAsset) {
        this.currentAsset = currentAsset;
    }

    /**
     * Gets additional classpath.
     *
     * @return путь к папке с дополнительным classpath.
     */
    @Nullable
    @FromAnyThread
    public Path getAdditionalClasspath() {
        return additionalClasspath;
    }

    /**
     * Sets additional classpath.
     *
     * @param additionalClasspath путь к папке с дополнительным classpath.
     */
    @FromAnyThread
    public void setAdditionalClasspath(@Nullable final Path additionalClasspath) {
        this.additionalClasspath = additionalClasspath;
    }

    /**
     * Gets additional envs.
     *
     * @return the path to the folder with additional envs.
     */
    @Nullable
    @FromAnyThread
    public Path getAdditionalEnvs() {
        return additionalEnvs;
    }

    /**
     * Sets additional envs.
     *
     * @param additionalEnvs the path to the folder with additional envs.
     */
    @FromAnyThread
    public void setAdditionalEnvs(@Nullable final Path additionalEnvs) {
        this.additionalEnvs = additionalEnvs;
    }

    /**
     * Is gamma correction boolean.
     *
     * @return flag is for enabling the gamma correction.
     */
    @FromAnyThread
    public boolean isGammaCorrection() {
        return gammaCorrection;
    }

    /**
     * Sets gamma correction.
     *
     * @param gammaCorrection flag is for enabling the gamma correction.
     */
    @FromAnyThread
    public void setGammaCorrection(final boolean gammaCorrection) {
        this.gammaCorrection = gammaCorrection;
    }

    /**
     * Is stop render on lost focus boolean.
     *
     * @return true if need to stop render on lost focus.
     */
    @FromAnyThread
    public boolean isStopRenderOnLostFocus() {
        return stopRenderOnLostFocus;
    }

    /**
     * Sets stop render on lost focus.
     *
     * @param stopRenderOnLostFocus true if need to stop render on lost focus.
     */
    @FromAnyThread
    public void setStopRenderOnLostFocus(final boolean stopRenderOnLostFocus) {
        this.stopRenderOnLostFocus = stopRenderOnLostFocus;
    }

    /**
     * Is tone map filter boolean.
     *
     * @return flag is for enabling the tone map filter.
     */
    @FromAnyThread
    public boolean isToneMapFilter() {
        return toneMapFilter;
    }

    /**
     * Sets tone map filter.
     *
     * @param toneMapFilter flag is for enabling the tone map filter.
     */
    @FromAnyThread
    public void setToneMapFilter(final boolean toneMapFilter) {
        this.toneMapFilter = toneMapFilter;
    }

    /**
     * Gets tone map filter white point.
     *
     * @return the current white point for the tone map filter.
     */
    @NotNull
    @FromAnyThread
    public Vector3f getToneMapFilterWhitePoint() {
        return notNull(toneMapFilterWhitePoint);
    }

    /**
     * Sets tone map filter white point.
     *
     * @param toneMapFilterWhitePoint the new white point for the tone map filter.
     */
    @FromAnyThread
    public void setToneMapFilterWhitePoint(@NotNull final Vector3f toneMapFilterWhitePoint) {
        this.toneMapFilterWhitePoint = toneMapFilterWhitePoint;
    }

    /**
     * Sets screen height.
     *
     * @param screenHeight the height of this screen.
     */
    @FromAnyThread
    public void setScreenHeight(final int screenHeight) {
        this.screenHeight = screenHeight;
    }

    /**
     * Sets screen width.
     *
     * @param screenWidth the width of this screen.
     */
    @FromAnyThread
    public void setScreenWidth(final int screenWidth) {
        this.screenWidth = screenWidth;
    }

    /**
     * Gets screen height.
     *
     * @return the height of this screen.
     */
    @FromAnyThread
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * Gets screen width.
     *
     * @return the width of this screen.
     */
    @FromAnyThread
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * Is maximized boolean.
     *
     * @return true is a window is maximized.
     */
    @FromAnyThread
    public boolean isMaximized() {
        return maximized;
    }

    /**
     * Sets maximized.
     *
     * @param maximized flag is for maximizing a window.
     */
    @FromAnyThread
    public void setMaximized(final boolean maximized) {
        this.maximized = maximized;
    }

    /**
     * Gets global left tool width.
     *
     * @return the global left tool width.
     */
    @FromAnyThread
    public int getGlobalLeftToolWidth() {
        return globalLeftToolWidth;
    }

    /**
     * Gets global bottom tool height.
     *
     * @return the global bottom tool height.
     */
    @FromAnyThread
    public int getGlobalBottomToolHeight() {
        return globalBottomToolHeight;
    }

    /**
     * Sets global left tool width.
     *
     * @param globalLeftToolWidth the global left tool width.
     */
    @FromAnyThread
    public void setGlobalLeftToolWidth(final int globalLeftToolWidth) {
        this.globalLeftToolWidth = globalLeftToolWidth;
    }

    /**
     * Sets global bottom tool height.
     *
     * @param globalBottomToolHeight the global bottom tool height.
     */
    @FromAnyThread
    public void setGlobalBottomToolHeight(final int globalBottomToolHeight) {
        this.globalBottomToolHeight = globalBottomToolHeight;
    }

    /**
     * Sets global left tool collapsed.
     *
     * @param globalLeftToolCollapsed flag is for collapsing the global left tool.
     */
    @FromAnyThread
    public void setGlobalLeftToolCollapsed(final boolean globalLeftToolCollapsed) {
        this.globalLeftToolCollapsed = globalLeftToolCollapsed;
    }

    /**
     * Sets global bottom tool collapsed.
     *
     * @param globalBottomToolCollapsed flag is for collapsing the global bottom tool.
     */
    @FromAnyThread
    public void setGlobalBottomToolCollapsed(final boolean globalBottomToolCollapsed) {
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
     * Sets analytics.
     *
     * @param analytics true if you want to enable analytics.
     */
    @FromAnyThread
    public void setAnalytics(final boolean analytics) {
        this.analytics = analytics;
    }

    /**
     * Is analytics boolean.
     *
     * @return true if analytics is enabled.
     */
    @FromAnyThread
    public boolean isAnalytics() {
        return analytics;
    }

    /**
     * Is auto tangent generating boolean.
     *
     * @return true if enabled auto tangent generating.
     */
    @FromAnyThread
    public boolean isAutoTangentGenerating() {
        return autoTangentGenerating;
    }

    /**
     * Sets auto tangent generating.
     *
     * @param autoTangentGenerating flag is of enabling auto tangent generating.
     */
    @FromAnyThread
    public void setAutoTangentGenerating(final boolean autoTangentGenerating) {
        this.autoTangentGenerating = autoTangentGenerating;
    }

    /**
     * Is default use flipped texture boolean.
     *
     * @return true if use flip textures by default.
     */
    public boolean isDefaultUseFlippedTexture() {
        return defaultUseFlippedTexture;
    }

    /**
     * Sets default use flipped texture.
     *
     * @param defaultUseFlippedTexture flag is of enabling using flip textures by default.
     */
    public void setDefaultUseFlippedTexture(final boolean defaultUseFlippedTexture) {
        this.defaultUseFlippedTexture = defaultUseFlippedTexture;
    }

    /**
     * Is default editor camera enabled boolean.
     *
     * @return true if enable camera lamp by default.
     */
    public boolean isDefaultEditorCameraEnabled() {
        return defaultEditorCameraEnabled;
    }

    /**
     * Sets default editor camera enabled.
     *
     * @param defaultEditorCameraEnabled Flag is of enabling camera lamp in editors by default.
     */
    public void setDefaultEditorCameraEnabled(final boolean defaultEditorCameraEnabled) {
        this.defaultEditorCameraEnabled = defaultEditorCameraEnabled;
    }

    /**
     * Gets frame rate.
     *
     * @return the current frameRate.
     */
    @FromAnyThread
    public int getFrameRate() {
        return frameRate;
    }

    /**
     * Sets frame rate.
     *
     * @param frameRate the current frameRate.
     */
    @FromAnyThread
    public void setFrameRate(final int frameRate) {
        this.frameRate = frameRate;
    }

    /**
     * Sets camera angle.
     *
     * @param cameraAngle the camera angle.
     */
    @FromAnyThread
    public void setCameraAngle(final int cameraAngle) {
        this.cameraAngle = cameraAngle;
    }

    /**
     * Gets camera angle.
     *
     * @return the camera angle.
     */
    @FromAnyThread
    public int getCameraAngle() {
        return cameraAngle;
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
    public void setAnalyticsQuestion(final boolean analyticsQuestion) {
        this.analyticsQuestion = analyticsQuestion;
    }

    /**
     * Gets the current theme.
     *
     * @return the current theme.
     */
    @NotNull
    @FromAnyThread
    public CssColorTheme getTheme() {
        return CssColorTheme.valueOf(theme);
    }

    /**
     * Sets the current theme.
     *
     * @param theme the current theme.
     */
    @FromAnyThread
    public void setTheme(@NotNull final CssColorTheme theme) {
        this.theme = theme.ordinal();
    }

    /**
     * Gets open gl version.
     *
     * @return the current open GL version.
     */
    @NotNull
    @FromAnyThread
    public OpenGLVersion getOpenGLVersion() {
        return notNull(openGLVersion);
    }

    /**
     * Sets open gl version.
     *
     * @param openGLVersion the current open GL version.
     */
    @FromAnyThread
    public void setOpenGLVersion(@NotNull final OpenGLVersion openGLVersion) {
        this.openGLVersion = openGLVersion;
    }

    /**
     * Gets settings.
     *
     * @return the settings for JME.
     */
    @FromAnyThread
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

        JmeToJFXIntegrator.prepareSettings(settings, getFrameRate());

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
        this.stopRenderOnLostFocus = prefs.getBoolean(PREF_GRAPHIC_STOP_RENDER_ON_LOST_FOCUS, true);
        this.toneMapFilter = prefs.getBoolean(PREF_GRAPHIC_TONEMAP_FILTER, false);
        this.maximized = prefs.getBoolean(PREF_SCREEN_MAXIMIZED, false);
        this.screenHeight = prefs.getInt(PREF_SCREEN_HEIGHT, 800);
        this.screenWidth = prefs.getInt(PREF_SCREEN_WIDTH, 1200);
        this.globalLeftToolWidth = prefs.getInt(PREF_OTHER_GLOBAL_LEFT_TOOL_WIDTH, 300);
        this.globalLeftToolCollapsed = prefs.getBoolean(PREF_OTHER_GLOBAL_LEFT_TOOL_COLLAPSED, false);
        this.globalBottomToolHeight = prefs.getInt(PREF_OTHER_GLOBAL_BOTTOM_TOOL_WIDTH, 300);
        this.globalBottomToolCollapsed = prefs.getBoolean(PREF_OTHER_GLOBAL_BOTTOM_TOOL_COLLAPSED, true);
        this.analytics = prefs.getBoolean(PREF_OTHER_ANALYTICS, true);
        this.frameRate = prefs.getInt(PREF_GRAPHIC_FRAME_RATE, 40);
        this.cameraAngle = prefs.getInt(PREF_GRAPHIC_CAMERA_ANGLE, 45);
        this.autoTangentGenerating = prefs.getBoolean(PREF_EDITING_AUTO_TANGENT_GENERATING, false);
        this.defaultUseFlippedTexture = prefs.getBoolean(PREF_EDITING_DEFAULT_USE_FLIPPED_TEXTURE, true);
        this.defaultEditorCameraEnabled = prefs.getBoolean(PREF_EDITING_CAMERA_LAMP_ENABLED, true);
        this.analyticsQuestion = prefs.getBoolean(PREF_OTHER_ANALYTICS_QUESTION, false);
        this.theme = prefs.getInt(PREF_OTHER_THEME, CssColorTheme.DARK.ordinal());
        this.openGLVersion = OpenGLVersion.valueOf(prefs.getInt(PREF_GRAPHIC_OPEN_GL, GL_32.ordinal()));

        final String currentAssetURI = prefs.get(PREF_ASSET_CURRENT_ASSET, null);

        if (currentAssetURI != null) {
            this.currentAsset = get(currentAssetURI, uri -> Paths.get(new URI(uri)));
        }

        final String classpathURI = prefs.get(PREF_OTHER_ADDITIONAL_CLASSPATH, null);

        if (classpathURI != null) {
            this.additionalClasspath = get(classpathURI, uri -> Paths.get(new URI(uri)));
        }

        final String envsURI = prefs.get(PREF_OTHER_ADDITIONAL_ENVS, null);

        if (envsURI != null) {
            this.additionalEnvs = get(envsURI, uri -> Paths.get(new URI(uri)));
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

        final byte[] byteArray = prefs.getByteArray(PREF_ASSET_LAST_OPENED_ASSETS, null);
        if (byteArray == null) return;

        final List<String> lastOpenedAssets = getLastOpenedAssets();
        try {
            lastOpenedAssets.addAll(EditorUtil.deserialize(byteArray));
        } catch (final RuntimeException e) {
            LOGGER.warning(e);
        }

        System.setProperty("jfx.frame.transfer.camera.angle", String.valueOf(getCameraAngle()));
    }

    /**
     * Save these settings.
     */
    @FromAnyThread
    public synchronized void save() {

        final Preferences prefs = Preferences.userNodeForPackage(Editor.class);
        prefs.putInt(PREF_GRAPHIC_ANISOTROPY, getAnisotropy());
        prefs.putBoolean(PREF_GRAPHIC_FXAA, isFXAA());
        prefs.putBoolean(PREF_GRAPHIC_GAMA_CORRECTION, isGammaCorrection());
        prefs.putBoolean(PREF_GRAPHIC_STOP_RENDER_ON_LOST_FOCUS, isStopRenderOnLostFocus());
        prefs.putBoolean(PREF_GRAPHIC_TONEMAP_FILTER, isToneMapFilter());
        prefs.putInt(PREF_SCREEN_HEIGHT, getScreenHeight());
        prefs.putInt(PREF_SCREEN_WIDTH, getScreenWidth());
        prefs.putBoolean(PREF_SCREEN_MAXIMIZED, isMaximized());
        prefs.putInt(PREF_OTHER_GLOBAL_LEFT_TOOL_WIDTH, getGlobalLeftToolWidth());
        prefs.putBoolean(PREF_OTHER_GLOBAL_LEFT_TOOL_COLLAPSED, isGlobalLeftToolCollapsed());
        prefs.putInt(PREF_OTHER_GLOBAL_BOTTOM_TOOL_WIDTH, getGlobalBottomToolHeight());
        prefs.putBoolean(PREF_OTHER_GLOBAL_BOTTOM_TOOL_COLLAPSED, isGlobalBottomToolCollapsed());
        prefs.putBoolean(PREF_OTHER_ANALYTICS, isAnalytics());
        prefs.putInt(PREF_GRAPHIC_FRAME_RATE, getFrameRate());
        prefs.putInt(PREF_GRAPHIC_CAMERA_ANGLE, getCameraAngle());
        prefs.putBoolean(PREF_EDITING_AUTO_TANGENT_GENERATING, isAutoTangentGenerating());
        prefs.putBoolean(PREF_EDITING_DEFAULT_USE_FLIPPED_TEXTURE, isDefaultUseFlippedTexture());
        prefs.putBoolean(PREF_EDITING_CAMERA_LAMP_ENABLED, isDefaultEditorCameraEnabled());
        prefs.putBoolean(PREF_OTHER_ANALYTICS_QUESTION, isAnalyticsQuestion());
        prefs.putInt(PREF_OTHER_THEME, getTheme().ordinal());
        prefs.putInt(PREF_GRAPHIC_OPEN_GL, getOpenGLVersion().ordinal());

        final Vector3f whitePoint = getToneMapFilterWhitePoint();

        prefs.put(PREF_GRAPHIC_TONEMAP_FILTER_WHITE_POINT, whitePoint.getX() + "," + whitePoint.getY() + "," + whitePoint.getZ());

        if (currentAsset != null && !Files.exists(currentAsset)) {
            currentAsset = null;
        }

        if (additionalClasspath != null && !Files.exists(additionalClasspath)) {
            additionalClasspath = null;
        }

        if (currentAsset != null) {
            prefs.put(PREF_ASSET_CURRENT_ASSET, currentAsset.toUri().toString());
        } else {
            prefs.remove(PREF_ASSET_CURRENT_ASSET);
        }

        if (additionalClasspath != null) {
            prefs.put(PREF_OTHER_ADDITIONAL_CLASSPATH, additionalClasspath.toUri().toString());
        } else {
            prefs.remove(PREF_OTHER_ADDITIONAL_CLASSPATH);
        }

        if (additionalEnvs != null) {
            prefs.put(PREF_OTHER_ADDITIONAL_ENVS, additionalEnvs.toUri().toString());
        } else {
            prefs.remove(PREF_OTHER_ADDITIONAL_ENVS);
        }

        final List<String> lastOpenedAssets = getLastOpenedAssets();

        prefs.putByteArray(PREF_ASSET_LAST_OPENED_ASSETS, EditorUtil.serialize((Serializable) lastOpenedAssets));
        try {
            prefs.flush();
        } catch (final BackingStoreException e) {
            throw new RuntimeException(e);
        }

        System.setProperty("jfx.frame.transfer.camera.angle", String.valueOf(getCameraAngle()));
    }
}
