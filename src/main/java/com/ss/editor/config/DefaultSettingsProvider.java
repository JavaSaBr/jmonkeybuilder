package com.ss.editor.config;

import static com.ss.editor.config.DefaultSettingsProvider.Categories.EDITOR;
import static com.ss.editor.config.DefaultSettingsProvider.Categories.GRAPHICS;
import static com.ss.editor.config.DefaultSettingsProvider.Categories.OTHER;
import static com.ss.editor.config.DefaultSettingsProvider.Defaults.*;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.*;
import static com.ss.editor.extension.property.EditablePropertyType.*;
import static com.ss.rlib.common.util.array.ArrayFactory.asArray;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.settings.SettingsCategory;
import com.ss.editor.plugin.api.settings.SettingsPropertyDefinition;
import com.ss.editor.plugin.api.settings.SettingsProvider;
import com.ss.editor.ui.css.CssColorTheme;
import com.ss.editor.util.OpenGLVersion;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * The default implementation of {@link SettingsProvider}
 *
 * @author JavaSaBr
 */
public class DefaultSettingsProvider implements SettingsProvider {

    public interface Categories {
        @NotNull SettingsCategory GRAPHICS = new SettingsCategory("core.graphics", Messages.SETTINGS_CATEGORY_GRAPHICS, 0);
        @NotNull SettingsCategory EDITOR = new SettingsCategory("core.editor", Messages.SETTINGS_CATEGORY_EDITOR, 1);
        @NotNull SettingsCategory OTHER = new SettingsCategory("core.other", Messages.SETTINGS_CATEGORY_OTHER, 2);
    }

    public interface Preferences {

        @NotNull String PREF_OPEN_GL = "core.graphics.openGL.version";
        @NotNull String PREF_ANISOTROPY = "core.graphics.anisotropy";
        @NotNull String PREF_GAMMA_CORRECTION = "core.graphics.gammaCorrection";
        @NotNull String PREF_FRAME_RATE = "core.graphics.frame.rate";
        @NotNull String PREF_CAMERA_ANGLE = "core.graphics.camera.angle";
        @NotNull String PREF_FILTER_FXAA = "core.graphics.filter.fxaa";
        @NotNull String PREF_FILTER_TONEMAP = "core.graphics.filter.toneMap";
        @NotNull String PREF_FILTER_TONEMAP_WHITE_POINT = "core.graphics.filter.toneMap.whitePoint";
        @NotNull String PREF_STOP_RENDER_ON_LOST_FOCUS = "core.graphics.render.stopOnLostFocus";

        @NotNull String PREF_USER_LIBRARY_FOLDER = "core.classpath.library.folder";
        @NotNull String PREF_USER_CLASSES_FOLDER = "core.classpath.classes.folder";

        @NotNull String PREF_UI_THEME = "core.ui.theme";
        @NotNull String PREF_ANALYTICS_GOOGLE = "core.other.analytics.google";
        @NotNull String PREF_NATIVE_FILE_CHOOSER = "core.other.native.file.chooser";

        @NotNull String PREF_FAST_SKY_FOLDER = "core.editor.fast.sky.folder";
        @NotNull String PREF_TANGENT_GENERATION = "core.editor.tangent.generation";
        @NotNull String PREF_FLIPPED_TEXTURES = "core.editor.texture.flipped";
        @NotNull String PREF_CAMERA_LAMP = "core.editor.camera.lamp";
    }

    public interface Defaults {

        @NotNull OpenGLVersion PREF_DEFAULT_OPEN_GL = OpenGLVersion.GL_33;
        @NotNull CssColorTheme PREF_DEFAULT_THEME = CssColorTheme.DARK;

        @NotNull Vector3f PREF_DEFAULT_TONEMAP_WHITE_POINT = new Vector3f(11, 11, 11);

        int PREF_DEFAULT_ANISOTROPY = 16;
        int PREF_DEFAULT_FRAME_RATE = 60;
        int PREF_DEFAULT_CAMERA_ANGLE = 75;

        boolean PREF_DEFAULT_TONEMAP_FILTER = true;
        boolean PREF_DEFAULT_GAMMA_CORRECTION = true;
        boolean PREF_DEFAULT_NATIVE_FILE_CHOOSER = false;
        boolean PREF_DEFAULT_FXAA_FILTER = true;
        boolean PREF_DEFAULT_CAMERA_LIGHT = true;
        boolean PREF_DEFAULT_TANGENT_GENERATION = true;
        boolean PREF_DEFAULT_STOP_RENDER_ON_LOST_FOCUS = true;
        boolean PREF_DEFAULT_FLIPPED_TEXTURES = true;
        boolean PREF_DEFAULT_ANALYTICS_GOOGLE = false;
    }

    @NotNull
    private static final Array<Integer> ANISOTROPYCS = ArrayFactory.newArray(Integer.class);

    @NotNull
    private static final Set<String> REQUIRED_RESTART_PREFS = new HashSet<>();

    @NotNull
    private static final Set<String> REQUIRED_UPDATE_CLASSPATH_PREFS = new HashSet<>();

    @NotNull
    private static final Set<String> REQUIRED_RESHAPE_PREFS = new HashSet<>();

    static {
        ANISOTROPYCS.add(0);
        ANISOTROPYCS.add(2);
        ANISOTROPYCS.add(4);
        ANISOTROPYCS.add(8);
        ANISOTROPYCS.add(16);
        REQUIRED_RESTART_PREFS.add(PREF_ANISOTROPY);
        REQUIRED_RESTART_PREFS.add(PREF_GAMMA_CORRECTION);
        REQUIRED_RESTART_PREFS.add(PREF_UI_THEME);
        REQUIRED_RESTART_PREFS.add(PREF_OPEN_GL);
        REQUIRED_RESTART_PREFS.add(PREF_FRAME_RATE);
        REQUIRED_UPDATE_CLASSPATH_PREFS.add(PREF_USER_LIBRARY_FOLDER);
        REQUIRED_UPDATE_CLASSPATH_PREFS.add(PREF_USER_CLASSES_FOLDER);
        REQUIRED_RESHAPE_PREFS.add(PREF_CAMERA_ANGLE);
    }

    @Override
    @FxThread
    public @NotNull Array<SettingsPropertyDefinition> getDefinitions() {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final OpenGLVersion glVersion = editorConfig.getEnum(PREF_OPEN_GL, PREF_DEFAULT_OPEN_GL);
        final int anisotropy = editorConfig.getInteger(PREF_ANISOTROPY, PREF_DEFAULT_ANISOTROPY);
        final boolean gammaCorrection = editorConfig.getBoolean(PREF_GAMMA_CORRECTION, PREF_DEFAULT_GAMMA_CORRECTION);
        final int frameRate = editorConfig.getInteger(PREF_FRAME_RATE, PREF_DEFAULT_FRAME_RATE);
        final int cameraAngle = editorConfig.getInteger(PREF_CAMERA_ANGLE, PREF_DEFAULT_CAMERA_ANGLE);
        final boolean fxaa = editorConfig.getBoolean(PREF_FILTER_FXAA, PREF_DEFAULT_FXAA_FILTER);
        final boolean stopRenderOnLostFocus = editorConfig.getBoolean(PREF_STOP_RENDER_ON_LOST_FOCUS, PREF_DEFAULT_STOP_RENDER_ON_LOST_FOCUS);
        final boolean tonemapFilter = editorConfig.getBoolean(PREF_FILTER_TONEMAP, PREF_DEFAULT_TONEMAP_FILTER);
        final Vector3f toneMapWhitePoint = editorConfig.getVector3f(PREF_FILTER_TONEMAP_WHITE_POINT, PREF_DEFAULT_TONEMAP_WHITE_POINT);

        final Array<SettingsPropertyDefinition> result = ArrayFactory.newArray(SettingsPropertyDefinition.class);
        result.add(new SettingsPropertyDefinition(ENUM, Messages.SETTINGS_PROPERTY_OPEN_GL, PREF_OPEN_GL, GRAPHICS, glVersion));
        result.add(new SettingsPropertyDefinition(OBJECT_FROM_LIST, Messages.SETTINGS_PROPERTY_ANISOTROPY, PREF_ANISOTROPY, GRAPHICS, anisotropy, ANISOTROPYCS));
        result.add(new SettingsPropertyDefinition(BOOLEAN, Messages.SETTINGS_PROPERTY_GAMMA_CORRECTION, PREF_GAMMA_CORRECTION, GRAPHICS, gammaCorrection));
        result.add(new SettingsPropertyDefinition(INTEGER, Messages.SETTINGS_PROPERTY_FRAME_RATE, PREF_FRAME_RATE, GRAPHICS, frameRate, 10, 120));
        result.add(new SettingsPropertyDefinition(INTEGER, Messages.SETTINGS_PROPERTY_CAMERA_ANGLE, PREF_CAMERA_ANGLE, GRAPHICS, cameraAngle, 45, 110));
        result.add(new SettingsPropertyDefinition(BOOLEAN, Messages.SETTINGS_PROPERTY_FXAA, PREF_FILTER_FXAA, GRAPHICS, fxaa));
        result.add(new SettingsPropertyDefinition(BOOLEAN, Messages.SETTINGS_PROPERTY_STOP_RENDER_ON_LOST_FOCUS, PREF_STOP_RENDER_ON_LOST_FOCUS, GRAPHICS, stopRenderOnLostFocus));
        result.add(new SettingsPropertyDefinition(BOOLEAN, Messages.SETTINGS_PROPERTY_TONEMAP_FILTER, PREF_FILTER_TONEMAP, GRAPHICS, tonemapFilter));
        result.add(new SettingsPropertyDefinition(VECTOR_3F, asArray(PREF_FILTER_TONEMAP), Messages.SETTINGS_PROPERTY_TONEMAP_FILTER_WHITE_POINT, PREF_FILTER_TONEMAP_WHITE_POINT, GRAPHICS, toneMapWhitePoint));

        final boolean tangentGeneration = editorConfig.getBoolean(PREF_TANGENT_GENERATION, PREF_DEFAULT_TANGENT_GENERATION);
        final boolean flippedTextures = editorConfig.getBoolean(PREF_FLIPPED_TEXTURES, PREF_DEFAULT_FLIPPED_TEXTURES);
        final boolean cameraLight = editorConfig.getBoolean(PREF_CAMERA_LAMP, PREF_DEFAULT_CAMERA_LIGHT);
        final Path fastSkyFolder = editorConfig.getFile(PREF_FAST_SKY_FOLDER);

        result.add(new SettingsPropertyDefinition(EXTERNAL_FILE, Messages.SETTINGS_PROPERTY_FAST_SKY_FOLDER, PREF_FAST_SKY_FOLDER, EDITOR, fastSkyFolder));
        result.add(new SettingsPropertyDefinition(BOOLEAN, Messages.SETTINGS_PROPERTY_TANGENT_GENERATION, PREF_TANGENT_GENERATION, EDITOR, tangentGeneration));
        result.add(new SettingsPropertyDefinition(BOOLEAN, Messages.SETTINGS_PROPERTY_FLIPPED_TEXTURE, PREF_FLIPPED_TEXTURES, EDITOR, flippedTextures));
        result.add(new SettingsPropertyDefinition(BOOLEAN, Messages.SETTINGS_PROPERTY_EDITOR_CAMERA_LAMP, PREF_CAMERA_LAMP, EDITOR, cameraLight));

        final CssColorTheme theme = editorConfig.getEnum(PREF_UI_THEME, PREF_DEFAULT_THEME);
        final Path libraryFolder = editorConfig.getFile(PREF_USER_LIBRARY_FOLDER);
        final Path classesFolder = editorConfig.getFile(PREF_USER_CLASSES_FOLDER);
        final boolean googleAnalytics = editorConfig.getBoolean(PREF_ANALYTICS_GOOGLE, PREF_DEFAULT_ANALYTICS_GOOGLE);
        final boolean nativeFileChooser = editorConfig.getBoolean(PREF_NATIVE_FILE_CHOOSER, PREF_DEFAULT_NATIVE_FILE_CHOOSER);

        result.add(new SettingsPropertyDefinition(ENUM, Messages.SETTINGS_PROPERTY_THEME, PREF_UI_THEME, OTHER, theme));
        result.add(new SettingsPropertyDefinition(EXTERNAL_FILE, Messages.SETTINGS_PROPERTY_USER_LIBRARIES_FOLDER, PREF_USER_LIBRARY_FOLDER, OTHER, libraryFolder));
        result.add(new SettingsPropertyDefinition(EXTERNAL_FILE, Messages.SETTINGS_PROPERTY_USER_CLASSES_FOLDER, PREF_USER_CLASSES_FOLDER, OTHER, classesFolder));
        result.add(new SettingsPropertyDefinition(BOOLEAN, Messages.SETTINGS_PROPERTY_GOOGLE_ANALYTICS, PREF_ANALYTICS_GOOGLE, OTHER, googleAnalytics));
        result.add(new SettingsPropertyDefinition(BOOLEAN, Messages.SETTINGS_PROPERTY_NATIVE_FILE_CHOOSER, PREF_NATIVE_FILE_CHOOSER, OTHER, nativeFileChooser));

        return result;
    }

    @Override
    @FxThread
    public boolean isRequiredRestart(@NotNull final String propertyId) {
        return REQUIRED_RESTART_PREFS.contains(propertyId);
    }

    @Override
    @FxThread
    public boolean isRequiredReshape3DView(@NotNull final String propertyId) {
        return REQUIRED_RESHAPE_PREFS.contains(propertyId);
    }

    @Override
    @FxThread
    public boolean isRequiredUpdateClasspath(@NotNull final String propertyId) {
        return REQUIRED_UPDATE_CLASSPATH_PREFS.contains(propertyId);
    }
}
