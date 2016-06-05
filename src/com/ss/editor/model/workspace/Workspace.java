package com.ss.editor.model.workspace;

import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.state.editor.EditorState;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.util.EditorUtil;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;

/**
 * Реализация воркспейса.
 *
 * @author Ronn
 */
public class Workspace implements Serializable {

    private static final Logger LOGGER = LoggerManager.getLogger(Workspace.class);

    /**
     * Счетчик изменений.
     */
    private final AtomicInteger changes;

    /**
     * Папка с Asset.
     */
    private transient Path assetFolder;

    /**
     * Таблица открытых файлов.
     */
    private Map<String, String> openedFiles;

    /**
     * Таблица состояний редакторов в воркспейсе.
     */
    private Map<String, EditorState> editorStateMap;

    /**
     * Текущий редактируемый файл.
     */
    private String currentEditFile;

    public Workspace() {
        this.changes = new AtomicInteger();
        this.openedFiles = new HashMap<>();
    }

    /**
     * @return текущий редактируемый файл.
     */
    public String getCurrentEditFile() {
        return currentEditFile;
    }

    /**
     * Обновление текущего редактируемого файла.
     *
     * @param file текущий редактируемый файл.
     */
    public synchronized void updateCurrentEditFile(final Path file) {

        if (file == null) {
            this.currentEditFile = null;
            return;
        }

        final Path assetFile = EditorUtil.getAssetFile(getAssetFolder(), file);
        this.currentEditFile = EditorUtil.toAssetPath(assetFile);
    }

    /**
     * @return таблица состояний редакторов в воркспейсе.
     */
    private Map<String, EditorState> getEditorStateMap() {
        return editorStateMap;
    }

    /**
     * Получение состояния редактора для указанного файла.
     *
     * @param file редактируемый файл.
     * @return состояние редактора.
     */
    public synchronized EditorState getEditorState(final Path file) {

        final Path assetFile = EditorUtil.getAssetFile(getAssetFolder(), file);
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final Map<String, EditorState> editorStateMap = getEditorStateMap();
        return editorStateMap.get(assetPath);
    }

    /**
     * Обновление состояния редактора.
     */
    public synchronized void updateEditorState(final Path file, final EditorState editorState) {

        final Path assetFile = EditorUtil.getAssetFile(getAssetFolder(), file);
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final Map<String, EditorState> editorStateMap = getEditorStateMap();
        editorStateMap.put(assetPath, editorState);

        incrementChanges();
    }

    /**
     * Удаление состояния редактора.
     */
    public synchronized void removeEditorState(final Path file, final EditorState editorState) {

        final Path assetFile = EditorUtil.getAssetFile(getAssetFolder(), file);
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final Map<String, EditorState> editorStateMap = getEditorStateMap();
        editorStateMap.remove(assetPath);

        incrementChanges();
    }

    /**
     * @param assetFolder папка Asset.
     */
    public void setAssetFolder(final Path assetFolder) {
        this.assetFolder = assetFolder;
    }

    /**
     * @return таблица открытых файлов.
     */
    public Map<String, String> getOpenedFiles() {
        return openedFiles;
    }

    /**
     * Добавление нового открытого файла.
     *
     * @param file       открытый файл.
     * @param fileEditor редактор, в котором он открыт.
     */
    public synchronized void addOpenedFile(final Path file, final FileEditor fileEditor) {

        final Path assetFile = EditorUtil.getAssetFile(getAssetFolder(), file);
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final EditorDescription description = fileEditor.getDescription();

        final Map<String, String> openedFiles = getOpenedFiles();
        openedFiles.put(assetPath, description.getEditorId());

        incrementChanges();
    }

    /**
     * Удаление открытого файла.
     */
    public synchronized void removeOpenedFile(final Path file) {

        final Path assetFile = EditorUtil.getAssetFile(getAssetFolder(), file);
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final Map<String, String> openedFiles = getOpenedFiles();
        openedFiles.remove(assetPath);

        incrementChanges();
    }

    /**
     * @return папка с Asset.
     */
    public Path getAssetFolder() {
        return assetFolder;
    }

    /**
     * Увелические счетчика зименений.
     */
    private void incrementChanges() {
        changes.incrementAndGet();
    }

    /**
     * Сохранение состояния в файл.
     */
    public void save() {

        if (changes.get() == 0) {
            return;
        }

        final Path assetFolder = getAssetFolder();
        final Path workspaceFile = assetFolder.resolve(WorkspaceManager.FOLDER_EDITOR).resolve(WorkspaceManager.FILE_WORKSPACE);

        try {

            if (!Files.exists(workspaceFile)) {
                Files.createDirectories(workspaceFile.getParent());
                Files.createFile(workspaceFile);
            }

        } catch (final IOException e) {
            LOGGER.warning(e);
        }

        try {

            final Boolean hidden = (Boolean) Files.getAttribute(workspaceFile, "dos:hidden", LinkOption.NOFOLLOW_LINKS);

            if (hidden != null && !hidden) {
                Files.setAttribute(workspaceFile, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
            }

        } catch (final IOException e) {
            LOGGER.warning(e);
        }

        changes.set(0);

        final byte[] serialize = EditorUtil.serialize(this);

        try (final SeekableByteChannel channel = Files.newByteChannel(workspaceFile, StandardOpenOption.WRITE)) {

            final ByteBuffer buffer = ByteBuffer.wrap(serialize);
            buffer.position(serialize.length);
            buffer.flip();

            channel.write(buffer);

        } catch (IOException e) {
            LOGGER.warning(e);
        }
    }
}
