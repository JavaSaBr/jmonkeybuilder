package com.ss.editor.ui.component.editor.impl.model;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static java.util.Objects.requireNonNull;

import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.asset.TextureKey;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.state.editor.impl.model.ModelEditorAppState;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.impl.AbstractFileEditor;
import com.ss.editor.ui.component.editor.impl.scene.AbstractSceneFileEditor;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.ModelFileEditorState;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.MaterialUtils;
import com.ss.editor.util.NodeUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Supplier;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link AbstractFileEditor} for working with {@link Spatial}.
 *
 * @author JavaSaBr
 */
public class ModelFileEditor extends AbstractSceneFileEditor<ModelFileEditor, Spatial, ModelEditorAppState, ModelFileEditorState> {

    public static final String NO_FAST_SKY = Messages.MODEL_FILE_EDITOR_NO_SKY;

    public static final Insets LIGHT_BUTTON_OFFSET = new Insets(0, 4, 0, 4);

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setEditorName(Messages.MODEL_FILE_EDITOR_NAME);
        DESCRIPTION.setConstructor(ModelFileEditor::new);
        DESCRIPTION.setEditorId(ModelFileEditor.class.getSimpleName());
        DESCRIPTION.addExtension(FileExtensions.JME_OBJECT);
    }

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

    /**
     * The list of fast skies.
     */
    private ComboBox<String> fastSkyComboBox;

    /**
     * The light toggle.
     */
    private ToggleButton lightButton;

    public ModelFileEditor() {
        super();
    }

    @NotNull
    @Override
    protected ModelEditorAppState createEditorAppState() {
        return new ModelEditorAppState(this);
    }

    /**
     * @return the list of fast skies.
     */
    @NotNull
    private ComboBox<String> getFastSkyComboBox() {
        return fastSkyComboBox;
    }

    @Override
    public void openFile(@NotNull final Path file) {
        super.openFile(file);

        final Path assetFile = requireNonNull(getAssetFile(file), "Asset file for " + file + " can't be null.");
        final ModelKey modelKey = new ModelKey(toAssetPath(assetFile));

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Spatial model = assetManager.loadAsset(modelKey);

        MaterialUtils.cleanUpMaterialParams(model);

        final ModelEditorAppState editorAppState = getEditorAppState();
        editorAppState.openModel(model);

        handleAddedObject(model);

        setCurrentModel(model);
        setIgnoreListeners(true);
        try {

            final ComboBox<String> fastSkyComboBox = getFastSkyComboBox();
            fastSkyComboBox.getSelectionModel().select(FAST_SKY_LIST.first());

            final ModelNodeTree modelNodeTree = getModelNodeTree();
            modelNodeTree.fill(model);

        } finally {
            setIgnoreListeners(false);
        }

        EXECUTOR_MANAGER.addFXTask(this::loadState);
    }

    @Override
    protected void loadState() {
        super.loadState();

        fastSkyComboBox.getSelectionModel().select(editorState.getSkyType());
        lightButton.setSelected(editorState.isEnableLight());
    }

    @NotNull
    @Override
    protected Supplier<EditorState> getStateConstructor() {
        return ModelFileEditorState::new;
    }

    @Override
    protected void handleAddedObject(@NotNull final Spatial model) {
        super.handleAddedObject(model);

        final ModelEditorAppState editorState = getEditorAppState();
        final Array<Geometry> geometries = ArrayFactory.newArray(Geometry.class);

        NodeUtils.addGeometry(model, geometries);

        if (!geometries.isEmpty()) {
            geometries.forEach(geometry -> {
                if (geometry.getUserData(ModelNodeTree.USER_DATA_IS_SKY) == Boolean.TRUE) {
                    editorState.addCustomSky(geometry);
                }
            });
        }
    }

    @NotNull
    @Override
    public EditorDescription getDescription() {
        return DESCRIPTION;
    }

    @Override
    protected void createToolbar(@NotNull final HBox container) {
        super.createToolbar(container);

        lightButton = new ToggleButton();
        lightButton.setGraphic(new ImageView(Icons.LIGHT_16));
        lightButton.setSelected(true);
        lightButton.selectedProperty().addListener((observable, oldValue, newValue) -> changeLight(newValue));

        final Label fastSkyLabel = new Label(Messages.MODEL_FILE_EDITOR_FAST_SKY + ":");

        fastSkyComboBox = new ComboBox<>();
        fastSkyComboBox.setId(CSSIds.MATERIAL_FILE_EDITOR_TOOLBAR_BOX);
        fastSkyComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> changeFastSky(newValue));

        final ObservableList<String> skyItems = fastSkyComboBox.getItems();

        FAST_SKY_LIST.forEach(skyItems::add);

        final ResourceManager resourceManager = ResourceManager.getInstance();
        final Array<Path> additionalEnvs = resourceManager.getAdditionalEnvs();
        additionalEnvs.forEach(path -> skyItems.add(path.toString()));

        FXUtils.addClassTo(lightButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(lightButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(fastSkyLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(fastSkyComboBox, CSSClasses.SPECIAL_FONT_13);

        FXUtils.addToPane(lightButton, container);
        FXUtils.addToPane(fastSkyLabel, container);
        FXUtils.addToPane(fastSkyComboBox, container);

        HBox.setMargin(lightButton, LIGHT_BUTTON_OFFSET);
    }

    /**
     * Handle changing a sky.
     */
    private void changeFastSky(@NotNull final String newSky) {
        if (isIgnoreListeners()) return;

        final ModelEditorAppState editorAppState = getEditorAppState();

        if (NO_FAST_SKY.equals(newSky)) {
            editorAppState.changeFastSky(null);
            final ModelFileEditorState editorState = getEditorState();
            if (editorState != null) editorState.setSkyType(0);
            return;
        }

        final AssetManager assetManager = EDITOR.getAssetManager();

        final TextureKey key = new TextureKey(newSky, true);
        key.setGenerateMips(false);

        final Texture texture = assetManager.loadTexture(key);
        final Spatial newFastSky = SkyFactory.createSky(assetManager, texture, EnvMapType.EquirectMap);

        editorAppState.changeFastSky(newFastSky);

        final SingleSelectionModel<String> selectionModel = fastSkyComboBox.getSelectionModel();
        final int selectedIndex = selectionModel.getSelectedIndex();

        final ModelFileEditorState editorState = getEditorState();
        if (editorState != null) editorState.setSkyType(selectedIndex);
    }

    /**
     * Handle changing camera light visibility.
     */
    private void changeLight(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final ModelEditorAppState editorAppState = getEditorAppState();
        editorAppState.updateLightEnabled(newValue);

        if (editorState != null) editorState.setEnableLight(newValue);
    }

    @Override
    public void notifyAddedChild(@NotNull final Object parent, @NotNull final Object added, final int index) {
        super.notifyAddedChild(parent, added, index);

        final ModelEditorAppState editorAppState = getEditorAppState();

        if (added instanceof Spatial) {

            final Spatial spatial = (Spatial) added;

            final boolean isSky = spatial.getUserData(ModelNodeTree.USER_DATA_IS_SKY) == Boolean.TRUE;

            if (isSky) {
                editorAppState.addCustomSky(spatial);
                editorAppState.updateLightProbe();
            }
        }
    }

    @Override
    public void notifyRemovedChild(@NotNull final Object parent, @NotNull final Object removed) {
        super.notifyRemovedChild(parent, removed);

        final ModelEditorAppState editorAppState = getEditorAppState();

        if (removed instanceof Spatial) {

            final Spatial spatial = (Spatial) removed;

            final boolean isSky = spatial.getUserData(ModelNodeTree.USER_DATA_IS_SKY) == Boolean.TRUE;

            if (isSky) {
                editorAppState.removeCustomSky(spatial);
                editorAppState.updateLightProbe();
            }
        }
    }

    @Override
    public String toString() {
        return "ModelFileEditor{" +
                "} " + super.toString();
    }
}
