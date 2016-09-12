package com.ss.editor.manager;

import com.ss.editor.config.EditorConfig;
import com.ss.editor.model.workspace.Workspace;
import com.ss.editor.util.EditorUtil;

import java.nio.file.Files;
import java.nio.file.Path;

import rlib.manager.InitializeManager;
import rlib.util.Util;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * Реализация менеджера по работе с воркспейсом.
 *
 * @author Ronn
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
     * Таблица воркспейсов.
     */
    private final ObjectDictionary<Path, Workspace> workspaces;

    public WorkspaceManager() {
        InitializeManager.valid(getClass());
        this.workspaces = DictionaryFactory.newObjectDictionary();
    }

    /**
     * @return таблица воркспейсов.
     */
    private ObjectDictionary<Path, Workspace> getWorkspaces() {
        return workspaces;
    }

    /**
     * Получение текущего воркспейса.
     */
    public Workspace getCurrentWorkspace() {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return null;

        return getWorkspace(currentAsset);
    }

    /**
     * Получение воркспейса для указанного asset папки.
     */
    public synchronized Workspace getWorkspace(final Path assetFolder) {

        final ObjectDictionary<Path, Workspace> workspaces = getWorkspaces();
        final Workspace exists = workspaces.get(assetFolder);

        if (exists != null) return exists;

        final Path workspaceFile = assetFolder.resolve(FOLDER_EDITOR).resolve(FILE_WORKSPACE);

        if (!Files.exists(workspaceFile)) {

            final Workspace workspace = new Workspace();
            workspace.setAssetFolder(assetFolder);
            workspaces.put(assetFolder, workspace);

            return workspace;
        }

        Workspace workspace = EditorUtil.deserialize(Util.safeGet(workspaceFile, Files::readAllBytes));

        if (workspace == null) {
            workspace = new Workspace();
        }

        workspace.setAssetFolder(assetFolder);
        workspaces.put(assetFolder, workspace);

        return workspace;
    }

    /**
     * Очистить воркспейсы.
     */
    public synchronized void clear() {
        final ObjectDictionary<Path, Workspace> workspaces = getWorkspaces();
        workspaces.forEach(Workspace::clear);
        workspaces.forEach(Workspace::save);
    }

    /**
     * Сохранить воркспейсы.
     */
    public synchronized void save() {
        final ObjectDictionary<Path, Workspace> workspaces = getWorkspaces();
        workspaces.forEach(Workspace::save);
    }
}
