package com.ss.editor.util;

import static rlib.util.ClassUtils.unsafeCast;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;

import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.VBox;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.StringUtils;

/**
 * The class with utility methods for the Editor.
 *
 * @author JavaSaBr.
 */
public abstract class EditorUtil {

    private static final Logger LOGGER = LoggerManager.getLogger(EditorUtil.class);

    public static final DataFormat JAVA_PARAM = new DataFormat("SSEditor.javaParam");

    private static final ThreadLocal<SimpleDateFormat> LOCATE_DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("HH:mm:ss:SSS"));

    /**
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
    public static float getAngle(@NotNull final Vector2f center, @NotNull final Vector2f first, @NotNull final Vector2f second) {

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

        return (float) Math.toDegrees(Math.acos(delta));
    }

    /**
     * @param path the path to resource.
     * @return the input stream of the resource or null.
     */
    public static InputStream getInputStream(@NotNull final String path) {
        return Object.class.getResourceAsStream(path);
    }

    /**
     * Get the user name of the computer user.
     *
     * @return the user name.
     */
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
    public static void movePoint(@NotNull final Vector3f first, @NotNull final Vector3f second, final @NotNull Vector3f store, final int length) {
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
     * @return true if you have a file in your system clipboard.
     */
    public static boolean hasFileInClipboard() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard == null) return false;
        final List<File> files = unsafeCast(clipboard.getContent(DataFormat.FILES));
        return !(files == null || files.isEmpty());
    }

    /**
     * @return the valid asset path for the file.
     */
    @NotNull
    public static String toAssetPath(@NotNull final Path path) {
        if (File.separatorChar == '/') return path.toString();
        return path.toString().replace("\\", "/");
    }

    /**
     * Handle exception.
     */
    public static void handleException(@Nullable final Logger logger, @Nullable final Object owner, @NotNull final Exception e) {
        handleException(logger, owner, e, null);
    }

    /**
     * Handle exception.
     */
    public static void handleException(@Nullable Logger logger, @Nullable final Object owner, @NotNull final Exception e, @Nullable final Runnable callback) {
        if (logger == null) logger = LOGGER;

        if (owner == null) {
            logger.warning(e);
        } else {
            logger.warning(owner, e);
        }

        final ExecutorManager executorManager = ExecutorManager.getInstance();
        executorManager.addFXTask(() -> {

            final StringWriter writer = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(writer);

            e.printStackTrace(printWriter);

            final String localizedMessage = e.getLocalizedMessage();
            final String stackTrace = writer.toString();

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
    private static Alert createErrorAlert(@NotNull final Exception e, @Nullable final String localizedMessage, @Nullable final String stackTrace) {

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
        } catch (IOException e) {
            handleException(LOGGER, null, e);
        }
    }

    /**
     * Convert the object to byte array.
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
     */
    public static float clipNumber(float value, float mod) {
        return (int) (value * mod) / mod;
    }
}
