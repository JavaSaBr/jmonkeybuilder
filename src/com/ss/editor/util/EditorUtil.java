package com.ss.editor.util;

import static java.lang.Math.acos;
import static java.lang.Math.toDegrees;
import static java.lang.ThreadLocal.withInitial;
import static com.ss.rlib.util.ClassUtils.cast;
import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.ss.editor.JFXApplication;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ClasspathManager;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.ClassUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The class with utility methods for the Editor.
 *
 * @author JavaSaBr.
 */
public abstract class EditorUtil {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(EditorUtil.class);

    /**
     * The constant JAVA_PARAM.
     */
    @NotNull
    public static final DataFormat JAVA_PARAM = new DataFormat("SSEditor.javaParam");

    @NotNull
    private static final ThreadLocal<SimpleDateFormat> LOCATE_DATE_FORMAT = withInitial(() -> new SimpleDateFormat("HH:mm:ss:SSS"));

    /**
     * Check exists boolean.
     *
     * @param path the path to resource.
     * @return true if the resource is exists.
     */
    public static boolean checkExists(@NotNull final String path) {
        final Class<EditorUtil> cs = EditorUtil.class;
        return cs.getResourceAsStream(path) != null || cs.getResourceAsStream("/" + path) != null;
    }

    /**
     * Get the angle between these points.
     *
     * @param center the center.
     * @param first  the first point.
     * @param second the second point.
     * @return the angle between these points.
     */
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
    @NotNull
    public static InputStream getInputStream(@NotNull final String path) {
        return Object.class.getResourceAsStream(path);
    }

    /**
     * Get the user name of the computer user.
     *
     * @return the user name.
     */
    @NotNull
    public static String getUserName() {
        return System.getProperty("user.name");
    }

    /**
     * Check visibility the position on the screen.
     *
     * @param position the position for checking.
     * @param camera   the camera of the screen.
     * @return true of we can see the position on the screen.
     */
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
    public static void movePoint(@NotNull final Vector3f first, @NotNull final Vector3f second,
                                 final @NotNull Vector3f store, final int length) {
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
    @NotNull
    public static String timeFormat(final long time) {
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
    @NotNull
    public static Path getAssetFile(@NotNull final Path assetFolder, @NotNull final Path file) {
        return assetFolder.relativize(file);
    }

    /**
     * Get the path to the file from the current asset folder.
     *
     * @param file the file.
     * @return the relative path.
     */
    @Nullable
    public static Path getAssetFile(@NotNull final Path file) {
        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return null;
        return currentAsset.relativize(file);
    }

    /**
     * Get the absolute path to the file in the current asset.
     *
     * @param assetFile the file.
     * @return the absolute path to the file.
     */
    @Nullable
    public static Path getRealFile(@NotNull final Path assetFile) {
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
    @Nullable
    public static Path getRealFile(@NotNull final String assetFile) {
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
    @NotNull
    public static String toAssetPath(@NotNull final Path path) {
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
    public static void handleException(@Nullable Logger logger, @Nullable final Object owner,
                                       @NotNull final Exception e, @Nullable final Runnable callback) {
        if (logger == null) logger = LOGGER;

        if (owner == null) {
            logger.warning(e);
        } else {
            logger.warning(owner, e);
        }

        final ExecutorManager executorManager = ExecutorManager.getInstance();
        executorManager.addFXTask(() -> {

            GAnalytics.sendException(e, false);

            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);

            e.printStackTrace(printWriter);

            final String localizedMessage = e.getLocalizedMessage();

            String stackTrace = writer.toString();

            int level = 0;

            for (Throwable cause = e.getCause(); cause != null && level < 6; cause = cause.getCause(), level++) {

                writer = new StringWriter();
                printWriter = new PrintWriter(writer);

                cause.printStackTrace(printWriter);

                stackTrace += "\n caused by " + writer.toString();
            }

            final Alert alert = createErrorAlert(e, localizedMessage, stackTrace);
            alert.show();
            alert.setWidth(500);
            alert.setHeight(220);

            if (callback != null) alert.setOnHidden(event -> callback.run());
        });
    }

    /**
     * Create a dialog for showing the exception.
     */
    @NotNull
    private static Alert createErrorAlert(@NotNull final Exception e, @Nullable final String localizedMessage,
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
    public static void openFileInExternalEditor(@NotNull final Path path) {

        final List<String> commands = new ArrayList<>();

        if (SystemUtils.IS_OS_MAC) {
            commands.add("open");
        } else if (SystemUtils.IS_OS_WINDOWS) {
            commands.add("cmd");
            commands.add("/c");
            commands.add("start");
        } else if (SystemUtils.IS_OS_LINUX) {
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
     * Convert the object to byte array.
     *
     * @param object the object
     * @return the byte [ ]
     */
    @NotNull
    public static byte[] serialize(@NotNull final Serializable object) {

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
     * @param bytes the bytes
     * @return the t
     */
    @NotNull
    public static <T> T deserialize(@NotNull final byte[] bytes) {

        final ByteArrayInputStream bin = new ByteArrayInputStream(bytes);

        try (final ObjectInputStream in = new ObjectInputStream(bin)) {
            return unsafeCast(in.readObject());
        } catch (final ClassNotFoundException | IOException e) {
            LOGGER.warning(e);
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
    public static float clipNumber(float value, float mod) {
        return (int) (value * mod) / mod;
    }

    /**
     * Increment the loading counter.
     */
    @FXThread
    public static void incrementLoading() {
        final JFXApplication jfxApplication = JFXApplication.getInstance();
        final EditorFXScene scene = jfxApplication.getScene();
        scene.incrementLoading();
    }

    /**
     * Decrement the loading counter.
     */
    @FXThread
    public static void decrementLoading() {
        final JFXApplication jfxApplication = JFXApplication.getInstance();
        final EditorFXScene scene = jfxApplication.getScene();
        scene.decrementLoading();
    }

    /**
     * Get an array of available enum values by an enum value.
     *
     * @param <E>   the type parameter
     * @param value the enum value.
     * @return the array of enum values.
     */
    @NotNull
    public static <E extends Enum<?>> E[] getAvailableValues(@NotNull final E value) {
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
    @Nullable
    public static <T> T tryToCreateUserObject(@NotNull final Object owner, @NotNull final String className,
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

            final URLClassLoader additionalCL = classpathManager.getAdditionalCL();
            if (additionalCL != null) {
                try {
                    final Class<?> targetClass = additionalCL.loadClass(className);
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
    @Nullable
    public static SceneLayer getDefaultLayer(@NotNull final ChangeConsumer consumer) {

        if (!(consumer instanceof SceneChangeConsumer)) {
            return null;
        }

        final SceneNode sceneNode = ((SceneChangeConsumer) consumer).getCurrentModel();
        return sceneNode.getLayers().first();
    }
}
