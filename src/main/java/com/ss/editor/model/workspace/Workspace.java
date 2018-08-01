package com.ss.editor.model.workspace;

import static com.ss.editor.util.EditorUtils.getAssetFile;
import static com.ss.editor.util.EditorUtils.toAssetPath;
import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.ui.component.editor.EditorDescriptor;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.util.EditorUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
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

    public Workspace() {
        this.changes = new AtomicInteger();
    }

    /**
     * Notify about finished restoring this workspace.
     */
    @FromAnyThread
    public synchronized void notifyRestored() {

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
     * Get the current edited file.
     *
     * @return the current edited file.
     */
    @FromAnyThread
    public synchronized @Nullable String getCurrentEditedFile() {
        return currentEditedFile;
    }

    /**
     * Update the current edited file.
     *
     * @param file the current edited file.
     */
    @FromAnyThread
    public synchronized void updateCurrentEditedFile(@Nullable final Path file) {

        if (file == null) {
            this.currentEditedFile = null;
            return;
        }

        final Path assetFile = getAssetFile(getAssetFolder(), file);
        this.currentEditedFile = toAssetPath(assetFile);
    }

    /**
     * Get the table with states of editors.
     *
     * @return the table with states of editors.
     */
    @FromAnyThread
    private synchronized @NotNull Map<String, EditorState> getEditorStateMap() {
        return notNull(editorStateMap);
    }

    /**
     * Get the list of expanded folders.
     *
     * @return the list of expanded folders.
     */
    @FromAnyThread
    private synchronized @NotNull List<String> getExpandedFolders() {
        return notNull(expandedFolders);
    }

    /**
     * Get the expanded absolute folders.
     *
     * @return the list of expanded absolute folders.
     */
    @FromAnyThread
    public synchronized @NotNull Array<Path> getExpandedAbsoluteFolders() {

        final Array<Path> result = ArrayFactory.newArray(Path.class);
        final Path assetFolder = getAssetFolder();

        final List<String> expandedFolders = getExpandedFolders();
        expandedFolders.forEach(path -> result.add(assetFolder.resolve(path)));

        return result;
    }

    /**
     * Update the list of expanded folders.
     *
     * @param folders the folders
     */
    @FromAnyThread
    public synchronized void updateExpandedFolders(@NotNull final Array<Path> folders) {

        final List<String> expandedFolders = getExpandedFolders();
        expandedFolders.clear();

        final Path assetFolder = getAssetFolder();
        folders.forEach(path -> expandedFolders.add(assetFolder.relativize(path).toString()));

        incrementChanges();
    }

    /**
     * Get the editor state for the file.
     *
     * @param <T>          the editor state's type.
     * @param file         the edited file.
     * @param stateFactory the state factory.
     * @return the state of the editor.
     */
    @FromAnyThread
    public synchronized <T extends EditorState> @NotNull T getEditorState(@NotNull final Path file,
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
     * Remove an editor state of the file.
     *
     * @param file the file.
     */
    @FromAnyThread
    public synchronized void removeEditorState(@NotNull final Path file) {

        final Path assetFile = getAssetFile(getAssetFolder(), file);
        final String assetPath = toAssetPath(assetFile);

        final Map<String, EditorState> editorStateMap = getEditorStateMap();
        if (editorStateMap.remove(assetPath) == null) return;

        incrementChanges();
    }

    /**
     * Set the asset folder.
     *
     * @param assetFolder the asset folder of this workspace.
     */
    @FromAnyThread
    public synchronized void setAssetFolder(@NotNull final Path assetFolder) {
        this.assetFolder = assetFolder;
    }

    /**
     * Get the opened files.
     *
     * @return the table of opened files.
     */
    @FromAnyThread
    public synchronized @NotNull Map<String, String> getOpenedFiles() {
        return notNull(openedFiles);
    }

    /**
     * Add the new opened file.
     *
     * @param file       the opened file.
     * @param fileEditor the editor.
     */
    @FromAnyThread
    public synchronized void addOpenedFile(@NotNull final Path file, @NotNull final FileEditor fileEditor) {

        final Path assetFile = getAssetFile(getAssetFolder(), file);
        final String assetPath = toAssetPath(assetFile);

        final EditorDescriptor description = fileEditor.getDescriptor();

        final Map<String, String> openedFiles = getOpenedFiles();
        final String previous = openedFiles.put(assetPath, description.getEditorId());
        if (StringUtils.equals(previous, description.getEditorId())) {
            return;
        }

        incrementChanges();
    }

    /**
     * Remove the opened file.
     *
     * @param file the removed file.
     */
    @FromAnyThread
    public synchronized void removeOpenedFile(@NotNull final Path file) {

        final Path assetFile = getAssetFile(getAssetFolder(), file);
        final String assetPath = toAssetPath(assetFile);

        final Map<String, String> openedFiles = getOpenedFiles();
        openedFiles.remove(assetPath);

        incrementChanges();
    }

    /**
     * Get the asset folder.
     *
     * @return the asset folder of this workspace.
     */
    @FromAnyThread
    public synchronized @NotNull Path getAssetFolder() {
        return notNull(assetFolder);
    }

    /**
     * Increase a counter of changes.
     */
    @FromAnyThread
    private void incrementChanges() {
        changes.incrementAndGet();
    }

    /**
     * Clear this workspace.
     */
    @FromAnyThread
    public synchronized void clear() {
        getOpenedFiles().clear();
    }

    /**
     * Save this workspace.
     *
     * @param force the force
     */
    @FromAnyThread
    public synchronized void save(final boolean force) {
        if (!force && changes.get() == 0) {
            return;
        }

        final Path assetFolder = getAssetFolder();
        if (!Files.exists(assetFolder)) {
            return;
        }

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
            // we can ignore that
        } catch (final IOException e) {
            LOGGER.warning(e);
        }

        changes.set(0);
        try {
            Files.write(workspaceFile, EditorUtils.serialize(this));
        } catch (final IOException e) {
            LOGGER.warning(e);
        }
    }
}
