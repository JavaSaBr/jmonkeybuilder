package com.ss.editor.ui.component.editor.impl.material;

import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_FLIPPED_TEXTURES;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_FLIPPED_TEXTURES;
import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.editor.util.MaterialUtils.updateMaterialIdNeed;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.asset.MaterialKey;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.model.node.material.RootMaterialSettings;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.part3d.editor.impl.material.MaterialEditor3DPart;
import com.ss.editor.plugin.api.editor.material.BaseMaterialEditor3dPart.ModelType;
import com.ss.editor.plugin.api.editor.material.BaseMaterialFileEditor;
import com.ss.editor.ui.component.editor.EditorDescriptor;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.impl.EditorMaterialEditorState;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.event.impl.FileChangedEvent;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.MaterialSerializer;
import com.ss.editor.util.MaterialUtils;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
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
import java.util.function.Supplier;

/**
 * The implementation of the Editor to edit materials.
 *
 * @author JavaSaBr
 */
public class MaterialFileEditor extends
        BaseMaterialFileEditor<MaterialEditor3DPart, EditorMaterialEditorState, ChangeConsumer> {

    public static final EditorDescriptor DESCRIPTOR = new EditorDescriptor(
            MaterialFileEditor::new,
            Messages.MATERIAL_EDITOR_NAME,
            MaterialFileEditor.class.getSimpleName(),
            FileExtensions.JME_MATERIAL
    );

    /**
     * The list of material definitions.
     */
    @NotNull
    private final ComboBox<String> materialDefinitionBox;

    /**
     * The current editing material.
     */
    @Nullable
    private Material currentMaterial;

    private MaterialFileEditor() {
        super();
        this.materialDefinitionBox = new ComboBox<>();
    }

    @Override
    @FxThread
    protected @NotNull MaterialEditor3DPart create3dEditorPart() {
        return new MaterialEditor3DPart(this);
    }

    @Override
    @FxThread
    protected void processChangedFile(@NotNull final FileChangedEvent event) {
        super.processChangedFile(event);

        var currentMaterial = getCurrentMaterial();
        var file = event.getFile();

        var executorManager = ExecutorManager.getInstance();
        executorManager.addJmeTask(() -> {
            var newMaterial = updateMaterialIdNeed(file, currentMaterial);
            if (newMaterial != null) {
                executorManager.addFxTask(() -> reload(newMaterial));
            }
        });
    }

    @Override
    @BackgroundThread
    public void doSave(@NotNull Path toStore) throws Throwable {
        super.doSave(toStore);

        var currentMaterial = getCurrentMaterial();
        var content = MaterialSerializer.serializeToString(currentMaterial);

        try (var out = new PrintWriter(Files.newOutputStream(toStore))) {
            out.print(content);
        }
    }

    @Override
    @FxThread
    protected void handleExternalChanges() {
        super.handleExternalChanges();

        var assetFile = notNull(getAssetFile(getEditFile()));
        var materialKey = new MaterialKey(toAssetPath(assetFile));

        var material = EditorUtil.getAssetManager()
                .loadAsset(materialKey);

        reload(material);

        operationControl.clear();
    }

    /**
     * Try to apply dropped texture.
     *
     * @param editor    the editor.
     * @param dragEvent the drag event.
     * @param path      the path to the texture.
     */
    private void applyTexture(@NotNull MaterialFileEditor editor, @NotNull DragEvent dragEvent, @NotNull Path path) {

        var textureName = path.getFileName().toString();
        var textureType = MaterialUtils.getPossibleTextureType(textureName);

        if (textureType == 0) {
            return;
        }

        var paramNames = MaterialUtils.getPossibleParamNames(textureType);
        var currentMaterial = getCurrentMaterial();
        var materialDef = currentMaterial.getMaterialDef();

        var param = Arrays.stream(paramNames)
                .map(materialDef::getMaterialParam)
                .filter(Objects::nonNull)
                .filter(p -> p.getVarType() == VarType.Texture2D)
                .findAny();

        if (!param.isPresent()) {
            return;
        }

        var matParam = param.get();

        var executorManager = ExecutorManager.getInstance();
        executorManager.addJmeTask(() -> {

            var config = EditorConfig.getInstance();
            var assetFile = EditorUtil.requireAssetFile(path);
            var textureKey = new TextureKey(EditorUtil.toAssetPath(assetFile));
            textureKey.setFlipY(config.getBoolean(PREF_FLIPPED_TEXTURES, PREF_DEFAULT_FLIPPED_TEXTURES));

            var texture = EditorUtil.getAssetManager()
                    .loadTexture(textureKey);

            texture.setWrap(Texture.WrapMode.Repeat);

            var paramName = matParam.getName();
            var textureParam = currentMaterial.getTextureParam(paramName);
            var currentTexture = textureParam == null? null : textureParam.getTextureValue();

            var operation = new PropertyOperation<ChangeConsumer, Material, Texture>(currentMaterial,
                    paramName, texture, currentTexture);

            operation.setApplyHandler((material, newTexture) -> material.setTexture(paramName, newTexture));

            execute(operation);
        });
    }

    @Override
    @FxThread
    protected void handleDragDroppedEvent(@NotNull DragEvent dragEvent) {
        super.handleDragDroppedEvent(dragEvent);
        UiUtils.handleDroppedFile(dragEvent, FileExtensions.TEXTURE_EXTENSIONS, this,
                dragEvent, this::applyTexture);
    }

    @Override
    @FxThread
    protected void handleDragOverEvent(@NotNull DragEvent dragEvent) {
        super.handleDragOverEvent(dragEvent);
        UiUtils.acceptIfHasFile(dragEvent, FileExtensions.TEXTURE_EXTENSIONS);
    }


    @Override
    @BackgroundThread
    protected void doOpenFile(@NotNull Path file) throws IOException {
        super.doOpenFile(file);

        var assetFile = notNull(getAssetFile(file));
        var materialKey = new MaterialKey(toAssetPath(assetFile));

        var material = EditorUtil.getAssetManager()
                .loadAsset(materialKey);

        editor3dPart.changeMode(ModelType.BOX);

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
    private void reload(@NotNull Material material) {

        setCurrentMaterial(material);
        setIgnoreListeners(true);
        try {

            editor3dPart.updateMaterial(material);
            settingsTree.fill(new RootMaterialSettings(material));

            var materialDef = material.getMaterialDef();
            var availableResources = ResourceManager.getInstance()
                    .getAvailableResources(FileExtensions.JME_MATERIAL_DEFINITION);

            var items = materialDefinitionBox.getItems();
            items.clear();
            items.addAll(availableResources);

            materialDefinitionBox.getSelectionModel()
                    .select(materialDef.getAssetName());

        } finally {
            setIgnoreListeners(false);
        }
    }

    @Override
    @FxThread
    protected void createToolbar(@NotNull HBox container) {
        super.createToolbar(container);

        var label = new Label(Messages.MATERIAL_EDITOR_MATERIAL_TYPE_LABEL + ":");

        FxControlUtils.onSelectedItemChange(materialDefinitionBox, this::changeType);

        FxUtils.addClass(label, CssClasses.FILE_EDITOR_TOOLBAR_LABEL)
                .addClass(materialDefinitionBox, CssClasses.FILE_EDITOR_TOOLBAR_FIELD);

        FxUtils.addChild(container, label, materialDefinitionBox);
    }

    /**
     * Handle changing the type.
     */
    @FxThread
    private void changeType(@Nullable String newType) {
        if (!isIgnoreListeners()) {
            processChangeTypeImpl(newType);
        }
    }

    /**
     * Handle changing the type.
     */
    @FxThread
    private void processChangeTypeImpl(@Nullable String newType) {

        if (newType == null) {
            return;
        }

        var assetManager = EditorUtil.getAssetManager();
        var newMaterial = new Material(assetManager, newType);

        MaterialUtils.migrateTo(newMaterial, getCurrentMaterial());

        operationControl.clear();

        incrementChange();

        reload(newMaterial);
    }

    @FromAnyThread
    private @NotNull Material getCurrentMaterial() {
        return notNull(currentMaterial);
    }

    /**
     * Set the current editing material.
     *
     * @param currentMaterial the current editing material.
     */
    @FxThread
    private void setCurrentMaterial(@NotNull Material currentMaterial) {
        this.currentMaterial = currentMaterial;
    }

    @Override
    @FromAnyThread
    public @NotNull EditorDescriptor getDescriptor() {
        return DESCRIPTOR;
    }
}
