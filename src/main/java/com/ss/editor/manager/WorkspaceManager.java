package com.ss.editor.manager;

import static java.util.Objects.requireNonNull;
import static com.ss.rlib.util.Utils.get;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.model.workspace.Workspace;
import com.ss.editor.util.EditorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.manager.InitializeManager;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The class to manage workspaces.
 *
 * @author JavaSaBr
 */
public class WorkspaceManager {

    /**
     * The constant FOLDER_EDITOR.
     */
    @NotNull
    public static final String FOLDER_EDITOR = ".jme3-spaceshift-editor";

    /**
     * The constant FILE_WORKSPACE.
     */
    @NotNull
    public static final String FILE_WORKSPACE = "workspace";

    @Nullable
    private static WorkspaceManager instance;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static WorkspaceManager getInstance() {
        if (instance == null) instance = new WorkspaceManager();
        return instance;
    }

    /**
     * The table of workspaces.
     */
    @NotNull
    private final ObjectDictionary<Path, Workspace> workspaces;

    private WorkspaceManager() {
        InitializeManager.valid(getClass());
        this.workspaces = DictionaryFactory.newObjectDictionary();
    }

    /**
     * @return the table of workspaces.
     */
    @NotNull
    private ObjectDictionary<Path, Workspace> getWorkspaces() {
        return workspaces;
    }

    /**
     * Get the current workspace.
     *
     * @return the current workspace or null.
     */
    @Nullable
    @FromAnyThread
    public Workspace getCurrentWorkspace() {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return null;

        return getWorkspace(currentAsset);
    }

    /**
     * Get the workspace for the asset folder.
     *
     * @return the workspace.
     */
    @NotNull
    @FromAnyThread
    private synchronized Workspace getWorkspace(@NotNull final Path assetFolder) {

        final ObjectDictionary<Path, Workspace> workspaces = getWorkspaces();
        final Workspace exists = workspaces.get(assetFolder);
        if (exists != null) return exists;

        final Path workspaceFile = assetFolder.resolve(FOLDER_EDITOR).resolve(FILE_WORKSPACE);

        if (!Files.exists(workspaceFile)) {

            final Workspace workspace = new Workspace();
            workspace.notifyRestored();
            workspace.setAssetFolder(assetFolder);
            workspaces.put(assetFolder, workspace);

            return workspace;
        }

        Workspace workspace;

        try {
            workspace = EditorUtil.deserialize(requireNonNull(get(workspaceFile, Files::readAllBytes)));
        } catch (final RuntimeException e) {
            workspace = new Workspace();
        }

        workspace.notifyRestored();
        workspace.setAssetFolder(assetFolder);
        workspaces.put(assetFolder, workspace);

        return workspace;
    }

    /**
     * Clear all workspaces.
     */
    @FromAnyThread
    public synchronized void clear() {
        final ObjectDictionary<Path, Workspace> workspaces = getWorkspaces();
        workspaces.forEach(Workspace::clear);
        workspaces.forEach((path, workspace) -> workspace.save(true));
    }

    /**
     * Save all workspaces.
     */
    @FromAnyThread
    public synchronized void save() {
        final ObjectDictionary<Path, Workspace> workspaces = getWorkspaces();
        workspaces.forEach((path, workspace) -> workspace.save(true));
    }
}
