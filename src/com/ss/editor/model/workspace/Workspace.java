package com.ss.editor.model.workspace;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.ClassUtils.unsafeCast;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * The workspace of an editor.
 *
 * @author JavaSaBr
 */
public class Workspace implements Serializable {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 63;

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(Workspace.class);

    /**
     * The changes counter.
     */
    @NotNull
    private final AtomicInteger changes;

    /**
     * The asset folder of this workspace.
     */
    @Nullable
    private volatile transient Path assetFolder;

    /**
     * The list of expanded folders.
     */
    @Nullable
    private volatile List<String> expandedFolders;

    /**
     * The table of opened files.
     */
    @Nullable
    private volatile Map<String, String> openedFiles;

    /**
     * The table with states of editors.
     */
    @Nullable
    private volatile Map<String, EditorState> editorStateMap;

    /**
     * The current edited file.
     */
    @Nullable
    private volatile String currentEditedFile;

    /**
     * Instantiates a new Workspace.
     */
    public Workspace() {
        this.changes = new AtomicInteger();
    }

    /**
     * Notify about finished restoring this workspace.
     */
    public void notifyRestored() {

        if (openedFiles == null) {
            openedFiles = new HashMap<>();
        }

        if (editorStateMap == null) {
            editorStateMap = new HashMap<>();
        } else {
            editorStateMap.forEach((key, editorState) ->
                    editorState.setChangeHandler(this::incrementChanges));
        }

        if (expandedFolders == null) {
            expandedFolders = new ArrayList<>();
        }
    }

    /**
     * Gets current edited file.
     *
     * @return the current edited file.
     */
    @Nullable
    public String getCurrentEditedFile() {
        return currentEditedFile;
    }

    /**
     * Update a current edited file.
     *
     * @param file the current edited file.
     */
    public synchronized void updateCurrentEditedFile(@Nullable final Path file) {

        if (file == null) {
            this.currentEditedFile = null;
            return;
        }

        final Path assetFile = getAssetFile(getAssetFolder(), file);
        this.currentEditedFile = toAssetPath(assetFile);
    }

    /**
     * @return the table with states of editors.
     */
    @NotNull
    private Map<String, EditorState> getEditorStateMap() {
        return notNull(editorStateMap);
    }

    /**
     * @return the list of expanded folders.
     */
    @NotNull
    private List<String> getExpandedFolders() {
        return notNull(expandedFolders);
    }

    /**
     * Gets expanded absolute folders.
     *
     * @return the list of expanded absolute folders.
     */
    @NotNull
    public synchronized Array<Path> getExpandedAbsoluteFolders() {

        final Array<Path> result = ArrayFactory.newArray(Path.class);
        final Path assetFolder = getAssetFolder();

        final List<String> expandedFolders = getExpandedFolders();
        expandedFolders.forEach(path -> result.add(assetFolder.resolve(path)));

        return result;
    }

    /**
     * Update a list of expanded folders.
     *
     * @param folders the folders
     */
    public synchronized void updateExpandedFolders(@NotNull final Array<Path> folders) {

        final List<String> expandedFolders = getExpandedFolders();
        expandedFolders.clear();

        final Path assetFolder = getAssetFolder();
        folders.forEach(path -> expandedFolders.add(assetFolder.relativize(path).toString()));

        incrementChanges();
    }

    /**
     * Get an editor state for a file.
     *
     * @param <T>          the type parameter
     * @param file         the edited file.
     * @param stateFactory the state factory.
     * @return the state of the editor.
     */
    @NotNull
    public synchronized <T extends EditorState> T getEditorState(@NotNull final Path file,
                                                                 @NotNull final Supplier<EditorState> stateFactory) {

        final Path assetFile = getAssetFile(getAssetFolder(), file);
        final String assetPath = toAssetPath(assetFile);

        final Map<String, EditorState> editorStateMap = getEditorStateMap();

        if (!editorStateMap.containsKey(assetPath)) {
            final EditorState editorState = stateFactory.get();
            editorState.setChangeHandler(this::incrementChanges);
            editorStateMap.put(assetPath, editorState);
            incrementChanges();
        }

        return unsafeCast(editorStateMap.get(assetPath));
    }

    /**
     * Update an editor state of a file.
     *
     * @param file        the file.
     * @param editorState the editor state.
     */
    public synchronized void updateEditorState(@NotNull final Path file, @NotNull final EditorState editorState) {

        final Path assetFile = getAssetFile(getAssetFolder(), file);
        final String assetPath = toAssetPath(assetFile);

        final Map<String, EditorState> editorStateMap = getEditorStateMap();
        editorStateMap.put(assetPath, editorState);

        incrementChanges();
    }

    /**
     * Remove an editor state of a file.
     *
     * @param file the file.
     */
    public synchronized void removeEditorState(@NotNull final Path file) {

        final Path assetFile = getAssetFile(getAssetFolder(), file);
        final String assetPath = toAssetPath(assetFile);

        final Map<String, EditorState> editorStateMap = getEditorStateMap();
        if (editorStateMap.remove(assetPath) == null) return;

        incrementChanges();
    }

    /**
     * Update an editor state for moved/renamed file.
     *
     * @param prevFile the previous file.
     * @param newFile  the new file.
     */
    public synchronized void updateEditorState(@NotNull final Path prevFile, @NotNull final Path newFile) {

        final Path prevAssetFile = getAssetFile(getAssetFolder(), prevFile);
        final String prevAssetPath = toAssetPath(prevAssetFile);

        final Map<String, EditorState> editorStateMap = getEditorStateMap();
        final EditorState editorState = editorStateMap.remove(prevAssetPath);
        if (editorState == null) return;

        final Path newAssetFile = getAssetFile(getAssetFolder(), newFile);
        final String newAssetPath = toAssetPath(newAssetFile);

        editorStateMap.put(newAssetPath, editorState);
        incrementChanges();
    }

    /**
     * Sets asset folder.
     *
     * @param assetFolder the asset folder of this workspace.
     */
    public void setAssetFolder(@NotNull final Path assetFolder) {
        this.assetFolder = assetFolder;
    }

    /**
     * Gets opened files.
     *
     * @return the table of opened files.
     */
    @NotNull
    public Map<String, String> getOpenedFiles() {
        return notNull(openedFiles);
    }

    /**
     * Add a new opened file.
     *
     * @param file       the opened file.
     * @param fileEditor the editor.
     */
    public synchronized void addOpenedFile(@NotNull final Path file, @NotNull final FileEditor fileEditor) {

        final Path assetFile = getAssetFile(getAssetFolder(), file);
        final String assetPath = toAssetPath(assetFile);

        final EditorDescription description = fileEditor.getDescription();

        final Map<String, String> openedFiles = getOpenedFiles();
        final String previous = openedFiles.put(assetPath, description.getEditorId());
        if (StringUtils.equals(previous, description.getEditorId())) return;

        incrementChanges();
    }

    /**
     * Remove an opened file.
     *
     * @param file the removed file.
     */
    public synchronized void removeOpenedFile(@NotNull final Path file) {

        final Path assetFile = getAssetFile(getAssetFolder(), file);
        final String assetPath = toAssetPath(assetFile);

        final Map<String, String> openedFiles = getOpenedFiles();
        openedFiles.remove(assetPath);

        incrementChanges();
    }

    /**
     * Gets asset folder.
     *
     * @return the asset folder of this workspace.
     */
    @NotNull
    public Path getAssetFolder() {
        return notNull(assetFolder);
    }

    /**
     * Increase a counter of changes.
     */
    private void incrementChanges() {
        changes.incrementAndGet();
    }

    /**
     * Clear this workspace.
     */
    public void clear() {
        getOpenedFiles().clear();
    }

    /**
     * Save this workspace.
     *
     * @param force the force
     */
    public void save(final boolean force) {
        if (!force && changes.get() == 0) return;

        final Path assetFolder = getAssetFolder();
        if (!Files.exists(assetFolder)) return;

        final Path workspaceFile = assetFolder.resolve(WorkspaceManager.FOLDER_EDITOR)
                .resolve(WorkspaceManager.FILE_WORKSPACE);

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

        } catch (final UnsupportedOperationException | IllegalArgumentException e) {
            // we can igone that
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

        } catch (final IOException e) {
            LOGGER.warning(e);
        }
    }
}
