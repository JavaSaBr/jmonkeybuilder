package com.ss.editor.ui.component.editor.impl.model;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.asset.TextureKey;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.state.editor.impl.model.ModelEditor3DState;
import com.ss.editor.state.editor.impl.model.ModelEditorBulletState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.impl.scene.AbstractSceneFileEditor;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.EditorModelEditorState;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.util.MaterialUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * The implementation of the {@link AbstractFileEditor} for working with {@link Spatial}.
 *
 * @author JavaSaBr
 */
public class ModelFileEditor extends AbstractSceneFileEditor<Spatial, ModelEditor3DState, EditorModelEditorState> {

    @NotNull
    private static final String NO_FAST_SKY = Messages.MODEL_FILE_EDITOR_NO_SKY;

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setEditorName(Messages.MODEL_FILE_EDITOR_NAME);
        DESCRIPTION.setConstructor(ModelFileEditor::new);
        DESCRIPTION.setEditorId(ModelFileEditor.class.getSimpleName());
        DESCRIPTION.addExtension(FileExtensions.JME_OBJECT);
    }

    @NotNull
    private static final Array<String> FAST_SKY_LIST = ArrayFactory.newArray(String.class);

    static {
        FAST_SKY_LIST.add(NO_FAST_SKY);
        FAST_SKY_LIST.add("graphics/textures/sky/studio.hdr");
        FAST_SKY_LIST.add("graphics/textures/sky/env1.hdr");
        FAST_SKY_LIST.add("graphics/textures/sky/env2.hdr");
        FAST_SKY_LIST.add("graphics/textures/sky/env3.hdr");
        FAST_SKY_LIST.add("graphics/textures/sky/env4.hdr");
        FAST_SKY_LIST.add("graphics/textures/sky/outside.hdr");
        FAST_SKY_LIST.add("graphics/textures/sky/inside.hdr");
    }

    @NotNull
    private final ModelEditorBulletState bulletState;

    /**
     * The list of fast skies.
     */
    @Nullable
    private ComboBox<String> fastSkyComboBox;

    /**
     * The light toggle.
     */
    @Nullable
    private ToggleButton lightButton;

    /**
     * The physics toggle.
     */
    @Nullable
    private ToggleButton physicsButton;

    /**
     * The debug physics toggle.
     */
    @Nullable
    private ToggleButton debugPhysicsButton;

    private ModelFileEditor() {
        super();
        this.bulletState = new ModelEditorBulletState(getEditor3DState());
        this.bulletState.setEnabled(false);
        this.bulletState.setDebugEnabled(false);
        this.bulletState.setSpeed(1F);
        addEditorState(bulletState);
    }

    @Override
    @FXThread
    protected @NotNull ModelEditor3DState create3DEditorState() {
        return new ModelEditor3DState(this);
    }

    /**
     * @return the list of fast skies.
     */
    private @NotNull ComboBox<String> getFastSkyComboBox() {
        return notNull(fastSkyComboBox);
    }

    /**
     * @return the light toggle.
     */
    private @NotNull ToggleButton getLightButton() {
        return notNull(lightButton);
    }

    /**
     * @return the physics toggle.
     */
    private @NotNull ToggleButton getPhysicsButton() {
        return notNull(physicsButton);
    }

    /**
     * @return the debug physics button.
     */
    private @NotNull ToggleButton getDebugPhysicsButton() {
        return notNull(debugPhysicsButton);
    }

    @Override
    @FXThread
    protected void doOpenFile(@NotNull final Path file) {
        super.doOpenFile(file);

        final Path assetFile = notNull(getAssetFile(file), "Asset file for " + file + " can't be null.");
        final ModelKey modelKey = new ModelKey(toAssetPath(assetFile));

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Spatial model = assetManager.loadAsset(modelKey);

        MaterialUtils.cleanUpMaterialParams(model);

        final ModelEditor3DState editor3DState = getEditor3DState();
        editor3DState.openModel(model);

        handleAddedObject(model);

        setCurrentModel(model);
        setIgnoreListeners(true);
        try {

            final ComboBox<String> fastSkyComboBox = getFastSkyComboBox();
            fastSkyComboBox.getSelectionModel().select(FAST_SKY_LIST.first());

            refreshTree();

        } finally {
            setIgnoreListeners(false);
        }
    }

    @Override
    protected void loadState() {
        super.loadState();

        final EditorModelEditorState editorState = notNull(getEditorState());

        final ComboBox<String> fastSkyComboBox = getFastSkyComboBox();
        fastSkyComboBox.getSelectionModel().select(editorState.getSkyType());

        final ToggleButton lightButton = getLightButton();
        lightButton.setSelected(editorState.isEnableLight());

        final ToggleButton physicsButton = getPhysicsButton();
        physicsButton.setSelected(editorState.isEnablePhysics());

        final ToggleButton debugPhysicsButton = getDebugPhysicsButton();
        debugPhysicsButton.setSelected(editorState.isEnableDebugPhysics());
    }

    @Override
    protected @Nullable Supplier<EditorState> getEditorStateFactory() {
        return EditorModelEditorState::new;
    }

    @Override
    protected void handleAddedObject(@NotNull final Spatial model) {
        super.handleAddedObject(model);

        final ModelEditor3DState editor3DState = getEditor3DState();
        final Array<Geometry> geometries = ArrayFactory.newArray(Geometry.class);

        NodeUtils.addGeometry(model, geometries);

        if (!geometries.isEmpty()) {
            geometries.forEach(geometry -> {
                if (geometry.getQueueBucket() == RenderQueue.Bucket.Sky) {
                    editor3DState.addCustomSky(geometry);
                }
            });
        }
    }

    @FXThread
    @Override
    protected void handleRemovedObject(@NotNull final Spatial model) {
        super.handleRemovedObject(model);

        final ModelEditor3DState editor3DState = getEditor3DState();
        final Array<Geometry> geometries = ArrayFactory.newArray(Geometry.class);

        NodeUtils.addGeometry(model, geometries);

        if (!geometries.isEmpty()) {
            geometries.forEach(geometry -> {
                if (geometry.getQueueBucket() == RenderQueue.Bucket.Sky) {
                    editor3DState.removeCustomSky(geometry);
                }
            });
        }
    }

    @Override
    public @NotNull EditorDescription getDescription() {
        return DESCRIPTION;
    }

    @Override
    protected void createToolbar(@NotNull final HBox container) {
        super.createToolbar(container);

        final Label fastSkyLabel = new Label(Messages.MODEL_FILE_EDITOR_FAST_SKY + ":");

        fastSkyComboBox = new ComboBox<>();
        fastSkyComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> changeFastSky(newValue));

        final ObservableList<String> skyItems = fastSkyComboBox.getItems();
        skyItems.addAll(FAST_SKY_LIST);

        final ResourceManager resourceManager = ResourceManager.getInstance();
        final Array<Path> additionalEnvs = resourceManager.getAdditionalEnvs();
        additionalEnvs.forEach(path -> skyItems.add(path.toString()));

        FXUtils.addToPane(fastSkyLabel, container);
        FXUtils.addToPane(fastSkyComboBox, container);
    }

    @Override
    protected void createActions(@NotNull final HBox container) {
        super.createActions(container);

        lightButton = new ToggleButton();
        lightButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_CAMERA_LIGHT));
        lightButton.setGraphic(new ImageView(Icons.LIGHT_16));
        lightButton.setSelected(true);
        lightButton.selectedProperty()
                .addListener((observable, oldValue, newValue) -> changeLight(newValue));

        physicsButton = new ToggleButton();
        physicsButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_CAMERA_LIGHT));
        physicsButton.setGraphic(new ImageView(Icons.PHYSICS_16));
        physicsButton.setSelected(false);
        physicsButton.selectedProperty()
                .addListener((observable, oldValue, newValue) -> changePhysics(newValue));

        debugPhysicsButton = new ToggleButton();
        debugPhysicsButton.setTooltip(new Tooltip(Messages.SCENE_FILE_EDITOR_ACTION_CAMERA_LIGHT));
        debugPhysicsButton.setGraphic(new ImageView(Icons.DEBUG_16));
        debugPhysicsButton.setSelected(false);
        debugPhysicsButton.selectedProperty()
                .addListener((observable, oldValue, newValue) -> changeDebugPhysics(newValue));

        DynamicIconSupport.addSupport(lightButton, physicsButton, debugPhysicsButton);

        FXUtils.addClassTo(lightButton, physicsButton, debugPhysicsButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addToPane(lightButton, physicsButton, debugPhysicsButton, container);
    }

    /**
     * Handle changing a sky.
     */
    private void changeFastSky(@NotNull final String newSky) {
        if (isIgnoreListeners()) return;

        final ModelEditor3DState editor3DState = getEditor3DState();

        if (NO_FAST_SKY.equals(newSky)) {
            editor3DState.changeFastSky(null);
            final EditorModelEditorState editorState = getEditorState();
            if (editorState != null) editorState.setSkyType(0);
            return;
        }

        final AssetManager assetManager = EDITOR.getAssetManager();

        final TextureKey key = new TextureKey(newSky, true);
        key.setGenerateMips(false);

        final Texture texture = assetManager.loadTexture(key);
        final Spatial newFastSky = SkyFactory.createSky(assetManager, texture, EnvMapType.EquirectMap);

        editor3DState.changeFastSky(newFastSky);

        final ComboBox<String> fastSkyComboBox = getFastSkyComboBox();
        final SingleSelectionModel<String> selectionModel = fastSkyComboBox.getSelectionModel();
        final int selectedIndex = selectionModel.getSelectedIndex();

        final EditorModelEditorState editorState = getEditorState();
        if (editorState != null) editorState.setSkyType(selectedIndex);
    }

    /**
     * @return the bullet state.
     */
    private @NotNull ModelEditorBulletState getBulletState() {
        return bulletState;
    }

    /**
     * Handle to change enabling of physics.
     */
    private void changePhysics(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        EXECUTOR_MANAGER.addJMETask(() -> getBulletState().setEnabled(newValue));

        if (editorState != null) editorState.setEnablePhysics(newValue);
    }

    /**
     * Handle to change enabling of physics.
     */
    private void changeDebugPhysics(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        EXECUTOR_MANAGER.addJMETask(() -> getBulletState().setDebugEnabled(newValue));

        if (editorState != null) editorState.setEnableDebugPhysics(newValue);
    }

    /**
     * Handle changing camera light visibility.
     */
    private void changeLight(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final ModelEditor3DState editor3DState = getEditor3DState();
        editor3DState.updateLightEnabled(newValue);

        if (editorState != null) editorState.setEnableLight(newValue);
    }

    @Override
    public void notifyFXAddedChild(@NotNull final Object parent, @NotNull final Object added, final int index,
                                   final boolean needSelect) {
        super.notifyFXAddedChild(parent, added, index, needSelect);

        final ModelEditor3DState editor3DState = getEditor3DState();

        if (added instanceof Spatial) {

            final Spatial spatial = (Spatial) added;
            final boolean isSky = spatial.getQueueBucket() == RenderQueue.Bucket.Sky;

            if (isSky) {
                editor3DState.addCustomSky(spatial);
                editor3DState.updateLightProbe();
            }
        }

        EXECUTOR_MANAGER.addFXTask(() -> getBulletState().notifyAdded(added));
    }

    @Override
    public void notifyFXRemovedChild(@NotNull final Object parent, @NotNull final Object removed) {
        super.notifyFXRemovedChild(parent, removed);

        final ModelEditor3DState editor3DState = getEditor3DState();

        if (removed instanceof Spatial) {

            final Spatial spatial = (Spatial) removed;
            final boolean isSky = spatial.getQueueBucket() == RenderQueue.Bucket.Sky;

            if (isSky) {
                editor3DState.removeCustomSky(spatial);
                editor3DState.updateLightProbe();
            }
        }

        EXECUTOR_MANAGER.addFXTask(() -> getBulletState().notifyRemoved(removed));
    }

    @Override
    public String toString() {
        return "ModelFileEditor{" +
                "} " + super.toString();
    }
}
