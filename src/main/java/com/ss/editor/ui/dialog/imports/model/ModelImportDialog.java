package com.ss.editor.ui.dialog.imports.model;

import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_TANGENT_GENERATION;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_TANGENT_GENERATION;
import static com.ss.editor.extension.property.EditablePropertyType.*;
import static com.ss.editor.util.EditorUtils.getAssetFile;
import static com.ss.editor.util.EditorUtils.toAssetPath;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static java.nio.file.StandardOpenOption.*;
import com.jme3.asset.AssetKey;
import com.jme3.asset.MaterialKey;
import com.jme3.asset.TextureKey;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.asset.locator.FileSystemAssetLocator;
import com.ss.editor.asset.locator.FolderAssetLocator;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.JmeFilePreviewManager;
import com.ss.editor.plugin.api.file.creator.GenericFileCreator;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.EditorUtils;
import com.ss.editor.util.MaterialSerializer;
import com.ss.editor.util.NodeUtils;
import com.ss.editor.util.TangentGenerator;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Function;

/**
 * The implementation of a dialog to import external models.
 *
 * @author JavaSaBr
 */
public class ModelImportDialog extends GenericFileCreator {

    private static final String PROP_FILE = "file";
    private static final String PROP_NEED_MATERIALS_EXPORT = "needMaterialsExport";
    private static final String PROP_MATERIALS_FOLDER = "materialsFolder";
    private static final String PROP_OVERWRITE_MATERIALS = "overwriteMaterials";
    private static final String PROP_OVERWRITE_TEXTURES = "overwriteTextures";
    private static final String PROP_TEXTURES_FOLDER = "texturesFolder";

    private static final Array<String> MATERIAL_EXPORT_DEPS = ArrayFactory.asArray(PROP_NEED_MATERIALS_EXPORT);

    /**
     * The image view to show preview of model.
     */
    @NotNull
    private final ImageView imageView;

    /**
     * The rendered file.
     */
    @Nullable
    private Path renderedFile;

    public ModelImportDialog() {
        this.imageView = new ImageView();
        setTitleText(Messages.IMPORT_MODEL_DIALOG_TITLE);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_IMPORT;
    }

    @Override
    @FromAnyThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {

        var result = Array.ofType(PropertyDefinition.class);
        result.add(new PropertyDefinition(EXTERNAL_FILE, Messages.IMPORT_MODEL_DIALOG_EXTERNAL_FILE, PROP_FILE, null));
        result.add(new PropertyDefinition(FOLDER_FROM_ASSET_FOLDER, Messages.IMPORT_MODEL_DIALOG_TEXTURES_FOLDER, PROP_TEXTURES_FOLDER, null));
        result.add(new PropertyDefinition(BOOLEAN, Messages.IMPORT_MODEL_DIALOG_OVERWRITE_TEXTURES, PROP_OVERWRITE_TEXTURES, true));
        result.add(new PropertyDefinition(BOOLEAN, Messages.MODEL_CONVERTER_DIALOG_EXPORT_MATERIALS, PROP_NEED_MATERIALS_EXPORT, false));
        result.add(new PropertyDefinition(FOLDER_FROM_ASSET_FOLDER, MATERIAL_EXPORT_DEPS, Messages.MODEL_CONVERTER_DIALOG_MATERIAL_FOLDER, PROP_MATERIALS_FOLDER, null));
        result.add(new PropertyDefinition(BOOLEAN, MATERIAL_EXPORT_DEPS, Messages.MODEL_CONVERTER_DIALOG_OVERWRITE_MATERIALS, PROP_OVERWRITE_MATERIALS, false));

        return result;
    }

    @Override
    @FxThread
    protected void createPreview(@NotNull BorderPane container) {
        super.createPreview(container);

        imageView.fitHeightProperty()
                .bind(container.heightProperty().subtract(4));
        imageView.fitWidthProperty()
                .bind(container.widthProperty().subtract(4));

        container.setCenter(imageView);
    }

    @Override
    @FromAnyThread
    protected boolean needPreview() {
        return true;
    }

    /**
     * Get the image view.
     *
     * @return the image view.
     */
    @FxThread
    private @NotNull ImageView getImageView() {
        return notNull(imageView);
    }

    @Override
    @FxThread
    protected boolean validate(@NotNull VarTable vars) {

        var imageView = getImageView();

        if (!vars.has(PROP_FILE)) {
            imageView.setImage(null);
            return false;
        }

        var file = vars.get(PROP_FILE, Path.class);

        if (!JmeFilePreviewManager.isModelFile(file)) {
            imageView.setImage(null);
            return false;
        }

        var renderedFile = getRenderedFile();
        if (file.equals(renderedFile)) {
            return super.validate(vars);
        }

        var width = (int) imageView.getFitWidth();
        var height = (int) imageView.getFitHeight();

        var previewManager = JmeFilePreviewManager.getInstance();
        previewManager.showExternal(file, width, height);

        var sourceView = previewManager.getImageView();
        var imageProperty = imageView.imageProperty();
        imageProperty.bind(sourceView.imageProperty());

        setRenderedFile(file);

        return super.validate(vars);
    }

    @Override
    @FxThread
    protected void processOk() {
        hide();

        UiUtils.incrementLoading();

        ExecutorManager.getInstance()
                .addBackgroundTask(this::startImportInBackground);
    }

    /**
     * Start importing model in background.
     */
    @BackgroundThread
    private void startImportInBackground() {
        try {
            importModelInBackground();
        } finally {
            UiUtils.decrementLoading();
        }
    }

    @Override
    @FromAnyThread
    protected @NotNull String getFileExtension() {
        return FileExtensions.JME_OBJECT;
    }

    /**
     * Import the external model in a background thread.
     */
    @BackgroundThread
    private void importModelInBackground() {

        var editorConfig = EditorConfig.getInstance();
        var modelFile = notNull(getFileToCreate());
        var parent = modelFile.getParent();
        var importedFile = vars.get(PROP_FILE, Path.class);
        var assetManager = EditorUtils.getAssetManager();

        Spatial model;

        FolderAssetLocator.setIgnore(true);
        try {
            model = assetManager.loadModel(importedFile.toString());
        } finally {
            FolderAssetLocator.setIgnore(false);
        }

        if (editorConfig.getBoolean(PREF_TANGENT_GENERATION, PREF_DEFAULT_TANGENT_GENERATION)) {
            TangentGenerator.useMikktspaceGenerator(model);
        }

        var texturesFolder = vars.get(PROP_TEXTURES_FOLDER, parent);
        var overwriteTextures = vars.getBoolean(PROP_OVERWRITE_TEXTURES);

        var needExportMaterials = vars.getBoolean(PROP_NEED_MATERIALS_EXPORT);
        var materialsFolder = vars.get(PROP_MATERIALS_FOLDER, parent);
        var overwriteMaterials = vars.getBoolean(PROP_OVERWRITE_MATERIALS, false);

        var textures = Array.ofType(Texture.class);
        var geometries = Array.ofType(Geometry.class);

        NodeUtils.visitGeometry(model, geometry -> {

            var material = geometry.getMaterial();

            if (needExportMaterials) {
                geometries.add(geometry);
            }

            material.getParams().stream()
                    .filter(MatParamTexture.class::isInstance)
                    .map(MatParam::getValue)
                    .filter(Texture.class::isInstance)
                    .map(Texture.class::cast)
                    .filter(texture -> texture.getKey() != null)
                    .forEach(textures::add);
        });

        copyTextures(texturesFolder, overwriteTextures, textures);

        if (needExportMaterials) {
            exportMaterials(materialsFolder, overwriteMaterials, geometries);
        }

        var assetFile = notNull(getAssetFile(modelFile));
        var assetPath = toAssetPath(assetFile);

        model.setName(assetPath);

        var exporter = BinaryExporter.getInstance();

        try (var out = Files.newOutputStream(modelFile, WRITE, TRUNCATE_EXISTING, CREATE)) {
            exporter.save(model, out);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        notifyFileCreated(modelFile, true);
    }

    /**
     * Export all embedded materials from the external model.
     *
     * @param materialsFolder    the materials folder.
     * @param overwriteMaterials true if we can overwrite existing materials.
     * @param geometries         the found geometries in the model.
     */
    @BackgroundThread
    private void exportMaterials(
            @NotNull Path materialsFolder,
            boolean overwriteMaterials,
            @NotNull Array<Geometry> geometries
    ) {

        if (geometries.isEmpty()) {
            return;
        }

        var resultNameToKey = ObjectDictionary.ofType(String.class);

        for (var geometry : geometries) {

            var material = geometry.getMaterial();
            var originalName = material.getName();
            var name = StringUtils.isEmpty(geometry.getName()) ? "geom" : geometry.getName();
            var resultName = StringUtils.isEmpty(originalName) ? "embedded-mat-" + name : originalName;

            var newKey = resultNameToKey.get(resultName);

            if (newKey != null) {
                material.setKey(new MaterialKey(newKey));
                continue;
            }

            var resultFile = materialsFolder.resolve(resultName + "." + FileExtensions.JME_MATERIAL);

            if (!Files.exists(resultFile) || overwriteMaterials) {
                try (var pout = new PrintWriter(Files.newOutputStream(resultFile, WRITE, TRUNCATE_EXISTING, CREATE))) {
                    pout.println(MaterialSerializer.serializeToString(material));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            var assetFile = notNull(getAssetFile(resultFile));
            var assetPath = toAssetPath(assetFile);

            material.setKey(new MaterialKey(assetPath));
            resultNameToKey.put(resultName, assetPath);
        }
    }

    /**
     * Copy textures from the external model.
     *
     * @param texturesFolder    the textures folder.
     * @param overwriteTextures true if need to overwrite existing textures.
     * @param textures          the found textures.
     */
    @BackgroundThread
    private void copyTextures(
            @NotNull Path texturesFolder,
            boolean overwriteTextures,
            @NotNull Array<Texture> textures) {

        if (textures.isEmpty()) {
            return;
        }

        var oldKeyToNew = ObjectDictionary.ofType(String.class);
        var newTextureKeys = Array.ofType(AssetKey.class);

        for (var texture : textures) {

            var textureKey = (TextureKey) texture.getKey();
            if (newTextureKeys.contains(textureKey)) {
                continue;
            }

            var newKey = oldKeyToNew.get(textureKey.getName(),
                    makeCopyTextureFunction(texturesFolder, overwriteTextures));

            var newTextureKey = new TextureKey(newKey, textureKey.isFlipY());
            newTextureKey.setGenerateMips(textureKey.isGenerateMips());
            newTextureKey.setTextureTypeHint(textureKey.getTextureTypeHint());

            texture.setKey(newTextureKey);
            newTextureKeys.add(newTextureKey);
        }
    }

    /**
     * Create the copy texture function.
     *
     * @param texturesFolder    the textures folder.
     * @param overwriteTextures true if we can overwrite existing textures.
     * @return the new asset path.
     */
    @FromAnyThread
    private @NotNull Function<String, String> makeCopyTextureFunction(
            @NotNull Path texturesFolder,
            boolean overwriteTextures)
    {
        return oldPath -> {

            var textureFile = Paths.get(oldPath);
            var fileName = textureFile.getFileName();

            var newTextureFile = texturesFolder.resolve(fileName);
            var assetFile = notNull(getAssetFile(newTextureFile));
            var assetPath = toAssetPath(assetFile);

            if (Files.exists(newTextureFile) && !overwriteTextures) {
                return assetPath;
            }

            try {
                Files.copy(textureFile, newTextureFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return assetPath;
        };
    }

    /**
     * Set the rendered file.
     *
     * @param renderedFile the rendered file.
     */
    @FxThread
    private void setRenderedFile(@Nullable Path renderedFile) {
        this.renderedFile = renderedFile;
    }

    /**
     * Get the rendered file.
     *
     * @return the rendered file.
     */
    @FxThread
    private @Nullable Path getRenderedFile() {
        return renderedFile;
    }

    @Override
    @FxThread
    public void hide() {

        var previewManager = JmeFilePreviewManager.getInstance();
        previewManager.clear();

        FileSystemAssetLocator.clear();

        super.hide();
    }
}
