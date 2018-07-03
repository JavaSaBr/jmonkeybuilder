package com.ss.editor.ui.component.editor.impl.model;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.asset.ModelKey;
import com.jme3.asset.TextureKey;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.part3d.editor.impl.model.ModelEditor3dPart;
import com.ss.editor.part3d.editor.impl.model.ModelEditorBulletPart;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescriptor;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.impl.scene.AbstractSceneFileEditor;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.EditorModelEditorState;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.MaterialUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * The implementation of the {@link AbstractFileEditor} for working with {@link Spatial}.
 *
 * @author JavaSaBr
 */
public class ModelFileEditor extends AbstractSceneFileEditor<Spatial, ModelEditor3dPart, EditorModelEditorState> {

    private static final String NO_FAST_SKY = Messages.MODEL_FILE_EDITOR_NO_SKY;

    public static final EditorDescriptor DESCRIPTOR = new EditorDescriptor(
            ModelFileEditor::new,
            Messages.MODEL_FILE_EDITOR_NAME,
            ModelFileEditor.class.getSimpleName(),
            FileExtensions.JME_OBJECT
    );

    private static final Array<String> FAST_SKY_LIST = Array.of(
            NO_FAST_SKY,
            "graphics/textures/sky/studio.hdr",
            "graphics/textures/sky/env1.hdr",
            "graphics/textures/sky/env2.hdr",
            "graphics/textures/sky/env3.hdr",
            "graphics/textures/sky/env4.hdr",
            "graphics/textures/sky/outside.hdr",
            "graphics/textures/sky/inside.hdr"
    );

    /**
     * The bullet state.
     */
    @NotNull
    private final ModelEditorBulletPart bulletState;

    /**
     * The list of fast skies.
     */
    @NotNull
    private final ComboBox<String> fastSkyComboBox;

    /**
     * The light toggle.
     */
    @NotNull
    private final ToggleButton lightButton;

    /**
     * The physics toggle.
     */
    @NotNull
    private final ToggleButton physicsButton;

    /**
     * The debug physics toggle.
     */
    @NotNull
    private final ToggleButton debugPhysicsButton;

    private ModelFileEditor() {
        super();
        fastSkyComboBox = new ComboBox<>();
        lightButton = new ToggleButton();
        physicsButton = new ToggleButton();
        debugPhysicsButton = new ToggleButton();
        this.bulletState = new ModelEditorBulletPart(editor3dPart);
        this.bulletState.setEnabled(false);
        this.bulletState.setDebugEnabled(false);
        this.bulletState.setSpeed(1F);
        addEditor3dPart(bulletState);
    }

    @Override
    @FxThread
    protected @NotNull ModelEditor3dPart create3dEditorPart() {
        return new ModelEditor3dPart(this);
    }

    @Override
    @FxThread
    protected void doOpenFile(@NotNull Path file) throws IOException {
        super.doOpenFile(file);

        var assetFile = EditorUtil.requireAssetFile(file);
        var modelKey = new ModelKey(EditorUtil.toAssetPath(assetFile));

        var assetManager = EditorUtil.getAssetManager();
        var model = assetManager.loadAsset(modelKey);

        MaterialUtils.cleanUpMaterialParams(model);

        editor3dPart.openModel(model);
        handleAddedObject(model);
        setCurrentModel(model);
        setIgnoreListeners(true);
        try {

            fastSkyComboBox.getSelectionModel()
                    .select(FAST_SKY_LIST.first());

            refreshTree();

        } finally {
            setIgnoreListeners(false);
        }
    }

    @Override
    @FxThread
    protected void loadState() {
        super.loadState();

        var editorState = notNull(getEditorState());

        fastSkyComboBox.getSelectionModel()
                .select(editorState.getSkyType());

        lightButton.setSelected(editorState.isEnableLight());
        physicsButton.setSelected(editorState.isEnablePhysics());
        debugPhysicsButton.setSelected(editorState.isEnableDebugPhysics());
    }

    @Override
    @FxThread
    protected @Nullable Supplier<EditorState> getEditorStateFactory() {
        return EditorModelEditorState::new;
    }

    @Override
    @FxThread
    protected void handleAddedObject(@NotNull Spatial model) {
        super.handleAddedObject(model);

        var geometries = NodeUtils.getGeometries(model);
        geometries.forEach(editor3dPart, (geometry, modelEditor3dPart) -> {
            if (geometry.getQueueBucket() == RenderQueue.Bucket.Sky) {
                modelEditor3dPart.addCustomSky(geometry);
            }
        });
    }

    @Override
    @FxThread
    protected void handleRemovedObject(@NotNull Spatial model) {
        super.handleRemovedObject(model);

        var geometries = NodeUtils.getGeometries(model);
        geometries.forEach(editor3dPart, (geometry, modelEditor3dPart) -> {
            if (geometry.getQueueBucket() == RenderQueue.Bucket.Sky) {
                modelEditor3dPart.removeCustomSky(geometry);
            }
        });
    }

    @Override
    @FromAnyThread
    public @NotNull EditorDescriptor getDescriptor() {
        return DESCRIPTOR;
    }

    @Override
    @FxThread
    protected void createToolbar(@NotNull HBox container) {
        super.createToolbar(container);

        var fastSkyLabel = new Label(Messages.MODEL_FILE_EDITOR_FAST_SKY + ":");

        FxControlUtils.onSelectedItemChange(fastSkyComboBox, this::changeFastSky);

        var skyItems = fastSkyComboBox.getItems();
        skyItems.addAll(FAST_SKY_LIST);

        ResourceManager.getInstance()
                .getAdditionalEnvs()
                .forEach(path -> skyItems.add(path.toString()));

        FxUtils.addChild(container, fastSkyLabel, fastSkyComboBox);
    }

    @Override
    @FxThread
    protected void createActions(@NotNull final HBox container) {
        super.createActions(container);

        lightButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_CAMERA_LIGHT));
        lightButton.setGraphic(new ImageView(Icons.LIGHT_16));
        lightButton.setSelected(true);
        lightButton.selectedProperty()
                .addListener((observable, oldValue, newValue) -> changeLight(newValue));

        physicsButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_PHYSICS));
        physicsButton.setGraphic(new ImageView(Icons.PHYSICS_16));
        physicsButton.setSelected(false);

        debugPhysicsButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_DEBUG_PHYSICS));
        debugPhysicsButton.setGraphic(new ImageView(Icons.DEBUG_16));
        debugPhysicsButton.setSelected(false);

        FxControlUtils.onSelectedChange(lightButton, this::changeLight);
        FxControlUtils.onSelectedChange(physicsButton, this::changePhysics);
        FxControlUtils.onSelectedChange(debugPhysicsButton, this::changeDebugPhysics);

        DynamicIconSupport.addSupport(lightButton, physicsButton, debugPhysicsButton);

        FxUtils.addClass(lightButton, physicsButton, debugPhysicsButton,
                CssClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        FxUtils.addChild(container, lightButton, physicsButton, debugPhysicsButton);
    }

    /**
     * Handle changing a sky.
     */
    @FxThread
    private void changeFastSky(@NotNull String newSky) {

        if (isIgnoreListeners()) {
            return;
        }

        var editorState = getEditorState();

        if (NO_FAST_SKY.equals(newSky)) {

            editor3dPart.changeFastSky(null);

            if (editorState != null) {
                editorState.setSkyType(0);
            }

            return;
        }

        var assetManager = EditorUtil.getAssetManager();

        var key = new TextureKey(newSky, true);
        key.setGenerateMips(false);

        var texture = assetManager.loadTexture(key);
        var newFastSky = SkyFactory.createSky(assetManager, texture, EnvMapType.EquirectMap);

        editor3dPart.changeFastSky(newFastSky);

        var selectedIndex = fastSkyComboBox.getSelectionModel()
                .getSelectedIndex();

        if (editorState != null) {
            editorState.setSkyType(selectedIndex);
        }
    }

    /**
     * Handle to change enabling of physics.
     */
    @FxThread
    private void changePhysics(@NotNull Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        ExecutorManager.getInstance()
                .addJmeTask(() -> bulletState.setEnabled(newValue));

        if (editorState != null) {
            editorState.setEnablePhysics(newValue);
        }
    }

    /**
     * Handle to change enabling of physics.
     */
    @FxThread
    private void changeDebugPhysics(@NotNull Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        ExecutorManager.getInstance()
                .addJmeTask(() -> bulletState.setDebugEnabled(newValue));

        if (editorState != null) {
            editorState.setEnableDebugPhysics(newValue);
        }
    }

    /**
     * Handle changing camera light visibility.
     */
    @FxThread
    private void changeLight(@NotNull Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        editor3dPart.updateLightEnabled(newValue);

        if (editorState != null) {
            editorState.setEnableLight(newValue);
        }
    }

    @Override
    @FxThread
    public void notifyFxAddedChild(@NotNull Object parent, @NotNull Object added, int index, boolean needSelect) {
        super.notifyFxAddedChild(parent, added, index, needSelect);

        if (added instanceof Spatial) {

            var spatial = (Spatial) added;
            var isSky = spatial.getQueueBucket() == RenderQueue.Bucket.Sky;

            if (isSky) {
                editor3dPart.addCustomSky(spatial);
                editor3dPart.updateLightProbe();
            }
        }

        ExecutorManager.getInstance()
                .addFxTask(() -> bulletState.notifyAdded(added));
    }

    @Override
    @FxThread
    public void notifyFxRemovedChild(@NotNull Object parent, @NotNull Object removed) {
        super.notifyFxRemovedChild(parent, removed);

        if (removed instanceof Spatial) {

            var spatial = (Spatial) removed;
            var isSky = spatial.getQueueBucket() == RenderQueue.Bucket.Sky;

            if (isSky) {
                editor3dPart.removeCustomSky(spatial);
                editor3dPart.updateLightProbe();
            }
        }

        ExecutorManager.getInstance()
                .addFxTask(() -> bulletState.notifyRemoved(removed));
    }

    @Override
    public String toString() {
        return "ModelFileEditor{" +
                "} " + super.toString();
    }
}
