package com.ss.editor.ui.component.editor.impl.scene;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static java.util.Objects.requireNonNull;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.light.Light;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.FileExtensions;
import com.ss.editor.control.transform.SceneEditorControl.TransformType;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.model.undo.EditorOperationControl;
import com.ss.editor.model.undo.UndoableEditor;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.workspace.Workspace;
import com.ss.editor.state.editor.impl.scene.SceneEditorAppState;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.state.impl.SceneFileEditorState;
import com.ss.editor.util.MaterialUtils;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.scene.layout.StackPane;

/**
 * The implementation of the {@link AbstractFileEditor} for working with {@link SceneNode}.
 *
 * @author JavaSaBr
 */
public class SceneFileEditor extends AbstractFileEditor<StackPane> implements UndoableEditor, ModelChangeConsumer {

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setEditorName("Scene editor");
        DESCRIPTION.setConstructor(SceneFileEditor::new);
        DESCRIPTION.setEditorId(SceneFileEditor.class.getSimpleName());
        DESCRIPTION.addExtension(FileExtensions.JME_SCENE);
    }

    /**
     * The scene editor app state.
     */
    @NotNull
    private final SceneEditorAppState editorAppState;

    /**
     * The operation control.
     */
    @NotNull
    private final EditorOperationControl operationControl;

    /**
     * The changes counter.
     */
    @NotNull
    private final AtomicInteger changeCounter;

    /**
     * The state of this editor.
     */
    private SceneFileEditorState editorState;

    /**
     * The current scene.
     */
    private SceneNode currentScene;

    /**
     * The flag of ignoring listeners.
     */
    private boolean ignoreListeners;

    public SceneFileEditor() {
        this.editorAppState = new SceneEditorAppState(this);
        this.operationControl = new EditorOperationControl(this);
        this.changeCounter = new AtomicInteger();
        addEditorState(editorAppState);
    }

    @Override
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        final Path assetFile = requireNonNull(getAssetFile(file), "Asset file for " + file + " can't be null.");
        final ModelKey modelKey = new ModelKey(toAssetPath(assetFile));

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.deleteFromCache(modelKey);

        final SceneNode model = (SceneNode) assetManager.loadAsset(modelKey);

        MaterialUtils.cleanUpMaterialParams(model);

        final SceneEditorAppState editorState = getEditorAppState();
        editorState.openModel(model);

        setCurrentScene(model);
        setIgnoreListeners(true);
        try {

        } finally {
            setIgnoreListeners(false);
        }

        EXECUTOR_MANAGER.addFXTask(this::loadState);
    }

    /**
     * Load the saved state.
     */
    protected void loadState() {

        final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
        final Workspace currentWorkspace = requireNonNull(workspaceManager.getCurrentWorkspace(),
                "Current workspace can't be null.");

        editorState = currentWorkspace.getEditorState(getEditFile(), SceneFileEditorState::new);

        final TransformType transformType = TransformType.valueOf(editorState.getTransformationType());

        switch (transformType) {
            case MOVE_TOOL:
                // moveToolButton.setSelected(true);
                break;
            case ROTATE_TOOL:
                //  rotationToolButton.setSelected(true);
                break;
            case SCALE_TOOL:
                // scaleToolButton.setSelected(true);
                break;
            default:
                break;
        }

        final SceneEditorAppState editorAppState = getEditorAppState();
        final Vector3f cameraLocation = editorState.getCameraLocation();

        final float hRotation = editorState.getCameraHRotation();
        final float vRotation = editorState.getCameraVRotation();
        final float tDistance = editorState.getCameraTDistance();

        EXECUTOR_MANAGER.addEditorThreadTask(() -> editorAppState.updateCamera(cameraLocation, hRotation, vRotation, tDistance));
    }

    /**
     * @return the 3D part of this editor.
     */
    @NotNull
    private SceneEditorAppState getEditorAppState() {
        return editorAppState;
    }

    @NotNull
    @Override
    protected StackPane createRoot() {
        return new StackPane();
    }

    @Override
    protected void createContent(@NotNull final StackPane root) {
    }

    @NotNull
    @Override
    public EditorDescription getDescription() {
        return DESCRIPTION;
    }

    @Override
    public void notifyChangeProperty(@Nullable final Object parent, @NotNull final Object object, @NotNull final String propertyName) {

    }

    @Override
    public void notifyAddedChild(@NotNull final Node parent, @NotNull final Spatial added, final int index) {

    }

    @Override
    public void notifyAddedChild(@NotNull final Object parent, @NotNull final Object added, final int index) {

    }

    @Override
    public void notifyAddedControl(@NotNull final Spatial spatial, @NotNull final Control control, final int index) {

    }

    @Override
    public void notifyRemovedControl(@NotNull final Spatial spatial, @NotNull final Control control) {

    }

    @Override
    public void notifyAddedLight(@NotNull final Node parent, @NotNull final Light added, final int index) {

    }

    @Override
    public void notifyRemovedChild(@NotNull final Node parent, @NotNull final Spatial removed) {

    }

    @Override
    public void notifyRemovedChild(@NotNull final Object parent, @NotNull final Object removed) {

    }

    @Override
    public void notifyRemovedLight(@NotNull final Node parent, @NotNull final Light removed) {

    }

    @Override
    public void notifyReplaced(@NotNull final Node parent, @NotNull final Spatial oldChild, @NotNull final Spatial newChild) {

    }

    @Override
    public void notifyReplaced(@NotNull final Object parent, @Nullable final Object oldChild, @Nullable final Object newChild) {

    }

    @Override
    public void notifyMoved(@NotNull final Node prevParent, @NotNull final Node newParent, @NotNull final Spatial child, final int index) {

    }

    @Override
    public void execute(@NotNull final EditorOperation operation) {
        operationControl.execute(operation);
    }

    @Override
    public void incrementChange() {
        final int result = changeCounter.incrementAndGet();
        setDirty(result != 0);
    }

    @Override
    public void decrementChange() {
        final int result = changeCounter.decrementAndGet();
        setDirty(result != 0);
    }

    /**
     * @return true if needs to ignore events.
     */
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * @param ignoreListeners true if needs to ignore events.
     */
    private void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @param currentScene the opened model.
     */
    private void setCurrentScene(@NotNull final SceneNode currentScene) {
        this.currentScene = currentScene;
    }

    @NotNull
    @Override
    public SceneNode getCurrentModel() {
        return currentScene;
    }
}
