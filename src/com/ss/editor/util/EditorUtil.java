package com.ss.editor.util;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;

import org.apache.commons.lang3.SystemUtils;

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

import static rlib.util.ClassUtils.unsafeCast;

/**
 * Набор полезных утилит для разработки.
 *
 * @author Ronn
 */
public abstract class EditorUtil {

    private static final Logger LOGGER = LoggerManager.getLogger(EditorUtil.class);

    public static final DataFormat JAVA_PARAM = new DataFormat("SSEditor.javaParam");

    private static final ThreadLocal<SimpleDateFormat> LOCATE_DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("HH:mm:ss:SSS"));

    /**
     * Проверка существования ресурса по указанному пути.
     *
     * @param path путь к ресрсу.
     * @return существуетли такой ресурс.
     */
    public static boolean checkExists(final String path) {
        final Class<EditorUtil> cs = EditorUtil.class;
        return cs.getResourceAsStream(path) != null || cs.getResourceAsStream("/" + path) != null;
    }

    /**
     * Проверка наличия ресурса по указанному пути.
     *
     * @param path интересуемый путь к ресурсу.
     * @return существует ли этот ресурс.
     */
    public static boolean exists(final String path) {
        return Object.class.getResource(path) != null;
    }

    /**
     * Получение угла между 2мя точками.
     *
     * @param center точка угла.
     * @param first  первая точка.
     * @param second вторая точка.
     * @return угол между первой и второй точкой.
     */
    public static float getAngle(final Vector2f center, final Vector2f first, final Vector2f second) {

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
     * @param path путь к ресурсу.
     * @return поток ввода.
     */
    public static InputStream getInputStream(final String path) {
        return Object.class.getResourceAsStream(path);
    }

    /**
     * Получение имя пользователя текущей системы.
     *
     * @return имя пользователя системы.
     */
    public static String getUserName() {
        return System.getProperty("user.name");
    }

    /**
     * Видно ли на экране объект с такими экранными координатами.
     *
     * @param position позиция на экране объекта.
     * @param camera   камера экрана
     * @return видно ли на экране.
     */
    public static boolean isVisibleOnScreen(final Vector3f position, final Camera camera) {

        final int maxHeight = camera.getHeight();
        final int maxWidth = camera.getWidth();

        final boolean isBottom = position.getY() < 0;
        final boolean isTop = position.getY() > maxHeight;
        final boolean isLeft = position.getX() < 0;
        final boolean isRight = position.getX() > maxWidth;

        return !isBottom && !isLeft && !isTop && !isRight && position.getZ() < 1F;
    }

    /**
     * Получение сдвинутой точки на нужную дистанцию.
     *
     * @param first  первая точка.
     * @param second вторая точка.
     * @param store  контейнер результата.
     * @param length дистанция сдвига.
     */
    public static void movePoint(final Vector3f first, final Vector3f second, final Vector3f store, final int length) {
        store.x = first.x + (second.x - first.x) * length;
        store.y = first.y + (second.y - first.y) * length;
        store.z = first.z + (second.z - first.z) * length;
    }

    public static String timeFormat(final long time) {
        final SimpleDateFormat format = LOCATE_DATE_FORMAT.get();
        return format.format(new Date(time));
    }

    /**
     * Получение пути к указанному ресурсу относительно указанного Asset.
     *
     * @param assetFolder указанная папка Asset.
     * @param file        интересуемый файл.
     * @return относительный путь к файлу.
     */
    public static Path getAssetFile(final Path assetFolder, final Path file) {
        return assetFolder.relativize(file);
    }

    /**
     * Получение пути к указанному ресурсу относительно текущего Asset.
     *
     * @param file интересуемый файл.
     * @return относительный путь к файлу.
     */
    public static Path getAssetFile(final Path file) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        return currentAsset.relativize(file);
    }

    /**
     * Получение абсолютного пути к указанному ресурса из Asset.
     *
     * @param assetFile интересуемый файл.
     * @return абсолютный путь к файлу.
     */
    public static Path getRealFile(final Path assetFile) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        return currentAsset.resolve(assetFile);
    }

    /**
     * Получение абсолютного пути к указанному ресурса из Asset.
     *
     * @param assetFile интересуемый файл.
     * @return абсолютный путь к файлу.
     */
    public static Path getRealFile(final String assetFile) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        return currentAsset.resolve(assetFile);
    }

    /**
     * @return есть ли в буфере обмена файл.
     */
    public static boolean hasFileInClipboard() {

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard == null) return false;

        final List<File> files = unsafeCast(clipboard.getContent(DataFormat.FILES));
        return !(files == null || files.isEmpty());
    }

    /**
     * Нормализация пути для обращения к ресурсу в classpath.
     */
    public static String toAssetPath(final Path path) {
        if (File.separatorChar == '/') return path.toString();
        return path.toString().replace("\\", "/");
    }

    /**
     * Обработка ошибки.
     */
    public static void handleException(final Logger logger, final Object owner, final Exception e) {
        handleException(logger, owner, e, null);
    }

    /**
     * Обработка ошибки.
     */
    public static void handleException(Logger logger, final Object owner, final Exception e, final Runnable callback) {
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

            if (callback != null) {
                alert.setOnHidden(event -> callback.run());
            }
        });
    }

    /**
     * Создание диалога с ошибкой.
     */
    private static Alert createErrorAlert(final Exception e, final String localizedMessage, final String stackTrace) {

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
     * Открытие айла во внешнем редакторе.
     */
    public static void openFileInExternalEditor(final Path path) {

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

        if (commands.isEmpty()) {
            return;
        }

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
     * Сериализация объекта в массив байтов.
     */
    public static byte[] serialize(final Serializable object) {

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();

        try (final ObjectOutputStream out = new ObjectOutputStream(bout)) {
            out.writeObject(object);
        } catch (final IOException e) {
            LOGGER.warning(e);
        }

        return bout.toByteArray();
    }

    /**
     * Десериализация объекта из массива байтов.
     */
    public static <T> T deserialize(final byte[] bytes) {

        if (bytes == null) {
            return null;
        }

        final ByteArrayInputStream bin = new ByteArrayInputStream(bytes);

        try (final ObjectInputStream in = new ObjectInputStream(bin)) {
            return unsafeCast(in.readObject());
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Обрезка кол-ва цыфр после запятой у указанного числа.
     */
    public static float clipNumber(float value, float mod) {
        return (int) (value * mod) / mod;
    }
}
