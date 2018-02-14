package com.ss.editor.ui.component.editor.impl.material;

import static com.ss.editor.Messages.MATERIAL_EDITOR_NAME;
import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_FLIPPED_TEXTURES;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_FLIPPED_TEXTURES;
import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.editor.util.MaterialUtils.updateMaterialIdNeed;
import static com.ss.rlib.util.ObjectUtils.notNull;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.asset.TextureKey;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.model.node.material.RootMaterialSettings;
import com.ss.editor.model.undo.EditorOperationControl;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.part3d.editor.impl.material.MaterialEditor3DPart;
import com.ss.editor.plugin.api.editor.material.BaseMaterialEditor3DPart.ModelType;
import com.ss.editor.plugin.api.editor.material.BaseMaterialFileEditor;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.EditorMaterialEditorState;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.event.impl.FileChangedEvent;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.MaterialSerializer;
import com.ss.editor.util.MaterialUtils;
import com.ss.rlib.ui.util.FXUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * The implementation of the Editor to edit materials.
 *
 * @author JavaSaBr
 */
public class MaterialFileEditor extends
        BaseMaterialFileEditor<MaterialEditor3DPart, EditorMaterialEditorState, ChangeConsumer> {

    /**
     * The description.
     */
    @NotNull
    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(MaterialFileEditor::new);
        DESCRIPTION.setEditorName(MATERIAL_EDITOR_NAME);
        DESCRIPTION.setEditorId(MaterialFileEditor.class.getSimpleName());
        DESCRIPTION.addExtension(FileExtensions.JME_MATERIAL);
    }

    @NotNull
    private static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();

    /**
     * The list of material definitions.
     */
    @Nullable
    private ComboBox<String> materialDefinitionBox;

    /**
     * The current editing material.
     */
    @Nullable
    private Material currentMaterial;

    private MaterialFileEditor() {
        super();
    }

    @Override
    @FxThread
    protected @NotNull MaterialEditor3DPart create3DEditorPart() {
        return new MaterialEditor3DPart(this);
    }

    @Override
    @FxThread
    protected void processChangedFile(@NotNull final FileChangedEvent event) {
        super.processChangedFile(event);

        final Material currentMaterial = getCurrentMaterial();
        final Path file = event.getFile();

        EXECUTOR_MANAGER.addJmeTask(() -> {
            final Material newMaterial = updateMaterialIdNeed(file, currentMaterial);
            if (newMaterial != null) {
                EXECUTOR_MANAGER.addFxTask(() -> reload(newMaterial));
            }
        });
    }

    @Override
    @BackgroundThread
    public void doSave(@NotNull final Path toStore) throws IOException {
        super.doSave(toStore);

        final Material currentMaterial = getCurrentMaterial();
        final String content = MaterialSerializer.serializeToString(currentMaterial);

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(toStore))) {
            out.print(content);
        }
    }

    @Override
    @FromAnyThread
    protected @NotNull AssetKey<?> getFileKey(@NotNull final String assetPath) {
        return new MaterialKey(assetPath);
    }

    @Override
    @FxThread
    protected void handleExternalChanges() {
        super.handleExternalChanges();

        final Path assetFile = notNull(getAssetFile(getEditFile()));
        final MaterialKey materialKey = new MaterialKey(toAssetPath(assetFile));

        final AssetManager assetManager = EditorUtil.getAssetManager();
        final Material material = assetManager.loadAsset(materialKey);

        reload(material);

        final EditorOperationControl operationControl = getOperationControl();
        operationControl.clear();
    }

    /**
     * Try to apply dropped texture.
     *
     * @param editor    the editor.
     * @param dragEvent the drag event.
     * @param path      the path to the texture.
     */
    private void applyTexture(@NotNull final MaterialFileEditor editor, @NotNull final DragEvent dragEvent,
                              @NotNull final Path path) {

        final String textureName = path.getFileName().toString();
        final int textureType = MaterialUtils.getPossibleTextureType(textureName);

        if (textureType == 0) {
            return;
        }

        final String[] paramNames = MaterialUtils.getPossibleParamNames(textureType);
        final Material currentMaterial = getCurrentMaterial();
        final MaterialDef materialDef = currentMaterial.getMaterialDef();

        final Optional<MatParam> param = Arrays.stream(paramNames)
                .map(materialDef::getMaterialParam)
                .filter(Objects::nonNull)
                .filter(p -> p.getVarType() == VarType.Texture2D)
                .findAny();

        if (!param.isPresent()) {
            return;
        }

        final MatParam matParam = param.get();

        EXECUTOR_MANAGER.addJmeTask(() -> {

            final EditorConfig config = EditorConfig.getInstance();
            final Path assetFile = notNull(getAssetFile(path));
            final TextureKey textureKey = new TextureKey(toAssetPath(assetFile));
            textureKey.setFlipY(config.getBoolean(PREF_FLIPPED_TEXTURES, PREF_DEFAULT_FLIPPED_TEXTURES));

            final AssetManager assetManager = EditorUtil.getAssetManager();
            final Texture texture = assetManager.loadTexture(textureKey);
            texture.setWrap(Texture.WrapMode.Repeat);

            final String paramName = matParam.getName();
            final MatParamTexture textureParam = currentMaterial.getTextureParam(paramName);
            final Texture currentTexture = textureParam == null? null : textureParam.getTextureValue();

            PropertyOperation<ChangeConsumer, Material, Texture> operation =
                    new PropertyOperation<>(currentMaterial, paramName, texture, currentTexture);
            operation.setApplyHandler((material, newTexture) -> material.setTexture(paramName, newTexture));

            execute(operation);
        });
    }

    @Override
    @FxThread
    protected void handleDragDroppedEvent(@NotNull final DragEvent dragEvent) {
        super.handleDragDroppedEvent(dragEvent);
        UiUtils.handleDroppedFile(dragEvent, FileExtensions.TEXTURE_EXTENSIONS, this,
                dragEvent, this::applyTexture);
    }

    @Override
    @FxThread
    protected void handleDragOverEvent(@NotNull final DragEvent dragEvent) {
        super.handleDragOverEvent(dragEvent);
        UiUtils.acceptIfHasFile(dragEvent, FileExtensions.TEXTURE_EXTENSIONS);
    }


    @Override
    @FxThread
    protected void doOpenFile(@NotNull final Path file) throws IOException {
        super.doOpenFile(file);

        final Path assetFile = notNull(getAssetFile(file));
        final MaterialKey materialKey = new MaterialKey(toAssetPath(assetFile));

        final AssetManager assetManager = EditorUtil.getAssetManager();
        final Material material = assetManager.loadAsset(materialKey);

        final MaterialEditor3DPart editor3DState = getEditor3DPart();
        editor3DState.changeMode(ModelType.BOX);

        reload(material);
    }

    @FxThread
    @Override
    protected @Nullable Supplier<EditorState> getEditorStateFactory() {
        return EditorMaterialEditorState::new;
    }

    /**
     * Reload the material.
     */
    @FxThread
    private void reload(@NotNull final Material material) {
        setCurrentMaterial(material);
        setIgnoreListeners(true);
        try {

            final MaterialEditor3DPart editor3DState = getEditor3DPart();
            editor3DState.updateMaterial(material);

            getSettingsTree().fill(new RootMaterialSettings(material));

            final ComboBox<String> materialDefinitionBox = getMaterialDefinitionBox();
            final ObservableList<String> items = materialDefinitionBox.getItems();
            items.clear();
            items.addAll(RESOURCE_MANAGER.getAvailableResources(FileExtensions.JME_MATERIAL_DEFINITION));

            final MaterialDef materialDef = material.getMaterialDef();
            materialDefinitionBox.getSelectionModel().select(materialDef.getAssetName());

        } finally {
            setIgnoreListeners(false);
        }
    }

    /**
     * @return the list of material definitions.
     */
    @FromAnyThread
    private @NotNull ComboBox<String> getMaterialDefinitionBox() {
        return notNull(materialDefinitionBox);
    }

    @Override
    @FxThread
    protected void createToolbar(@NotNull final HBox container) {
        super.createToolbar(container);

        final Label materialDefinitionLabel = new Label(Messages.MATERIAL_EDITOR_MATERIAL_TYPE_LABEL + ":");

        materialDefinitionBox = new ComboBox<>();
        materialDefinitionBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> changeType(newValue));

        FXUtils.addToPane(materialDefinitionLabel, container);
        FXUtils.addToPane(materialDefinitionBox, container);

        FXUtils.addClassTo(materialDefinitionLabel, CssClasses.FILE_EDITOR_TOOLBAR_LABEL);
        FXUtils.addClassTo(materialDefinitionBox, CssClasses.FILE_EDITOR_TOOLBAR_FIELD);
    }

    /**
     * Handle changing the type.
     */
    @FxThread
    private void changeType(@Nullable final String newType) {
        if (isIgnoreListeners()) return;
        processChangeTypeImpl(newType);
    }

    /**
     * Handle changing the type.
     */
    @FxThread
    private void processChangeTypeImpl(@Nullable final String newType) {
        if (newType == null) return;

        final AssetManager assetManager = EditorUtil.getAssetManager();
        final Material newMaterial = new Material(assetManager, newType);

        MaterialUtils.migrateTo(newMaterial, getCurrentMaterial());

        final EditorOperationControl operationControl = getOperationControl();
        operationControl.clear();

        incrementChange();
        reload(newMaterial);
    }

    @FromAnyThread
    private @NotNull Material getCurrentMaterial() {
        return notNull(currentMaterial);
    }

    /**
     * @param currentMaterial the current editing material.
     */
    @FxThread
    private void setCurrentMaterial(@NotNull final Material currentMaterial) {
        this.currentMaterial = currentMaterial;
    }

    @Override
    @FromAnyThread
    public @NotNull EditorDescription getDescription() {
        return DESCRIPTION;
    }
}
