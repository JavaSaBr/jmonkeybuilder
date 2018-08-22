package com.ss.editor.manager;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static com.ss.rlib.common.util.Utils.get;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.model.workspace.Workspace;
import com.ss.editor.util.EditorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.common.manager.InitializeManager;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * The class to manage workspaces.
 *
 * @author JavaSaBr
 */
public class WorkspaceManager {

    public static final String FOLDER_EDITOR = ".jmonkeybuilder";
    public static final String FILE_WORKSPACE = "workspace";

    @Nullable
    private static WorkspaceManager instance;

    @FromAnyThread
    public static @NotNull WorkspaceManager getInstance() {
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
     * Get the table of workspaces.
     *
     * @return the table of workspaces.
     */
    @FromAnyThread
    private @NotNull ObjectDictionary<Path, Workspace> getWorkspaces() {
        return workspaces;
    }

    /**
     * Get the current workspace.
     *
     * @return the current workspace or null.
     */
    @FromAnyThread
    public @Nullable Workspace getCurrentWorkspace() {

        var editorConfig = EditorConfig.getInstance();
        var currentAsset = editorConfig.getCurrentAsset();

        if (currentAsset == null) {
            return null;
        }

        return getWorkspace(currentAsset);
    }

    /**
     * Get the optional value of current workspace.
     *
     * @return the optional value of current workspace.
     */
    @FromAnyThread
    public @NotNull Optional<Workspace> getCurrentWorkspaceOpt() {
        return Optional.ofNullable(getCurrentWorkspace());
    }

    /**
     * Require the current workspace.
     *
     * @return the current workspace.
     */
    @FromAnyThread
    public @NotNull Workspace requiredCurrentWorkspace() {
        return notNull(getCurrentWorkspace());
    }

    /**
     * Get the workspace for the asset folder.
     *
     * @return the workspace.
     */
    @FromAnyThread
    private synchronized @NotNull Workspace getWorkspace(@NotNull Path assetFolder) {

        var workspaces = getWorkspaces();
        var exists = workspaces.get(assetFolder);
        if (exists != null) {
            return exists;
        }

        var workspaceFile = assetFolder.resolve(FOLDER_EDITOR)
                .resolve(FILE_WORKSPACE);

        if (!Files.exists(workspaceFile)) {

            var workspace = new Workspace();
            workspace.notifyRestored();
            workspace.setAssetFolder(assetFolder);
            workspaces.put(assetFolder, workspace);

            return workspace;
        }

        Workspace workspace;
        try {
            workspace = EditorUtils.deserialize(notNull(get(workspaceFile, Files::readAllBytes)));
        } catch (RuntimeException e) {
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
        var workspaces = getWorkspaces();
        workspaces.forEach(Workspace::clear);
        workspaces.forEach((path, workspace) -> workspace.save(true));
    }

    /**
     * Save all workspaces.
     */
    @FromAnyThread
    public synchronized void save() {
        getWorkspaces().forEach((path, workspace) -> workspace.save(true));
    }
}
