package com.ss.editor.util;

import static com.ss.rlib.util.ClassUtils.cast;
import static com.ss.rlib.util.ClassUtils.unsafeCast;
import static java.lang.Math.acos;
import static java.lang.Math.toDegrees;
import static java.lang.ThreadLocal.withInitial;
import static java.util.stream.Collectors.toList;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.input.InputManager;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.scene.Node;
import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;
import com.ss.editor.JfxApplication;
import com.ss.editor.JmeApplication;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.manager.ClasspathManager;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.scene.EditorFxScene;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.ClassUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The class with utility methods for the Editor.
 *
 * @author JavaSaBr
 */
public abstract class EditorUtil {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(EditorUtil.class);

    /**
     * The constant JAVA_PARAM.
     */
    @NotNull
    public static final DataFormat JAVA_PARAM = new DataFormat("SSEditor.javaParam");

    /**
     * Represents a list of files.
     */
    public static final DataFormat GNOME_FILES = new DataFormat("x-special/gnome-copied-files");

    @NotNull
    private static final ThreadLocal<SimpleDateFormat> LOCATE_DATE_FORMAT = withInitial(() ->
            new SimpleDateFormat("HH:mm:ss:SSS"));

    @NotNull
    private static JmeApplication jmeApplication;

    @NotNull
    private static JfxApplication jfxApplication;

    public static void setJmeApplication(@NotNull final JmeApplication jmeApplication) {
        EditorUtil.jmeApplication = jmeApplication;
    }

    public static void setJfxApplication(@NotNull final JfxApplication jfxApplication) {
        EditorUtil.jfxApplication = jfxApplication;
    }

    /**
     * Get the asset manager.
     *
     * @return the asset manager.
     */
    @FromAnyThread
    public static @NotNull AssetManager getAssetManager() {
        return jmeApplication.getAssetManager();
    }

    /**
     * Get the input manager.
     *
     * @return the input manager.
     */
    @FromAnyThread
    public static @NotNull InputManager getInputManager() {
        return jmeApplication.getInputManager();
    }

    /**
     * Get the render manager.
     *
     * @return the render manager.
     */
    @FromAnyThread
    public static @NotNull RenderManager getRenderManager() {
        return jmeApplication.getRenderManager();
    }

    /**
     * Get the renderer.
     *
     * @return the renderer.
     */
    @FromAnyThread
    public static @NotNull Renderer getRenderer() {
        return jmeApplication.getRenderer();
    }

    /**
     * Get the root node.
     *
     * @return the root node.
     */
    @JmeThread
    public static @NotNull Node getGlobalRootNode() {
        return jmeApplication.getRootNode();
    }

    /**
     * Get the preview node.
     *
     * @return the preview node.
     */
    @JmeThread
    public static @NotNull Node getPreviewNode() {
        return jmeApplication.getPreviewNode();
    }

    /**
     * Get the preview camera.
     *
     * @return the preview camera.
     */
    @JmeThread
    public static @NotNull Camera getPreviewCamera() {
        return jmeApplication.getPreviewCamera();
    }

    /**
     * Get the camera.
     *
     * @return the camera.
     */
    @FromAnyThread
    public static @NotNull Camera getGlobalCamera() {
        return jmeApplication.getCamera();
    }

    /**
     * Get the global filter post processor.
     *
     * @return the global filter post processor.
     */
    @JmeThread
    public static @NotNull FilterPostProcessor getGlobalFilterPostProcessor() {
        return jmeApplication.getPostProcessor();
    }

    /**
     * Get the default material.
     *
     * @return the default material.
     */
    @FromAnyThread
    public static @NotNull Material getDefaultMaterial() {
        return jmeApplication.getDefaultMaterial();
    }

    /**
     * Disable the global PBR light probe.
     */
    @JmeThread
    public static void disableGlobalLightProbe() {
        jmeApplication.disableLightProbe();
    }

    /**
     * Enable the global PBR light probe.
     */
    @JmeThread
    public static void enableGlobalLightProbe() {
        jmeApplication.enableLightProbe();
    }

    /**
     * Update the light probe.
     *
     * @param progressAdapter the progress adapter
     */
    @JmeThread
    public static void updateGlobalLightProbe(@NotNull final JobProgressAdapter<LightProbe> progressAdapter) {
        jmeApplication.updateLightProbe(progressAdapter);
    }

    /**
     * Get the state manager.
     *
     * @return the state manager.
     */
    @FromAnyThread
    public static @NotNull AppStateManager getStateManager() {
        return jmeApplication.getStateManager();
    }

    /**
     * Gets the last opened window.
     *
     * @return the last opened window.
     */
    @FxThread
    public static @NotNull Window getFxLastWindow() {
        return jfxApplication.getLastWindow();
    }

    /**
     * Get the current JavaFX scene.
     *
     * @return the JavaFX scene.
     */
    @FxThread
    public static @NotNull EditorFxScene getFxScene() {
        return jfxApplication.getScene();
    }

    /**
     * Get the current stage of JavaFX.
     *
     * @return the current stage of JavaFX.
     */
    @FxThread
    public static @NotNull Stage getFxStage() {
        return jfxApplication.getStage();
    }

    /**
     * Register the opened new window.
     *
     * @param window the opened new window.
     */
    @FromAnyThread
    public static void addFxWindow(@NotNull final Window window) {
        jfxApplication.addWindow(window);
    }

    /**
     * Delete the closed window.
     *
     * @param window the closed window.
     */
    @FromAnyThread
    public static void removeFxWindow(@NotNull final Window window) {
        jfxApplication.removeWindow(window);
    }

    /**
     * Request focus to FX window.
     */
    @FxThread
    public static void requestFxFocus() {
        jfxApplication.requestFocus();
    }

    /**
     * Added files like files to copy to clipboard content.
     *
     * @param paths   the list of files.
     * @param content the content to store.
     */
    @FxThread
    public static void addCopiedFile(@NotNull final Array<Path> paths, @NotNull final ClipboardContent content) {

        final List<File> files = paths.stream()
                .map(Path::toFile)
                .collect(toList());

        content.putFiles(files);
        content.put(EditorUtil.JAVA_PARAM, "copy");

        final Platform platform = JmeSystem.getPlatform();

        if (platform == Platform.Linux64 || platform == Platform.Linux32) {

            final StringBuilder builder = new StringBuilder("copy\n");

            paths.forEach(builder, (path, b) ->
                    b.append(path.toUri().toASCIIString()).append('\n'));

            builder.delete(builder.length() - 1, builder.length());

            final ByteBuffer buffer = ByteBuffer.allocate(builder.length());

            for (int i = 0, length = builder.length(); i < length; i++) {
                buffer.put((byte) builder.charAt(i));
            }

            buffer.flip();

            content.put(GNOME_FILES, buffer);
        }
    }

    /**
     * Check exists boolean.
     *
     * @param path the path to resource.
     * @return true if the resource is exists.
     */
    @FromAnyThread
    public static boolean checkExists(@NotNull final String path) {
        final Class<EditorUtil> cs = EditorUtil.class;
        return cs.getResource(path) != null || cs.getResource("/" + path) != null;
    }

    /**
     * Check exists boolean.
     *
     * @param path        the path to resource.
     * @param classLoader the class loader.
     * @return true if the resource is exists.
     */
    @FromAnyThread
    public static boolean checkExists(@NotNull final String path, @NotNull final ClassLoader classLoader) {
        return classLoader.getResource(path) != null || classLoader.getResource("/" + path) != null;
    }

    /**
     * Convert classpath path to external path.
     *
     * @param path        the path to resource.
     * @param classLoader the class loader.
     * @return the external form or null.
     */
    @FromAnyThread
    public static @Nullable String toExternal(@NotNull final String path, @NotNull final ClassLoader classLoader) {
        if (!checkExists(path, classLoader)) return null;
        URL resource = classLoader.getResource(path);
        if (resource == null) resource = classLoader.getResource("/" + path);
        return resource == null ? null : resource.toExternalForm();
    }

    /**
     * Get the angle between these points.
     *
     * @param center the center.
     * @param first  the first point.
     * @param second the second point.
     * @return the angle between these points.
     */
    @FromAnyThread
    public static float getAngle(@NotNull final Vector2f center, @NotNull final Vector2f first,
                                 @NotNull final Vector2f second) {

        final float x = center.getX();
        final float y = center.getY();

        final float ax = first.getX() - x;
        final float ay = first.getY() - y;
        final float bx = second.getX() - x;
        final float by = second.getY() - y;

        final float delta = (float) ((ax * bx + ay * by) / Math.sqrt((ax * ax + ay * ay) * (bx * bx + by * by)));

        if (delta > 1.0) {
            return 0.0F;
        } else if (delta < -1.0) {
            return 180.0F;
        }

        return (float) toDegrees(acos(delta));
    }

    /**
     * Gets input stream.
     *
     * @param path the path to resource.
     * @return the input stream of the resource or null.
     */
    @FromAnyThread
    public static @Nullable InputStream getInputStream(@NotNull final String path) {
        return JfxApplication.class.getResourceAsStream(path);
    }

    /**
     * Gets input stream.
     *
     * @param path        the path to resource.
     * @param classLoader the class loader.
     * @return the input stream of the resource or null.
     */
    @FromAnyThread
    public static @Nullable InputStream getInputStream(@NotNull final String path, @NotNull final ClassLoader classLoader) {
        return classLoader.getResourceAsStream(path);
    }

    /**
     * Get the user name of the computer user.
     *
     * @return the user name.
     */
    @FromAnyThread
    public static @NotNull String getUserName() {
        return System.getProperty("user.name");
    }

    /**
     * Check visibility the position on the screen.
     *
     * @param position the position for checking.
     * @param camera   the camera of the screen.
     * @return true of we can see the position on the screen.
     */
    @FromAnyThread
    public static boolean isVisibleOnScreen(@NotNull final Vector3f position, @NotNull final Camera camera) {

        final int maxHeight = camera.getHeight();
        final int maxWidth = camera.getWidth();

        final boolean isBottom = position.getY() < 0;
        final boolean isTop = position.getY() > maxHeight;
        final boolean isLeft = position.getX() < 0;
        final boolean isRight = position.getX() > maxWidth;

        return !isBottom && !isLeft && !isTop && !isRight && position.getZ() < 1F;
    }

    /**
     * Calculate new point from first point and using second point like a direction.
     *
     * @param first  the first point.
     * @param second the second point.
     * @param store  the container of the result.
     * @param length the distance.
     */
    @FromAnyThread
    public static void movePoint(@NotNull final Vector3f first, @NotNull final Vector3f second,
                                 @NotNull final Vector3f store, final int length) {
        store.x = first.x + (second.x - first.x) * length;
        store.y = first.y + (second.y - first.y) * length;
        store.z = first.z + (second.z - first.z) * length;
    }

    /**
     * Convert unix time to string presentation.
     *
     * @param time the unix time.
     * @return the string presentation.
     */
    @FromAnyThread
    public static @NotNull String timeFormat(final long time) {
        final SimpleDateFormat format = LOCATE_DATE_FORMAT.get();
        return format.format(new Date(time));
    }

    /**
     * Get the path to the file from the asset folder.
     *
     * @param assetFolder the asset folder.
     * @param file        the file.
     * @return the relative path.
     */
    @FromAnyThread
    public static @NotNull Path getAssetFile(@NotNull final Path assetFolder, @NotNull final Path file) {
        return assetFolder.relativize(file);
    }

    /**
     * Get the path to the file from the current asset folder.
     *
     * @param file the file.
     * @return the relative path.
     */
    @FromAnyThread
    public static @Nullable Path getAssetFile(@NotNull final Path file) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) {
            return null;
        }

        try {
            return currentAsset.relativize(file);
        } catch (final IllegalArgumentException e) {
            LOGGER.warning("Can't create asset file of the " + file + " for asset folder " + currentAsset);
            LOGGER.warning(e);
            return null;
        }
    }

    /**
     * Get the absolute path to the file in the current asset.
     *
     * @param assetFile the file.
     * @return the absolute path to the file.
     */
    @FromAnyThread
    public static @Nullable Path getRealFile(@NotNull final Path assetFile) {
        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return null;
        return currentAsset.resolve(assetFile);
    }

    /**
     * Get the absolute path to the file in the current asset.
     *
     * @param assetFile the asset path to file.
     * @return the absolute path to the file.
     */
    @FromAnyThread
    public static @Nullable Path getRealFile(@NotNull final String assetFile) {
        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return null;
        return currentAsset.resolve(assetFile);
    }

    /**
     * Has file in clipboard boolean.
     *
     * @return true if you have a file in your system clipboard.
     */
    @FxThread
    public static boolean hasFileInClipboard() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard == null) return false;
        final List<File> files = unsafeCast(clipboard.getContent(DataFormat.FILES));
        return !(files == null || files.isEmpty());
    }

    /**
     * To asset path string.
     *
     * @param path the path
     * @return the valid asset path for the file.
     */
    @FromAnyThread
    public static @NotNull String toAssetPath(@NotNull final Path path) {
        if (File.separatorChar == '/') return path.toString();
        return path.toString().replace("\\", "/");
    }

    /**
     * Handle exception.
     *
     * @param logger the logger
     * @param owner  the owner
     * @param e      the e
     */
    @FromAnyThread
    public static void handleException(@Nullable final Logger logger, @Nullable final Object owner,
                                       @NotNull final Exception e) {
        handleException(logger, owner, e, null);
    }

    /**
     * Handle exception.
     *
     * @param logger   the logger
     * @param owner    the owner
     * @param e        the e
     * @param callback the callback
     */
    @FromAnyThread
    public static void handleException(@Nullable Logger logger, @Nullable final Object owner,
                                       @NotNull final Exception e, @Nullable final Runnable callback) {
        if (logger == null) {
            logger = LOGGER;
        }

        if (owner == null) {
            logger.warning(e);
        } else {
            logger.warning(owner, e);
        }

        final ExecutorManager executorManager = ExecutorManager.getInstance();
        executorManager.addFxTask(() -> {

            GAnalytics.sendException(e, false);

            final String localizedMessage = e.getLocalizedMessage();
            final String stackTrace = buildStackTrace(e);

            final Alert alert = createErrorAlert(e, localizedMessage, stackTrace);
            alert.show();
            alert.setWidth(500);
            alert.setHeight(220);

            if (callback != null) alert.setOnHidden(event -> callback.run());
        });
    }

    /**
     * Build the stack trace of the exception.
     *
     * @param exception the exception.
     * @return the built stack trace.
     */
    @FromAnyThread
    public static String buildStackTrace(@NotNull final Exception exception) {

        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        exception.printStackTrace(printWriter);

        String stackTrace = writer.toString();

        int level = 0;

        for (Throwable cause = exception.getCause(); cause != null && level < 6; cause = cause.getCause(), level++) {

            writer = new StringWriter();
            printWriter = new PrintWriter(writer);

            cause.printStackTrace(printWriter);

            stackTrace += "\n caused by " + writer.toString();
        }

        return stackTrace;
    }

    /**
     * Create a dialog for showing the exception.
     */
    @FxThread
    private static @NotNull Alert createErrorAlert(@NotNull final Exception e, @Nullable final String localizedMessage,
                                          @Nullable final String stackTrace) {

        final TextArea textArea = new TextArea(stackTrace);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        VBox.setMargin(textArea, new Insets(2, 5, 2, 5));

        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(StringUtils.isEmpty(localizedMessage) ? e.getClass().getSimpleName() : localizedMessage);

        final DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setExpandableContent(new VBox(textArea));
        dialogPane.expandedProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == Boolean.TRUE) {
                alert.setWidth(800);
                alert.setHeight(400);
            } else {
                alert.setWidth(500);
                alert.setHeight(220);
            }
        });

        return alert;
    }

    /**
     * Open the file in an external editor.
     *
     * @param path the path
     */
    @FromAnyThread
    public static void openFileInExternalEditor(@NotNull final Path path) {

        final Platform platform = JmeSystem.getPlatform();
        final List<String> commands = new ArrayList<>();

        if (platform == Platform.MacOSX64 || platform == Platform.MacOSX_PPC64) {
            commands.add("open");
        } else if (platform == Platform.Windows32 || platform == Platform.Windows64) {
            commands.add("cmd");
            commands.add("/c");
            commands.add("start");
        } else if (platform == Platform.Linux32|| platform == Platform.Linux64) {
            commands.add("xdg-open");
        }

        if (commands.isEmpty()) return;

        final String url;
        try {
            url = path.toUri().toURL().toString();
        } catch (final MalformedURLException e) {
            handleException(LOGGER, null, e);
            return;
        }

        commands.add(url);

        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(commands);

        try {
            processBuilder.start();
        } catch (final IOException e) {
            handleException(LOGGER, null, e);
        }
    }

    /**
     * Open the file in a system explorer.
     *
     * @param path the path
     */
    @FromAnyThread
    public static void openFileInSystemExplorer(@NotNull Path path) {

        final Platform platform = JmeSystem.getPlatform();
        final List<String> commands = new ArrayList<>();

        if (platform == Platform.MacOSX64 || platform == Platform.MacOSX_PPC64) {
            commands.add("open");
            commands.add("-R");
        } else if (platform == Platform.Windows32 || platform == Platform.Windows64) {
            commands.add("explorer");
            commands.add("/select,");
        } else if (platform == Platform.Linux32 || platform == Platform.Linux64) {
            if (isAppExists("nautilus -v")) {
                commands.add("nautilus");
            } else if (isAppExists("dolphin -v")) {
                commands.add("dolphin");
            } else {
                commands.add("xdg-open");
                if (!Files.isDirectory(path)) {
                    path = path.getParent();
                }
            }
        }

        if (commands.isEmpty()) return;

        final String url;
        try {
            url = path.toUri().toURL().toString();
        } catch (final MalformedURLException e) {
            handleException(LOGGER, null, e);
            return;
        }

        commands.add(url);

        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(commands);
        try {
            processBuilder.start();
        } catch (final IOException e) {
            handleException(LOGGER, null, e);
        }
    }

    @FromAnyThread
    private static boolean isAppExists(@NotNull final String command) {

        final Runtime runtime = Runtime.getRuntime();
        final int result;
        try {
            final Process exec = runtime.exec(command);
            result = exec.waitFor();
        } catch (final InterruptedException | IOException e) {
            return false;
        }

        return result >= 0;
    }

    /**
     * Convert the object to byte array.
     *
     * @param object the object
     * @return the byte array.
     */
    @FromAnyThread
    public static @NotNull byte[] serialize(@NotNull final Serializable object) {

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();

        try (final ObjectOutputStream out = new ObjectOutputStream(bout)) {
            out.writeObject(object);
        } catch (final IOException e) {
            LOGGER.warning(e);
        }

        return bout.toByteArray();
    }

    /**
     * Convert the byte array to object.
     *
     * @param <T>   the type parameter
     * @param bytes the byte array.
     * @return the result object.
     */
    @FromAnyThread
    public static <T> @NotNull T deserialize(@NotNull final byte[] bytes) {

        final ByteArrayInputStream bin = new ByteArrayInputStream(bytes);

        try (final ObjectInputStream in = new ExtObjectInputStream(bin)) {
            return unsafeCast(in.readObject());
        } catch (final ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Format the float number.
     *
     * @param value the value
     * @param mod   the mod
     * @return the float
     */
    @FromAnyThread
    public static float clipNumber(float value, float mod) {
        return (int) (value * mod) / mod;
    }

    /**
     * Increment the loading counter.
     */
    @FxThread
    public static void incrementLoading() {
        getFxScene().incrementLoading();
    }

    /**
     * Decrement the loading counter.
     */
    @FxThread
    public static void decrementLoading() {
        getFxScene().decrementLoading();
    }

    /**
     * Get an array of available enum values by an enum value.
     *
     * @param <E>   the type parameter
     * @param value the enum value.
     * @return the array of enum values.
     */
    @FromAnyThread
    public static <E extends Enum<?>> @NotNull E[] getAvailableValues(@NotNull final E value) {
        final Class<? extends Enum> valueClass = value.getClass();
        if (!valueClass.isEnum()) throw new RuntimeException("The class " + valueClass + " isn't enum.");
        final Enum<?>[] enumConstants = valueClass.getEnumConstants();
        return ClassUtils.unsafeCast(enumConstants);
    }

    /**
     * Try to create an user object using asset classpath and additional classpath.
     *
     * @param <T>        the type parameter
     * @param owner      the requester.
     * @param className  the classname.
     * @param resultType the result type.
     * @return the new instance or null.
     */
    @FromAnyThread
    public static <T> @Nullable T tryToCreateUserObject(@NotNull final Object owner, @NotNull final String className,
                                                        @NotNull final Class<T> resultType) {

        final ResourceManager resourceManager = ResourceManager.getInstance();
        final ClasspathManager classpathManager = ClasspathManager.getInstance();

        Object newExample = null;
        try {
            newExample = ClassUtils.newInstance(className);
        } catch (final RuntimeException e) {

            final Array<URLClassLoader> classLoaders = resourceManager.getClassLoaders();

            for (final URLClassLoader classLoader : classLoaders) {
                try {
                    final Class<?> targetClass = classLoader.loadClass(className);
                    newExample = ClassUtils.newInstance(targetClass);
                } catch (final ClassNotFoundException ex) {
                    LOGGER.warning(owner, e);
                }
            }

            final URLClassLoader librariesLoader = classpathManager.getLibrariesLoader();
            if (librariesLoader != null) {
                try {
                    final Class<?> targetClass = librariesLoader.loadClass(className);
                    newExample = ClassUtils.newInstance(targetClass);
                } catch (final ClassNotFoundException ex) {
                    LOGGER.warning(owner, e);
                }
            }
        }

        return cast(resultType, newExample);
    }

    /**
     * Get a default layer of the change consumer.
     *
     * @param consumer the change consumer.
     * @return the default layer or null.
     */
    @FromAnyThread
    public static @Nullable SceneLayer getDefaultLayer(@NotNull final ChangeConsumer consumer) {

        if (!(consumer instanceof SceneChangeConsumer)) {
            return null;
        }

        final SceneNode sceneNode = ((SceneChangeConsumer) consumer).getCurrentModel();
        final List<SceneLayer> layers = sceneNode.getLayers();

        if (layers.isEmpty()) {
            return null;
        }

        return layers.get(0);
    }

    /**
     * Convert the color to hex presentation to use in web.
     *
     * @param color the color.
     * @return the web presentation.
     */
    public static @NotNull String toWeb(@NotNull final Color color) {
        final int red = (int) (color.getRed() * 255);
        final int green = (int) (color.getGreen() * 255);
        final int blue = (int) (color.getBlue() * 255);
        return "#" + Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue);
    }
}
