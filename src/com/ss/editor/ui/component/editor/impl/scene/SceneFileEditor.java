package com.ss.editor.ui.component.editor.impl.scene;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static java.util.Objects.requireNonNull;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.state.editor.impl.scene.SceneEditorAppState;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.SceneFileEditorState;
import com.ss.editor.ui.control.app.state.list.AppStateList;
import com.ss.editor.ui.control.app.state.property.AppStatePropertyEditor;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.MaterialUtils;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;
import com.ss.extension.scene.app.state.EditableSceneAppState;
import com.ss.extension.scene.app.state.SceneAppState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;

/**
 * The implementation of the {@link AbstractFileEditor} for working with {@link SceneNode}.
 *
 * @author JavaSaBr
 */
public class SceneFileEditor extends AbstractSceneFileEditor<SceneFileEditor, SceneNode,
        SceneEditorAppState, SceneFileEditorState> implements SceneChangeConsumer {

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setEditorName(Messages.SCENE_FILE_EDITOR_NAME);
        DESCRIPTION.setConstructor(SceneFileEditor::new);
        DESCRIPTION.setEditorId(SceneFileEditor.class.getSimpleName());
        DESCRIPTION.addExtension(FileExtensions.JME_SCENE);
    }

    /**
     * The selection handler.
     */
    private Consumer<EditableSceneAppState> selectionAppStateHandler;

    /**
     * The list with app states.
     */
    private AppStateList appStateList;

    /**
     * The property editor of app states.
     */
    private AppStatePropertyEditor appStatePropertyEditor;

    public SceneFileEditor() {
    }

    @NotNull
    @Override
    protected SceneEditorAppState createEditorAppState() {
        return new SceneEditorAppState(this);
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

        setCurrentModel(model);
        setIgnoreListeners(true);
        try {

            final ModelNodeTree modelNodeTree = getModelNodeTree();
            modelNodeTree.fill(model);

            final AppStateList appStateList = getAppStateList();
            appStateList.fill(model);

        } finally {
            setIgnoreListeners(false);
        }

        EXECUTOR_MANAGER.addFXTask(this::loadState);
    }

    /**
     * @return the list with app states.
     */
    private AppStateList getAppStateList() {
        return appStateList;
    }

    @Override
    protected void createContent(@NotNull final StackPane root) {
        this.selectionAppStateHandler = this::selectAppStateFromList;

        super.createContent(root);

        appStateList = new AppStateList(selectionAppStateHandler, this);
        appStatePropertyEditor = new AppStatePropertyEditor(this);

        final SplitPane appStateSplitContainer = new SplitPane(appStateList, appStatePropertyEditor);
        appStateSplitContainer.setId(CSSIds.FILE_EDITOR_TOOL_SPLIT_PANE);
        appStateSplitContainer.prefHeightProperty().bind(root.heightProperty());
        appStateSplitContainer.prefWidthProperty().bind(root.widthProperty());

        editorToolComponent.addComponent(appStateSplitContainer, "App states");

        root.heightProperty().addListener((observableValue, oldValue, newValue) ->
                calcVSplitSize(appStateSplitContainer));
    }

    /**
     * Handle the selected app state from the list.
     */
    @FXThread
    public void selectAppStateFromList(@Nullable final EditableSceneAppState appState) {
        appStatePropertyEditor.buildFor(appState, null);
    }

    @NotNull
    @Override
    public EditorDescription getDescription() {
        return DESCRIPTION;
    }

    @NotNull
    @Override
    protected Supplier<EditorState> getStateConstructor() {
        return SceneFileEditorState::new;
    }

    @Override
    protected void updateSelection(@Nullable Spatial spatial) {

        if (spatial instanceof SceneNode || spatial instanceof SceneLayer) {
            spatial = null;
        }

        super.updateSelection(spatial);
    }

    @Override
    public String toString() {
        return "SceneFileEditor{} " + super.toString();
    }

    @Override
    public void notifyAddedAppState(@NotNull final SceneAppState appState) {
        getEditorAppState().addAppState(appState);
        getAppStateList().fill(getCurrentModel());
    }

    @Override
    public void notifyRemovedAppState(@NotNull final SceneAppState appState) {
        getEditorAppState().removedAppState(appState);
        getAppStateList().fill(getCurrentModel());
    }

    @Override
    public void notifyChangedAppState(@NotNull final SceneAppState appState) {
        getAppStateList().fill(getCurrentModel());
    }
}
