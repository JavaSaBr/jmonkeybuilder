package com.ss.editor.manager;

import com.ss.editor.config.EditorConfig;
import com.ss.editor.model.workspace.Workspace;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;

import rlib.manager.InitializeManager;
import rlib.util.Util;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * The manager for working with workspaces.
 *
 * @author JavaSaBr
 */
public class WorkspaceManager {

    public static final String FOLDER_EDITOR = ".jme3-spaceshift-editor";
    public static final String FILE_WORKSPACE = "workspace";

    private static WorkspaceManager instance;

    public static WorkspaceManager getInstance() {
        if (instance == null) instance = new WorkspaceManager();
        return instance;
    }

    /**
     * The table of workspaces.
     */
    @NotNull
    private final ObjectDictionary<Path, Workspace> workspaces;

    public WorkspaceManager() {
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
    public synchronized Workspace getWorkspace(@NotNull final Path assetFolder) {

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
            workspace = EditorUtil.deserialize(Util.safeGet(workspaceFile, Files::readAllBytes));
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
    public synchronized void clear() {
        final ObjectDictionary<Path, Workspace> workspaces = getWorkspaces();
        workspaces.forEach(Workspace::clear);
        workspaces.forEach(Workspace::save);
    }

    /**
     * Save all workspaces.
     */
    public synchronized void save() {
        final ObjectDictionary<Path, Workspace> workspaces = getWorkspaces();
        workspaces.forEach(Workspace::save);
    }
}
